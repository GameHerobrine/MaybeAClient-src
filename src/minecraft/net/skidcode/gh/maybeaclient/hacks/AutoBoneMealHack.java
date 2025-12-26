package net.skidcode.gh.maybeaclient.hacks;

import java.util.ArrayList;
import java.util.HashSet;

import org.lwjgl.input.Keyboard;

import net.minecraft.src.Block;
import net.minecraft.src.ItemBlock;
import net.minecraft.src.ItemDye;
import net.minecraft.src.ItemStack;
import net.minecraft.src.MathHelper;
import net.skidcode.gh.maybeaclient.events.Event;
import net.skidcode.gh.maybeaclient.events.EventListener;
import net.skidcode.gh.maybeaclient.events.EventRegistry;
import net.skidcode.gh.maybeaclient.events.impl.EventPlayerUpdatePost;
import net.skidcode.gh.maybeaclient.hacks.category.Category;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingInteger;
import net.skidcode.gh.maybeaclient.utils.BlockPos;
import net.skidcode.gh.maybeaclient.utils.PlayerUtils;

public class AutoBoneMealHack extends Hack implements EventListener{
	static class TimedBlockPos extends BlockPos{
		public long timens;
		public TimedBlockPos(int x, int y, int z) {
			super(x, y, z);
			this.timens = System.nanoTime();
		}
	}
	
	public static AutoBoneMealHack instance;
	public SettingInteger radius = new SettingInteger(this, "Radius", 3, 1, 4);
	
	public AutoBoneMealHack() {
		super("AutoBoneMeal", "Automatically uses bonemeal on saplings/crops", Keyboard.KEY_NONE, Category.MISC);
		instance = this;
		EventRegistry.registerListener(EventPlayerUpdatePost.class, this);
		this.addSetting(this.radius);
	}

	public HashSet<TimedBlockPos> usedOn = new HashSet<>();
	
	@Override
	public void onEnable() {
		usedOn = new HashSet<>();
	}

	//TODO: move it into PlayerUtils
	public int findItemToPlace() {
		for(int i = 0; i < 9; ++i) {
			ItemStack stack = mc.thePlayer.inventory.mainInventory[i];
			
			if(stack != null && stack.getItem() instanceof ItemDye && stack.getItemDamage() == 15) {
				if(stack.stackSize == 0) {
					mc.thePlayer.inventory.mainInventory[i] = null;
					continue;
				}
				return i;
			}
		}
		return -1;
	}
	
	public void checkYUse(int x, int y, int z) {
		TimedBlockPos bp = new TimedBlockPos(x, y, z);
		if(this.usedOn.contains(bp)) return;
		
		int id = mc.theWorld.getBlockId(x, y, z);

		if(id == Block.crops.blockID || id == Block.sapling.blockID) {
			int item = findItemToPlace();
			if(item == -1) return;
			
			int prev = mc.thePlayer.inventory.currentItem;
			mc.thePlayer.inventory.currentItem = item;
			PlayerUtils.placeBlockUnsafe(x, y, z, 0);
			this.usedOn.add(bp);
			mc.thePlayer.inventory.currentItem = prev;
		}
	}
	
	@Override
	public void handleEvent(Event event) {
		if(event instanceof EventPlayerUpdatePost) {
			long current = System.nanoTime();
			ArrayList<TimedBlockPos> toRemove = new ArrayList<>();
			for(TimedBlockPos bp : this.usedOn) {
				if((current  - bp.timens) > 1000000000) {
					toRemove.add(bp);
				}
			}
			while(toRemove.size() > 0) this.usedOn.remove(toRemove.remove(toRemove.size()-1));
			
			int xc = MathHelper.floor_double(mc.thePlayer.posX);
			int yc = MathHelper.floor_double(mc.thePlayer.posY);
			int zc = MathHelper.floor_double(mc.thePlayer.posZ);
			int d = this.radius.getValue();
			
			for(int x = xc-d; x <= xc+d; ++x) {
				for(int z = zc-d; z <= zc+d; ++z) {
					this.checkYUse(x, yc-2, z);
					for(int y = yc-d; y < yc-2; ++y) this.checkYUse(x, y, z);
					int o = 1;
					if(x == xc && zc == z) o = 0; //skip xc y-1 zc
					for(int y = yc-o; y <= yc+d; ++y) this.checkYUse(x, y, z);
				}
			}
		}
	}
}
