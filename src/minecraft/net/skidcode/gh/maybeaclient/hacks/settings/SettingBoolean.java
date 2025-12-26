package net.skidcode.gh.maybeaclient.hacks.settings;

import net.minecraft.src.NBTTagCompound;
import net.skidcode.gh.maybeaclient.Client;
import net.skidcode.gh.maybeaclient.gui.click.Tab;
import net.skidcode.gh.maybeaclient.gui.click.element.Element;
import net.skidcode.gh.maybeaclient.hacks.ClickGUIHack;
import net.skidcode.gh.maybeaclient.hacks.ClickGUIHack.Theme;
import net.skidcode.gh.maybeaclient.utils.GUIUtils;

public class SettingBoolean extends Setting{
	
	public boolean value, initialValue;
	
	public SettingBoolean(SettingsProvider hack, String name, boolean initialValue) {
		super(hack, name);
		this.setValue(initialValue);
		this.initialValue = initialValue;
	}
	
	public boolean getValue() {
		return this.value;
	}
	
	public void setValue(boolean d) {
		this.value = d;
	}
	
	@Override
	public String valueToString() {
		return ""+this.value;
	}

	public boolean validateValue(String value) {
		try {
			Boolean.parseBoolean(value);
			return true;
		}catch(NumberFormatException e) {
			return false;
		}
	}
	
	@Override
	public void renderText(int x, int y) {
		if(ClickGUIHack.theme() == Theme.IRIDIUM) {
			int txtColor;
			if(this.value) txtColor = Theme.IRIDIUM_ENABLED_COLOR;
			else  txtColor = Theme.IRIDIUM_DISABLED_COLOR;
			Client.mc.fontRenderer.drawStringWithShadow(this.name, x + 2, y + ClickGUIHack.theme().yaddtocenterText, txtColor);
			this.mouseHovering = false;
			return;
		}
		super.renderText(x, y);
	}
	
	@Override
	public void renderElement(Element tab, int xStart, int yStart, int xEnd, int yEnd) {
		if(ClickGUIHack.theme() == Theme.IRIDIUM) return;
		if(ClickGUIHack.theme() == Theme.HEPHAESTUS) {
			float r, g, b, a = 128f/255f;
			g = b = r = 21/255f;
			if(this.value) {
				r = ClickGUIHack.r();
				g = ClickGUIHack.g();
				b = ClickGUIHack.b();
				a = 255/255f;
			}
			Tab.renderFrameBackGround(xEnd - Theme.HEPH_OPT_XADD - 7, yStart+3, xEnd - Theme.HEPH_OPT_XADD + 1, yEnd - 3, r, g, b, a);
			Tab.renderFrameOutlines((double)xEnd - Theme.HEPH_OPT_XADD - 7, (double)yStart+3, (double)xEnd - Theme.HEPH_OPT_XADD + 1, (double)yEnd - 3);
			
			return;
		}
		
		if(this.value) {
			if(ClickGUIHack.theme() == Theme.NODUS) {
				Tab.renderFrameBackGround(xStart, yStart, xEnd, yEnd, 0, 0, 0, 0x80/255f);
			}else {
				Tab.renderFrameBackGround(xStart, yStart, xEnd, yEnd, ClickGUIHack.r(), ClickGUIHack.g(), ClickGUIHack.b(), 1f);
			}
		}
	}
	
	@Override
	public void reset() {
		this.setValue(this.initialValue);
	}
	
	public void onPressedInside(Element tab, int xMin, int yMin, int xMax, int yMax, int mouseX, int mouseY, int mouseClick) {
		if(ClickGUIHack.theme() == Theme.HEPHAESTUS && !GUIUtils.isInsideRect(mouseX, mouseY, xMax - Theme.HEPH_OPT_XADD - 7, yMin+3, xMax - Theme.HEPH_OPT_XADD + 1, yMax - 3)) {
			return;
		}
		this.setValue(!this.value);
	}
	
	@Override
	public void writeToNBT(NBTTagCompound output) {
		output.setBoolean(this.name, this.value);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound input) {
		if(input.hasKey(this.name)) this.setValue(input.getBoolean(this.name));
	}
}
