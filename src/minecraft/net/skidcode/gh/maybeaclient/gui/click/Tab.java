package net.skidcode.gh.maybeaclient.gui.click;

import java.awt.Cursor;
import java.util.ArrayList;
import org.lwjgl.opengl.GL11;
import net.minecraft.src.NBTBase;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.ScaledResolution;
import net.minecraft.src.Tessellator;
import net.skidcode.gh.maybeaclient.Client;
import net.skidcode.gh.maybeaclient.gui.click.element.ExpandToggleButtonElement;
import net.skidcode.gh.maybeaclient.gui.click.element.VerticalContainer;
import net.skidcode.gh.maybeaclient.gui.click.element.ExpandToggleButtonElement.ExpandToggleButtonActionListener;
import net.skidcode.gh.maybeaclient.gui.click.element.Element;
import net.skidcode.gh.maybeaclient.hacks.ClickGUIHack;
import net.skidcode.gh.maybeaclient.hacks.ClickGUIHack.ShowFrame;
import net.skidcode.gh.maybeaclient.hacks.ClickGUIHack.Theme;
import net.skidcode.gh.maybeaclient.hacks.settings.Setting;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingBoolean;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingEnum;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingTextBox;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingsProvider;
import net.skidcode.gh.maybeaclient.hacks.settings.enums.EnumAlign;
import net.skidcode.gh.maybeaclient.hacks.settings.enums.EnumExpand;
import net.skidcode.gh.maybeaclient.hacks.settings.enums.EnumStaticPos;
import net.skidcode.gh.maybeaclient.utils.GUIUtils;

public abstract class Tab extends Element implements SettingsProvider{
	abstract class Pin{
		public Tab tab;
		public boolean alignRight;
		public Pin(Tab tab) {
			this.tab = tab;
		}
		public abstract int getMinX();
		public abstract int getMinY();
		public abstract int getMaxX();
		public abstract int getMaxY();
	}
	static enum FrameMode{
		YES("Yes"),
		GRAY("Gray"),
		NO("No");

		public final String name;
		FrameMode(String name) {
			this.name = name;
		}
		
		@Override
		public String toString() {
			return this.name;
		}
	}
	
	static enum HeaderMode{
		YES("Yes"),
		NO("No");

		public final String name;
		HeaderMode(String name) {
			this.name = name;
		}
		
		@Override
		public String toString() {
			return this.name;
		}
	}
	
	public boolean isHUD;
	public int selectedMouseX = 0;
	public int selectedMouseY = 0;
	public boolean dragging = false;
	public boolean renderHeader = true;
	public boolean canMinimize = true;
	
	public SettingBoolean minimized = new SettingBoolean(this, "Minimize", false);
	public SettingTextBox name = new SettingTextBox(this, "Name", "Unnamed Tab", 100);
	
	//public SettingEnum<FrameMode> renderFrame_ = new SettingEnum<FrameMode>(this, "RenderFrame", FrameMode.YES);
	//public SettingEnum<HeaderMode> renderHeader_ = new SettingEnum<HeaderMode>(this, "RenderHeader", HeaderMode.YES);
	
	public boolean showninmanager = true;
	
	public ArrayList<Setting> settings = new ArrayList<Setting>();
	public int hiddens;
	@Override
	public ArrayList<Setting> getSettings(){
		return this.settings;
	}
	
	public void addSetting(Setting s) {
		this.settings.add(s);
		this.settingContainer.addElement(s.guielement);
	}
	
	@Override
	public void incrHiddens(int i) {
		hiddens += i;
	}
	
	public int getUsableWidth() {
		return this.getCachedWidth();
	}
	public int getMaxHeight() {
		return this.getCachedHeight();
	}
	
	public void renderTop() {
		
	}
	public void renderBottom() {
		this.render();
	}
	
	@Override
	public int getParentEndX() {
		return this.endX;
	}
	@Override
	public int getParentStartY() {
		return this.startY;
	}
	
	@Override
	public void recalculatePosition(Element parent, int x, int y) {
		this.preRender();
	}
	@Override
	public boolean onClick(int mx, int my, int click) {
		return this.onSelect(click, mx, my);
	}
	@Override
	public boolean hoveringOver(int x, int y) {
		return this.mouseHovered(x, y, -1);
	}
	@Override
	public boolean mouseMovedSelected(int x, int y) {
		return this.mouseMovedSelected(-1, x, y); //TODO key
	}
	@Override
	public void onDeselect(int x, int y) {
		this.onDeselect(-1, x, y); //TODO key
	}
	
	public String getTabName() {
		return this.name.value;
	}
	
	@Override
	public boolean isShown() {
		if(!this.shown) return false;
		return !minimized.getValue();
	}
	
	public int getStartX() {
		return this.startX;
	}
	public int getStartY() {
		return this.startY;
	}
	public int getEndX() {
		return this.endX;
	}
	public int getEndY() {
		return this.endY;
	}
	
	public Pin tabMinimize = new Pin(this) {
		@Override
		public int getMinX() {
			Theme t = ClickGUIHack.theme();
			if(t == Theme.IRIDIUM) return this.tab.endX-t.yspacing;
			if(this.alignRight) {
				return this.tab.startX + 2;
			}
			return this.tab.endX - 10 - 2;
		}

		@Override
		public int getMinY() {
			if(ClickGUIHack.theme() == Theme.IRIDIUM) return this.tab.startY;
			return this.tab.startY + (ClickGUIHack.theme().yspacing - 10) / 2;
		}

		@Override
		public int getMaxX() {
			if(ClickGUIHack.theme() == Theme.IRIDIUM) return this.tab.endX;
			if(this.alignRight) {
				return this.tab.startX + 2 + 10;
			}
			return this.tab.endX - 2;
		}

		@Override
		public int getMaxY() {
			Theme t = ClickGUIHack.theme();
			if(t == Theme.IRIDIUM) return this.tab.startY+t.yspacing;
			return this.tab.startY + (ClickGUIHack.theme().yspacing - 10) / 2 + 10;
		}
	};
	public int xDefPos = 0;
	public int yDefPos = 0;
	
	public Tab settingsTab;
	public VerticalContainer settingContainer = new VerticalContainer();
	public ExpandToggleButtonElement tabmanagerentry;
	public Tab(String name) {
		Tab t = this;
		this.name.setValue(name);
		this.tabmanagerentry = new ExpandToggleButtonElement(new ExpandToggleButtonActionListener() {

			@Override
			public String getDisplayString(boolean v) {
				return t.getTabName();
			}

			@Override
			public boolean getValue() {
				return t.shown;
			}

			@Override
			public void onPressed(int mx, int my, int click) {
				t.shown = !t.shown;
			}

			@Override
			public void onExpand(Element caller, int startX, int startY, int endX, int endY, int mx, int my, int click, boolean expanded) {
				if(t.settingsTab != null) {
					ClickGUI.removeTab(t.settingsTab);
					t.settingsTab = null;
				}else {
					ClickGUI.addTab(0, t.settingsTab = new SettingsTab(caller, t));
				}
			}

			@Override
			public boolean hoveringOver(int x, int y) {
				return false;
			}
		});

		this.addSetting(this.name);
		this.addSetting(this.minimized);
		//this.addSetting(this.renderFrame_);
		//this.addSetting(this.renderHeader_);
	}
	
	public VerticalContainer getSettingContainer() {
		return this.settingContainer;
	}
	
	public int getYOffset() {
		if(!renderHeader || !ClickGUIHack.renderHeader(this)) return 0;
		return ClickGUIHack.theme().yspacing + ClickGUIHack.theme().titlebasediff;
	}
	
	public Tab(String name, int width, int height) {
		this(name);
		this.endX = this.startX + width;
		this.endY = this.startY + height;
	}
	
	static final class ResizingMode{
		public static final int N = 1;
		public static final int S = 2;
		public static final int W = 4;
		public static final int E = 8;
	}
	
	boolean minimizePressed;
	int resizingMode = 0;
	
	
	public boolean onSelect(int click, int x, int y) {
		if(ClickGUIHack.theme() == Theme.NODUS || ClickGUIHack.theme() == Theme.HEPHAESTUS || ClickGUIHack.theme() == Theme.IRIDIUM) {
			boolean inside = GUIUtils.isInsideRect(x, y, 
				this.tabMinimize.getMinX()+1, 
				this.tabMinimize.getMinY()+1, 
				this.tabMinimize.getMaxX()-1, 
				this.tabMinimize.getMaxY()-1
			);
			if(inside && this.renderHeader) {
				if(!minimizePressed) {
					this.actMinimize();
					minimizePressed = true;
				}
				return true;
			}
		}
		
		int ydrag = ClickGUIHack.renderHeader(this) ? this.startY + ClickGUIHack.theme().yspacing : this.endY;
		
		if(y <= ydrag && this.renderHeader) {
			if(click == 0) {
				this.selectedMouseX = x;
				this.selectedMouseY = y;
				this.dragging = true;
				return true;
			}else if(click == 1) {
				if(!minimizePressed && ClickGUIHack.theme() == Theme.CLIFF) {
					this.actMinimize();
					minimizePressed = true;
				}
				return true;
			}
		}
		return false;
		
	}
	
	public boolean isAlignedRight(EnumStaticPos sp, EnumAlign al) {
		if(sp == EnumStaticPos.BOTTOM_RIGHT) return true;
		else if(sp == EnumStaticPos.BOTTOM_LEFT) return false;
		else if(sp == EnumStaticPos.TOP_RIGHT) return true;
		else if(sp == EnumStaticPos.TOP_LEFT) return false;
		else return al == EnumAlign.RIGHT;
	}
	
	public boolean setPosition(EnumStaticPos st, EnumAlign al) {
		return this.setPosition(st, al, EnumExpand.BOTTOM);
	}
	
	public EnumExpand expand;
	public EnumAlign align;
	public boolean setPosition(EnumStaticPos sp, EnumAlign al, EnumExpand e) {
		boolean alignRight = this.isAlignedRight(sp, al);
		this.align = alignRight ? EnumAlign.RIGHT : EnumAlign.LEFT;
		boolean expandTop = e == EnumExpand.TOP;
		ScaledResolution scaledResolution = new ScaledResolution(Client.mc.gameSettings, Client.mc.displayWidth, Client.mc.displayHeight);
		
		if(sp == EnumStaticPos.BOTTOM_RIGHT) {
			expandTop = true;
			this.startX = scaledResolution.getScaledWidth() - this.getCachedWidth();
			this.startY = scaledResolution.getScaledHeight() - this.getCachedHeight();
		}else if(sp == EnumStaticPos.BOTTOM_LEFT) {
			expandTop = true;
			this.startX = 0;
			this.startY = scaledResolution.getScaledHeight() - this.getCachedHeight();
		}else if(sp == EnumStaticPos.TOP_RIGHT) {
			expandTop = false;
			this.startX = scaledResolution.getScaledWidth() - this.getCachedWidth();
			this.startY = 0;
		}else if(sp == EnumStaticPos.TOP_LEFT) {
			expandTop = false;
			this.startX = 0;
			this.startY = 0;
		}
		this.tabMinimize.alignRight = alignRight;
		this.alignRight = alignRight;
		this.expand = expandTop ? EnumExpand.TOP : EnumExpand.BOTTOM;
		return expandTop;
	}
	
	public boolean hasSavedPos = false;
	
	public int svdHeight, svdWidth;

	public void preRender() {
		if(!hasSavedPos) {
			svdHeight = this.getCachedHeight();
			svdWidth = this.getCachedWidth();
			hasSavedPos = true;
		}else{
			boolean save = false;
			if(this.expand == EnumExpand.TOP && svdHeight != this.getCachedHeight()) {
				int diff = this.getCachedHeight() - svdHeight;
				this.startY -= diff;
				this.endY -= diff;
				
				if(this.startY < 0) {
					int d = this.startY;
					this.startY = 0;
					this.endY += -d;
				}
				
				svdHeight = this.getCachedHeight();
				save = true;
			}
			if(this.alignRight && svdWidth != this.getCachedWidth()) {
				int diff = this.getCachedWidth() - svdWidth;
				this.startX -= diff;
				this.endX -= diff;
				svdWidth = this.getCachedWidth();
				save = true;
			}
			if(save) Client.saveClickGUI();
		}
		
	}
	public void minimize() {
		this.endY = this.startY + ClickGUIHack.theme().yspacing;
	}
	
	public void maximize() {
		//this.height = this.heightPrev;
	}
	public void actMinimize() {
		this.minimized.setValue(!this.minimized.getValue());
		if(this.minimized.getValue()) this.minimize();
		else this.maximize();
	}
	public void onDeselect(int click, int x, int y) {
		this.minimizePressed = false;
		
		this.dragging = false;
		this.resizingMode = 0;
		Client.saveClickGUI();
	}
	
	public boolean mouseMovedSelected(int click, int x, int y) {
		if(this.dragging) {
			this.startX += x-this.selectedMouseX;
			this.startY += y-this.selectedMouseY;
			if(this.startX < 0) this.startX = 0;
			if(this.startY < 0) this.startY = 0;
			this.selectedMouseX = x;
			this.selectedMouseY = y;
			return true;
		}
		return false;
	}
	
	public boolean isPointInside(float x, float y) {
		return x >= this.startX && x <= (this.endX) && y >= this.startY && y <= (this.endY);
	}
	
	public static void renderFrameOutlines(double xStart, double yStart, double xEnd, double yEnd) {
		Tessellator tess = Tessellator.instance;
		if(ClickGUIHack.theme() == Theme.IRIDIUM) {
			GL11.glColor4f(63f/255f,  63f/255f, 63f/255f, 1);
			GL11.glLineWidth(1.0f);
			tess.startDrawing(GL11.GL_LINE_STRIP);
			tess.addVertex(xStart, yStart, 0);
			tess.addVertex(xEnd, yStart, 0);
			tess.addVertex(xEnd, yEnd, 0);
			tess.addVertex(xStart, yEnd, 0);
			tess.addVertex(xStart, yStart-0.5f, 0);
			tess.draw();
		}else if(ClickGUIHack.theme() == Theme.HEPHAESTUS) {
			renderFrameBackGround(xStart, yStart, xStart+0.5, yEnd, 0, 0, 0, 1f);
			renderFrameBackGround(xEnd-0.5, yStart, xEnd, yEnd, 0, 0, 0, 1f);
			renderFrameBackGround(xStart, yStart, xEnd, yStart+0.5, 0, 0, 0, 1f);
			renderFrameBackGround(xStart, yEnd-0.5, xEnd, yEnd, 0, 0, 0, 1f);
		}else {
			GL11.glColor4f(0, 0, 0, 0.9f);
			GL11.glLineWidth(2.5f);
			
			tess.startDrawingQuads();
			tess.addVertex(xStart-1, yStart, 0);
			tess.addVertex(xStart-1, yEnd, 0);
			tess.addVertex(xStart, yEnd, 0);
			tess.addVertex(xStart, yStart, 0);
			
			tess.addVertex(xStart, yEnd+1, 0);
			tess.addVertex(xEnd, yEnd+1, 0);
			tess.addVertex(xEnd, yEnd, 0);
			tess.addVertex(xStart, yEnd, 0);
			
			tess.addVertex(xEnd, yStart, 0);
			tess.addVertex(xEnd, yEnd, 0);
			tess.addVertex(xEnd+1, yEnd, 0);
			tess.addVertex(xEnd+1, yStart, 0);
			
			tess.addVertex(xStart, yStart, 0);
			tess.addVertex(xEnd, yStart, 0);
			tess.addVertex(xEnd, yStart-1, 0);
			tess.addVertex(xStart, yStart-1, 0);
			tess.draw();
		}
	}
	
	public static void renderFrameBackGround(int xStart, int yStart, int xEnd, int yEnd) {
		renderFrameBackGround(xStart, yStart, xEnd, yEnd, 0, 0, 0, 0.5f);
	}
	public static void renderFrameBackGround(double xStart, double yStart, double xEnd, double yEnd, float r, float g, float b, float a) {
		Tessellator tess = Tessellator.instance;
		GL11.glColor4f(r, g, b, a);
		tess.startDrawingQuads();
		tess.addVertex(xStart, yEnd, 0);
		tess.addVertex(xEnd, yEnd, 0);
		tess.addVertex(xEnd, yStart, 0);
		tess.addVertex(xStart, yStart, 0);
		tess.draw();
	}
	
	public static void renderGrayFrame(int xStart, int yStart, int xEnd, int yEnd) {
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glBlendFunc(770, 771);
		renderFrameBackGround(xStart, yStart, xEnd, yEnd, 0, 0, 0, 0x7f/255f);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
	}
	
	public static void renderFrame(Tab tab, int xStart, int yStart, int xEnd, int yEnd) {
		ShowFrame sf = ClickGUIHack.renderFrame(tab);
		if(sf != ShowFrame.YES && sf != ShowFrame.NOHEADER) {
			if(!Tab.renderingIngame || sf == ShowFrame.TRANSPARENT) renderGrayFrame(xStart, yStart, xEnd, yEnd);
			return;
		}
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glBlendFunc(770, 771);
		if(ClickGUIHack.theme() == Theme.CLIFF) {
			renderFrameBackGround(xStart, yStart, xEnd, yEnd);
		}else if(ClickGUIHack.theme() == Theme.NODUS) {
			renderFrameBackGround(xStart-2, yStart-2, xEnd+2, yEnd+2, 1, 1, 1, 0x20/255f);
			renderFrameBackGround(xStart, yStart, xEnd, yEnd, 0, 0, 0, 0x80/255f);
		}else if(ClickGUIHack.theme() == Theme.HEPHAESTUS) {
			renderFrameBackGround(xStart, yStart, xEnd, yEnd, 0, 0, 0, 100/255f);
		}else if(ClickGUIHack.theme() == Theme.IRIDIUM) {
			renderFrameBackGround(xStart, yStart, xEnd, yEnd, 0, 0, 0, 127/255f);
		}
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
	}

	public static void renderFrameTop(Tab tab, int xStart, int yStart, int xEnd, int yEnd) {
		ShowFrame sf = ClickGUIHack.renderFrame(tab);
		if(sf != ShowFrame.YES && sf != ShowFrame.NOHEADER) return;
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glBlendFunc(770, 771);
		if(ClickGUIHack.theme() == Theme.CLIFF) {
			renderFrameOutlines(xStart, yStart, xEnd, yEnd);
		}else if(ClickGUIHack.theme() == Theme.NODUS) {
			
		}else if(ClickGUIHack.theme() == Theme.HEPHAESTUS) {
			renderFrameOutlines(xStart, yStart, xEnd, yEnd);
		}else if(ClickGUIHack.theme() == Theme.IRIDIUM) {
			renderFrameOutlines(xStart, yStart, xEnd, yEnd);
		}
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
	}
	
	public static void renderFrameFull(Tab tab, int xStart, int yStart, int xEnd, int yEnd) {
		renderFrame(tab, xStart, yStart, xEnd, yEnd);
		renderFrameTop(tab, xStart, yStart, xEnd, yEnd);
	}
	
	public void renderNameBG() {
		int xStart = this.startX;
		int yStart = this.startY;
		if(!ClickGUIHack.renderHeader(this)) return;
		
		if(ClickGUIHack.theme() == Theme.CLIFF) {
			renderFrameFull(this, xStart, yStart, this.endX, yStart + ClickGUIHack.theme().yspacing);
		}else if(ClickGUIHack.theme() == Theme.NODUS) {
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			
			renderFrameBackGround(xStart, yStart, this.endX, yStart+ClickGUIHack.theme().yspacing, 0, 0, 0, 0x80/255f);
			renderFrameBackGround(xStart-2, yStart-2, this.endX+2, yStart+ClickGUIHack.theme().yspacing+2, 1, 1, 1, 0x20/255f);
			renderFrameBackGround(xStart, yStart, this.endX, yStart+ClickGUIHack.theme().yspacing, 0, 0, 0, 0x80/255f);
			
			//expand
			if(this.canMinimize) {
				renderFrameBackGround(this.tabMinimize.getMinX(), this.tabMinimize.getMinY(), this.tabMinimize.getMaxX(), this.tabMinimize.getMaxY(), 1, 1, 1, 0x40/255f);
				renderFrameBackGround(this.tabMinimize.getMinX()+1, this.tabMinimize.getMinY()+1, this.tabMinimize.getMaxX()-1, this.tabMinimize.getMaxY()-1, 0, 0, 0, this.minimized.getValue() ? (0xcc/255f) : (90/255f)); //XXX why 90?
			}
			
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_BLEND);
		}else if(ClickGUIHack.theme() == Theme.HEPHAESTUS) {
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			
			renderFrameBackGround(xStart, yStart, this.endX, yStart+ClickGUIHack.theme().yspacing, ClickGUIHack.r(), ClickGUIHack.g(), ClickGUIHack.b(), 1);
			renderFrameOutlines(xStart, yStart, this.endX, yStart+ClickGUIHack.theme().yspacing+0.5);
			
			
			if(this.canMinimize) {
				renderFrameBackGround(this.tabMinimize.getMinX(), this.tabMinimize.getMinY(), this.tabMinimize.getMaxX(), this.tabMinimize.getMaxY(), 0, 0, 0, (this.minimized.getValue() ? 50 : 150)/255f);
			}
			
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_BLEND);
		}else if(ClickGUIHack.theme() == Theme.IRIDIUM) {
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			renderFrameBackGround(xStart, yStart, this.endX, yStart+ClickGUIHack.theme().yspacing, ClickGUIHack.r(), ClickGUIHack.g(), ClickGUIHack.b(), 0xaa/255f);
			
			//expand
			if(this.canMinimize) {
				renderFrameBackGround(this.tabMinimize.getMinX(), this.tabMinimize.getMinY(), this.tabMinimize.getMaxX(), this.tabMinimize.getMaxY(), 33/255f, 215/255f, 198/255f, 1);
			}
			
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_BLEND);
		}
	}
	public void renderNameAt(int x, int y) {
		int xpos = x + ClickGUIHack.theme().headerXAdd;
		int ypos = y + ClickGUIHack.theme().yaddtocenterText;
		
		if(ClickGUIHack.theme() == Theme.CLIFF) {
			Client.mc.fontRenderer.drawString(this.getTabName(), xpos, ypos, 0xffffff);
		}else if(ClickGUIHack.theme() == Theme.NODUS) {
			Client.mc.fontRenderer.drawString(this.getTabName(), xpos, ypos, ClickGUIHack.instance.secColor.rgb());
		}else if(ClickGUIHack.theme() == Theme.HEPHAESTUS) {
			Client.mc.fontRenderer.drawStringWithShadow(this.getTabName(), xpos, ypos, 0xffffff);
		}else if(ClickGUIHack.theme() == Theme.IRIDIUM) {
			String name = this.getTabName();
			Client.mc.fontRenderer.drawStringWithShadow(name, xpos, ypos, 0xffffff);
		}
	}
	
	public void drawString(String s, int x, int y, int color) {
		if(ClickGUIHack.theme() == Theme.HEPHAESTUS) {
			Client.mc.fontRenderer.drawStringWithShadow(s, x, y, color);
		}else {
			Client.mc.fontRenderer.drawString(s, x, y, color);
		}
	}
	
	public void renderName(boolean alignRight) {
		if(!ClickGUIHack.renderHeader(this)) return;
		
		if(!this.renderHeader) return;
		if(ClickGUIHack.theme() == Theme.IRIDIUM) { //iridium has tab name in the center
			this.renderNameBG();
			int endX = this.canMinimize ? this.tabMinimize.getMinX() : this.endX;
			int w = endX - (this.startX + ClickGUIHack.theme().headerXAdd);
			this.renderNameAt(startX + (w -Client.mc.fontRenderer.getStringWidth(this.getTabName()))/2, this.startY);
		}else if(alignRight) {
			int yStart = this.startY;
			this.renderNameBG();
			this.renderNameAt(this.endX - Client.mc.fontRenderer.getStringWidth(this.getTabName()) - ClickGUIHack.theme().headerXAdd, yStart); //XXX - 2 is needed
		}else {
			this.renderNameBG();
			this.renderNameAt(this.startX, this.startY);
		}
		
	}
	
	public void renderMinimized() {
		this.renderName(this.align == EnumAlign.RIGHT);
	}
	
	public static boolean renderingIngame;
	public void renderIngame() {
		renderingIngame = true;
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		this.render();
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		renderingIngame = false;
	}
	
	public void render() {
		if(this.minimized.getValue()) this.renderMinimized();
		else this.renderName(this.align == EnumAlign.RIGHT);
	}
	
	public void readFromNBT(NBTTagCompound tag) {
		if(Client.convertingVersion > 0 && Client.convertingVersion < 4) {
			NBTTagCompound comp = tag.getCompoundTag("Position");
			this.startX = comp.getInteger("xPos");
			this.startY = comp.getInteger("yPos");
			this.minimized.setValue(comp.getBoolean("Minimized"));
			if(this.minimized.getValue()) this.minimize();
			else this.maximize();
		}else {
			this.startX = tag.getInteger("xPos");
			this.startY = tag.getInteger("yPos");
			NBTTagCompound settings = tag.getCompoundTag("Settings");
			for(Setting s : this.settings) {
				s.readFromNBT(settings);
			}
		}
		
	}
	public final void writeToNBT(NBTTagCompound tag) {
		tag.setInteger("xPos", this.startX);
		tag.setInteger("yPos", this.startY);
		NBTTagCompound settings = (NBTTagCompound) NBTBase.createTagOfType(NBTBase.COMPOUND);
		for(Setting s : this.settings) {
			s.writeToNBT(settings);
		}
		tag.setCompoundTag("Settings", settings);
		tag.setInteger("Priority", ClickGUI.tabs.indexOf(this));
	}
	public static final int SELSIZE = 1;
	public boolean mouseHovered(int x, int y, int click) {
		if(this.canMinimize) {
			int ysize = 10;
			boolean inside = GUIUtils.isInsideRect(x, y, 
				this.tabMinimize.getMinX()+1, 
				this.tabMinimize.getMinY()+1, 
				this.tabMinimize.getMaxX()-1, 
				this.tabMinimize.getMaxY()-1
			);
			if(ClickGUIHack.theme() == Theme.NODUS && inside && this.renderHeader) {
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glDisable(GL11.GL_TEXTURE_2D);
				GL11.glBlendFunc(770, 771);
				String s = this.minimized.getValue() ? "Expand" : "Minimize";
				int ssize = Client.mc.fontRenderer.getStringWidth(s) + 3;
				renderFrameBackGround(x, y - 12, x + ssize, y, 0, 0, 0, 0x90/255f);
				GL11.glEnable(GL11.GL_TEXTURE_2D);
				Client.mc.fontRenderer.drawString(s, x + 2, y - 10, 0xffffffff);
				GL11.glDisable(GL11.GL_BLEND);
				return true;
			}
		}
		
		if(ClickGUIHack.instance.manualResize.getValue()) {
			int xmin = this.startX;
			int xmax = this.endX;
			int ymin = this.startY;
			int ymax = this.endY;
			boolean inymin = y == ymin || (y > ymin && y-ymin <= SELSIZE);
			boolean inymax = y == ymax || (y < ymax && ymax-y <= SELSIZE);
			boolean inxmin = x == xmin || (x > xmin && x-xmin <= SELSIZE);
			boolean inxmax = x == xmax || (x < xmax && xmax-x <= SELSIZE);
			
			if(inymin) {
				if(inxmin) GUIUtils.setCursor(Cursor.NW_RESIZE_CURSOR);
				else if(inxmax) GUIUtils.setCursor(Cursor.NE_RESIZE_CURSOR);
				else  GUIUtils.setCursor(Cursor.N_RESIZE_CURSOR);
			}else if(inymax){
				if(inxmin) GUIUtils.setCursor(Cursor.SW_RESIZE_CURSOR);
				else if(inxmax) GUIUtils.setCursor(Cursor.SE_RESIZE_CURSOR);
				else GUIUtils.setCursor(Cursor.S_RESIZE_CURSOR);
			}else if(inxmin) GUIUtils.setCursor(Cursor.W_RESIZE_CURSOR);
			else if(inxmax) GUIUtils.setCursor(Cursor.E_RESIZE_CURSOR);
			else GUIUtils.setCursor(Cursor.DEFAULT_CURSOR);
		}
		return false;
	}

	public void stopHovering() {
		if(ClickGUIHack.instance.manualResize.getValue()) {
			GUIUtils.setCursor(Cursor.DEFAULT_CURSOR);
		}
	}

	public void wheelMoved(int wheel, int x, int y) {
	}
}
