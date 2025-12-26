package net.skidcode.gh.maybeaclient.gui.click;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import net.skidcode.gh.maybeaclient.Client;
import net.skidcode.gh.maybeaclient.gui.click.element.Element;
import net.skidcode.gh.maybeaclient.gui.click.element.ScrollBarElement;
import net.skidcode.gh.maybeaclient.gui.click.element.VerticalContainer;
import net.skidcode.gh.maybeaclient.hacks.ClickGUIHack;
import net.skidcode.gh.maybeaclient.hacks.ClickGUIHack.Theme;
import net.skidcode.gh.maybeaclient.utils.GUIUtils;

public abstract class ElementTab extends Tab{
	
	private ArrayList<Element> elements = new ArrayList<>();
	
	public ElementTab(String name) {
		super(name);
	}
	
	public void clearElements() {
		this.elements.clear();
	}
	public void addElement(Element e) {
		this.elements.add(e);
	}
	
	@Override
	public boolean mouseHovered(int x, int y, int click) {
		boolean b = super.mouseHovered(x, y, click);
		if(b) return b;
		
		for(Element e : this.elements) {
			if(!e.isShown()) continue;
			if(GUIUtils.isInsideRect(x, y, e.startX, e.startY, e.endX, e.endY) && e.hoveringOver(x, y)) return true;
		}
		return false;
	}
	
	@Override
	public boolean mouseMovedSelected(int click, int x, int y) {
		boolean b = super.mouseMovedSelected(click, x, y);
		if(b) return b;
		
		for(Element e : this.elements) {
			if(!e.isShown()) continue;
			if(e.mouseMovedSelected(x, y)) return true;
		}
		return false;
	}
	
	@Override
	public void onDeselect(int click, int x, int y) {
		super.onDeselect(click, x, y);
		
		for(Element e : this.elements) {
			if(!e.isShown()) continue;
			e.onDeselect(x, y);
		}
	}
	
	@Override
	public boolean onSelect(int click, int x, int y) {
		boolean b = super.onSelect(click, x, y);
		if(b) return b;
		
		for(Element e : this.elements) {
			if(!e.isShown()) continue;
			if(GUIUtils.isInsideRect(x, y, e.startX, e.startY, e.endX, e.endY) && e.onClick(x, y, click)) return true;
		}
		return false;
	}
	
	public int getTitleWidth() {
		return Client.mc.fontRenderer.getStringWidth(this.getTabName()) + ClickGUIHack.theme().titleXadd + 1;
	}
	
	@Override
	public void preRender() {
		int width = this.getTitleWidth();
		int height = this.getYOffset();
		
		int x = this.startX;
		for(Element e : this.elements) {
			e.recalculatePosition(this, x, this.startY + height);
			if(!e.isShown()) continue;
			int w = e.getCachedWidth();
			int h = e.getCachedHeight();
			if(w > width) width = w;
			height += h;
		}
		
		this.endX = this.startX + width;
		this.endY = this.startY + height;
		super.preRender();
	}

	public int getYPos() {
		return this.startY;
	}
	
	@Override
	public void render() {
		super.render();
		if(this.minimized.getValue()) return;
		
		int startY = this.getStartY() + this.getYOffset();
		int startX = this.getStartX();
		int endX = this.getEndX();
		int endY = this.getEndY();
		
		Tab.renderFrame(this, this.getStartX(), startY, this.getEndX(), this.getEndY());
		GUIUtils.enableScissorTest();
		GUIUtils.scissorStart(startX, startY, endX, endY);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glBlendFunc(770, 771);
		
		for(Element e : this.elements) {
			if(!e.isShown()) continue;
			e.renderBottom();
		}
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);

		for(Element e : this.elements) {
			if(!e.isShown()) continue;
			e.renderTop();
		}

		GUIUtils.scissorEnd();
		GUIUtils.disableScissorTest();
		Tab.renderFrameTop(this, this.getStartX(), startY, this.getEndX(), this.getEndY());
	}
}