package net.skidcode.gh.maybeaclient.gui.click.element;

import java.util.ArrayList;
import java.util.HashMap;

import net.skidcode.gh.maybeaclient.gui.click.Tab;
import net.skidcode.gh.maybeaclient.hacks.ClickGUIHack;
import net.skidcode.gh.maybeaclient.hacks.settings.enums.EnumAlign;
import net.skidcode.gh.maybeaclient.utils.GUIUtils;

public class VerticalContainer extends Element{
	public ArrayList<Element> elements = new ArrayList<>();
	public ArrayList<Element> queueToAdd = new ArrayList<>();
	public ArrayList<Element> queueToRemove = new ArrayList<>();
	
	public VerticalContainer addElement(Element e) {
		this.queueToAdd.add(e);
		return this;
	}
	public void removeElement(Element e) {
		this.queueToRemove.add(e);
	}
	@Override
	public void renderTop() {
		for(Element e : this.elements) {
			if(!e.isShown()) continue;
			e.renderTop();
		}
	}
	
	@Override
	public boolean onClick(int mx, int my, int click) {
		for(Element e : this.elements) {
			if(!e.isShown()) continue;
			if(GUIUtils.isInsideRect(mx, my, e.startX, e.startY, e.endX, e.endY) && e.onClick(mx, my, click)) return true;
		}
		return false;
	}
	
	@Override
	public void onDeselect(int x, int y) {
		for(Element e : this.elements) {
			if(!e.isShown()) continue;
			e.onDeselect(x, y);
		}
	}
	
	@Override
	public boolean hoveringOver(int x, int y) {
		for(Element e : this.elements) {
			if(!e.isShown()) continue;
			if(GUIUtils.isInsideRect(x, y, e.startX, e.startY, e.endX, e.endY) && e.hoveringOver(x, y)) return true;
		}
		return false;
	}
	@Override
	public boolean mouseMovedSelected(int x, int y) {
		for(Element e : this.elements) {
			if(!e.isShown()) continue;
			if(e.mouseMovedSelected(x, y)) return true;
		}
		return false;
	}
	
	@Override
	public void renderBottom() {
		for(Element e : this.elements) {
			if(!e.isShown()) continue;
			e.renderBottom();
		}
	}
	
	@Override
	public void overrideWidth(int width) {
		this.endX = this.startX + width;
		for(Element e : this.elements) {
			if(!e.isShown()) continue;
			e.overrideWidth(width);
		}
	}
	
	
	@Override
	public void recalculatePosition(Element parent, int x, int y) {
		this.startX = x;
		this.startY = y;
		this.parent = parent;
		this.alignRight = parent.alignRight;
		
		for(Element e : this.queueToRemove) this.elements.remove(e);
		this.queueToRemove.clear();
		for(Element e : this.queueToAdd) this.elements.add(e);
		this.queueToAdd.clear();
		
		int width = 0, height = 0;
		for(Element e : this.elements) {
			e.recalculatePosition(this, x, y);
			if(!e.isShown()) continue;
			int w = e.getCachedWidth();
			if(w > width) width = w;
			int h = e.getCachedHeight();
			height += h;
			y += h;
		}
		
		for(Element e : this.elements) {
			e.overrideWidth(width);
		}
		
		this.endX = this.startX + width;
		this.endY = this.startY + height;
	}

}
