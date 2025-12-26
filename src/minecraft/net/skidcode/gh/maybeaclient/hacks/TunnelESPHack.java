package net.skidcode.gh.maybeaclient.hacks;

import java.util.ArrayList;
import java.util.HashSet;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraft.src.Block;
import net.minecraft.src.Material;
import net.minecraft.src.RenderManager;
import net.skidcode.gh.maybeaclient.events.Event;
import net.skidcode.gh.maybeaclient.events.EventListener;
import net.skidcode.gh.maybeaclient.events.EventRegistry;
import net.skidcode.gh.maybeaclient.events.impl.EventWorldRenderPreFog;
import net.skidcode.gh.maybeaclient.hacks.category.Category;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingBoolean;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingColor;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingInteger;
import net.skidcode.gh.maybeaclient.utils.Direction;
import net.skidcode.gh.maybeaclient.utils.RenderUtils;
import net.skidcode.gh.maybeaclient.utils.TunnelPos;
import net.skidcode.gh.maybeaclient.utils.WorldUtils;

public class TunnelESPHack extends Hack implements EventListener{
	public static TunnelESPHack instance;
	public SettingInteger searchDepth = new SettingInteger(this, "Search Depth", 10, 1, 20);
	public SettingInteger maxEmptyAbove = new SettingInteger(this, "MaxEmptyAbove", 5, 0, 10);
	public SettingBoolean checkIsBelow = new SettingBoolean(this, "CheckIsBelow", true);
	public SettingColor color = new SettingColor(this, "Color", 255, 255, 0);
	public TunnelESPHack() {
		super("TunnelESP", "Highlights 2x1 tunnels", Keyboard.KEY_NONE, Category.RENDER);
		instance = this;
		this.addSetting(this.searchDepth);
		this.addSetting(this.checkIsBelow);
		this.addSetting(this.maxEmptyAbove);
		this.addSetting(this.color);
		
		EventRegistry.registerListener(EventWorldRenderPreFog.class, this);
	}
	
	public static HashSet<TunnelPos> positions = new HashSet<TunnelPos>();
	public static ArrayList<TunnelPos> toRemove = new ArrayList<TunnelPos>();
	@Override
	public void handleEvent(Event event) {
		if(event instanceof EventWorldRenderPreFog) {
			GL11.glPushMatrix();
			GL11.glBlendFunc(770, 771);
			GL11.glColor3d(this.color.red / 255d, this.color.green / 255d, this.color.blue / 255d);
			GL11.glLineWidth(1F);
			GL11.glDisable(3553 /* GL_TEXTURE_2D */);
			GL11.glDisable(2929 /* GL_DEPTH_TEST */);
			GL11.glDepthMask(false);
			for (TunnelPos pos : positions) {
				if(!this.checkBlock(pos.x, pos.y-1, pos.z, Direction.NULL, false)) {
					toRemove.add(pos);
					continue;
				}
				double renderX = pos.x - RenderManager.renderPosX;
				double renderZ = pos.z - RenderManager.renderPosZ;
				RenderUtils.drawOutlinedBlockBB(renderX, pos.y - RenderManager.renderPosY, renderZ, pos.xwidth, 2, pos.zwidth);
			}
			
			for(int i = toRemove.size() - 1; i >= 0; --i) {
				TunnelPos p = toRemove.remove(i);
				positions.remove(p);
			}
			
			GL11.glDepthMask(true);
			GL11.glEnable(3553 /* GL_TEXTURE_2D */);
			GL11.glEnable(2929 /* GL_DEPTH_TEST */);
			GL11.glPopMatrix();
		}
	}
	
	
	public boolean forceCheckBlock(int id, int x, int y, int z) {
		positions.remove(new TunnelPos(x, y+1, z, 1, 1));
		positions.remove(new TunnelPos(x, y, z, 1, 1));
		positions.remove(new TunnelPos(x, y-1, z, 1, 1));
		return checkBlock(x, y, z, Direction.NULL, true);
	}
	public boolean checkBlock(int id, int x, int y, int z) {
		return checkBlock(x, y, z, Direction.NULL, true);
	}
	public void onDisable() {
		positions.clear();
		toRemove.clear();
	}
	public boolean checkBlock(int x, int y, int z, Direction dir, boolean recurse) {
		//initial check
		if(!WorldUtils.isWalkable(x, y, z) || !WorldUtils.isPassable(x, y+1, z) || !WorldUtils.isPassable(x, y+2, z)) return false;
		
		int maxemptyabove = this.maxEmptyAbove.getValue();
		int yy = y+1;
		int cnt = -2;
		for(; yy < 128; ++yy) {
			int id = mc.theWorld.getBlockId(x, yy, z);
			Material material = id == 0 ? Material.air : Block.blocksList[id].blockMaterial;
			if(material.getIsSolid()) {
				break;
			}else {
				if(cnt > maxemptyabove) return false; //too many nonsolid above
				++cnt;
			}
		}
		if(this.checkIsBelow.getValue() && yy >= 127) return false; //above the ground
		
		//wall check
		boolean xpwall = !WorldUtils.isPassable(x+1, y+1, z) || !WorldUtils.isPassable(x+1, y+2, z);
		boolean xnwall = !WorldUtils.isPassable(x-1, y+1, z) || !WorldUtils.isPassable(x-1, y+2, z);
		boolean zpwall = !WorldUtils.isPassable(x, y+1, z+1) || !WorldUtils.isPassable(x, y+2, z+1);
		boolean znwall = !WorldUtils.isPassable(x, y+1, z-1) || !WorldUtils.isPassable(x, y+2, z-1);
		if(dir == Direction.XPOS && !zpwall && !znwall) return false;
		if(dir == Direction.ZPOS && !xpwall && !xnwall) return false;
		
		TunnelPos pos = new TunnelPos(x, y+1, z, 1, 1);
		if(positions.contains(pos)) return true;
		
		
		if(recurse) {
			boolean isX = checkBlock(x+1, y, z, Direction.NULL, false) && checkBlock(x-1, y, z, Direction.NULL, false);
			boolean isZ = checkBlock(x, y, z+1, Direction.NULL, false) && checkBlock(x, y, z-1, Direction.NULL, false);
			
			if(!isX && !isZ) return false; //not a tunnel
			if(isX && isZ) return false; //it is either not a tunnel, or diagonal, which would require other algorithm to check
			if(isX && !zpwall && !znwall) return false; //x tunnel has no any wall
			if(isZ && !xpwall && !xnwall) return false; //z tunnel has no any wall;
			
			//try finding a direction
			if(isX) dir = Direction.XPOS;
			if(isZ) dir = Direction.ZPOS;
			int xo = isX ? 1 : 0;
			int zo = isZ ? 1 : 0;
			int xpos = x;
			int zpos = z;
			
			for(int i = 0; i < this.searchDepth.getValue(); ++i) {
				xpos += xo;
				zpos += zo;
				if(!this.checkBlock(xpos, y, zpos, dir, false)) return false;
			}
			
			//add positions
			positions.add(pos);
			xpos = x;
			zpos = z;
			
			for(int i = 0; i < this.searchDepth.getValue(); ++i) {
				xpos += xo;
				zpos += zo;
				TunnelPos tp = new TunnelPos(xpos, y+1, zpos, 1, 1);
				positions.add(tp);
			}
			
			return true;
		}else {
			return true;
		}
	}

}
