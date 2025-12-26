package net.skidcode.gh.maybeaclient.hacks.settings;

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
	public void renderText(int x, int y) {
		if(ClickGUIHack.theme() == Theme.IRIDIUM) this.value = true;
		super.renderText(x, y);
		if(ClickGUIHack.theme() == Theme.IRIDIUM) this.value = false;
	}
	
	public void onPressedInside(Element tab, int xMin, int yMin, int xMax, int yMax, int mouseX, int mouseY, int mouseClick) {
		if(ClickGUIHack.theme() == Theme.HEPHAESTUS && !GUIUtils.isInsideRect(mouseX, mouseY, xMax - Theme.HEPH_OPT_XADD - 7, yMin+3, xMax - Theme.HEPH_OPT_XADD + 1, yMax - 3)) {
			return;
		}
		((Hack)this.hack).onPressed(this);
	}
}
