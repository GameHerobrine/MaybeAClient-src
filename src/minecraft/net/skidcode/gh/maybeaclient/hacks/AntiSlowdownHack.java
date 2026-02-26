package net.skidcode.gh.maybeaclient.hacks;

import org.lwjgl.input.Keyboard;

import net.skidcode.gh.maybeaclient.hacks.category.Category;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingBoolean;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingMode;

public class AntiSlowdownHack extends Hack{
	public static AntiSlowdownHack instance;
	public SettingBoolean noWater = new SettingBoolean(this, "NoWater", false);
	public AntiSlowdownHack() {
		super("AntiSlowdown", "Removes slowdown", Keyboard.KEY_NONE, Category.MOVEMENT);
		instance = this;
		this.addSetting(this.noWater);
	}

}
