package net.skidcode.gh.maybeaclient.hacks;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;

import org.lwjgl.input.Keyboard;

import net.skidcode.gh.maybeaclient.Client;
import net.skidcode.gh.maybeaclient.events.Event;
import net.skidcode.gh.maybeaclient.events.EventListener;
import net.skidcode.gh.maybeaclient.events.EventRegistry;
import net.skidcode.gh.maybeaclient.events.impl.EventRenderIngameNoDebug;
import net.skidcode.gh.maybeaclient.gui.click.ArrayListTab;
import net.skidcode.gh.maybeaclient.gui.click.ClickGUI;
import net.skidcode.gh.maybeaclient.hacks.category.Category;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingEnum;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingMode;
import net.skidcode.gh.maybeaclient.hacks.settings.enums.EnumAlign;
import net.skidcode.gh.maybeaclient.hacks.settings.enums.EnumExpand;
import net.skidcode.gh.maybeaclient.hacks.settings.enums.EnumStaticPos;
import net.skidcode.gh.maybeaclient.utils.ChatColor;

public class ArrayListHack extends Hack{
	
	public SettingMode sortMode = new SettingMode(this, "Sorting", "Descending", "Ascending", "None");
	public SettingEnum<EnumAlign> alignment = new SettingEnum<>(this, "Alignment", EnumAlign.LEFT);
	public SettingEnum<EnumStaticPos> staticPositon;
	public SettingEnum<EnumExpand> expand = new SettingEnum<>(this, "Expand", EnumExpand.TOP);
	public static ArrayListHack instance;
	
	public ArrayListHack() {
		super("ArrayList", "Shows list of enabled modules", Keyboard.KEY_NONE, Category.UI);
		instance = this;
		this.addSetting(this.sortMode);
		this.addSetting(this.staticPositon = new SettingEnum<EnumStaticPos>(this, "Static Position", EnumStaticPos.BOTTOM_RIGHT) {
			public void setValue(String value) {
				super.setValue(value);
				if(this.getValue() == EnumStaticPos.DISABLED) {
					ArrayListHack.instance.alignment.show();
					ArrayListHack.instance.expand.show();
				}else {
					ArrayListHack.instance.alignment.hide();
					ArrayListHack.instance.expand.hide();
				}
			}
		});
		this.addSetting(this.alignment);
		this.addSetting(this.expand);
	}
	
	public static class SorterAZ implements Comparator<String>{
		public static SorterAZ inst = new SorterAZ();
		
		@Override
		public int compare(String o1, String o2) {
			int name1 = mc.fontRenderer.getStringWidth(o1);
			int name2 = mc.fontRenderer.getStringWidth(o2);
			if(name1 > name2) return -1;
			else if(name1 < name2) return 1;
			else return 0;
		}
	}
	
	public static class SorterZA implements Comparator<String>{
		public static SorterZA inst = new SorterZA();
		
		@Override
		public int compare(String o1, String o2) {
			int name1 = mc.fontRenderer.getStringWidth(o1);
			int name2 = mc.fontRenderer.getStringWidth(o2);
			if(name1 > name2) return 1;
			else if(name1 < name2) return -1;
			else return 0;
		}
	}
	
}
