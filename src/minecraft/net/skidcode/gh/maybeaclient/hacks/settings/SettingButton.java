package net.skidcode.gh.maybeaclient.hacks.settings;

import net.skidcode.gh.maybeaclient.gui.click.Tab;
import net.skidcode.gh.maybeaclient.hacks.Hack;

public class SettingButton extends SettingBoolean{
	public SettingButton(Hack hack, String name) {
		super(hack, name, false);
	}
	
	public void setValue(boolean d) {}
	
	public void onDeselect(Tab tab, int xMin, int yMin, int xMax, int yMax, int mouseX, int mouseY, int mouseClick) {
		this.hack.onPressed(this);
	}

}
