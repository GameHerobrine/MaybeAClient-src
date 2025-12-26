package net.skidcode.gh.maybeaclient.gui.click;

import org.lwjgl.opengl.GL11;

import net.minecraft.src.ScaledResolution;
import net.minecraft.src.Tessellator;
import net.skidcode.gh.maybeaclient.Client;
import net.skidcode.gh.maybeaclient.hacks.ClickGUIHack;
import net.skidcode.gh.maybeaclient.hacks.Hack;
import net.skidcode.gh.maybeaclient.hacks.ClickGUIHack.Theme;
import net.skidcode.gh.maybeaclient.hacks.category.Category;
import net.skidcode.gh.maybeaclient.hacks.settings.Setting;
import net.skidcode.gh.maybeaclient.utils.GUIUtils;

public class CategoryTab extends Tab {
	public Category category;
	public boolean resize = true;
	public int maxHeight;
	public CategoryTab(Category category) {
		super(category.name);
		this.category = category;
		this.category.tab = this;
	}
	
	public CategoryTab(Category category, int x, int y, int width) {
		this(category);
		this.xPos = x;
		this.yPos = y;
		this.xDefPos = x;
		this.yDefPos = y;
		
		this.width = width;
	}
	boolean hoveringOverScrollbar = false;
	int hoveringOver = -1;
	long hoverStart = -1;
	int hoverX, hoverY;
	boolean onHoverRender = false;
	@Override
	public void stopHovering() {
		super.stopHovering();
		this.hoveringOver = -1;
		this.hoverStart = -1;
		this.onHoverRender = false;
		this.hoveringOverScrollbar = false;
	}
	public void renderIngame() {
		
	}
	
	public int getMaxHeight() {
		return this.maxHeight;
	}
	
	@Override
	public void mouseHovered(int x, int y, int click) {
		Theme theme = ClickGUIHack.theme();
		super.mouseHovered(x, y, click);
		if(!this.minimized && y > (this.yPos + ClickGUIHack.theme().yspacing + theme.titlebasediff)) {
			int ae = y - (this.yPos + theme.yspacing + theme.titlebasediff);
			int sel = ((int)ae - this.getVScrollOffset())/theme.yspacing;
			this.hoverX = x + 8;
			this.hoverY = y;
			if(hoveringOver == sel) {
				if(!ClickGUIHack.instance.showDescription.hidden && ClickGUIHack.instance.showDescription.getValue()) this.onHoverRender = true;
			}else {
				hoveringOver = sel;
				hoverStart = System.currentTimeMillis();
				this.onHoverRender = false;
			}
			if(this.category.hacks.size() <= this.hoveringOver) {
				this.onHoverRender = false;
			}
		}else {
			hoveringOver = -1;
			hoverStart = System.currentTimeMillis();
			this.onHoverRender = false;
		}
	}
	@Override
	public void mouseMovedSelected(int click, int x, int y) {
		if(this.isScrolling) {
			int uwu = this.vScrollSelectY - y;
			int newOffset = (int)this.vScrollOffset + uwu;
			
			this.vScrollOffset = newOffset;
			this.vScrollSelectY = y;
			return;
		}
		if(this.selected != null) {
			this.selected.onMouseMoved(this.selectedMinX, this.selectedMinY, this.selectedMaxX, this.selectedMaxY, x, y, click);
		}
		super.mouseMovedSelected(click, x, y);
	}
	
	public Setting selected;
	public int selectedMinX = 0, selectedMinY = 0, selectedMaxX = 0, selectedMaxY = 0;
	
	boolean canToggle = true;
	@Override
	public boolean onSelect(int click, int x, int y) {
		Theme theme = ClickGUIHack.theme();
		boolean pressed = super.onSelect(click, x, y);
		if(pressed) return pressed;
		int contentsYBegin = this.yPos + theme.yspacing + theme.titlebasediff;
		int vscrollOffset = this.getVScrollOffset();
		
		if(!this.minimized && y > contentsYBegin && this.canToggle) {
			int ae = y - (this.yPos + theme.yspacing + theme.titlebasediff);
			
			if(this.vScrollBarVisible) {
				int sbWidth = theme.scrollbarSize;
				int sbXEnd = this.xPos + this.width;
				int sbXStart = sbXEnd - sbWidth;
				int sbYStart = (this.yPos + theme.yspacing + theme.titlebasediff);
				int sbYEnd = this.yPos + this.height;
				if(GUIUtils.isInsideRect(x, y, sbXStart, sbYStart, sbXEnd, sbYEnd)) {
					int yStart = (int)this.yPos + theme.yspacing + theme.titlebasediff;
					int yEnd = (int)this.yPos + this.height;
					
					int percMax = yEnd - yStart;
					int max = this.getMaxHeight();
					int min = this.height;
					float perc = max/100f;
					float toRender = ((float)min/100f)*(percMax/perc);
					int sbbYEnd = (int)(yStart+toRender);
					if(GUIUtils.isInsideRect(x, y, sbXStart, yStart - (int)this.vScrollOffset, sbXEnd, sbbYEnd - (int)this.vScrollOffset)) {
						this.vScrollSelectY = y;
						this.isScrolling = true;
					}else {
						int bst = yStart - (int)this.vScrollOffset;
						int ben = sbbYEnd - (int)this.vScrollOffset;
						int mbef = bst + ((ben - bst) / 2);
						
						this.vScrollSelectY = y;
						this.isScrolling = true;
						this.vScrollOffset += (mbef - y);
					}
					
					return true;
				}
			}
			
			int sel = ((int)ae - this.getVScrollOffset())/theme.yspacing;
			if(theme.verticalSettings) {
				int yoff = contentsYBegin + vscrollOffset;
				uwu: {
					for(int i = 0; i < this.category.hacks.size(); ++i) {
						Hack h = this.category.hacks.get(i);
						int ymin = yoff;
						int ymax = ymin + theme.yspacing;
						if(GUIUtils.isInsideRect(x, y, this.xPos, ymin, this.xPos+this.width, ymax)) {
							sel = i;
							break uwu;
						}
						yoff = ymax;
						if(h.expanded) {
							yoff += h.getDescriptionHeight(this);
							int sx = this.xPos;
							int setBord = ClickGUIHack.theme().settingBorder;
							for(Setting s : h.getSettings()) {
								if(s.hidden) continue;
								int he = s.getSettingHeight(this);
								if(x >= sx && x <= (sx + this.width)) {
									if(y >= yoff && y <= (yoff + he)) {
										int uswid = this.getUsableWidth();
										s.onPressedInside(this, sx + setBord, yoff + setBord, sx + uswid - setBord, yoff + he - setBord, x, y, click);
										this.selected = s;
										this.selectedMinX = sx + setBord;
										this.selectedMinY = yoff + setBord;
										this.selectedMaxX = sx + uswid - setBord;
										this.selectedMaxY = yoff + he - setBord;
										
										return true;
									}
								}
								yoff += he;
							}
							//yoff += h.totalSettingHeight(this);
						}
					}
					
					return false;
				}
			}
			if(sel < this.category.hacks.size() && sel >= 0) {
				Hack hacc = this.category.hacks.get(sel);
				if(click == 0) hacc.toggle();
				else if(click == 1) {
					if(theme.verticalSettings) {
						hacc.expanded = !hacc.expanded;
					}else {
						hacc.expanded = !hacc.expanded;
						if(hacc.expanded) {
							hacc.tab = new SettingsTab(this, hacc, sel);
							ClickGUI.addTab(0, hacc.tab);
						}else {
							ClickGUI.removeTab(hacc.tab);
							hacc.tab = null;
						}
					}
					
					hacc.onExpandToggled();
					
				}
				this.canToggle = false;
				return true;
			}
		}
		
		
		return false;
	}
	
	public int getVScrollOffset() {
		int yStart = (int)this.yPos + ClickGUIHack.theme().yspacing + ClickGUIHack.theme().titlebasediff;
		int yEnd = (int)this.yPos + this.height;
		int amax = this.getMaxHeight() - ClickGUIHack.theme().yspacing - ClickGUIHack.theme().titlebasediff;
		int amin = yEnd-yStart;
		float vScrollOffsetf = (this.vScrollOffset * ((float)amax/(float)amin));
		int vScrollOffset = (int) vScrollOffsetf;
		return vScrollOffset;
	}
	
	public float calcNonScaledVScrollOffset(float scaledResult) {
		int yStart = (int)this.yPos + ClickGUIHack.theme().yspacing + ClickGUIHack.theme().titlebasediff;
		int yEnd = (int)this.yPos + this.height;
		int amax = this.getMaxHeight() - ClickGUIHack.theme().yspacing - ClickGUIHack.theme().titlebasediff;
		int amin = yEnd-yStart;
		float diff = ((float)amax/(float)amin);
		return scaledResult/diff;
	}
	
	public void onDeselect(int click, int x, int y) {
		this.canToggle = true;
		if(this.selected != null) {
			this.selected.onDeselect(this, this.selectedMinX, this.selectedMinY, this.selectedMaxX, this.selectedMaxY, x, y, click);
			this.selected = null;
			Client.saveModules();
		}
		super.onDeselect(click, x, y);
	}
	public void renderModules() {
		Tessellator tess = Tessellator.instance;
		boolean verticalSettings = ClickGUIHack.theme().verticalSettings;
		int xStart = (int)this.xPos;
		int yStart = (int)this.yPos + ClickGUIHack.theme().yspacing + ClickGUIHack.theme().titlebasediff;
		int xEnd = (int)this.xPos + this.width;
		int yEnd = (int)this.yPos + this.height;
		Theme theme = ClickGUIHack.theme();
		int vScrollOffset = (int) this.getVScrollOffset();
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		GL11.glEnable(GL11.GL_STENCIL_TEST);
		GL11.glStencilOp(GL11.GL_ZERO, GL11.GL_ZERO, GL11.GL_REPLACE);  
		GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
		GL11.glStencilFunc(GL11.GL_ALWAYS, Client.STENCIL_REF_ELDRAW, 0xFF);
		GL11.glStencilMask(0xFF);
		
		if(theme == Theme.CLIFF) {
			this.renderFrameBackGround(xStart, yStart, xEnd, yEnd);
		}else if(theme == Theme.NODUS) {
			GL11.glStencilOp(GL11.GL_ZERO, GL11.GL_ZERO, GL11.GL_KEEP);
			this.renderFrameBackGround(xStart-2, yStart-2, xEnd+2, yEnd+2, 1, 1, 1, 0x20/255f);
			GL11.glStencilOp(GL11.GL_ZERO, GL11.GL_ZERO, GL11.GL_REPLACE);
			this.renderFrameBackGround(xStart, yStart, xEnd, yEnd, 0, 0, 0, 0x80/255f);
		}else if(theme == Theme.HEPHAESTUS) {
			this.renderFrameBackGround(xStart, yStart, xEnd, yEnd, 0, 0, 0, 100/255f);
			GL11.glStencilOp(GL11.GL_ZERO, GL11.GL_ZERO, GL11.GL_ZERO);
			this.renderFrameOutlines(xStart, yStart, xEnd, yEnd);
		}
		
		GL11.glStencilOp(GL11.GL_ZERO, GL11.GL_ZERO, GL11.GL_KEEP);
		GL11.glStencilFunc(GL11.GL_EQUAL, Client.STENCIL_REF_ELDRAW, 0xFF);
		
		
		
		int spacing = theme.yspacing;
		int xEndOff = this.vScrollBarVisible ? theme.scrollbarSize : 0;
		int yOff = 0;
		for(int i = 0; i < category.hacks.size(); ++i) {
			Hack h = this.category.hacks.get(i);
			if(!ClickGUIHack.theme().verticalSettings && h.expanded && h.tab == null) {
				h.tab = new SettingsTab(this, h, i);
				ClickGUI.addTab(0, h.tab);
			}
			if(h.status) {
				if(theme == Theme.CLIFF) {
					this.renderFrameBackGround(xStart, yStart + vScrollOffset + yOff, xEnd - xEndOff, yStart + vScrollOffset + yOff + spacing, ClickGUIHack.r(), ClickGUIHack.g(), ClickGUIHack.b(), 1f);
				}else if(theme == Theme.NODUS && ClickGUIHack.instance.fillEnabled.value) {
					this.renderFrameBackGround(xStart, yStart + vScrollOffset + yOff, xEnd - xEndOff, yStart + vScrollOffset + yOff + spacing, 0, 0, 0, 0x80/255f);
				}
			}
			yOff += spacing;
			if(verticalSettings && h.expanded) yOff += h.totalSettingHeight(this);
		}
		
		GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
		GL11.glStencilFunc(GL11.GL_ALWAYS, Client.STENCIL_REF_ELDRAW, 0xFF);
		
		if(theme == Theme.CLIFF) {
			this.renderFrameOutlines(xStart, yStart, xEnd, yEnd);
		}
		
		GL11.glStencilOp(GL11.GL_ZERO, GL11.GL_ZERO, GL11.GL_KEEP);
		GL11.glStencilFunc(GL11.GL_EQUAL, Client.STENCIL_REF_ELDRAW, 0xFF);
		//GL11.glStencilFunc(GL11.GL_ALWAYS, Client.STENCIL_REF_ELDRAW, 0xFF);
		boolean hoveringOverScrollBar = false;
		if(this.vScrollBarVisible) {
			int sbWidth = theme.scrollbarSize;
			int sbXEnd = this.xPos + this.width;
			int sbXStart = sbXEnd - sbWidth;
			
			int total = this.getMaxHeight() - ClickGUIHack.theme().yspacing - ClickGUIHack.theme().titlebasediff;
			int shown = yEnd-yStart;
			float toRender = (float)shown*((float)shown/(float)total);
			
			float r = 0, g = 0, b = 0, a = 0.5f, a2 = 0.25f;
			if(theme == Theme.NODUS) {
				int col = ClickGUIHack.highlightedTextColor();
				r = ((col >> 16) & 0xff) / 255f;
				g = ((col >> 8) & 0xff) / 255f;
				b = ((col >> 0) & 0xff) / 255f;
				if(this.hoveringOver != -1 && GUIUtils.isInsideRect(this.hoverX, this.hoverY, sbXStart, yStart, sbXEnd, yEnd)) {
					a = 0.75f;
					a2 = 0.35f;
					hoveringOverScrollBar = true;
				}
			}else if(theme == Theme.CLIFF || theme == Theme.HEPHAESTUS) {
				r = ClickGUIHack.r();
				g = ClickGUIHack.g();
				b = ClickGUIHack.b();
				if(theme == Theme.HEPHAESTUS) a = 0.75f;
			}
			this.renderFrameBackGround(sbXStart, yStart, sbXEnd, yEnd, r, g, b, a2);
			this.renderFrameBackGround(sbXStart, yStart - this.vScrollOffset, sbXEnd, (int)(yStart+toRender) - this.vScrollOffset, r, g, b, a);
		}
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
		yOff = 0;
		for(int i = 0; i < category.hacks.size(); ++i) {
			Hack h = this.category.hacks.get(i);
			int txtColor = 0xffffff;
			if(theme == Theme.NODUS) {
				txtColor = ClickGUIHack.instance.themeColor.rgb();
				if(hoveringOver == i && !hoveringOverScrollBar) {
					txtColor = ClickGUIHack.instance.secColor.rgb();
				}
			}
			if(theme == Theme.CLIFF || theme == Theme.NODUS) {
				Client.mc.fontRenderer.drawString(h.name, (int)xStart + 2, (int)yStart + vScrollOffset + theme.yaddtocenterText + yOff , txtColor);
			}else if(theme == Theme.HEPHAESTUS) {
				boolean expanded = h.expanded;
				
				if(this.category.hacks.get(i).status) txtColor = 0xF3F3F3;
				else txtColor = Theme.HEPH_DISABLED_COLOR;
				
				String exp = expanded ? "-" : "+";
				Client.mc.fontRenderer.drawStringWithShadow(h.name, (int)xStart + 5, (int)yStart + vScrollOffset + theme.yaddtocenterText + yOff , txtColor);
				Client.mc.fontRenderer.drawStringWithShadow(exp, (int)xEnd - 5 - Client.mc.fontRenderer.getStringWidth(exp), (int)yStart + vScrollOffset + theme.yaddtocenterText + yOff, txtColor);
			}
			yOff += spacing;
			if(verticalSettings && h.expanded) yOff += h.totalSettingHeight(this);
		}
		
		this.renderSettings();
		
		GL11.glDisable(GL11.GL_STENCIL_TEST);
	}
	@Override
	public void renderNameAt(int x, int y) {
		super.renderNameAt(x, y);
		if(ClickGUIHack.theme() == Theme.NODUS) {
			x = x + Client.mc.fontRenderer.getStringWidth(this.name);
			Client.mc.fontRenderer.drawString(" ("+this.category.hacks.size()+")", x + ClickGUIHack.theme().headerXAdd, y + ClickGUIHack.theme().yaddtocenterText, ClickGUIHack.instance.themeColor.rgb());
		}
	}
	
	public boolean initialSizeSet = false;
	public void preRender() {
		Theme theme = ClickGUIHack.theme();
		float prevCalculatedVScrollOffset = this.getVScrollOffset();
		if(ClickGUIHack.instance.manualResize.getValue()) {
			if(!this.initialSizeSet) {
				this.initialSizeSet = true;
				this.height = category.hacks.size()*theme.yspacing + theme.yspacing + theme.titlebasediff;
			}
			this.vScrollBarVisible = category.hacks.size()*theme.yspacing + theme.yspacing + theme.titlebasediff > this.height;
		}else {
			String toRender = this.name+" ("+this.category.hacks.size()+")";
			int wd = Math.max(90, Client.mc.fontRenderer.getStringWidth(toRender) + theme.titleXadd);
			
			this.width = wd;
			for(int i = 0; i < category.hacks.size(); ++i) {
				Hack h = this.category.hacks.get(i);
				int width;
				if(theme == Theme.HEPHAESTUS) {
					width = Client.mc.fontRenderer.getStringWidth(h.name) + 10 + Client.mc.fontRenderer.getStringWidth(" +");
					
					for(int j = 0; h.expanded && j < h.getSettings().size(); ++j) {
						Setting s = h.getSettings().get(j);
						if(s.hidden) continue;
						int sw = s.getSettingWidth();
						if(sw > width) width = sw;
					}
				}else {
					width = Client.mc.fontRenderer.getStringWidth(h.name) + 2;
				}
				
				if(width > this.width) this.width = width;
			}
			for(int i = 0; i < category.hacks.size(); ++i) {
				Hack h = this.category.hacks.get(i);
				int[] wh = Client.mc.fontRenderer.getSplittedStringWidthAndHeight_h(h.description, this.width - Theme.HEPH_OPT_XADD, Theme.HEPH_DESC_YADD);
				if(this.width < wh[0]) this.width = wh[0];
			}
			this.resize = false;
			
			
			
			if(theme.verticalSettings) {
				this.height = theme.yspacing + theme.titlebasediff;
				for(int i = 0; i < category.hacks.size(); ++i) {
					Hack h = category.hacks.get(i);
					this.height += theme.yspacing;
					if(h.expanded) this.height += h.totalSettingHeight(this);
				}
			}else {
				this.height = category.hacks.size()*theme.yspacing + theme.yspacing + theme.titlebasediff;
			}
			if(ClickGUIHack.theme() == Theme.HEPHAESTUS) this.height += 3;	
			
			ScaledResolution sr = new ScaledResolution(Client.mc.gameSettings, Client.mc.displayWidth, Client.mc.displayHeight);
			if(this.yPos + this.height > sr.getScaledHeight()) {
				this.maxHeight = this.height;
				this.height -= (this.yPos + this.height) - sr.getScaledHeight();
				
				this.vScrollBarVisible = true;
				this.width += ClickGUIHack.theme().scrollbarSize;
			}else {
				this.maxHeight = this.height;
				this.vScrollBarVisible = false;
			}
			
		}		
		
		int yStart = (int)this.yPos + ClickGUIHack.theme().yspacing + ClickGUIHack.theme().titlebasediff;
		int yEnd = (int)this.yPos + this.height;
		int max = this.getMaxHeight() - ClickGUIHack.theme().yspacing - ClickGUIHack.theme().titlebasediff;
		int min = yEnd-yStart;
		int total = max;
		int shown = min;
		float toRender = (float)shown*((float)shown/(float)total);
		int ae = yEnd - ((int)(yStart+toRender) - (int)this.vScrollOffset);
		if(this.getVScrollOffset() != prevCalculatedVScrollOffset) {
			this.vScrollOffset = calcNonScaledVScrollOffset(prevCalculatedVScrollOffset);
		}
		
		if((int)(yStart+toRender) - this.vScrollOffset > yEnd) this.vScrollOffset -= ae;
		if(this.vScrollOffset > 0) this.vScrollOffset = 0;
	}
	
	public void renderSettings() {
		Theme theme = ClickGUIHack.theme();
		if(!theme.verticalSettings) return;
		int vscrollOffset = this.getVScrollOffset();
		int yoff = this.yPos + theme.yspacing + theme.titlebasediff + vscrollOffset;
		int settingsWidth = this.width;
		int xStart = this.xPos;
		if(this.vScrollBarVisible) settingsWidth -= theme.scrollbarSize;
		int setBord = ClickGUIHack.theme().settingBorder;
		
		
		for(Hack settingProvider : this.category.hacks) {
			int settingsHeight = 0;
			yoff += theme.yspacing;
			if(!settingProvider.expanded) {
				continue;
			}
			int[] wh = new int[] {0, 0};
			if(theme == Theme.HEPHAESTUS) {
				wh = Client.mc.fontRenderer.getSplittedStringWidthAndHeight_h(settingProvider.description, this.getUsableWidth() - Theme.HEPH_OPT_XADD, Theme.HEPH_DESC_YADD);
			}
			settingsHeight = settingProvider.totalSettingHeight(this);
			
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(770, 771);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			int height = (int)yoff;
			height += wh[1];
			if(theme == Theme.HEPHAESTUS) height += 3;
			
			for(Setting set : settingProvider.getSettings()) {
				if(set.hidden) continue;
				int sHeight = set.getSettingHeight(this);
				set.renderElement(this, (int)xStart + setBord, height + setBord, (int)xStart + settingsWidth - setBord, height + sHeight - setBord);
				height += sHeight;
			}
			if(theme == Theme.HEPHAESTUS) {
				this.renderFrameBackGround(xStart + 2 + setBord, yoff, xStart + 3 + setBord, height, 1, 1, 1, 1);
			}
			
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_BLEND);
			
			height = (int)yoff;
			if(theme == Theme.HEPHAESTUS) {
				Client.mc.fontRenderer.drawSplittedStringWithShadow_h(settingProvider.description, xStart + Theme.HEPH_OPT_XADD, height + ClickGUIHack.theme().yaddtocenterText, 0xffffff, this.getUsableWidth() - Theme.HEPH_OPT_XADD, Theme.HEPH_DESC_YADD);
				height += 3;
			}
			
			height += wh[1];
			for(Setting set : settingProvider.getSettings()) {
				if(set.hidden) continue;
				int sHeight = set.getSettingHeight(this);
				set.renderText(this, (int)xStart + setBord, height + setBord, (int)xStart + settingsWidth - setBord, height + sHeight - setBord);
				height += sHeight;
			}
			
			yoff += settingsHeight;
		}
	}
	
	public void render() {
		if(this.minimized) {
			this.renderMinimized();
			return;
		}
		
		super.render();
		this.renderModules();
		
		if(this.onHoverRender) {
			int xMin = this.hoverX;
			int yMin = this.hoverY;
			ClickGUI.showDescription(xMin, yMin, this.category.hacks.get(this.hoveringOver), this);
		}
	}
}
