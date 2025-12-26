package net.skidcode.gh.maybeaclient.hacks;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraft.src.Packet14BlockDig;
import net.minecraft.src.RenderManager;
import net.minecraft.src.Tessellator;
import net.skidcode.gh.maybeaclient.events.Event;
import net.skidcode.gh.maybeaclient.events.EventListener;
import net.skidcode.gh.maybeaclient.events.EventRegistry;
import net.skidcode.gh.maybeaclient.events.impl.EventPlayerUpdatePre;
import net.skidcode.gh.maybeaclient.events.impl.EventWorldRenderPreFog;
import net.skidcode.gh.maybeaclient.hacks.category.Category;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingBoolean;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingColor;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingInteger;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingMode;
import net.skidcode.gh.maybeaclient.utils.BlockPos;
import net.skidcode.gh.maybeaclient.utils.BlockState;

public class PacketMineHack extends Hack implements EventListener{
	public static PacketMineHack instance;
	public SettingBoolean renderSelected;
	public SettingColor renderColor = new SettingColor(this, "Render Color", 0, 0xff, 0xcc);
	public SettingMode miningMode ;
	public SettingInteger maxBlocks = new SettingInteger(this, "Max Blocks in queue", 5, 1, 10);
	public SettingInteger maxPackets = new SettingInteger(this, "Max Blocks at the same time", 5, 1, 10);
	public static ArrayList<BlockState> blocks = new ArrayList<>();
	
	public PacketMineHack() {
		super("PacketMine", "Sends packets to mine blocks", Keyboard.KEY_NONE, Category.MISC);
		instance = this;
		this.addSetting(this.renderSelected = new SettingBoolean(this, "Render Selected", true) {
			public void setValue(boolean d) {
				super.setValue(d);
				PacketMineHack.instance.renderColor.hidden = !this.getValue();
			}
		});
		this.addSetting(this.renderColor);
		this.addSetting(this.miningMode = new SettingMode(this, "Mining mode", "Sequential", "AllAtTheSameTime") {
			@Override
			public void setValue(String s) {
				super.setValue(s);
				PacketMineHack.instance.maxPackets.hidden = !this.currentMode.equalsIgnoreCase("AllAtTheSameTime");
			}
		});
		
		this.addSetting(this.maxPackets);
		this.addSetting(this.maxBlocks);
		
		EventRegistry.registerListener(EventPlayerUpdatePre.class, this);
		EventRegistry.registerListener(EventWorldRenderPreFog.class, this);
	}

	@Override
	public void onEnable() {
		super.onEnable();
		blocks.clear();
		toRemove.clear();
	}
	
	@Override
	public void onDisable() {
		super.onDisable();
		blocks.clear();
		toRemove.clear();
	}

	public void packetMine(int x, int y, int z, int id) {
		BlockState bs = new BlockState(x, y, z, id);
		if(!blocks.contains(bs)) {
			if(blocks.size() >= this.maxBlocks.getValue()) return;
			blocks.add(bs);
		}
	}
	
	static ArrayList<BlockState> toRemove = new ArrayList<>();
	@Override
	public void handleEvent(Event event) {
		if(event instanceof EventWorldRenderPreFog) {
			if(!this.renderSelected.getValue()) return;
			
			GL11.glPushMatrix();
			GL11.glBlendFunc(770, 771);
			
			GL11.glLineWidth(1);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glDepthMask(false);
			Tessellator tess = Tessellator.instance;
			GL11.glColor3f(this.renderColor.red/255f, this.renderColor.green/255f, this.renderColor.blue/255f);
			for(BlockState pos : blocks) {
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
		if(event instanceof EventPlayerUpdatePre && mc.isMultiplayerWorld()) {
			int pks = 0;
			
			for(BlockState bs : blocks) {
				int id = mc.theWorld.getBlockId(bs.x, bs.y, bs.z);
				if(bs.id != id) {
					toRemove.add(bs);
					continue;
				}
				
				Packet14BlockDig pk;
				pk = new Packet14BlockDig(0, bs.x, bs.y, bs.z, 0);
				mc.getSendQueue().addToSendQueue(pk);
				
				pk = new Packet14BlockDig(2, bs.x, bs.y, bs.z, 0);
				mc.getSendQueue().addToSendQueue(pk);
				if(this.miningMode.currentMode.equalsIgnoreCase("Sequential")) break;
				if(++pks > this.maxPackets.getValue()) break;
			}
			
			for(BlockState b : toRemove) blocks.remove(b);
			
		}
	}
}
