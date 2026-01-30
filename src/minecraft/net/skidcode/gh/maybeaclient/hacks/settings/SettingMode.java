package net.skidcode.gh.maybeaclient.hacks.settings;

import java.util.HashMap;
import java.util.HashSet;

import net.minecraft.src.NBTTagCompound;
import net.skidcode.gh.maybeaclient.Client;
import net.skidcode.gh.maybeaclient.gui.click.Tab;
import net.skidcode.gh.maybeaclient.gui.click.element.Element;
import net.skidcode.gh.maybeaclient.hacks.AutoTunnelHack;
import net.skidcode.gh.maybeaclient.hacks.ClickGUIHack;
import net.skidcode.gh.maybeaclient.hacks.Hack;
import net.skidcode.gh.maybeaclient.hacks.ClickGUIHack.Theme;
import net.skidcode.gh.maybeaclient.utils.ChatColor;

public class SettingMode extends Setting{

	public HashMap<String, String> modes = new HashMap<>();
	public HashMap<String, Integer> mode2pos = new HashMap<>();
	public HashMap<Integer, String> pos2mode = new HashMap<>();
	
	public String defaultMode;
	public String currentMode = "";
	
	public SettingMode(SettingsProvider hack, String name, String... modes) {
		super(hack, name);
		this.init();
		for(int i = 0; i < modes.length; ++i) {
			this.mode2pos.put(modes[i].toLowerCase(), i);
			this.pos2mode.put(i, modes[i].toLowerCase());
			this.modes.put(modes[i].toLowerCase(), modes[i]);
		}
		this.setValue(modes[0]);
		this.defaultMode = modes[0];
	}

	public void init() {}
	@Override
	public String valueToString() {
		return this.currentMode;
	}

	@Override
	public void reset() {
		this.setValue(this.defaultMode);
	}
	
	@Override
	public String valueToStringConsole() {
		String s = "";
		for(String mod : this.modes.values()) {
			s += (this.currentMode.equalsIgnoreCase(mod) ? ChatColor.LIGHTGREEN : ChatColor.LIGHTRED) + mod + ", ";
		}
		return s.substring(0, s.length() - 2);
	}
	
	@Override
	public boolean validateValue(String value) {
		return this.modes.containsKey(value.toLowerCase());
	}

	@Override
	public void writeToNBT(NBTTagCompound output) {
		output.setString(this.name, this.currentMode == null ? this.defaultMode : this.currentMode);
	}
	@Override
	public void renderText(Element tab, int x, int y, int xEnd, int yEnd) {
		int txtColor = 0xffffff;
		if(ClickGUIHack.theme() == Theme.IRIDIUM) {
			this.mouseHovering = false;
			Client.mc.fontRenderer.drawStringWithShadow(this.name + " - " + this.currentMode, x + 2, y + ClickGUIHack.theme().yaddtocenterText, Theme.IRIDIUM_ENABLED_COLOR);
			return;
		}
		if(ClickGUIHack.theme() == Theme.HEPHAESTUS) {
			this.mouseHovering = false;
			Client.mc.fontRenderer.drawStringWithShadow(this.name, x + Theme.HEPH_OPT_XADD, y + ClickGUIHack.theme().yaddtocenterText, txtColor);
			Client.mc.fontRenderer.drawStringWithShadow(this.currentMode, xEnd - Theme.HEPH_OPT_XADD + 1 - Client.mc.fontRenderer.getStringWidth(this.currentMode), y + ClickGUIHack.theme().yaddtocenterText, txtColor);
			
			return;
		}
		if(ClickGUIHack.theme() == Theme.NODUS) {
			txtColor = ClickGUIHack.instance.themeColor.rgb();
			if(this.mouseHovering) {
				txtColor = ClickGUIHack.instance.secColor.rgb();
			}
		}
		Client.mc.fontRenderer.drawString(this.name + " - " + this.currentMode, x + 2, y + ClickGUIHack.theme().yaddtocenterText, txtColor);
		this.mouseHovering = false;
	}
	
	public int getSettingWidth() {
		return Client.mc.fontRenderer.getStringWidth(this.name+" - "+this.currentMode) + 5 + (ClickGUIHack.theme() == Theme.HEPHAESTUS ? Theme.HEPH_OPT_XADD : 0);
	}
	
	@Override
	public void onPressedInside(Element tab, int xMin, int yMin, int xMax, int yMax, int mouseX, int mouseY, int mouseClick) {
		boolean set = false;
		int i = 0;
		if(this.mode2pos.get(this.currentMode.toLowerCase()) != null) {
			i = this.mode2pos.get(this.currentMode.toLowerCase()) + 1;
			if(this.pos2mode.get(i) == null) {
				i = 0;
			}
		}
		
		this.setValue(this.modes.get(this.pos2mode.get(i)));
	}
	
	
	
	public void renderElement(Element tab, int xStart, int yStart, int xEnd, int yEnd) {
		if(ClickGUIHack.theme() == Theme.HEPHAESTUS) return;
		if(ClickGUIHack.theme() == Theme.NODUS) {
			Tab.renderFrameBackGround(xStart, yStart, xEnd, yEnd, 0, 0, 0, 0x80/255f);
		}else if(ClickGUIHack.theme() == Theme.UWARE) {
			Tab.renderFrameBackGround(xStart, yStart, xEnd, yEnd, 0x16/255f, 0x16/255f, 0x16/255f, 0xaa/255f);
		} else{
			Tab.renderFrameBackGround(xStart, yStart, xEnd, yEnd, ClickGUIHack.r(), ClickGUIHack.g(), ClickGUIHack.b(), 1f);
		}
	}
	
	@Override
	public void readFromNBT(NBTTagCompound input) {
		if(input.hasKey(this.name)) {
			this.setValue(input.getString(this.name));
		}
	}

	public void setValue(String value) {
		this.currentMode = this.modes.get(value.toLowerCase());
		if(this.currentMode == null) {
			this.reset();
		}
	}
	
}
