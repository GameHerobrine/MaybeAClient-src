package net.skidcode.gh.maybeaclient.hacks;

import org.lwjgl.input.Keyboard;

import net.skidcode.gh.maybeaclient.events.Event;
import net.skidcode.gh.maybeaclient.events.EventListener;
import net.skidcode.gh.maybeaclient.events.EventRegistry;
import net.skidcode.gh.maybeaclient.events.impl.EventRenderIngameNoDebug;
import net.skidcode.gh.maybeaclient.gui.click.ClickGUI;
import net.skidcode.gh.maybeaclient.gui.click.ClientInfoTab;
import net.skidcode.gh.maybeaclient.hacks.category.Category;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingBoolean;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingEnum;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingMode;
import net.skidcode.gh.maybeaclient.hacks.settings.enums.EnumAlign;
import net.skidcode.gh.maybeaclient.hacks.settings.enums.EnumStaticPos;
import net.skidcode.gh.maybeaclient.utils.ChatColor;

public class ClientInfoHack extends Hack{
	
	public SettingBoolean coords;
	public SettingBoolean facing = new SettingBoolean(this, "Show facing", true);
	public SettingBoolean fps = new SettingBoolean(this, "Show fps", false);
	public SettingBoolean username = new SettingBoolean(this, "Show username", false);
	public SettingBoolean showNetherCoords;
	public SettingMode isInNether = new SettingMode(this, "IsInNether", "Detect", "Nether", "Overworld");
	public SettingBoolean walkingSpeed;
	public SettingBoolean useHorizontal = new SettingBoolean(this, "Use horizontal speed", true);
	
	public SettingEnum<EnumAlign> alignment = new SettingEnum<>(this, "Alignment", EnumAlign.LEFT);
	public SettingEnum<EnumStaticPos> staticPositon;
	
	public static ClientInfoHack instance;
	
	public ClientInfoHack() {
		super("PlayerInfo", "Displays player info on screen", Keyboard.KEY_RBRACKET, Category.UI);
		instance = this;
		this.showNetherCoords = new SettingBoolean(this, "Show nether coords", false){
			@Override
			public void setValue(boolean value) {
				super.setValue(value);
				ClientInfoHack.instance.isInNether.hidden = !this.value;
			}
		};
		
		this.coords = new SettingBoolean(this, "Show coords", true) {
			@Override
			public void setValue(boolean value) {
				super.setValue(value);
				ClientInfoHack.instance.showNetherCoords.hidden = !this.value;
			}
		};
		
		this.walkingSpeed = new SettingBoolean(this, "Show walking speed", false) {
			@Override
			public void setValue(boolean value) {
				super.setValue(value);
				ClientInfoHack.instance.useHorizontal.hidden = !this.value;
			}
		};
		
		this.addSetting(coords);
		this.addSetting(this.showNetherCoords);
		this.addSetting(this.isInNether);
		this.addSetting(facing);
		this.addSetting(fps);
		this.addSetting(username);
		this.addSetting(this.walkingSpeed);
		this.addSetting(this.useHorizontal);
		this.addSetting(this.staticPositon = new SettingEnum<EnumStaticPos>(this, "Static Position", EnumStaticPos.DISABLED) {
			public void setValue(String value) {
				super.setValue(value);
				EnumStaticPos val = this.getValue();
				if(val == EnumStaticPos.DISABLED) ClientInfoHack.instance.alignment.show();
				else ClientInfoHack.instance.alignment.hide();
			}
		});
		
		this.addSetting(this.alignment);
	}
}
