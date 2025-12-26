package net.skidcode.gh.maybeaclient.gui.click.element;

import net.skidcode.gh.maybeaclient.Client;
import net.skidcode.gh.maybeaclient.hacks.ClickGUIHack;
import net.skidcode.gh.maybeaclient.hacks.ClickGUIHack.Theme;

public class ExpandToggleButtonElement extends ToggleButtonElement{
	public static interface ExpandToggleButtonActionListener extends ToggleButtonElement.ToggleButtonActionListener{
		public void onExpand(Element caller, int startX, int startY, int endX, int endY, int mx, int my, int click, boolean expanded);
		public boolean hoveringOver(int x, int y);
	}
	
	public boolean expanded = false;
	public ExpandToggleButtonActionListener listener;
	
	public ExpandToggleButtonElement(ExpandToggleButtonActionListener listener) {
		super(listener);
		this.listener = listener;
	}
	
	@Override
	public boolean hoveringOver(int x, int y) {
		super.hoveringOver(x, y);
		return this.listener.hoveringOver(x, y);
	}
	
	@Override
	public void renderTop() {
		boolean v = this.listener.getValue();
		String s = this.listener.getDisplayString(v);
		int txtColor = 0xffffff;
		if(ClickGUIHack.theme() == Theme.IRIDIUM) {
			if(v) txtColor = Theme.IRIDIUM_ENABLED_COLOR;
			else  txtColor = Theme.IRIDIUM_DISABLED_COLOR;
			Client.mc.fontRenderer.drawStringWithShadow(s, startX + 1, startY + ClickGUIHack.theme().yaddtocenterText, txtColor);
			mouseHovering = false;
			return;
			
		}
		if(ClickGUIHack.theme() == Theme.HEPHAESTUS) {
			
			if(v) txtColor = 0xF3F3F3;
			else txtColor = Theme.HEPH_DISABLED_COLOR;
			
			Client.mc.fontRenderer.drawStringWithShadow(s, startX + 5, startY + ClickGUIHack.theme().yaddtocenterText, txtColor);
			String z = this.expanded ? "-" : "+";
			
			Client.mc.fontRenderer.drawStringWithShadow(z, endX - 5 - Client.mc.fontRenderer.getStringWidth(z), startY + ClickGUIHack.theme().yaddtocenterText, txtColor);
			mouseHovering = false;
			return;
		}
		
		if(ClickGUIHack.theme() == Theme.NODUS) {
			txtColor = ClickGUIHack.instance.themeColor.rgb();
			if(this.mouseHovering) {
				txtColor = ClickGUIHack.instance.secColor.rgb();
			}
		}
		Client.mc.fontRenderer.drawString(s, startX + 2, startY + ClickGUIHack.theme().yaddtocenterText, txtColor);
		mouseHovering = false;
	}
	
	@Override
	public int getWidth() {
		if(ClickGUIHack.theme() == Theme.HEPHAESTUS) {
			String s = this.listener.getDisplayString(this.listener.getValue());
			String z = this.expanded ? "-" : "+";
			
			return Client.mc.fontRenderer.getStringWidth(s+"  "+z) + 5;
		}
		return super.getWidth();
	}
	
	@Override
	public boolean onClick(int mx, int my, int click) {
		if(click == 1) {
			this.expanded = !this.expanded;
			this.listener.onExpand(this, startX, startY, endX, endY, mx, my, click, this.expanded);
		}
		return super.onClick(mx, my, click);
	}
}
