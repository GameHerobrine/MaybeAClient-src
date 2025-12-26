package net.skidcode.gh.maybeaclient.gui.click;

import java.awt.Cursor;

import org.lwjgl.opengl.GL11;

import net.minecraft.src.NBTBase;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.ScaledResolution;
import net.minecraft.src.Tessellator;
import net.skidcode.gh.maybeaclient.Client;
import net.skidcode.gh.maybeaclient.hacks.ClickGUIHack;
import net.skidcode.gh.maybeaclient.hacks.ClientInfoHack;
import net.skidcode.gh.maybeaclient.hacks.ClickGUIHack.Theme;
import net.skidcode.gh.maybeaclient.hacks.settings.enums.EnumAlign;
import net.skidcode.gh.maybeaclient.hacks.settings.enums.EnumExpand;
import net.skidcode.gh.maybeaclient.hacks.settings.enums.EnumStaticPos;
import net.skidcode.gh.maybeaclient.utils.GUIUtils;

public abstract class Tab {
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
	public String name;
	public int width = 0;
	public int height = 0;
	public int xPos = 0;
	public int yPos = 0;
	public int heightPrev = 12;
	public int selectedMouseX = 0;
	public int selectedMouseY = 0;
	public boolean dragging = false;
	public boolean waitsForInput = false;
	public boolean canMinimize = true;
	public boolean minimized = false;
	public boolean shown = true;
	public boolean vScrollBarVisible = false;
	public boolean isScrolling = false;
	public int vScrollSelectY = -1;
	public float vScrollOffset = 0;
	
	public int getVScrollOffset() {
		return (int)this.vScrollOffset;
	}
	public int getUsableWidth() {
		return this.width - (this.vScrollBarVisible ? ClickGUIHack.theme().scrollbarSize : 0);
	}
	public int getMaxHeight() {
		return this.height;
	}
	
	public Pin tabMinimize = new Pin(this) {
		@Override
		public int getMinX() {
			if(this.alignRight) {
				return this.tab.xPos + 2;
			}
			return this.tab.xPos + this.tab.width - 10 - 2;
		}

		@Override
		public int getMinY() {
			return this.tab.yPos + (ClickGUIHack.theme().yspacing - 10) / 2;
		}

		@Override
		public int getMaxX() {
			if(this.alignRight) {
				return this.tab.xPos + 2 + 10;
			}
			return this.tab.xPos + this.tab.width - 2;
		}

		@Override
		public int getMaxY() {
			return this.tab.yPos + (ClickGUIHack.theme().yspacing - 10) / 2 + 10;
		}
	};
	public int xDefPos = 0;
	public int yDefPos = 0;
	
	
	public Tab(String name) {
		this.name = name;
	}
	
	public Tab(String name, int width, int height) {
		this(name);
		this.width = width;
		this.height = height;
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
		if(ClickGUIHack.theme() == Theme.NODUS || ClickGUIHack.theme() == Theme.HEPHAESTUS) {
			boolean inside = GUIUtils.isInsideRect(x, y, 
				this.tabMinimize.getMinX()+1, 
				this.tabMinimize.getMinY()+1, 
				this.tabMinimize.getMaxX()-1, 
				this.tabMinimize.getMaxY()-1
			);
			if(inside) {
				if(!minimizePressed) {
					this.actMinimize();
					minimizePressed = true;
				}
				return true;
			}
		}
		
		if(ClickGUIHack.instance.manualResize.getValue()) {
			int xmin = this.xPos;
			int xmax = this.xPos+this.width;
			int ymin = this.yPos;
			int ymax = this.yPos+this.height;
			boolean inymin = y == ymin || (y > ymin && y-ymin <= SELSIZE);
			boolean inymax = y == ymax || (y < ymax && ymax-y <= SELSIZE);
			boolean inxmin = x == xmin || (x > xmin && x-xmin <= SELSIZE);
			boolean inxmax = x == xmax || (x < xmax && xmax-x <= SELSIZE);
			
			if(inymin) {
				if(inxmin) this.resizingMode = ResizingMode.N | ResizingMode.W;
				else if(inxmax) this.resizingMode = ResizingMode.N | ResizingMode.E;
				else this.resizingMode = ResizingMode.N;
			}else if(inymax){
				if(inxmin) this.resizingMode = ResizingMode.S | ResizingMode.W;
				else if(inxmax) this.resizingMode = ResizingMode.S | ResizingMode.E;
				else this.resizingMode = ResizingMode.S;
			}else if(inxmin) this.resizingMode = ResizingMode.W;
			else if(inxmax) this.resizingMode = ResizingMode.E;
			else this.resizingMode = 0;
			this.dragging &= this.resizingMode == 0;
			
			if(this.resizingMode != 0) return true;
		}
		
		if(click == 0 && y <= this.yPos + ClickGUIHack.theme().yspacing) {
			this.selectedMouseX = x;
			this.selectedMouseY = y;
			this.dragging = true;
			return true;
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
	public boolean setPosition(EnumStaticPos sp, EnumAlign al, EnumExpand e) {
		boolean alignRight = this.isAlignedRight(sp, al);
		boolean expandTop = e == EnumExpand.TOP;
		ScaledResolution scaledResolution = new ScaledResolution(Client.mc.gameSettings, Client.mc.displayWidth, Client.mc.displayHeight);
		
		if(sp == EnumStaticPos.BOTTOM_RIGHT) {
			expandTop = true;
			this.xPos = scaledResolution.getScaledWidth() - this.width;
			this.yPos = scaledResolution.getScaledHeight() - this.height;
		}else if(sp == EnumStaticPos.BOTTOM_LEFT) {
			expandTop = true;
			this.xPos = 0;
			this.yPos = scaledResolution.getScaledHeight() - this.height;
		}else if(sp == EnumStaticPos.TOP_RIGHT) {
			expandTop = false;
			this.xPos = scaledResolution.getScaledWidth() - this.width;
			this.yPos = 0;
		}else if(sp == EnumStaticPos.TOP_LEFT) {
			expandTop = false;
			this.xPos = 0;
			this.yPos = 0;
		}
		this.tabMinimize.alignRight = alignRight;
		
		return expandTop;
	}
	
	public void preRender() {
		
	}
	public void minimize() {
		this.heightPrev = this.height;
		this.height = ClickGUIHack.theme().yspacing;
	}
	
	public void maximize() {
		this.height = this.heightPrev;
	}
	public void actMinimize() {
		this.minimized = !this.minimized;
		if(this.minimized) this.minimize();
		else this.maximize();
	}
	public void onDeselect(int click, int x, int y) {
		this.minimizePressed = false;
		if(click == 1 && y <= this.yPos + ClickGUIHack.theme().yspacing) {
			if(ClickGUIHack.theme() == Theme.CLIFF) {
				this.actMinimize();
			}
		}
		
		this.dragging = false;
		this.resizingMode = 0;
		this.isScrolling = false;
		this.vScrollSelectY = -1;
		Client.saveClickGUI();
	}
	
	public void mouseMovedSelected(int click, int x, int y) {
		if(this.dragging) {
			this.xPos += x-this.selectedMouseX;
			this.yPos += y-this.selectedMouseY;
			if(this.xPos < 0) this.xPos = 0;
			if(this.yPos < 0) this.yPos = 0;
			this.selectedMouseX = x;
			this.selectedMouseY = y;
		}
		
		if(ClickGUIHack.instance.manualResize.getValue()) {
			int yy = y;
			int xx = x;
			if((this.resizingMode & ResizingMode.N) > 0) {
				int maxy = (this.yPos + this.height);
				
				if(maxy - yy > ClickGUIHack.theme().yspacing*2+ClickGUIHack.theme().titlebasediff) {
					this.yPos = yy;
					this.height = maxy - this.yPos;
				}
			}
			if((this.resizingMode & ResizingMode.S) > 0) {
				if(yy - this.yPos > ClickGUIHack.theme().yspacing*2+ClickGUIHack.theme().titlebasediff) {
					this.height = yy - this.yPos;
				}
			}
			
			if((this.resizingMode & ResizingMode.W) > 0) {
				int maxx = (this.xPos + this.width);
				if(maxx - xx > 0) {
					this.xPos = xx;
					this.width = maxx - this.xPos;
				}
			}
			if((this.resizingMode & ResizingMode.E) > 0) {
				if(xx - this.xPos > 0) {
					this.width = xx - this.xPos;
				}
			}
		}
	}
	
	public boolean isPointInside(float x, float y) {
		return x >= this.xPos && x <= (this.xPos + this.width) && y >= this.yPos && y <= (this.yPos + this.height);
	}
	
	public void renderFrameOutlines(double xStart, double yStart, double xEnd, double yEnd) {
		if(ClickGUIHack.theme() == Theme.HEPHAESTUS) {
			this.renderFrameBackGround(xStart, yStart, xStart+0.5, yEnd, 0, 0, 0, 1f);
			this.renderFrameBackGround(xEnd-0.5, yStart, xEnd, yEnd, 0, 0, 0, 1f);
			this.renderFrameBackGround(xStart, yStart, xEnd, yStart+0.5, 0, 0, 0, 1f);
			this.renderFrameBackGround(xStart, yEnd-0.5, xEnd, yEnd, 0, 0, 0, 1f);
		}else {
			Tessellator tess = Tessellator.instance;
			
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
	
	public void renderFrameBackGround(int xStart, int yStart, int xEnd, int yEnd) {
		this.renderFrameBackGround(xStart, yStart, xEnd, yEnd, 0, 0, 0, 0.5f);
	}
	public void renderFrameBackGround(double xStart, double yStart, double xEnd, double yEnd, float r, float g, float b, float a) {
		Tessellator tess = Tessellator.instance;
		GL11.glColor4f(r, g, b, a);
		tess.startDrawingQuads();
		tess.addVertex(xStart, yEnd, 0);
		tess.addVertex(xEnd, yEnd, 0);
		tess.addVertex(xEnd, yStart, 0);
		tess.addVertex(xStart, yStart, 0);
		tess.draw();
	}
	
	public void renderFrame(int xStart, int yStart, int xEnd, int yEnd) {
		if(ClickGUIHack.theme() == Theme.CLIFF) {
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glBlendFunc(770, 771);
			
			this.renderFrameBackGround(xStart, yStart, xEnd, yEnd);
			this.renderFrameOutlines(xStart, yStart, xEnd, yEnd);
			
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_BLEND);
		}else if(ClickGUIHack.theme() == Theme.NODUS) {
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glBlendFunc(770, 771);
			this.renderFrameBackGround(xStart-2, yStart-2, xEnd+2, yEnd+2, 1, 1, 1, 0x20/255f);
			this.renderFrameBackGround(xStart, yStart, xEnd, yEnd, 0, 0, 0, 0x80/255f);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_BLEND);
		}else if(ClickGUIHack.theme() == Theme.HEPHAESTUS) {
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glBlendFunc(770, 771);
			this.renderFrameBackGround(xStart, yStart, xEnd, yEnd, 0, 0, 0, 100/255f);
			this.renderFrameOutlines(xStart, yStart, xEnd, yEnd);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_BLEND);
		}
	}
	
	public void renderNameBG() {
		int xStart = this.xPos;
		int yStart = this.yPos;
		if(ClickGUIHack.theme() == Theme.CLIFF) {
			this.renderFrame(xStart, yStart, xStart + this.width, yStart + ClickGUIHack.theme().yspacing);
		}else if(ClickGUIHack.theme() == Theme.NODUS) {
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			
			this.renderFrameBackGround(xStart, yStart, xStart + this.width, yStart+ClickGUIHack.theme().yspacing, 0, 0, 0, 0x80/255f);
			this.renderFrameBackGround(xStart-2, yStart-2, xStart + this.width+2, yStart+ClickGUIHack.theme().yspacing+2, 1, 1, 1, 0x20/255f);
			this.renderFrameBackGround(xStart, yStart, xStart + this.width, yStart+ClickGUIHack.theme().yspacing, 0, 0, 0, 0x80/255f);
			
			//expand
			if(this.canMinimize) {
				this.renderFrameBackGround(this.tabMinimize.getMinX(), this.tabMinimize.getMinY(), this.tabMinimize.getMaxX(), this.tabMinimize.getMaxY(), 1, 1, 1, 0x40/255f);
				this.renderFrameBackGround(this.tabMinimize.getMinX()+1, this.tabMinimize.getMinY()+1, this.tabMinimize.getMaxX()-1, this.tabMinimize.getMaxY()-1, 0, 0, 0, this.minimized ? (0xcc/255f) : (90/255f)); //XXX why 90?
			}
			
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_BLEND);
		}else if(ClickGUIHack.theme() == Theme.HEPHAESTUS) {
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			
			this.renderFrameBackGround(xStart, yStart, xStart + this.width, yStart+ClickGUIHack.theme().yspacing, ClickGUIHack.r(), ClickGUIHack.g(), ClickGUIHack.b(), 1);
			this.renderFrameOutlines(xStart, yStart, xStart + this.width, yStart+ClickGUIHack.theme().yspacing+0.5);
			
			
			if(this.canMinimize) {
				this.renderFrameBackGround(this.tabMinimize.getMinX(), this.tabMinimize.getMinY(), this.tabMinimize.getMaxX(), this.tabMinimize.getMaxY(), 0, 0, 0, (this.minimized ? 50 : 150)/255f);
			}
			
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_BLEND);
		}
	}
	public void renderNameAt(int x, int y) {
		if(ClickGUIHack.theme() == Theme.CLIFF) {
			Client.mc.fontRenderer.drawString(this.name, x + ClickGUIHack.theme().headerXAdd, y + ClickGUIHack.theme().yaddtocenterText, 0xffffff);
		}else if(ClickGUIHack.theme() == Theme.NODUS) {
			Client.mc.fontRenderer.drawString(this.name, x + ClickGUIHack.theme().headerXAdd, y + ClickGUIHack.theme().yaddtocenterText, ClickGUIHack.instance.secColor.rgb());
		}else if(ClickGUIHack.theme() == Theme.HEPHAESTUS) {
			Client.mc.fontRenderer.drawStringWithShadow(this.name, x + ClickGUIHack.theme().headerXAdd, y + ClickGUIHack.theme().yaddtocenterText, 0xffffff);
		}
	}
	
	public void drawString(String s, int x, int y, int color) {
		if(ClickGUIHack.theme() == Theme.HEPHAESTUS) {
			Client.mc.fontRenderer.drawStringWithShadow(s, x, y, color);
		}else {
			Client.mc.fontRenderer.drawString(s, x, y, color);
		}
	}
	
	public void renderName() {
		this.renderNameBG();
		this.renderNameAt(this.xPos, this.yPos);
	}
	
	public void renderMinimized() {
		this.height = ClickGUIHack.theme().yspacing;
		this.renderName();
	}
	
	public void renderIngame() {
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		this.render();
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}
	
	public void render() {
		this.renderName();
	}
	public void readFromNBT(NBTTagCompound tag) {
		NBTTagCompound comp = tag.getCompoundTag("Position");
		this.xPos = comp.getInteger("xPos");
		this.yPos = comp.getInteger("yPos");
		this.minimized = comp.getBoolean("Minimized");
		if(this.minimized) this.minimize();
		else this.maximize();
	}
	public void writeToNBT(NBTTagCompound tag) {
		NBTTagCompound comp = (NBTTagCompound) NBTBase.createTagOfType((byte) 10);
		comp.setInteger("xPos", this.xPos);
		comp.setInteger("yPos", this.yPos);
		comp.setBoolean("Minimized", this.minimized);
		tag.setCompoundTag("Position", comp);
		tag.setInteger("Priority", ClickGUI.tabs.indexOf(this));
	}
	public static final int SELSIZE = 1;
	public void mouseHovered(int x, int y, int click) {
		if(this.canMinimize) {
			int ysize = 10;
			int centeryoff = (ClickGUIHack.theme().yspacing - ysize) / 2;
			int st = this.xPos + this.width - 10 - 2;
			int yStart = this.yPos;
			boolean inside = GUIUtils.isInsideRect(x, y, 
				this.tabMinimize.getMinX()+1, 
				this.tabMinimize.getMinY()+1, 
				this.tabMinimize.getMaxX()-1, 
				this.tabMinimize.getMaxY()-1
			);
			if(ClickGUIHack.theme() == Theme.NODUS && inside) {
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glDisable(GL11.GL_TEXTURE_2D);
				GL11.glBlendFunc(770, 771);
				String s = this.minimized ? "Expand" : "Minimize";
				int ssize = Client.mc.fontRenderer.getStringWidth(s) + 3;
				this.renderFrameBackGround(x, y - 12, x + ssize, y, 0, 0, 0, 0x90/255f);
				GL11.glEnable(GL11.GL_TEXTURE_2D);
				Client.mc.fontRenderer.drawString(s, x + 2, y - 10, 0xffffffff);
				GL11.glDisable(GL11.GL_BLEND);
			}
		}
		
		
		if(ClickGUIHack.instance.manualResize.getValue()) {
			int xmin = this.xPos;
			int xmax = this.xPos+this.width;
			int ymin = this.yPos;
			int ymax = this.yPos+this.height;
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
	}

	public void stopHovering() {
		if(ClickGUIHack.instance.manualResize.getValue()) {
			GUIUtils.setCursor(Cursor.DEFAULT_CURSOR);
		}
	}

	public void wheelMoved(int wheel, int x, int y) {
	}
}
