package net.skidcode.gh.maybeaclient.gui.click.element;

public abstract class Element{
	public Element parent;
	public int startX, startY, endX, endY;
	public boolean shown = true;
	public boolean alignRight = false;
	
	public abstract void recalculatePosition(Element parent, int x, int y);
	public abstract void renderTop();
	public abstract void renderBottom();
	
	public boolean onClick(int mx, int my, int click) {
		return false;
	}
	public boolean hoveringOver(int x, int y) {
		return false;
	}
	public boolean mouseMovedSelected(int x, int y) {
		return false;
	}
	
	public boolean wheelMoved(int x, int y, int wheel) {
		return false;
	}
	
	public void onDeselect(int x, int y) {
		
	}
	public void overrideWidth(int width) {
		this.endX = this.startX + width;
	}
	public int getCachedWidth() {
		return this.endX - this.startX;
	}
	public int getCachedHeight() {
		return this.endY - this.startY;
	}
	
	public boolean isInRenderBounds(Element e) {
		if(this.parent != null) return this.parent.isInRenderBounds(e);
		return true;
	}
	
	public int getParentStartY() {
		if(this.parent != null) return this.parent.getParentStartY();
		return this.startY;
	}
	public int getParentEndX() {
		if(this.parent != null) return this.parent.getParentEndX();
		return this.endX;
	}
	
	public boolean isShown() {
		if(this.parent == null) return this.shown;
		return this.shown && this.parent.isShown();
	}
}
