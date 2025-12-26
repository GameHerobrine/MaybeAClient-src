package net.skidcode.gh.maybeaclient.hacks;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import net.minecraft.src.Block;
import net.minecraft.src.MathHelper;
import net.skidcode.gh.maybeaclient.events.Event;
import net.skidcode.gh.maybeaclient.events.EventListener;
import net.skidcode.gh.maybeaclient.events.EventRegistry;
import net.skidcode.gh.maybeaclient.events.impl.EventPlayerUpdatePost;
import net.skidcode.gh.maybeaclient.hacks.category.Category;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingInteger;
import net.skidcode.gh.maybeaclient.utils.BlockPos;
import net.skidcode.gh.maybeaclient.utils.PlayerUtils;
import net.skidcode.gh.maybeaclient.utils.PlayerUtils.LookStatus;

public class FireExtinguisherHack extends Hack implements EventListener{

	public SettingInteger radius = new SettingInteger(this, "Radius", 3, 1, 4);
	
	public FireExtinguisherHack() {
		super("FireExtinguisher", "Removes fire around the player", Keyboard.KEY_NONE, Category.MISC);
		this.addSetting(this.radius);
		EventRegistry.registerListener(EventPlayerUpdatePost.class, this);
	}

	public static ArrayList<BlockPos> blocksArr = new ArrayList<BlockPos>();
	@Override
	public void handleEvent(Event event) {
		if(event instanceof EventPlayerUpdatePost) {
			blocksArr.clear();
			int xc = MathHelper.floor_double(mc.thePlayer.posX);
			int yc = MathHelper.floor_double(mc.thePlayer.posY);
			int zc = MathHelper.floor_double(mc.thePlayer.posZ);
			int d = this.radius.getValue();
			
			for(int x = xc-d; x <= xc+d; ++x) {
				for(int z = zc-d; z <= zc+d; ++z) {
					for(int y = yc-d; y <= yc+d; ++y) {
						if(mc.theWorld.getBlockId(x, y, z) == Block.fire.blockID) {
							double dd = MathHelper.sqrt_double((xc - x)*(xc - x) + (zc - z)*(zc - z) + (yc - y)*(yc - y));
							if(dd <= (radius.getValue())) {
								//blocksArr.add(new BlockPos(x, y, z));
								mc.playerController.clickBlock(x, y-1, z, 1);
							}
							
						}
					}
				}
			}
			
		}
	}

}
