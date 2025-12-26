package net.skidcode.gh.maybeaclient.hacks;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.lwjgl.input.Keyboard;

import net.skidcode.gh.maybeaclient.events.Event;
import net.skidcode.gh.maybeaclient.events.EventListener;
import net.skidcode.gh.maybeaclient.events.EventRegistry;
import net.skidcode.gh.maybeaclient.events.impl.EventPlayerUpdatePre;
import net.skidcode.gh.maybeaclient.hacks.category.Category;

public class LowHopSpeedHack extends Hack implements EventListener{
	public static LowHopSpeedHack instance;
	public LowHopSpeedHack() {
		super("LowHopSpeed", "Speedhack?", Keyboard.KEY_NONE, Category.MOVEMENT);
		EventRegistry.registerListener(EventPlayerUpdatePre.class, this);
		instance = this;
	}
	
	public static double round(double value, int places) {
        return places < 0 ? value : new BigDecimal(value).setScale(places, RoundingMode.HALF_UP).doubleValue();
    }
	@Override
	public void onDisable() {
		mc.timer.timerSpeed = 0;
	}
	@Override
	public void handleEvent(Event event) {
		if(event instanceof EventPlayerUpdatePre) {
			mc.timer.timerSpeed = 1.0888f;
			if (!mc.thePlayer.isCollidedHorizontally) {
				if (round(mc.thePlayer.posY - ((int) mc.thePlayer.posY), 3) == round(0.4d, 3)) {
					mc.thePlayer.motionY = 0.31d;
				} else if (round(mc.thePlayer.posY - ((int) mc.thePlayer.posY), 3) == round(0.71d, 3)) {
					mc.thePlayer.motionY = 0.04d;
				} else if (round(mc.thePlayer.posY - ((int) mc.thePlayer.posY), 3) == round(0.75d, 3)) {
					mc.thePlayer.motionY = -0.2d;
				} else if (round(mc.thePlayer.posY - ((int) mc.thePlayer.posY), 3) == round(0.55d, 3)) {
					mc.thePlayer.motionY = -0.14d;
				} else if (round(mc.thePlayer.posY - ((int) mc.thePlayer.posY), 3) == round(0.41d, 3)) {
					mc.thePlayer.motionY = -0.2d;
				}
			}
		}
	}

}
