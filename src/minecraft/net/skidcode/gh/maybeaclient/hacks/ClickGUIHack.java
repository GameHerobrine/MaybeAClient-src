package net.skidcode.gh.maybeaclient.hacks;

import java.util.Comparator;

import org.lwjgl.input.Keyboard;

import net.skidcode.gh.maybeaclient.Client;
import net.skidcode.gh.maybeaclient.gui.click.ClickGUI;
import net.skidcode.gh.maybeaclient.gui.click.Tab;
import net.skidcode.gh.maybeaclient.gui.click.element.Element;
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
	public SettingEnum<ShowFrame> showFrameInHud = new SettingEnum<ShowFrame>(this, "HUDFrameStyle", ShowFrame.YES);
	public SettingEnum<SortDirection> sortModules = new SettingEnum<SortDirection>(this, "Sort Modules", SortDirection.A_Z) {
		@Override
		public void setValue(String value) {
			super.setValue(value);
			for(Category c : Category.categories) {
				c.notifyContentChange();
			}
		}
	};
	public SettingBoolean scrollUsingScrollWheel = new SettingBoolean(this, "Scroll using scroll wheel", true);
	
	public ClickGUIHack() {
		super("ClickGUI", "Open ClickGUI", Keyboard.KEY_UP, Category.UI);
		instance = this;
		this.addSetting(this.guiScale);
		this.addSetting(this.theme = new SettingEnum<Theme>(this, "Theme", Theme.CLIFF) {
			public void setValue(String value) {
				super.setValue(value);
				Theme theme = this.getValue();
				boolean nodus = this.getValue() == Theme.NODUS;
				boolean cliff = this.getValue() == Theme.CLIFF;
				boolean hephaestus = this.getValue() == Theme.HEPHAESTUS;
				boolean uware = this.getValue() == Theme.UWARE;
				
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
					
					if(theme == Theme.IRIDIUM) {
						ClickGUIHack.instance.themeColor.setValue(0xff, 0x33, 0x33);
						ClickGUIHack.instance.secColor.setValue(0xfd, 0xfd, 0x96);
					}
					
					if(uware) {
						ClickGUIHack.instance.themeColor.setValue(0x00, 0x9C, 0xE0);
						ClickGUIHack.instance.secColor.setValue(0xff, 0xff, 0xff);
					}
				}
				ClickGUIHack.instance.showDescription.hidden = hephaestus;
				
				for(Hack h : Client.hacksByName.values()) {
					h.themeChanged(this.getValue());
				}
			}
		});
		this.addSetting(this.showFrameInHud);
		this.addSetting(this.resetColor);
		this.addSetting(this.themeColor);
		this.addSetting(this.secColor);
		this.addSetting(this.fillEnabled);
		this.addSetting(this.showDescription);
		this.addSetting(this.sortModules);
		this.addSetting(this.scrollUsingScrollWheel);
	}
	
	public static boolean renderHeader(Tab tab) {
		ShowFrame h = instance.showFrameInHud.getValue();
		return h == ShowFrame.YES || (!tab.isHUD || tab.minimized.getValue());
	}
	
	public static ShowFrame renderFrame(Tab tab) {
		ShowFrame h = instance.showFrameInHud.getValue();
		return (tab != null && (!tab.isHUD || tab.minimized.getValue())) ? ShowFrame.YES : h;
	}
	public static int themeColor() {
		return ClickGUIHack.instance.themeColor.rgb();
	}
	public static int normTextColor() {
		if(theme() == Theme.UWARE) return Theme.UWARE_ENABLED_COLOR;
		if(theme() == Theme.CLIFF || theme() == Theme.HEPHAESTUS || theme() == Theme.IRIDIUM) return 0xffffff;
		return themeColor();
	}
	public static int highlightedTextColor() {
		if(theme() == Theme.UWARE) return themeColor();
		if(theme() == Theme.IRIDIUM) return 0x55ffff;
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
		HEPHAESTUS("Hephaestus", 14, 0, 3, 0, 0, 10+4+2+2, 4, true, 4),
		IRIDIUM("Iridium", 10, 2, 1, 0, 0, 11, 4, false, 1),
		UWARE("Uware", 14, 0, 3, 0, 0, 10+4+2, 4, true, 2);
		
		public static final int HEPH_DESC_YADD = 10;
		public static final int HEPH_OPT_XADD = 7;
		public static final int HEPH_SLIDER_HEIGHT = 2;
		public static final int HEPH_DISABLED_COLOR = 0x676767;
		public static final int IRIDIUM_DISABLED_COLOR = 0x555555;
		public static final int IRIDIUM_ENABLED_COLOR = 0xf7f7f7;

		public static final int UWARE_OPT_XADD = 8;
		public static final int UWARE_SLIDER_HEIGHT = 2;
		//public static final int UWARE_THEME_COLOR = 0x009CE0;
		public static final int UWARE_HEAD_OPACITY = 0xCC;
		public static final float UWARE_SLIDER_LEFT_A = (58.5f)/255f;
		public static final float UWARE_SETTING_OVERLAY_A = 65/255f;
		public static final int UWARE_ROUNDNESS = 1;
		public static final int UWARE_DISABLED_COLOR = 0x8a8a8a;
		public static final int UWARE_ENABLED_COLOR = 0xffffff;
		
		
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

	public static enum ShowFrame{
		YES("Full"),
		NOHEADER("NoHeader"),
		TRANSPARENT("NoHeaderHalfTransparentBG"),
		NO("NoHeaderNoBG");
		
		public final String name;
		ShowFrame(String name) {
			this.name = name;
		}
		public String toString() {
			return this.name;
		}
	}
	public static enum SortDirection{
		A_Z("A-Z"),
		Z_A("Z-A"),
		NONE("None");
		
		final String name;
		
		SortDirection(String s){
			this.name = s;
		}
		
		public String toString() {
			return this.name;
		}
	}
}
