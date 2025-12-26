package net.skidcode.gh.maybeaclient.hacks;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.RenderManager;
import net.minecraft.src.ScaledResolution;
import net.skidcode.gh.maybeaclient.events.Event;
import net.skidcode.gh.maybeaclient.events.EventListener;
import net.skidcode.gh.maybeaclient.events.EventRegistry;
import net.skidcode.gh.maybeaclient.events.impl.EventRenderIngameNoDebug;
import net.skidcode.gh.maybeaclient.gui.click.ClickGUI;
import net.skidcode.gh.maybeaclient.gui.click.InventoryTab;
import net.skidcode.gh.maybeaclient.gui.click.PlayerViewTab;
import net.skidcode.gh.maybeaclient.gui.click.RadarTab;
import net.skidcode.gh.maybeaclient.hacks.category.Category;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingBoolean;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingEnum;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingMode;
import net.skidcode.gh.maybeaclient.hacks.settings.enums.EnumAlign;
import net.skidcode.gh.maybeaclient.hacks.settings.enums.EnumExpand;
import net.skidcode.gh.maybeaclient.hacks.settings.enums.EnumStaticPos;
import net.skidcode.gh.maybeaclient.utils.ChatColor;

public class RadarHack extends Hack{
	
	public SettingBoolean showXYZ = new SettingBoolean(this, "Show XYZ", false);
	public SettingEnum<EnumAlign> alignment = new SettingEnum<>(this, "Alignment", EnumAlign.LEFT);
	public SettingEnum<EnumExpand> expand = new SettingEnum<>(this, "Expand", EnumExpand.BOTTOM);
	public SettingEnum<EnumStaticPos> staticPositon;
	public static RadarHack instance;
	public RadarHack() {
		super("Radar", "Shows players nearby", Keyboard.KEY_TAB, Category.UI);
		instance = this;
		this.addSetting(this.showXYZ);
		this.addSetting(this.staticPositon = new SettingEnum<EnumStaticPos>(this, "Static Position", EnumStaticPos.TOP_RIGHT) {
			public void setValue(String value) {
				super.setValue(value);
				if(this.getValue() == EnumStaticPos.DISABLED) {
					RadarHack.instance.alignment.show();
					RadarHack.instance.expand.show();
				}else {
					RadarHack.instance.alignment.hide();
					RadarHack.instance.expand.hide();
				}
			}
		});
		this.addSetting(this.alignment);
		this.addSetting(this.expand);
	}
}
