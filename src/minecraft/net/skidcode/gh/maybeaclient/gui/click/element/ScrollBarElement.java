package net.skidcode.gh.maybeaclient.gui.click.element;

import net.minecraft.src.ScaledResolution;
import net.skidcode.gh.maybeaclient.Client;
import net.skidcode.gh.maybeaclient.gui.click.Tab;
import net.skidcode.gh.maybeaclient.hacks.ClickGUIHack;
import net.skidcode.gh.maybeaclient.hacks.ClickGUIHack.Theme;
import net.skidcode.gh.maybeaclient.utils.GUIUtils;

public class ScrollBarElement extends Element{
	
	public Element scrollable;
	
	public ScrollBarElement(Element s) {
		this.scrollable = s;
	}
	
	private boolean barVisible = false;
	
	@Override
	public void renderTop() {
		this.scrollable.renderTop();
	}
	
	public boolean selected = false;
	public int vScrollSelectY = -1;
	@Override
	public boolean onClick(int mx, int my, int click) {
		if(this.barVisible) {
			int scrollbarwidth = this.getScrollbarWidth();
			int sbXStart = endX - scrollbarwidth;
			if(GUIUtils.isInsideRect(mx, my, sbXStart, startY, endX, endY)) {
				
				float toRender = ((float)this.realHeight/100f)*(this.realHeight/(this.maxHeight/100f));
				int sbbYEnd = (int)(startY+toRender);
				if(GUIUtils.isInsideRect(mx, my, sbXStart, startY - (int)this.vScrollOffset, endX, sbbYEnd - (int)this.vScrollOffset)) {
					this.vScrollSelectY = my;
					selected = true;
				}else {
					int bst = startY - (int)this.vScrollOffset;
					int ben = sbbYEnd - (int)this.vScrollOffset;
					int mbef = bst + ((ben - bst) / 2);
					
					this.vScrollSelectY = my;
					selected = true;
					this.vScrollOffset += (mbef - my);
					if(this.vScrollOffset > 0) this.vScrollOffset = 0;
					int a = (int)(startY+toRender) - this.vScrollOffset;
					if(a > endY) this.vScrollOffset -= endY - a;
				}
				
			}
		}
		return this.scrollable.onClick(mx, my, click);
	}
	
	public boolean hoveringOver = false;
	@Override
	public boolean hoveringOver(int x, int y) {
		if(this.barVisible) {
			int scrollbarwidth = this.getScrollbarWidth();
			
			if(GUIUtils.isInsideRect(x, y, endX - scrollbarwidth, startY, endX, endY)) {
				hoveringOver = true;
				return true;
			}
		}
		return this.scrollable.hoveringOver(x, y);
	}
	@Override
	public boolean mouseMovedSelected(int x, int y) {
		if(this.barVisible) {
			if(this.selected) {
				int uwu = this.vScrollSelectY - y;
				int newOffset = (int)this.vScrollOffset + uwu;
				
				this.vScrollOffset = newOffset;
				this.vScrollSelectY = y;


				float toRender = ((float)this.realHeight/100f)*(this.realHeight/(this.maxHeight/100f));
				int a = (int)(startY+toRender) - this.vScrollOffset;
				if(a > endY) this.vScrollOffset -= endY - a;
				if(this.vScrollOffset > 0) this.vScrollOffset = 0;
				return true;
			}
		}
		return this.scrollable.mouseMovedSelected(x, y);
	}
	
	@Override
	public void onDeselect(int x, int y) {
		if(this.barVisible) {
			this.selected = false;
		}
		this.scrollable.onDeselect(x, y);
	}
	
	@Override
	public void renderBottom() {
		if(this.barVisible) {
			Theme theme = ClickGUIHack.theme();
			int scrollbarwidth = this.getScrollbarWidth();
			
			int total = this.maxHeight;
			int shown = this.realHeight;
			float toRender = (float)shown*((float)shown/(float)total);
			
			float r = 0, g = 0, b = 0, a = 0.5f, a2 = 0.25f;
			if(theme == Theme.NODUS) {
				int col = ClickGUIHack.highlightedTextColor();
				r = ((col >> 16) & 0xff) / 255f;
				g = ((col >> 8) & 0xff) / 255f;
				b = ((col >> 0) & 0xff) / 255f;
				if(hoveringOver) {
					a = 0.75f;
					a2 = 0.35f;
					hoveringOver = false;
				}
			}else if(theme == Theme.CLIFF || theme == Theme.HEPHAESTUS) {
				r = ClickGUIHack.r();
				g = ClickGUIHack.g();
				b = ClickGUIHack.b();
				if(theme == Theme.HEPHAESTUS) a = 0.75f;
			}
			Tab.renderFrameBackGround(endX - scrollbarwidth, startY, endX, endY, r, g, b, a2);
			Tab.renderFrameBackGround(endX - scrollbarwidth, startY - this.vScrollOffset, endX, (int)(startY+toRender) - this.vScrollOffset, r, g, b, a);
		}

		this.scrollable.renderBottom();
	}
	
	private int vScrollOffset = 0;
	private float vScrollCalc = 0;
	private int maxHeight = 0;
	private int realHeight = 0;
	
	public int getVScrollOffset() {
		float vScrollOffsetf = (this.vScrollOffset * ((float)this.maxHeight/(float)this.realHeight));
		int vScrollOffset = (int) vScrollOffsetf;
		return vScrollOffset;
	}
	
	public boolean barVisible(int x, int yp) {
		ScaledResolution sr = new ScaledResolution(Client.mc.gameSettings, Client.mc.displayWidth, Client.mc.displayHeight);
		int hei = this.scrollable.getCachedHeight();
		int sch = sr.getScaledHeight();
		if(yp+hei > sch) {
			this.maxHeight = hei;
			this.realHeight = hei - ((yp+hei) - sch);
			this.barVisible = true;
			return true;
		}else {
			this.maxHeight = this.realHeight = hei;
			this.barVisible = false;
			return false;
		}
	}

	public int getScrollbarWidth() {
		return ClickGUIHack.theme().scrollbarSize;
	}
	
	public float calcNonScaledVScrollOffset(float scaledResult) {
		int amax = this.maxHeight;
		int amin = this.endY-this.startY;
		float diff = ((float)amax/(float)amin);
		return scaledResult/diff;
	}
	
	@Override
	public boolean isInRenderBounds(Element e) {
		if(e.startY < this.startY && e.endY < this.startY) return false;
		return super.isInRenderBounds(e);
	}
	
	@Override
	public void recalculatePosition(Element e, int x, int y) {
		this.startX = x;
		this.startY = y;
		this.parent = e;
		this.alignRight = parent.alignRight;
		this.scrollable.recalculatePosition(this, x, y);
		
		int w = this.scrollable.getCachedWidth();
		if(this.barVisible(x, y)) {
			w += this.getScrollbarWidth();
			int voff = this.getVScrollOffset();
			if(this.vScrollCalc != voff) this.vScrollCalc = (int) this.calcNonScaledVScrollOffset(voff);
			
			float toRender = ((float)this.realHeight/100f)*(this.realHeight/(this.maxHeight/100f));
			int a = (int)(startY+toRender) - this.vScrollOffset;
			if(a > endY) this.vScrollOffset -= endY - a;
			if(this.vScrollOffset > 0) this.vScrollOffset = 0;
			
			int sty = y + this.getVScrollOffset();
			this.scrollable.recalculatePosition(this, x, sty);
		}
		
		this.endX = x+w;
		this.endY = y+realHeight;
	}

}
