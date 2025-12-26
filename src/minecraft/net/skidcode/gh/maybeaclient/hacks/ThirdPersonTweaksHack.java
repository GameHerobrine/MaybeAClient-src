package net.skidcode.gh.maybeaclient.hacks;

import org.lwjgl.input.Keyboard;

import net.skidcode.gh.maybeaclient.hacks.category.Category;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingBoolean;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingDouble;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingMode;

public class ThirdPersonTweaksHack extends Hack{

	public static ThirdPersonTweaksHack instance;
	public SettingBoolean noclip = new SettingBoolean(this, "Noclip", false);
	public SettingMode frontView = new SettingMode(this, "Front View", "Off", "ThirdF5Mode");
	public SettingDouble distance = new SettingDouble(this, "Distance", 4, 4, 12, 0.1);
	
	public ThirdPersonTweaksHack() {
		super("ThirdPersonTweaks", "Allows to change how third person view behaves", Keyboard.KEY_NONE, Category.RENDER);
		instance = this;
		
		this.addSetting(this.noclip);
		this.addSetting(this.frontView);
		this.addSetting(this.distance);
	}
	
	public boolean thirdPersonEnabled = false;
	
	@Override
	public void onEnable() {
		this.thirdPersonEnabled = false;
	}
	
	public boolean frontViewEnabled() {
		if(this.frontView.currentMode.equalsIgnoreCase("ThirdF5Mode")) {
			return this.status && this.thirdPersonEnabled;
		}
		return false;
		//return this.status && this.mode.currentMode.equalsIgnoreCase("AlwaysOn");
	}

	public boolean noclipEnabled() {
		return this.status && this.noclip.getValue();
	}

	public double getDistance() {
		return this.distance.getValue();
	}

}
