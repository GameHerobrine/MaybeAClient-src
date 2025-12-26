package net.skidcode.gh.maybeaclient.hacks;

import org.lwjgl.input.Keyboard;

import net.skidcode.gh.maybeaclient.gui.click.Tab;
import net.skidcode.gh.maybeaclient.gui.click.element.Element;
import net.skidcode.gh.maybeaclient.hacks.category.Category;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingBoolean;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingInteger;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingMode;

public class AutoMouseClickHack extends Hack{
	
	public SettingMode left, right;
	public SettingInteger lDelayTicks, rDelayTicks;
	public SettingBoolean showDisableButton = new SettingBoolean(this, "ShowDisableButtonInContainers", true);
	public int ldelay, rdelay;
	
	public static AutoMouseClickHack instance;
	public AutoMouseClickHack() {
		super("AutoMouseClick", "Automatically clicks mouse button", Keyboard.KEY_NONE, Category.MISC);
		instance = this;
		this.lDelayTicks = new SettingInteger(this, "Left Click Delay", 0, 2, 20);
		this.rDelayTicks = new SettingInteger(this, "Right Click Delay", 0, 2, 20);
		
		this.left = new SettingMode(this, "Left", "Disabled", "Hold", "Click") {
			@Override
			public void setValue(String value) {
				super.setValue(value);
				AutoMouseClickHack.instance.lDelayTicks.hidden = !this.currentMode.equalsIgnoreCase("Click");
			}
			
			@Override
			public void renderElement(Element tab, int xStart, int yStart, int xEnd, int yEnd) {
				if(!this.currentMode.equalsIgnoreCase("Disabled")) super.renderElement(tab, xStart, yStart, xEnd, yEnd);
			}
		};
		this.right = new SettingMode(this, "Right", "Disabled", "Hold", "Click") {
			@Override
			public void setValue(String value) {
				super.setValue(value);
				AutoMouseClickHack.instance.rDelayTicks.hidden = !this.currentMode.equalsIgnoreCase("Click");
			}
			
			@Override
			public void renderElement(Element tab, int xStart, int yStart, int xEnd, int yEnd) {
				if(!this.currentMode.equalsIgnoreCase("Disabled")) super.renderElement(tab, xStart, yStart, xEnd, yEnd);
			}
		};
		
		this.addSetting(this.showDisableButton);
		this.addSetting(this.left);
		this.addSetting(this.lDelayTicks);
		this.addSetting(this.right);
		this.addSetting(this.rDelayTicks);
	}

}
