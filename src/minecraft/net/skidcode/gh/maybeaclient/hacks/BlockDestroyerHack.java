package net.skidcode.gh.maybeaclient.hacks;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraft.src.MathHelper;
import net.minecraft.src.RenderManager;
import net.minecraft.src.Tessellator;
import net.skidcode.gh.maybeaclient.events.Event;
import net.skidcode.gh.maybeaclient.events.EventListener;
import net.skidcode.gh.maybeaclient.events.EventRegistry;
import net.skidcode.gh.maybeaclient.events.impl.EventPacketSend;
import net.skidcode.gh.maybeaclient.events.impl.EventPlayerUpdatePost;
import net.skidcode.gh.maybeaclient.events.impl.EventWorldRenderPreFog;
import net.skidcode.gh.maybeaclient.hacks.category.Category;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingBlockChooser;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingColor;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingInteger;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingMode;
import net.skidcode.gh.maybeaclient.utils.BlockPos;
import net.skidcode.gh.maybeaclient.utils.PlayerUtils;
import net.skidcode.gh.maybeaclient.utils.PlayerUtils.LookStatus;

public class BlockDestroyerHack extends Hack implements EventListener{
	public static BlockDestroyerHack instance;
	
	public SettingBlockChooser blocks = new SettingBlockChooser(this, "Blocks to break");
	public SettingInteger radius = new SettingInteger(this, "Radius", 3, 1, 4);
	public SettingInteger delayBetweenBlocks = new SettingInteger(this, "Delay Between Blocks", 4, 0, 20);
	public SettingMode destroyMode = new SettingMode(this, "Destroy Mode", "Vanilla", "Instant");
	public SettingColor color = new SettingColor(this, "Currently Destroying Color", 255, 0, 0);
	public SettingMode rotationMode = new SettingMode(this, "Rotation mode", "ServerSideOnly", "Off");
	
	public BlockDestroyerHack() {
		super("BlockDestroyer", "Destroys blocks around the player", Keyboard.KEY_NONE, Category.MISC);
		instance = this;
		
		this.addSetting(this.destroyMode);
		this.addSetting(this.delayBetweenBlocks);
		this.addSetting(this.radius);
		this.addSetting(this.rotationMode);
		this.addSetting(this.color);
		this.addSetting(this.blocks);
		
		EventRegistry.registerListener(EventPlayerUpdatePost.class, this);
		EventRegistry.registerListener(EventWorldRenderPreFog.class, this);
		EventRegistry.registerListener(EventPacketSend.class, this);
	}

	public void destroyBlock(int x, int y, int z) {
		if(this.destroyMode.currentMode.equalsIgnoreCase("Vanilla")) {
			PlayerUtils.destroyBlock(x, y, z, 0); //TODO side detection?
		}else {
			PlayerUtils.destroyBlockInstant(x, y, z, 0); //TODO side detection?
		}
	}
	
	@Override
	public void onDisable() {
		this.timeout = 0;
		if(this.destroyMode.currentMode.equalsIgnoreCase("Vanilla")) mc.playerController.field_1064_b = false;
	}
	
	public void deselect() {
		this.select = false;
		if(this.timeout <= 0) {
			this.timeout = this.delayBetweenBlocks.getValue();
		}
	}
	
	public static ArrayList<BlockPos> blocksArr = new ArrayList<BlockPos>();
	public int lastX, lastY, lastZ;
	public boolean select = false;
	public int timeout = 0;
	@Override
	public void handleEvent(Event event) {
		if(event instanceof EventPlayerUpdatePost) {
			if(this.timeout > 0) --this.timeout;
			blocksArr.clear();
			int xc = MathHelper.floor_double(mc.thePlayer.posX);
			int yc = MathHelper.floor_double(mc.thePlayer.posY);
			int zc = MathHelper.floor_double(mc.thePlayer.posZ);
			int d = this.radius.getValue();
			
			for(int x = xc-d; x <= xc+d; ++x) {
				for(int z = zc-d; z <= zc+d; ++z) {
					for(int y = yc-d; y <= yc+d; ++y) {
						if(this.blocks.blocks[mc.theWorld.getBlockId(x, y, z)]) {
							double dd = MathHelper.sqrt_double((xc - x)*(xc - x) + (zc - z)*(zc - z) + (yc - y)*(yc - y)); //mc.thePlayer.getDistance(x, y, z);
							if(dd <= (radius.getValue())) {
								blocksArr.add(new BlockPos(x, y, z));
							}
							
						}
					}
				}
			}
			blocksArr.sort(new BlockPos.Sorter(xc, yc, zc));
			
			if(blocksArr.size() > 0) {
				BlockPos bp = blocksArr.get(0);
				if(this.select && (bp.x != lastX || bp.y != lastY || bp.z != lastZ)) {
					this.deselect();
				}
				
				if(this.timeout <= 0) {
					if(this.rotationMode.currentMode.equalsIgnoreCase("ServerSideOnly")) PlayerUtils.lookAt(bp.x, bp.y, bp.z, LookStatus.PACKET);
					
					this.destroyBlock(bp.x, bp.y, bp.z);
					lastX = bp.x;
					lastY = bp.y;
					lastZ = bp.z;
					this.select = true;
					
				}
			}else {
				if(this.destroyMode.currentMode.equalsIgnoreCase("Vanilla")) mc.playerController.field_1064_b = false;
			}
			
		}else if(event instanceof EventWorldRenderPreFog) {
			GL11.glPushMatrix();
			GL11.glBlendFunc(770, 771);
			
			GL11.glLineWidth(1);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glDepthMask(false);
			Tessellator tess = Tessellator.instance;
			GL11.glColor3f(this.color.red / 255f, this.color.green / 255f, this.color.blue / 255f);
			if(blocksArr.size() > 0){
				BlockPos pos = blocksArr.get(0);
				double renderX = pos.x - RenderManager.renderPosX;
				double renderY = pos.y - RenderManager.renderPosY;
				double renderZ = pos.z - RenderManager.renderPosZ;
				
				tess.startDrawing(3);
				tess.addVertex(renderX, renderY, renderZ);
				tess.addVertex(renderX+1, renderY, renderZ);
				tess.addVertex(renderX+1, renderY, renderZ+1);
				tess.addVertex(renderX, renderY, renderZ+1);
				tess.addVertex(renderX, renderY, renderZ);
				
				tess.addVertex(renderX, renderY+1, renderZ);
				tess.addVertex(renderX+1, renderY+1, renderZ);
				tess.addVertex(renderX+1, renderY+1, renderZ+1);
				tess.addVertex(renderX, renderY+1, renderZ+1);
				tess.addVertex(renderX, renderY+1, renderZ);
				tess.draw();
					
				tess.startDrawing(1);
				tess.addVertex(renderX, renderY, renderZ);
				tess.addVertex(renderX, renderY+1, renderZ);
				tess.addVertex(renderX+1, renderY, renderZ);
				tess.addVertex(renderX+1, renderY+1, renderZ);
				tess.addVertex(renderX+1, renderY, renderZ+1);
				tess.addVertex(renderX+1, renderY+1, renderZ+1);
				tess.addVertex(renderX, renderY, renderZ+1);
				tess.addVertex(renderX, renderY+1, renderZ+1);
				tess.draw();
			}
			
			GL11.glDepthMask(true);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			GL11.glPopMatrix();
		}
	}

}
