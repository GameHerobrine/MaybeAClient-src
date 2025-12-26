package net.skidcode.gh.maybeaclient.hacks;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.PlayerController;
import net.minecraft.src.World;
import net.skidcode.gh.maybeaclient.events.Event;
import net.skidcode.gh.maybeaclient.events.EventListener;
import net.skidcode.gh.maybeaclient.events.EventRegistry;
import net.skidcode.gh.maybeaclient.events.impl.EventPlayerUpdatePost;
import net.skidcode.gh.maybeaclient.hacks.category.Category;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingInteger;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingMode;
import net.skidcode.gh.maybeaclient.utils.Direction;
import net.skidcode.gh.maybeaclient.utils.PlayerUtils;
import net.skidcode.gh.maybeaclient.utils.PlayerUtils.LookStatus;

public class SwastikaBuilderHack extends Hack implements EventListener{
	static class BlockInfo{
		public EntityPlayer player;
		public World world; ItemStack itemstack;
		public int tx, ty, tz, face;
		public BlockInfo(EntityPlayer player, World world, ItemStack itemstack, int tx, int ty, int tz, int face) {
			this.player = player;
			this.world = world;
			this.itemstack = itemstack;
			this.tx = tx;
			this.ty = ty;
			this.tz = tz;
			this.face = face;
		}
	}
	public static SwastikaBuilderHack instance;
	public static boolean started = false;
	public SettingMode mode;
	public SettingInteger blocksPerTick = new SettingInteger(this, "Blocks Per Tick", 1, 1, 20);
	
	public SwastikaBuilderHack() {
		super("SwastikaBuilder", "Automatically builds a swastika", Keyboard.KEY_NONE, Category.MISC);
		instance = this;
		this.addSetting(this.mode = new SettingMode(this, "Mode", "Instant", "Slow") {
			@Override
			public void setValue(String value) {
				super.setValue(value);
				blocksPerTick.hidden = !this.currentMode.equalsIgnoreCase("Slow");
			}
		});
		this.addSetting(this.blocksPerTick);
		EventRegistry.registerListener(EventPlayerUpdatePost.class, this);
	}
	
	public ArrayList<BlockInfo> queue = new ArrayList<>();
	
	@Override
	public void handleEvent(Event event) {
		if(event instanceof EventPlayerUpdatePost) {
			int cnt = 0;
			while(this.queue.size() > 0) {
				if(this.mode.currentMode.equalsIgnoreCase("Slow") && cnt >= this.blocksPerTick.value) break;
				BlockInfo bi = this.queue.remove(0);
				SwastikaBuilderHack.started = true;
				mc.playerController.sendPlaceBlock(bi.player, bi.world, bi.itemstack, bi.tx, bi.ty, bi.tz, bi.face);
				SwastikaBuilderHack.started = false;
				++cnt;
			}
		}
	}
	
	public void placeBlock(EntityPlayer var1, World var2, ItemStack var3, int tx, int ty, int tz, int face) {
		queue.add(new BlockInfo(var1, var2, var3, tx, ty, tz, face));
	}
	
	public void onEnable() {
		queue = new ArrayList<>();
	}
	
	public void placeSwastika_(Direction dir, EntityPlayer var1, World var2, ItemStack var3, int tx, int ty, int tz) {
		
		switch(dir) {
			case XNEG:
				placeBlock(var1, var2, var3, tx, ty, tz, 3);
				placeBlock(var1, var2, var3, tx, ty, tz+1, 3);
				placeBlock(var1, var2, var3, tx, ty, tz, 1);
				placeBlock(var1, var2, var3, tx, ty+1, tz, 1);
				placeBlock(var1, var2, var3, tx, ty+2, tz, 1);
				placeBlock(var1, var2, var3, tx, ty+3, tz, 1);
				placeBlock(var1, var2, var3, tx, ty+4, tz, 2);
				placeBlock(var1, var2, var3, tx, ty+4, tz-1, 2);
				placeBlock(var1, var2, var3, tx, ty+2, tz, 2);
				placeBlock(var1, var2, var3, tx, ty+2, tz-1, 2);
				placeBlock(var1, var2, var3, tx, ty+2, tz-2, 0);
				placeBlock(var1, var2, var3, tx, ty+1, tz-2, 0);
				placeBlock(var1, var2, var3, tx, ty+2, tz, 3);
				placeBlock(var1, var2, var3, tx, ty+2, tz+1, 3);
				placeBlock(var1, var2, var3, tx, ty+2, tz+2, 1);
				placeBlock(var1, var2, var3, tx, ty+3, tz+2, 1);
				break;
			case XPOS:
				placeBlock(var1, var2, var3, tx, ty, tz, 2);
				placeBlock(var1, var2, var3, tx, ty, tz-1, 2);
				placeBlock(var1, var2, var3, tx, ty, tz, 1);
				placeBlock(var1, var2, var3, tx, ty+1, tz, 1);
				placeBlock(var1, var2, var3, tx, ty+2, tz, 1);
				placeBlock(var1, var2, var3, tx, ty+3, tz, 1);
				placeBlock(var1, var2, var3, tx, ty+4, tz, 3);
				placeBlock(var1, var2, var3, tx, ty+4, tz+1, 3);
				placeBlock(var1, var2, var3, tx, ty+2, tz, 3);
				placeBlock(var1, var2, var3, tx, ty+2, tz+1, 3);
				placeBlock(var1, var2, var3, tx, ty+2, tz+2, 0);
				placeBlock(var1, var2, var3, tx, ty+1, tz+2, 0);
				placeBlock(var1, var2, var3, tx, ty+2, tz, 2);
				placeBlock(var1, var2, var3, tx, ty+2, tz-1, 2);
				placeBlock(var1, var2, var3, tx, ty+2, tz-2, 1);
				placeBlock(var1, var2, var3, tx, ty+3, tz-2, 1);
				break;
			case ZNEG:
				placeBlock(var1, var2, var3, tx, ty, tz, 5);
				placeBlock(var1, var2, var3, tx+1, ty, tz, 5);
				placeBlock(var1, var2, var3, tx, ty, tz, 1);
				placeBlock(var1, var2, var3, tx, ty+1, tz, 1);
				placeBlock(var1, var2, var3, tx, ty+2, tz, 1);
				placeBlock(var1, var2, var3, tx, ty+3, tz, 1);
				placeBlock(var1, var2, var3, tx, ty+4, tz, 4);
				placeBlock(var1, var2, var3, tx-1, ty+4, tz, 4);
				placeBlock(var1, var2, var3, tx, ty+2, tz, 4);
				placeBlock(var1, var2, var3, tx-1, ty+2, tz, 4);
				placeBlock(var1, var2, var3, tx-2, ty+2, tz, 0);
				placeBlock(var1, var2, var3, tx-2, ty+1, tz, 0);
				placeBlock(var1, var2, var3, tx, ty+2, tz, 5);
				placeBlock(var1, var2, var3, tx+1, ty+2, tz, 5);
				placeBlock(var1, var2, var3, tx+2, ty+2, tz, 1);
				placeBlock(var1, var2, var3, tx+2, ty+3, tz, 1);
				break;
			case ZPOS:
				placeBlock(var1, var2, var3, tx, ty, tz, 4);
				placeBlock(var1, var2, var3, tx-1, ty, tz, 4);
				placeBlock(var1, var2, var3, tx, ty, tz, 1);
				placeBlock(var1, var2, var3, tx, ty+1, tz, 1);
				placeBlock(var1, var2, var3, tx, ty+2, tz, 1);
				placeBlock(var1, var2, var3, tx, ty+3, tz, 1);
				placeBlock(var1, var2, var3, tx, ty+4, tz, 5);
				placeBlock(var1, var2, var3, tx+1, ty+4, tz, 5);
				placeBlock(var1, var2, var3, tx, ty+2, tz, 5);
				placeBlock(var1, var2, var3, tx+1, ty+2, tz, 5);
				placeBlock(var1, var2, var3, tx+2, ty+2, tz, 0);
				placeBlock(var1, var2, var3, tx+2, ty+1, tz, 0);
				placeBlock(var1, var2, var3, tx, ty+2, tz, 4);
				placeBlock(var1, var2, var3, tx-1, ty+2, tz, 4);
				placeBlock(var1, var2, var3, tx-2, ty+2, tz, 1);
				placeBlock(var1, var2, var3, tx-2, ty+3, tz, 1);
				break;
			default:
		}
	}
	
	
	public void placeSwastika(EntityPlayer var1, World var2, ItemStack var3, int tx, int ty, int tz) {
		Direction dir = PlayerUtils.getDirection();
		placeSwastika_(dir, var1, var2, var3, tx, ty, tz);
	}
}
