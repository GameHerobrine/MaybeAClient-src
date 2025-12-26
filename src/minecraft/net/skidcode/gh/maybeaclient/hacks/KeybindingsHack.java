package net.skidcode.gh.maybeaclient.hacks;

import org.lwjgl.input.Keyboard;

import net.skidcode.gh.maybeaclient.Client;
import net.skidcode.gh.maybeaclient.events.Event;
import net.skidcode.gh.maybeaclient.events.EventListener;
import net.skidcode.gh.maybeaclient.events.EventRegistry;
import net.skidcode.gh.maybeaclient.events.impl.EventRenderIngameNoDebug;
import net.skidcode.gh.maybeaclient.gui.click.ClickGUI;
import net.skidcode.gh.maybeaclient.gui.click.KeybindingsTab;
import net.skidcode.gh.maybeaclient.hacks.category.Category;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingEnum;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingMode;
import net.skidcode.gh.maybeaclient.hacks.settings.enums.EnumAlign;
import net.skidcode.gh.maybeaclient.hacks.settings.enums.EnumExpand;
import net.skidcode.gh.maybeaclient.hacks.settings.enums.EnumStaticPos;
import net.skidcode.gh.maybeaclient.utils.ChatColor;

public class KeybindingsHack extends Hack{
	public SettingEnum<EnumAlign> alignment = new SettingEnum<>(this, "Alignment", EnumAlign.LEFT);
	public SettingEnum<EnumExpand> expand = new SettingEnum<>(this, "Expand", EnumExpand.BOTTOM);
	public static KeybindingsHack instance;
	public KeybindingsHack() {
		super("Keybindings", "Show binds in hud", Keyboard.KEY_NONE, Category.UI);
		
		this.addSetting(this.alignment);
		this.addSetting(this.expand);
		instance = this;
	}
}
