package net.skidcode.gh.maybeaclient.hacks;

import org.lwjgl.input.Keyboard;

import net.skidcode.gh.maybeaclient.hacks.category.Category;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingFloat;
import net.skidcode.gh.maybeaclient.utils.ChatColor;

public class FOVHack extends Hack{
	
	public SettingFloat fov = new SettingFloat(this, "FOV", 70, 30, 110, 1);
	public static FOVHack instance;
	public FOVHack() {
		super("Fov", "Change field of view", Keyboard.KEY_NONE, Category.RENDER);
		this.addSetting(this.fov);
		instance = this;
	}
	@Override
	public String getPrefix() {
		return ""+this.fov.value;
	}
}
