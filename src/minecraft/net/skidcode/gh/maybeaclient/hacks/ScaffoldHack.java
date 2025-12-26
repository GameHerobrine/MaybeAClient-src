package net.skidcode.gh.maybeaclient.hacks;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import net.minecraft.src.Block;
import net.minecraft.src.ItemBlock;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Material;
import net.minecraft.src.MathHelper;
import net.minecraft.src.Packet16BlockItemSwitch;
import net.minecraft.src.PlayerControllerMP;
import net.skidcode.gh.maybeaclient.Client;
import net.skidcode.gh.maybeaclient.events.Event;
import net.skidcode.gh.maybeaclient.events.EventListener;
import net.skidcode.gh.maybeaclient.events.EventRegistry;
import net.skidcode.gh.maybeaclient.events.impl.EventPlayerUpdatePre;
import net.skidcode.gh.maybeaclient.hacks.category.Category;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingBlockChooser;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingBoolean;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingInteger;
import net.skidcode.gh.maybeaclient.utils.BlockPos;
import net.skidcode.gh.maybeaclient.utils.ChatColor;

public class ScaffoldHack extends Hack implements EventListener{
	static enum PlaceStatus{
		PLACED,
		FAILED,
		ALREADY
	};
	public SettingInteger delayBetweenBlocks = new SettingInteger(this, "DelayBetweenBlocks", 0, 0, 10);
	public SettingInteger height = new SettingInteger(this, "Height", 1, 1, 3);
	public SettingInteger radius = new SettingInteger(this, "Radius", 1, 1, 5);
	public SettingBoolean enableBlockFilter;
	public SettingBlockChooser filter = new SettingBlockChooser(this, "Filter");
	
	public ScaffoldHack() {
		super("Scaffold", "Auto places blocks below the player", Keyboard.KEY_NONE, Category.MOVEMENT);
		this.enableBlockFilter = new SettingBoolean(this, "Allow using only specific blocks", false) {
			public void setValue(boolean d) {
				super.setValue(d);
				((ScaffoldHack)this.hack).filter.hidden = !this.value;
			}
		};
		//this.addSetting(this.radius);
		this.addSetting(this.enableBlockFilter);
		this.addSetting(this.filter);
		this.addSetting(this.radius);
		this.addSetting(this.height);
		this.addSetting(this.delayBetweenBlocks);
		EventRegistry.registerListener(EventPlayerUpdatePre.class, this);
	}
	
	public int findSlotInHotbar() {
		for(int i = 0; i < 9; ++i) {
			ItemStack stack = mc.thePlayer.inventory.mainInventory[i];
			
			if(stack != null && stack.getItem() instanceof ItemBlock) {
				if(stack.stackSize == 0) {
					mc.thePlayer.inventory.mainInventory[i] = null;
					continue;
				}
				ItemBlock bl = (ItemBlock) stack.getItem();
				if(!this.enableBlockFilter.value) {
					if(Block.blocksList[bl.blockID].blockMaterial.getIsSolid()) return i;
					continue;
				}else if(this.filter.blocks[bl.blockID]) {
					return i;
				}
			}
		}
		return -1;
	}
	
	
	
	public PlaceStatus placeBlock(int x, int y, int z) {
		Material mat = Client.mc.theWorld.getBlockMaterial(x, y, z);
		if(mat.getIsSolid()) return PlaceStatus.ALREADY;
		//int px = MathHelper.floor_double(mc.thePlayer.posX);
		//int pz = MathHelper.floor_double(mc.thePlayer.posZ);
		//int py = MathHelper.floor_double(mc.thePlayer.posY - 2);
		//if(px != x || pz != z || py != y){
		int slot = this.findSlotInHotbar();
		if(slot != -1) {
			int saved = mc.thePlayer.inventory.currentItem;
			ItemStack item = mc.thePlayer.inventory.mainInventory[slot];

			int side = 0;
			PlaceStatus ret = PlaceStatus.FAILED;
			for(; side <= 6; ++side) {
				if(side == 6) break;
				int xp = x;
				int yp = y; //TODO fix please
				int zp = z;
				
				if(side == 0) ++yp;
				if(side == 1) --yp;
				if(side == 2) ++zp;
				if(side == 3) --zp;
				if(side == 4) ++xp;
				if(side == 5) --xp;
				int placeon = mc.theWorld.getBlockId(xp, yp, zp);
				if(placeon == 0) continue;
				Block b = Block.blocksList[placeon];
				if(b.blockMaterial.getIsSolid()) {
					mc.thePlayer.inventory.currentItem = slot;
					if(mc.isMultiplayerWorld()) {
						((PlayerControllerMP)mc.playerController).syncCurrentPlayItem();
					}
					
					mc.playerController.sendPlaceBlock(mc.thePlayer, mc.theWorld, item, xp, yp, zp, side);
					mc.thePlayer.inventory.currentItem = saved;
					if(mc.isMultiplayerWorld()) {
						((PlayerControllerMP)mc.playerController).syncCurrentPlayItem();
					}
					ret = PlaceStatus.PLACED;
					break;
				}
			}
			
			return ret;
		}
		//}
		return PlaceStatus.FAILED;
	}
	public PlaceStatus placeBlock(int x, int y, int z, double offX, double offY, double offZ) {
		int px = MathHelper.floor_double(mc.thePlayer.posX);
		int py = MathHelper.floor_double(mc.thePlayer.posY - 2);
		int pz = MathHelper.floor_double(mc.thePlayer.posZ);
		
		if(offX != 0 && offZ != 0) {
			if(this.placeBlock(x, y, pz) != PlaceStatus.FAILED) {
				return this.placeBlock(x, y, z);
			}else if(this.placeBlock(px, y, z) != PlaceStatus.FAILED) {
				return this.placeBlock(x, y, z);
			}
		}else if(offX == 0 || offZ == 0){
			return this.placeBlock(x, y, z);
		}else{
			int id = mc.theWorld.getBlockId(x, y, z);
			if(id == 0 || !Block.blocksList[id].blockMaterial.getIsSolid()) {
				this.placeBlock(x, y, z);
			}
		}
		return PlaceStatus.FAILED;
	}
	
	
	public int delay = 0;
	@Override
	public void handleEvent(Event event) {
		if(event instanceof EventPlayerUpdatePre) {
			if(this.delay > 0 && --this.delay > 0) return; //<3
			
			double strafe = mc.thePlayer.moveStrafing;
			double forward = mc.thePlayer.moveForward;
			boolean jumping = mc.thePlayer.isJumping;
			
			//if(forward == 0 && strafe == 0) forward = 1;
			float rotYaw = mc.thePlayer.rotationYaw;
			
			if(AutoTunnelHack.autoWalking()) {
				rotYaw = AutoTunnelHack.instance.getDirection().yaw;
	    	}
			
			double var5 = MathHelper.sin(rotYaw * 3.1415927F / 180.0F);
			double var6 = MathHelper.cos(rotYaw * 3.1415927F / 180.0F);
			if(Math.abs(var5) < 0.01) var5 = 0;
			if(Math.abs(var6) < 0.01) var6 = 0;
			double xo = strafe * var6 - forward * var5;
			double zo = forward * var6 + strafe * var5;
			double yo = 0; //jumping ? -1 : 0;
			
			if(!mc.thePlayer.onGround) {
				double mx = mc.thePlayer.motionX;
				double my = mc.thePlayer.motionY;
				double mz = mc.thePlayer.motionZ;
				
				if(Math.abs(mx) < 0.1) mx = 0;
				if(Math.abs(my) < 0.1) my = 0;
				if(Math.abs(mz) < 0.1) mz = 0;
				
				if(Math.signum(xo) != Math.signum(mx)) xo = 0;
				if(Math.signum(yo) != Math.signum(my)) yo = 0;
				if(Math.signum(zo) != Math.signum(mz)) zo = 0;
			}
			
			if(xo > 1) xo = 1;
			if(xo < -1) xo = -1;
			
			if(zo > 1) zo = 1;
			if(zo < -1) zo = -1;
			
			int placeX = MathHelper.floor_double(xo + mc.thePlayer.posX);
			int placeY = MathHelper.floor_double(mc.thePlayer.posY - 2 + yo);
			int placeZ = MathHelper.floor_double(zo + mc.thePlayer.posZ);
			
			
			float r = this.radius.getValue()*2-1;
			heightloop:
			for(int yoff = 0; yoff < this.height.getValue(); ++yoff) {
				int xoff = 0, zoff = 0;
				int xx = 0, zz = -1;
				for(int c = 0; c < r*r; ++c) {
					if(-r/2 < xoff && xoff <= r/2 && -r/2 < zoff && zoff <= r/2) {
						PlaceStatus b = this.placeBlock(placeX+xoff, placeY-yoff, placeZ+zoff, xo, yo, zo);
						if(b == PlaceStatus.PLACED) {
							this.delay = this.delayBetweenBlocks.getValue();
							if(this.delay > 0) break heightloop;
						}
					}
					if(xoff == zoff || (xoff < 0 && xoff == -zoff) || (xoff > 0 && xoff == 1-zoff)) {
						int tmp = xx;
						xx = -zz;
						zz = tmp;
					}
					xoff += xx;
					zoff += zz;
				}
			}
			
		}
	}
}
