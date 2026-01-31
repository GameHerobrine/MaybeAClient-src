package net.skidcode.gh.maybeaclient.gui.click.element;

import net.skidcode.gh.maybeaclient.Client;
import net.skidcode.gh.maybeaclient.gui.click.Tab;
import net.skidcode.gh.maybeaclient.hacks.ClickGUIHack;
import net.skidcode.gh.maybeaclient.hacks.ClickGUIHack.Theme;

public class ToggleButtonElement extends Element{
	public static interface ToggleButtonActionListener{
		public String getDisplayString(boolean v);
		public boolean getValue();
		public void onPressed(int mx, int my, int click);
	}
	
	public ToggleButtonActionListener listener;
	
	public ToggleButtonElement(ToggleButtonActionListener listener) {
		this.listener = listener;
	}
	
	public boolean mouseHovering = false;
	@Override
	public boolean hoveringOver(int x, int y) {
		mouseHovering = true;
		return true;
	}
	
	@Override
	public boolean onClick(int mx, int my, int click) {
		if(click == 0) this.listener.onPressed(mx, my, click);
		return true;
	}
	
	@Override
	public void renderTop() {
		boolean v = this.listener.getValue();
		String s = this.listener.getDisplayString(v);
		int txtColor = 0xffffff;
		
		if(ClickGUIHack.theme() == Theme.NODUS) {
			txtColor = ClickGUIHack.instance.themeColor.rgb();
			if(this.mouseHovering) {
				txtColor = ClickGUIHack.instance.secColor.rgb();
			}
		}
		if(ClickGUIHack.theme() == Theme.UWARE) {
			txtColor = this.listener.getValue() ? Theme.UWARE_ENABLED_COLOR : Theme.UWARE_DISABLED_COLOR;
		}
		Client.mc.fontRenderer.drawString(s, startX + 2, startY + ClickGUIHack.theme().yaddtocenterText, txtColor);
		mouseHovering = false;
	}

	@Override
	public void renderBottom() {
		boolean v = this.listener.getValue();
		if(ClickGUIHack.theme() == Theme.UWARE) {
			//Tab.renderFrameBackGround(startX, startY, endX, endY, 0x26/255f, 0x26/255f, 0x26/255f, 0xaa/255f);
			return;
		}
		if(ClickGUIHack.theme() == Theme.HEPHAESTUS || ClickGUIHack.theme() == Theme.IRIDIUM) {
			return;
		}
		
		if(v) {
			if(ClickGUIHack.theme() == Theme.NODUS) {
				Tab.renderFrameBackGround(startX, startY, endX, endY, 0, 0, 0, 0x80/255f);
			}else {
				Tab.renderFrameBackGround(startX, startY, endX, endY, ClickGUIHack.r(), ClickGUIHack.g(), ClickGUIHack.b(), 1f);
			}
		}
	}
	
	public int getWidth() {
		return Client.mc.fontRenderer.getStringWidth(this.listener.getDisplayString(this.listener.getValue())) + (ClickGUIHack.theme() == Theme.HEPHAESTUS ? Theme.HEPH_OPT_XADD : 2);
	}

	@Override
	public void recalculatePosition(Element element, int x, int y) {
		this.startX = x;
		this.startY = y;
		this.parent = element;
		this.alignRight = parent.alignRight;
		int w = this.getWidth(); //TODO better way to get width
		int h = ClickGUIHack.theme().yspacing;
		
		this.endX = x+w;
		this.endY = y+h;
	}

}
