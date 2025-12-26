package net.skidcode.gh.maybeaclient.hacks;

import org.lwjgl.input.Keyboard;

import net.minecraft.src.GuiContainer;
import net.minecraft.src.MathHelper;
import net.skidcode.gh.maybeaclient.events.Event;
import net.skidcode.gh.maybeaclient.events.EventListener;
import net.skidcode.gh.maybeaclient.events.EventRegistry;
import net.skidcode.gh.maybeaclient.events.impl.EventPlayerUpdatePost;
import net.skidcode.gh.maybeaclient.hacks.category.Category;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingBoolean;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingDouble;

public class StrafeHack extends Hack implements EventListener{

	public static StrafeHack instance;
	public SettingDouble speedMultiplier = new SettingDouble(this, "Speed Multiplier", 0.219, 0.2, 0.3, 0.001);
	public SettingBoolean disableWhenSneaking = new SettingBoolean(this, "DisableWhenSneaking", false);
	public SettingBoolean disableInLiquids = new SettingBoolean(this, "DisableInLiquids", false);
	
	public StrafeHack() {
		super("Strafe", "Allows to instantly switch directions", Keyboard.KEY_NONE, Category.MOVEMENT);
		instance = this;
		EventRegistry.registerListener(EventPlayerUpdatePost.class, this);
		
		this.addSetting(this.speedMultiplier);
		this.addSetting(this.disableInLiquids);
		this.addSetting(this.disableWhenSneaking);
	}
	
	public boolean enabled() {
		if(!this.status) return false;
		if(this.disableWhenSneaking.getValue() && mc.thePlayer.isSneaking()) return false;
		if(this.disableInLiquids.getValue() && inLiquid) return false;
		
		return true;
	}
	
	public static boolean inLiquid = false;
	@Override
	public void handleEvent(Event event) {
		if(event instanceof EventPlayerUpdatePost && !FlyHack.instance.status && !FreecamHack.instance.status) {
			if(!this.enabled()) return;
			float yaw = mc.thePlayer.rotationYaw;
			mc.thePlayer.motionX = mc.thePlayer.motionZ = 0;
			boolean fw = false, bw = false, lw = false, rw = false;
			
			
			checkinputs: {
				if(!mc.inGameHasFocus) {
					if(!InventoryWalkHack.instance.status || !(mc.currentScreen instanceof GuiContainer)) break checkinputs;
				}
				
				fw = Keyboard.isKeyDown(mc.gameSettings.keyBindForward.keyCode);
				bw = Keyboard.isKeyDown(mc.gameSettings.keyBindBack.keyCode);
				lw = Keyboard.isKeyDown(mc.gameSettings.keyBindLeft.keyCode);
				rw = Keyboard.isKeyDown(mc.gameSettings.keyBindRight.keyCode);
			}
				
			if(AutoTunnelHack.autoWalking()) {
				fw = true;
				yaw = AutoTunnelHack.instance.getDirection().yaw;
			}
				
			float d1 = yaw + 90;
				
			if(AutoWalkHack.instance.status) fw = true;
			if((fw || bw) && !(fw && bw)) {
				if(bw) d1 += 180;
				if(lw) d1 += fw ? -45 : 45;
				if(rw) d1 += fw ? 45 : -45;
			} else {
				if(lw) d1 -= 90D;
				if(rw) d1 += 90D;
			}

			if(((fw || bw) && !(fw && bw)) || ((lw || rw) && !(lw && rw))){
				mc.thePlayer.motionX = Math.cos(Math.toRadians(d1))*this.speedMultiplier.getValue(); //0.22;
				mc.thePlayer.motionZ = Math.sin(Math.toRadians(d1))*this.speedMultiplier.getValue();
			}
		}
	}
	
	

}
