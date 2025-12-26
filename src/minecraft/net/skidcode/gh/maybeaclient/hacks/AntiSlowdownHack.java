package net.skidcode.gh.maybeaclient.hacks;

import org.lwjgl.input.Keyboard;

import net.skidcode.gh.maybeaclient.hacks.category.Category;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingMode;

public class AntiSlowdownHack extends Hack{
	public static AntiSlowdownHack instance;
	
	public AntiSlowdownHack() {
		super("AntiSlowdown", "Removes slowdown", Keyboard.KEY_NONE, Category.MOVEMENT);
		instance = this;
		
		
	}

}
