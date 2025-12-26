package net.skidcode.gh.maybeaclient.hacks;

import org.lwjgl.input.Keyboard;

import net.skidcode.gh.maybeaclient.gui.click.ClickGUI;
import net.skidcode.gh.maybeaclient.hacks.category.Category;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingBoolean;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingColor;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingEnum;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingMode;

public class ClickGUIHack extends Hack{
	public static ClickGUIHack instance;
	public SettingBoolean manualResize = new SettingBoolean(this, "ManualResizing", false); //XXX debug purposes only
	public SettingMode guiScale = new SettingMode(this, "Gui Scale", "Normal", "Large", "Game" , "Small");
	public SettingColor themeColor = new SettingColor(this, "PrimaryColor", 0, 0xaa, 0xaa);
	public SettingColor secColor = new SettingColor(this, "SecondaryColor", 0x55, 0xff, 0x55);
	public SettingBoolean fillEnabled = new SettingBoolean(this, "Show Enabled Modules", true);
	public SettingBoolean resetColor = new SettingBoolean(this, "Reset Colors On Theme Switch", true);
	public SettingBoolean showDescription = new SettingBoolean(this, "Show Description", false);
	
	public SettingEnum<Theme> theme;
	
	
	public ClickGUIHack() {
		super("ClickGUI", "Open ClickGUI", Keyboard.KEY_UP, Category.UI);
		instance = this;
		this.addSetting(this.guiScale);
		this.addSetting(this.theme = new SettingEnum<Theme>(this, "Theme", Theme.CLIFF) {
			public void setValue(String value) {
				super.setValue(value);
				boolean nodus = this.getValue() == Theme.NODUS;
				boolean cliff = this.getValue() == Theme.CLIFF;
				boolean hephaestus = this.getValue() == Theme.HEPHAESTUS;
				ClickGUIHack.instance.secColor.hidden = !nodus && !hephaestus;
				ClickGUIHack.instance.fillEnabled.hidden = !nodus;
				ClickGUIHack.instance.showDescription.hidden = hephaestus;
				
				if(ClickGUIHack.instance.resetColor.value) {
					if(nodus) {
						ClickGUIHack.instance.secColor.setValue(0x55, 0xff, 0x55);
						ClickGUIHack.instance.themeColor.setValue(0xff, 0xff, 0xff);
					}
					if(cliff) {
						ClickGUIHack.instance.themeColor.setValue(0x00, 0xaa, 0xaa);
						ClickGUIHack.instance.secColor.setValue(0xff, 0xff, 0xff);
					}
					if(hephaestus) {
						ClickGUIHack.instance.themeColor.setValue(29, 34, 54);
						ClickGUIHack.instance.secColor.setValue(0xfd, 0xfd, 0x96);
					}
				}
				ClickGUIHack.instance.showDescription.hidden = hephaestus;
			}
		});
		this.addSetting(this.resetColor);
		this.addSetting(this.themeColor);
		this.addSetting(this.secColor);
		this.addSetting(this.fillEnabled);
		this.addSetting(this.showDescription);
	}
	
	public static int normTextColor() {
		if(theme() == Theme.CLIFF || theme() == Theme.HEPHAESTUS) return 0xffffff;
		return ClickGUIHack.instance.themeColor.rgb();
	}
	public static int highlightedTextColor() {
		if(theme() == Theme.CLIFF) return 0x55FFFF;
		return ClickGUIHack.instance.secColor.rgb();
	}
	
	public static Theme theme() {
		return instance.theme.getValue();
	}
	
	public static float r() {
		return (float)instance.themeColor.red / 255f;
	}
	public static float g() {
		return (float)instance.themeColor.green / 255f;
	}
	public static float b() {
		return (float)instance.themeColor.blue / 255f;
	}
	public int getScale() {
		if(this.guiScale.currentMode.equalsIgnoreCase("Large")) return 3;
		if(this.guiScale.currentMode.equalsIgnoreCase("Normal")) return 2;
		if(this.guiScale.currentMode.equalsIgnoreCase("Small")) return 1;
		return mc.gameSettings.guiScale;
	}
	
	public void onEnable() {
		mc.displayGuiScreen(new ClickGUI(mc.currentScreen));
	}
	
	public void onDisable() {
		if(mc.currentScreen instanceof ClickGUI) {
			mc.displayGuiScreen(((ClickGUI)mc.currentScreen).parent);
		}
		
	}
	
	public enum Theme{
		CLIFF("Cliff", 12, 3, 2, 2, 1, 2, 4, false, 2),
		NODUS("Nodus", 14, 2, 3, 0, 0, 10+4+2, 4, false, 2),
		HEPHAESTUS("Hephaestus", 14, 0, 3, 0, 0, 10+4+2+2, 4, true, 4);
		
		public static final int HEPH_DESC_YADD = 10;
		public static final int HEPH_OPT_XADD = 7;
		public static final int HEPH_SLIDER_HEIGHT = 2;
		public static final int HEPH_DISABLED_COLOR = 0x676767;
		public int yspacing;
		public int titlebasediff;
		public int yaddtocenterText;
		public int headerXAdd;
		public int settingYreduce;
		public int settingBorder;
		public int titleXadd;
		public int scrollbarSize;
		public boolean verticalSettings;
		public final String name;
		Theme(String name, int spacing, int tdb, int yd, int syr, int border, int txa, int scrollbarSize, boolean vs, int hxa){
			this.name = name;
			this.yspacing = spacing;
			this.titlebasediff = tdb;
			this.yaddtocenterText = yd;
			this.settingYreduce = syr;
			this.settingBorder = border;
			this.titleXadd = txa;
			this.scrollbarSize = scrollbarSize;
			this.verticalSettings = vs;
			this.headerXAdd = hxa;
		}
		
		public String toString() {
			return this.name;
		}
	}
}
