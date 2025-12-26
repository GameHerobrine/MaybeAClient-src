package net.skidcode.gh.maybeaclient.hacks;

import org.lwjgl.input.Keyboard;

import net.skidcode.gh.maybeaclient.hacks.category.Category;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingFloat;

public class CustomHandPositionHack extends Hack{
	public static CustomHandPositionHack instance;
	public SettingFloat xOff = new SettingFloat(this, "X offset", 0, -2, 2, 0.05f);
	public SettingFloat yOff = new SettingFloat(this, "Y offset", -0.1f, -0.5f, 1.5f, 0.05f);
	public SettingFloat zOff = new SettingFloat(this, "Z offset", 0, -1.5f, 0.5f, 0.05f);
	public SettingFloat rotX = new SettingFloat(this, "X Rotation", 0, -180f, 180f, 3);
	public SettingFloat rotY = new SettingFloat(this, "Y Rotation", 0, -180f, 180f, 3);
	public SettingFloat rotZ = new SettingFloat(this, "Z Rotation", 0, -180f, 180f, 3);
	//public SettingFloat xOff = new SettingFloat(this, "X offset");
	
	
	public CustomHandPositionHack() {
		super("CustomHandPosition", "Custom position for hand", Keyboard.KEY_NONE, Category.RENDER);
		this.addSetting(this.xOff);
		this.addSetting(this.yOff);
		this.addSetting(this.zOff);
		this.addSetting(this.rotX);
		this.addSetting(this.rotY);
		this.addSetting(this.rotZ);
		instance = this;
	}

}
