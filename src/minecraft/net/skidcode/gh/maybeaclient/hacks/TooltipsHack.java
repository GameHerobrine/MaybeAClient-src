package net.skidcode.gh.maybeaclient.hacks;

import org.lwjgl.input.Keyboard;

import net.skidcode.gh.maybeaclient.hacks.category.Category;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingBoolean;

public class TooltipsHack extends Hack{
	public static TooltipsHack instance;
	
	public SettingBoolean showIdMeta = new SettingBoolean(this, "Show item id and meta", true);
	
	public TooltipsHack() {
		super("Tooltips", "Shows tooltips for some items", Keyboard.KEY_NONE, Category.MISC);
		instance = this;
		
		this.addSetting(this.showIdMeta);
	}

}
