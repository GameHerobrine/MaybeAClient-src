package net.skidcode.gh.maybeaclient.gui.click.element;

import net.skidcode.gh.maybeaclient.Client;
import net.skidcode.gh.maybeaclient.gui.click.Tab;
import net.skidcode.gh.maybeaclient.hacks.ClickGUIHack;

public class TextElement extends Element{

	public String text;
	
	public TextElement(String text) {
		this.text = text;
	}

	@Override
	public void renderTop() {
		int rendX = startX + 2;
		int rendY = startY + 2;
		if(this.alignRight) {
			rendX = endX - Client.mc.fontRenderer.getStringWidth(this.text) - 2;
		}
		Client.mc.fontRenderer.drawString(this.text, rendX, rendY, ClickGUIHack.normTextColor());
	}

	@Override
	public void renderBottom() {
		
	}

	@Override
	public void recalculatePosition(Element element, int x, int y) {
		this.startX = x;
		this.startY = y;
		this.parent = element;
		this.alignRight = parent.alignRight;
		
		this.endX = x + Client.mc.fontRenderer.getStringWidth(this.text) + ClickGUIHack.theme().titleXadd;
		this.endY = y + ClickGUIHack.theme().yspacing;
	}
	
}
