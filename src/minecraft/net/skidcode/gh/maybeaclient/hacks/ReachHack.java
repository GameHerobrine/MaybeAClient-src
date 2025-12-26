package net.skidcode.gh.maybeaclient.hacks;

import org.lwjgl.input.Keyboard;

import net.skidcode.gh.maybeaclient.hacks.category.Category;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingFloat;
import net.skidcode.gh.maybeaclient.utils.ChatColor;

public class ReachHack extends Hack{
	
	public SettingFloat radius = new SettingFloat(this, "Radius", 6.0f, 4.0f, 10.0f, 0.5f);
	public static ReachHack instance;
	
	public ReachHack() {
		super("Reach", "Increases allowed radius to interact with blocks", Keyboard.KEY_NONE, Category.MISC);
		this.addSetting(this.radius);
		instance = this;
	}
	
	@Override
	public String getPrefix() {
		return ""+this.radius.value;
	}
}
