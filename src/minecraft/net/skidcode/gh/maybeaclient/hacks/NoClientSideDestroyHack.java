package net.skidcode.gh.maybeaclient.hacks;

import org.lwjgl.input.Keyboard;

import net.skidcode.gh.maybeaclient.hacks.category.Category;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingBoolean;

public class NoClientSideDestroyHack extends Hack{
	
	public static NoClientSideDestroyHack instance;
	public SettingBoolean noDestroy = new SettingBoolean(this, "No Destroy", true);
	public SettingBoolean noTNT = new SettingBoolean(this, "No TNT", false);
	public SettingBoolean noArrowRotation = new SettingBoolean(this, "No Arrow Rotation", false);
	
	public NoClientSideDestroyHack() {
		super("NoClientSideActions", "Disables cerain actions on client side", Keyboard.KEY_NONE, Category.MISC);
		instance = this;
		this.addSetting(this.noDestroy);
		this.addSetting(this.noTNT);
		//this.addSetting(this.noArrowRotation);
	}

}
