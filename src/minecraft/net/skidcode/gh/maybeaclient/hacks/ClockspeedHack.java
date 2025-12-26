package net.skidcode.gh.maybeaclient.hacks;

import org.lwjgl.input.Keyboard;

import net.skidcode.gh.maybeaclient.hacks.category.Category;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingFloat;
import net.skidcode.gh.maybeaclient.utils.ChatColor;

public class ClockspeedHack extends Hack{
	
	public SettingFloat speed = new SettingFloat(this, "Speed", 1.5f, 0.1f, 5f, 0.05f);
	
	public static ClockspeedHack instance;
	
	public ClockspeedHack() {
		super("ClockSpeed", "Increase tick speed", Keyboard.KEY_F6, Category.MISC);
		instance = this;
		
		this.addSetting(speed);
	}
	
	@Override
	public String getPrefix() {
		return ""+this.speed.value;
	}
	
}
