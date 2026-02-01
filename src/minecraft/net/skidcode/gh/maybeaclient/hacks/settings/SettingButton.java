package net.skidcode.gh.maybeaclient.hacks.settings;

import net.skidcode.gh.maybeaclient.Client;
import net.skidcode.gh.maybeaclient.gui.click.Tab;
import net.skidcode.gh.maybeaclient.gui.click.element.Element;
import net.skidcode.gh.maybeaclient.hacks.ClickGUIHack;
import net.skidcode.gh.maybeaclient.hacks.Hack;
import net.skidcode.gh.maybeaclient.hacks.ClickGUIHack.Theme;
import net.skidcode.gh.maybeaclient.utils.GUIUtils;

public class SettingButton extends SettingBoolean{
	public SettingButton(Hack hack, String name) {
		super(hack, name, false);
	}
	
	public void setValue(boolean d) {}
	@Override
	public void renderText(Element tab, int x, int y, int xEnd, int yEnd) {
		if(ClickGUIHack.theme() == Theme.UWARE) {
			int xo = ((xEnd - x) - Client.mc.fontRenderer.getStringWidth(this.name))/2;
			Client.mc.fontRenderer.drawString(this.name, x + xo, y + ClickGUIHack.theme().yaddtocenterText, Theme.UWARE_ENABLED_COLOR);
			return;
		}
		if(ClickGUIHack.theme() == Theme.IRIDIUM) this.value = true;
		super.renderText(tab, x, y, xEnd, yEnd);
		if(ClickGUIHack.theme() == Theme.IRIDIUM) this.value = false;
		
	}
	@Override
	public void renderElement(Element tab, int xStart, int yStart, int xEnd, int yEnd) {
		if(ClickGUIHack.theme() == Theme.UWARE) {
			Tab.renderFrameBackGround(xStart, yStart, xEnd, yEnd, 0, 0, 0, Theme.UWARE_SETTING_OVERLAY_A);
			Tab.renderFrameBackGround(xStart+2, yStart, xEnd-2, yEnd, 2/255f, 10/255f, 10/255f, 60/255f);
			return;
		}
		super.renderElement(tab, xStart, yStart, xEnd, yEnd);
	}
	
	public void onPressedInside(Element tab, int xMin, int yMin, int xMax, int yMax, int mouseX, int mouseY, int mouseClick) {
		if(ClickGUIHack.theme() == Theme.UWARE && !GUIUtils.isInsideRect(mouseX, mouseY, xMin+2, yMin, xMax-2, yMax)) {
			return;
		}
		if(ClickGUIHack.theme() == Theme.HEPHAESTUS && !GUIUtils.isInsideRect(mouseX, mouseY, xMax - Theme.HEPH_OPT_XADD - 7, yMin+3, xMax - Theme.HEPH_OPT_XADD + 1, yMax - 3)) {
			return;
		}
		((Hack)this.hack).onPressed(this);
	}
}
