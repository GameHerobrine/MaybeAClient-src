package net.skidcode.gh.maybeaclient.hacks;

import org.lwjgl.input.Keyboard;

import net.skidcode.gh.maybeaclient.hacks.category.Category;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingBoolean;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingFloat;

public class NameTagsHack extends Hack{
	
	public SettingFloat scale = new SettingFloat(this, "Scale", 1, 1, 5f, 0.1f);
	public SettingBoolean showHeldItem = new SettingBoolean(this, "Show Held Item", false);
	public SettingBoolean showArmor = new SettingBoolean(this, "Show Armor", false);
	
	public static NameTagsHack instance;
	
	
	public NameTagsHack() {
		super("NameTags", "Always show nametags", Keyboard.KEY_NONE, Category.RENDER);
		instance = this;
		this.addSetting(this.scale);
		this.addSetting(this.showHeldItem);
		this.addSetting(this.showArmor);
	}

}
