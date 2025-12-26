package net.skidcode.gh.maybeaclient.hacks;

import org.lwjgl.input.Keyboard;

import net.skidcode.gh.maybeaclient.hacks.category.Category;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingMode;
import net.skidcode.gh.maybeaclient.utils.ChatColor;

public class JesusHack extends Hack{
	
	public SettingMode mode;
	public static JesusHack INSTANCE;
	public JesusHack() {
		super("Jesus", "Allows to walk on water", Keyboard.KEY_J, Category.MOVEMENT);
		INSTANCE = this;
		this.mode = new SettingMode(this, "Mode", "Normal+", "Normal", "Jump") {
			public void setValue(String value) {
				super.setValue(value);
			}
		};
		this.addSetting(this.mode);
	}
	@Override
	public String getPrefix() {
		return this.mode.currentMode;
	}
}
