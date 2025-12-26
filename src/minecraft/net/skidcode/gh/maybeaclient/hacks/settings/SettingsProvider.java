package net.skidcode.gh.maybeaclient.hacks.settings;

import java.util.ArrayList;

import net.skidcode.gh.maybeaclient.gui.click.element.VerticalContainer;

public interface SettingsProvider {
	public ArrayList<Setting> getSettings();
	public VerticalContainer getSettingContainer();
	public void incrHiddens(int i);
}
