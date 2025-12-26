package net.skidcode.gh.maybeaclient.hacks;

import java.util.ArrayList;
import java.util.HashMap;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.skidcode.gh.maybeaclient.Client;
import net.skidcode.gh.maybeaclient.gui.click.ClickGUI;
import net.skidcode.gh.maybeaclient.gui.click.SettingsTab;
import net.skidcode.gh.maybeaclient.gui.click.Tab;
import net.skidcode.gh.maybeaclient.gui.click.element.Element;
import net.skidcode.gh.maybeaclient.gui.click.element.ExpandToggleButtonElement;
import net.skidcode.gh.maybeaclient.gui.click.element.ExpandToggleButtonElement.ExpandToggleButtonActionListener;
import net.skidcode.gh.maybeaclient.gui.click.element.VerticalContainer;
import net.skidcode.gh.maybeaclient.hacks.ClickGUIHack.Theme;
import net.skidcode.gh.maybeaclient.hacks.category.Category;
import net.skidcode.gh.maybeaclient.hacks.settings.Setting;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingBoolean;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingButton;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingKeybind;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingsProvider;
import net.skidcode.gh.maybeaclient.utils.ChatColor;

public class Hack implements SettingsProvider, ExpandToggleButtonActionListener{
	public String name;
	public String description;
	public SettingKeybind keybinding;
	public SettingButton resetToDefaults;
	public boolean status = false;
	public boolean hasSettings = false;
	public HashMap<String, Setting> settings = new HashMap<>();
	public ArrayList<Setting> settingsArr = new ArrayList<>(); 
	public int hiddens = 0;
	public static Minecraft mc;
	public Category category;
	public Tab tab = null;
	public VerticalContainer categorybutton;
	public VerticalContainer settingscontainer;
	
	public int getDescriptionHeight(Tab tab) {
		Theme t = ClickGUIHack.theme();
		if(t == Theme.HEPHAESTUS) {
			int[] wh = mc.fontRenderer.getSplittedStringWidthAndHeight_h(this.description, tab.getUsableWidth() - Theme.HEPH_OPT_XADD, Theme.HEPH_DESC_YADD);
			return 3 + wh[1];
		}
		
		return 0;
	}

	public void onPressed(SettingButton b) {
		if(b == this.resetToDefaults) {
			for(Setting s : this.settingsArr) {
				s.reset();
			}
		}
	}
	
	public int totalSettingHeight(Tab tab) {
		int result = 0;
		result = this.getDescriptionHeight(tab);
		
		for(int i = 0; i < this.settingsArr.size(); ++i) {
			Setting s = this.settingsArr.get(i);
			if(s.hidden) continue;
			result += s.getSettingHeight(tab);
		}
		return result;
	}
	
	public Hack(String name, String description, int keybind, Category category) {
		VerticalContainer vc = new VerticalContainer();
		vc.addElement(new ExpandToggleButtonElement(this));
		this.settingscontainer = new VerticalContainer();
		this.categorybutton = vc;
		this.name = name;
		this.description = description;
		this.keybinding = new SettingKeybind(this, "Keybind", keybind);
		this.resetToDefaults = new SettingButton(this, "Reset all settings");
		this.category = category;
		category.hacks.add(this);
		this.addSetting(this.keybinding);
		this.addSetting(this.resetToDefaults);
	}
	
	@Override
	public String getDisplayString(boolean v) {
		return this.name;
	}


	@Override
	public boolean getValue() {
		return this.status;
	}


	@Override
	public void onPressed(int mx, int my, int click) {
		this.toggle();
	}


	@Override
	public void onExpand(Element caller, int startX, int startY, int endX, int endY, int mx, int my, int click, boolean expanded) {
		this.expandChanged(expanded, ClickGUIHack.theme());
	}


	@Override
	public boolean hoveringOver(int x, int y) {
		if(ClickGUIHack.instance.showDescription.getValue()) {
			ClickGUI.showDescription(x + 8, y, this);
			return true;
		}
		return true;
	}
	
	public void addSetting(Setting setting) {
		this.settingsArr.add(setting);
		this.settings.put(setting.noWhitespacesName.toLowerCase(), setting);
		this.hasSettings = true;
		this.settingscontainer.addElement(setting.guielement);
		//this.categorybutton.addElement(setting.guielement);
	}
	
	public void bind(int key) {
		this.keybinding.setValue(key);
		Client.addMessage(
				"Module "+ChatColor.GOLD+this.name+ChatColor.WHITE+" is now binded to "+
				ChatColor.GOLD+this.keybinding.valueToString()+ChatColor.WHITE+
				"("+ChatColor.GOLD+this.keybinding.value+ChatColor.WHITE+")"
		);
		Client.saveModules();
	}
	
	public String getPrefix() {
		return "";
	}
	
	public String getNameForArrayList() {
		return this.name;
	}
	
	public void toggle() {
		this.status = !this.status;
		
		if(this.status) {
			this.onEnable();
		}else {
			this.onDisable();
		}
		
		if(OnToggleMessageHack.instance.status) {
			OnToggleMessageHack.toggled(this);
		}
		
		Client.saveModules();
	}
	public void toggleByKeybind() {
		this.toggle();
	}
	
	public void onEnable() {
		
	}
	
	public void onDisable() {
		
	}

	@Override
	public ArrayList<Setting> getSettings() {
		return this.settingsArr;
	}

	@Override
	public void incrHiddens(int i) {
		this.hiddens += i;
	}

	public boolean expanded = false;
	public void expandChanged(boolean expanded, Theme theme) {
		if(expanded) {
			this.expanded = true;
			if(theme.verticalSettings) this.categorybutton.addElement(this.getSettingContainer());
			else if(tab == null) ClickGUI.addTab(0, tab = new SettingsTab(this.categorybutton, this));
		}else {
			this.expanded = false;
			if(theme.verticalSettings) this.categorybutton.removeElement(this.getSettingContainer());
			else if(tab != null) {
				ClickGUI.removeTab(tab);
				tab = null;
			}
		}
	}
	public void themeChanged(Theme theme) {
		if(theme.verticalSettings && this.tab != null) {
			ClickGUI.removeTab(tab);
			tab = null;
		}
		else this.categorybutton.removeElement(this.getSettingContainer());

		this.expandChanged(this.expanded, theme);
	}


	@Override
	public VerticalContainer getSettingContainer() {
		return this.settingscontainer;
	}
}
