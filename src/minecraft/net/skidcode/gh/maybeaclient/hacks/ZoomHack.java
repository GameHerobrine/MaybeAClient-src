package net.skidcode.gh.maybeaclient.hacks;

import org.lwjgl.input.Keyboard;

import net.skidcode.gh.maybeaclient.hacks.category.Category;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingBoolean;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingInteger;

public class ZoomHack extends Hack{
	public static ZoomHack instance;
	
	public SettingBoolean mustHoldKeybind = new SettingBoolean(this, "Hold to zoom", false);
	public SettingInteger targetFOV = new SettingInteger(this, "Target FOV", 30, 1, 120);
	public SettingBoolean smoothCameraWhenZoomed = new SettingBoolean(this, "Smooth camera when zoomed", true);
	public ZoomHack() {
		super("Zoom", "Make some distant object bigger", Keyboard.KEY_NONE, Category.MISC);
		
		this.addSetting(this.mustHoldKeybind);
		this.addSetting(this.targetFOV);
		this.addSetting(this.smoothCameraWhenZoomed);
		
		instance = this;
	}
	
	@Override
	public String getPrefix() {
		return ""+this.targetFOV.value;
	}

	public static boolean holdingStarted = false;
	@Override
	public void toggleByKeybind() {
		if(this.status && this.mustHoldKeybind.getValue()) {
			holdingStarted = true;
		}else {
			super.toggle();
			if(this.status && this.mustHoldKeybind.getValue()) {
				holdingStarted = true;
			}
		}
	}
	public static void onStopHolding() {
		if(holdingStarted) {
			holdingStarted = false;
		}
	}
	public static boolean isZoomed() {
		return instance.status && ((instance.mustHoldKeybind.value && holdingStarted) || (!instance.mustHoldKeybind.value));
	}
}
