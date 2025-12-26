package net.skidcode.gh.maybeaclient.hacks;

import java.util.List;

import org.lwjgl.input.Keyboard;

import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityArrow;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.MathHelper;
import net.skidcode.gh.maybeaclient.events.Event;
import net.skidcode.gh.maybeaclient.events.EventListener;
import net.skidcode.gh.maybeaclient.events.EventRegistry;
import net.skidcode.gh.maybeaclient.events.impl.EventPlayerUpdatePost;
import net.skidcode.gh.maybeaclient.hacks.ForceFieldHack.DistEntitySorter;
import net.skidcode.gh.maybeaclient.hacks.category.Category;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingDouble;
import net.skidcode.gh.maybeaclient.utils.PlayerUtils;

public class AimBotHack extends Hack implements EventListener{

	public static AimBotHack instance;
	public SettingDouble maxTargetingDistance = new SettingDouble(this, "MaxTargetingDistance", 30, 10, 50);
	public AimBotHack() {
		super("AimBot", "Aims at other player", Keyboard.KEY_NONE, Category.COMBAT);
		instance = this;
		EventRegistry.registerListener(EventPlayerUpdatePost.class, this);
		
		this.addSetting(this.maxTargetingDistance);
	}

	public Entity target;
	
	@Override
	public void handleEvent(Event event) {
		if(event instanceof EventPlayerUpdatePost) {
			target = null;
			double dist = this.maxTargetingDistance.value*this.maxTargetingDistance.value;
			Entity at = null;
			for(Object o : mc.theWorld.playerEntities) {
				Entity e = (Entity)o;
				if(!e.isDead && e != mc.thePlayer) {
					double d = e.getDistanceSqToEntity(mc.thePlayer);
					if(d <= dist) {
						at = e;
						dist = d;
					}
				}
			}
			target = at;
			if(at == null) return;
			double xDiff = at.posX - mc.thePlayer.posX;
			double zDiff = at.posZ - mc.thePlayer.posZ;
			
			double x = xDiff;
			double z = zDiff;
			double d = Math.sqrt(x * x + z * z);
			double mp = 0.2;
			double cns = 100;
			if(d > 40) cns = 90;
			if(d > 44.6) cns = 80;
			
			double y = (at.posY - mc.thePlayer.posY) + d * (d/cns);
			
			float var9 = MathHelper.sqrt_double(x * x + y * y + z * z);
			x /= (double)var9;
			y /= (double)var9;
			z /= (double)var9;
	        y *= 1.5;
			y *= (double)0.6;
			
			double dist2 = MathHelper.sqrt_double(x * x + z * z);
			mc.thePlayer.rotationPitch = -(float)(Math.atan2(y, dist2) * 180.0D / Math.PI);
			mc.thePlayer.rotationYaw = (float)(Math.atan2(zDiff, xDiff) * 180.0D / 3.1415927410125732D) - 90.0F;
		}
	}

}
