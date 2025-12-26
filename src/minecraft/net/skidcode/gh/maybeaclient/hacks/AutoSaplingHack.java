package net.skidcode.gh.maybeaclient.hacks;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraft.src.Block;
import net.minecraft.src.ItemBlock;
import net.minecraft.src.ItemStack;
import net.minecraft.src.MathHelper;
import net.minecraft.src.RenderManager;
import net.minecraft.src.Tessellator;
import net.skidcode.gh.maybeaclient.events.Event;
import net.skidcode.gh.maybeaclient.events.EventListener;
import net.skidcode.gh.maybeaclient.events.EventRegistry;
import net.skidcode.gh.maybeaclient.events.impl.EventPlayerUpdatePost;
import net.skidcode.gh.maybeaclient.events.impl.EventWorldRenderPreFog;
import net.skidcode.gh.maybeaclient.hacks.category.Category;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingBoolean;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingInteger;
import net.skidcode.gh.maybeaclient.utils.BlockPos;
import net.skidcode.gh.maybeaclient.utils.PlayerUtils;

public class AutoSaplingHack extends Hack implements EventListener{
	
	public SettingInteger radius = new SettingInteger(this, "Radius", 3, 1, 4);
	public SettingInteger distanceBetweenSaplings = new SettingInteger(this, "Disatnce from blocks", 1, 0, 4);
	
	public AutoSaplingHack() {
		super("AutoSapling", "Automatically places saplings from hotbar", Keyboard.KEY_NONE, Category.MISC);
		EventRegistry.registerListener(EventPlayerUpdatePost.class, this);
		//EventRegistry.registerListener(EventWorldRenderPreFog.class, this);
		
		this.addSetting(this.radius);
		this.addSetting(this.distanceBetweenSaplings);
	}
	
	public int findItemToPlace() {
		for(int i = 0; i < 9; ++i) {
			ItemStack stack = mc.thePlayer.inventory.mainInventory[i];
			
			if(stack != null && stack.getItem() instanceof ItemBlock) {
				if(stack.stackSize == 0) {
					mc.thePlayer.inventory.mainInventory[i] = null;
					continue;
				}
				ItemBlock bl = (ItemBlock) stack.getItem();
				if(bl.blockID == Block.sapling.blockID) {
					return i;
				}
			}
		}
		return -1;
	}

	public boolean canPlantAt(int x, int y, int z) {
		int idb = mc.theWorld.getBlockId(x, y, z);
		int ida = mc.theWorld.getBlockId(x, y+1, z);
		
		if(idb == Block.grass.blockID || idb == Block.dirt.blockID || idb == Block.tilledField.blockID) {
			if(ida != 0) return false;
			int n = this.distanceBetweenSaplings.getValue();
			for(int xo = x-n; xo <= x+n; ++xo) {
				for(int zo = z-n; zo <= z+n; ++zo) {
					int bb = mc.theWorld.getBlockId(xo, y+1, zo);
					if(bb != 0 && bb != Block.leaves.blockID) {
						
						return false;
					}
				}
			}
			return true;
		}
		return false;
	}
	
	public static ArrayList<BlockPos> blocks = new ArrayList<>();
	@Override
	public void handleEvent(Event event) {
		if(event instanceof EventPlayerUpdatePost) {
			blocks.clear();
			int xc = MathHelper.floor_double(mc.thePlayer.posX);
			int yc = MathHelper.floor_double(mc.thePlayer.posY);
			int zc = MathHelper.floor_double(mc.thePlayer.posZ);
			int d = this.radius.getValue();
			
			for(int x = xc-d; x <= xc+d; ++x) {
				for(int z = zc-d; z <= zc+d; ++z) {
					this.checkYPlacement(x, yc-2, z);
					for(int y = yc-d; y < yc-2; ++y) this.checkYPlacement(x, y, z);
					for(int y = yc-1; y <= yc+d; ++y) this.checkYPlacement(x, y, z);
				}
			}
		}else if(event instanceof EventWorldRenderPreFog) {
			/*GL11.glPushMatrix();
			GL11.glBlendFunc(770, 771);
			
			GL11.glLineWidth(1);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glDepthMask(false);
			Tessellator tess = Tessellator.instance;
			GL11.glColor3f(1, 0, 0);
			for(BlockPos pos : blocks) {
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
			GL11.glPopMatrix();*/
		}
	}

	public void checkYPlacement(int x, int y, int z) {
		int xx = MathHelper.floor_double(mc.thePlayer.posX);
		int yy = MathHelper.floor_double(mc.thePlayer.posY)-2;
		int zz = MathHelper.floor_double(mc.thePlayer.posZ);
		
		int item = this.findItemToPlace();
		if(item == -1) return;
		int prev = mc.thePlayer.inventory.currentItem;
		mc.thePlayer.inventory.currentItem = item;
		double d = MathHelper.sqrt_double((xx - x)*(xx - x) + (zz - z)*(zz - z) + (yy - y)*(yy - y)); //mc.thePlayer.getDistance(x, y, z);
		if(d <= (radius.getValue())) {
			if(this.canPlantAt(x, y, z)) {
				PlayerUtils.placeBlockUnsafe(x, y, z, 1);
				blocks.add(new BlockPos(x, y, z));
			}
		}
		mc.thePlayer.inventory.currentItem = prev;
	}
}
