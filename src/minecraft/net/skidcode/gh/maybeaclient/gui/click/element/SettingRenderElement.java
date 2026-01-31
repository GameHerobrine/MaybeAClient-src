package net.skidcode.gh.maybeaclient.gui.click.element;

import net.skidcode.gh.maybeaclient.Client;
import net.skidcode.gh.maybeaclient.hacks.ClickGUIHack;
import net.skidcode.gh.maybeaclient.hacks.settings.Setting;
import net.skidcode.gh.maybeaclient.utils.GUIUtils;

public class SettingRenderElement extends Element{
	public Setting toRender;
	public SettingRenderElement(Setting s) {
		super();
		this.toRender = s;
	}
	
	@Override
	public void recalculatePosition(Element parent, int x, int y) {
		this.shown = !this.toRender.hidden;
		this.startX = x;
		this.startY = y;
		this.parent = parent;
		this.alignRight = parent.alignRight;
		int w = this.toRender.getSettingWidth();
		this.endX = this.startX + w;
		int h = this.toRender.getSettingHeight(parent);
		this.endY = this.startY + h;
	}

	@Override
	public void renderTop() {
		int setBord = ClickGUIHack.theme().settingBorder;
		this.toRender.renderText(this.parent, this.startX + setBord, this.startY + setBord, this.endX - setBord, this.endY - setBord);
	}

	@Override
	public void renderBottom() {
		int setBord = ClickGUIHack.theme().settingBorder;
		this.toRender.renderElement(this, this.startX + setBord, this.startY + setBord, this.endX - setBord, this.endY - setBord);
	}
	
	public boolean onClick(int mx, int my, int click) {
		int setBord = ClickGUIHack.theme().settingBorder;
		this.toRender.onPressedInside(this, this.startX + setBord, this.startY + setBord, this.endX - setBord, this.endY - setBord, mx, my, click);
		return GUIUtils.isInsideRect(mx, my, this.startX + setBord, this.startY + setBord, this.endX - setBord, this.endY - setBord);
	}
	public boolean hoveringOver(int x, int y) {
		this.toRender.mouseHovered(x, y, -1);
		return true;
	}
	public boolean mouseMovedSelected(int x, int y) {
		int setBord = ClickGUIHack.theme().settingBorder;
		this.toRender.onMouseMoved(this.startX + setBord, this.startY + setBord, this.endX - setBord, this.endY - setBord, x, y, -1);
		return false;
	}
	public void onDeselect(int x, int y) {
		int setBord = ClickGUIHack.theme().settingBorder;
		Client.saveModules();
		this.toRender.onDeselect(this, this.startX + setBord, this.startY + setBord, this.endX - setBord, this.endY - setBord, x, y, -1);
	}

}
