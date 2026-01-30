package net.skidcode.gh.maybeaclient.hacks;

import net.skidcode.gh.maybeaclient.hacks.settings.SettingBoolean;
import org.lwjgl.input.Keyboard;

import net.minecraft.src.GuiContainer;
import net.skidcode.gh.maybeaclient.events.Event;
import net.skidcode.gh.maybeaclient.events.EventListener;
import net.skidcode.gh.maybeaclient.events.EventRegistry;
import net.skidcode.gh.maybeaclient.events.impl.EventPlayerUpdatePost;
import net.skidcode.gh.maybeaclient.hacks.category.Category;
import net.skidcode.gh.maybeaclient.hacks.settings.Setting;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingDouble;
import net.skidcode.gh.maybeaclient.utils.ChatColor;

public class FlyHack extends Hack implements EventListener{
	
	public SettingDouble speedMultiplier = new SettingDouble(this, "Speed Multiplier", 0.2, 0.1, 2.0, 0.05);
	public SettingBoolean onGround = new SettingBoolean(this, "OnGround", false);
	
	public static FlyHack instance;
	public FlyHack() {
		super("Fly", "FlyHack ^-^", Keyboard.KEY_R, Category.MOVEMENT);
		instance = this;
		this.addSetting(speedMultiplier);
		this.addSetting(onGround);
		EventRegistry.registerListener(EventPlayerUpdatePost.class, this);
	}
	
	public void onEnable() {
		
	}
	
	public void onDisable() {
		
	}
	@Override
	public String getPrefix() {
		return ""+this.speedMultiplier.value;
	}
	
	public static void handleFly(double speedMultiplier, boolean onGround) {
		mc.thePlayer.movementInput.sneak = false;
		mc.thePlayer.movementInput.jump = false;
		mc.thePlayer.onGround = onGround;
		mc.thePlayer.motionX = 0.0D;
		mc.thePlayer.motionY = 0.0D;
		mc.thePlayer.motionZ = 0.0D;
		double d1 = mc.thePlayer.rotationYaw + 90F;
		boolean fw = false, bw = false, lw = false, rw = false;
		boolean up = false, down = false;
		checkinputs: {
			if(!mc.inGameHasFocus) {
				if(!InventoryWalkHack.instance.status || !(mc.currentScreen instanceof GuiContainer)) break checkinputs;
			}
			fw = Keyboard.isKeyDown(mc.gameSettings.keyBindForward.keyCode);
			bw = Keyboard.isKeyDown(mc.gameSettings.keyBindBack.keyCode);
			lw = Keyboard.isKeyDown(mc.gameSettings.keyBindLeft.keyCode);
			rw = Keyboard.isKeyDown(mc.gameSettings.keyBindRight.keyCode);
			
			up = Keyboard.isKeyDown(mc.gameSettings.keyBindJump.keyCode);
			down = Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.keyCode);
		}
		

		
		if(AutoTunnelHack.autoWalking()) {
			fw = true;
			d1 = AutoTunnelHack.instance.getDirection().yaw + 90;
		}
		
		if(AutoWalkHack.instance.status) fw = true;
		if((fw || bw) && !(fw && bw)) {
			if(bw) d1 += 180;
			if(lw) d1 += fw ? -45 : 45;
			if(rw) d1 += fw ? 45 : -45;
		}else {
			if(lw) d1 -= 90D;
			if(rw) d1 += 90D;
		}

		if(((fw || bw) && !(fw && bw)) || ((lw || rw) && !(lw && rw))){
			mc.thePlayer.motionX = Math.cos(Math.toRadians(d1));
			mc.thePlayer.motionZ = Math.sin(Math.toRadians(d1));
		}
		if(up) mc.thePlayer.motionY++;
		if(down) mc.thePlayer.motionY--;
		
		mc.thePlayer.motionX *= speedMultiplier;
		mc.thePlayer.motionY *= speedMultiplier;
		mc.thePlayer.motionZ *= speedMultiplier;
	}
	
	@Override
	public void handleEvent(Event event) {
		if(event instanceof EventPlayerUpdatePost) {
			handleFly(this.speedMultiplier.value, this.onGround.value);
		}
	}
}
