package net.skidcode.gh.maybeaclient.gui.click;

import org.lwjgl.opengl.GL11;

import net.minecraft.src.ScaledResolution;
import net.skidcode.gh.maybeaclient.Client;
import net.skidcode.gh.maybeaclient.hacks.ClickGUIHack;
import net.skidcode.gh.maybeaclient.hacks.Hack;
import net.skidcode.gh.maybeaclient.hacks.ClickGUIHack.Theme;
import net.skidcode.gh.maybeaclient.hacks.settings.Setting;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingsProvider;

public class SettingsTab extends Tab{
	
	public SettingsProvider settingProvider;
	public Tab parent;
	public int hackID;
	
	public Setting selected = null;
	public int selectedMinX = 0, selectedMinY = 0, selectedMaxX = 0, selectedMaxY = 0;
	public boolean vertical = false;
	
	public SettingsTab(Tab parent, SettingsProvider sp, int hackID) {
		super("");
		this.settingProvider = sp;
		this.hackID = hackID;
		this.setParent(parent);
	}
	
	@Override
	public boolean isPointInside(float x, float y) {
		if(this.getParent().minimized) return false;
		return super.isPointInside(x, y);
	}
	
	@Override
	public void mouseHovered(int x, int y, int click) {
		if(this.getParent().minimized) return;
		int sx = (int)this.xPos;
		int sy = (int)this.yPos;
		for(Setting s : this.settingProvider.getSettings()) {
			if(s.hidden) continue;
			int h = s.getSettingHeight(this);
			if(x >= sx && x <= (sx + this.width)) {
				if(y >= sy && y <= (sy + h)) {
					s.mouseHovered(x, y, click);
					break;
				}
			}
			sy += h;
		}
	}
	
	@Override
	public boolean onSelect(int click, int x, int y) {
		if(this.getParent().minimized) return false;
		int setBord = ClickGUIHack.theme().settingBorder;
		int sx = (int)this.xPos;
		int sy = (int)this.yPos;
		for(Setting s : this.settingProvider.getSettings()) {
			if(s.hidden) continue;
			int h = s.getSettingHeight(this);
			if(x >= sx && x <= (sx + this.width)) {
				if(y >= sy && y <= (sy + h)) {
					
					s.onPressedInside(this, sx + setBord, sy + setBord, sx + this.width - setBord, sy + h - setBord, x, y, click);
					this.selected = s;
					this.selectedMinX = sx + setBord;
					this.selectedMinY = sy + setBord;
					this.selectedMaxX = sx + this.width - setBord;
					this.selectedMaxY = sy + h - setBord;
					
					return true;
				}
			}
			sy += h;
		}
		return false;
	}
	public void renderIngame() {
		
	}
	public void mouseMovedSelected(int click, int x, int y) {
		if(this.getParent().minimized) return;
		if(this.selected != null) {
			this.selected.onMouseMoved(this.selectedMinX, this.selectedMinY, this.selectedMaxX, this.selectedMaxY, x, y, click);
		}
	}
	
	@Override
	public void onDeselect(int click, int x, int y) {
		if(this.getParent().minimized) return;
		super.onDeselect(click, x, y);
		if(this.selected != null) {
			this.selected.onDeselect(this, this.selectedMinX, this.selectedMinY, this.selectedMinX + this.width, this.selectedMaxY, x, y, click);
			this.selected = null;
		}
		Client.saveModules();
	}
	public int getInitialYPos() {
		return this.getParent().yPos + this.getParent().getVScrollOffset() + this.hackID*ClickGUIHack.theme().yspacing + ClickGUIHack.theme().yspacing + ClickGUIHack.theme().titlebasediff;
	}
	@Override
	public void preRender() {
		if(this.getParent().minimized) return;
		int settingsHeight = 0;
		int settingsWidth = 60;
		
		this.xPos = this.getParent().xPos + this.getParent().width + ClickGUIHack.theme().titlebasediff;
		this.yPos = this.getInitialYPos();
		if(ClickGUIHack.theme() == Theme.HEPHAESTUS) this.xPos += 2;
		for(Setting set : this.settingProvider.getSettings()) {
			if(set.hidden) continue;
			int w = set.getSettingWidth();
			if(w > settingsWidth) settingsWidth = w;
		}
		this.width = settingsWidth;
		
		
		for(Setting set : this.settingProvider.getSettings()) {
			if(set.hidden) continue;
			settingsHeight += set.getSettingHeight(this);
		}
		if(ClickGUIHack.theme().verticalSettings) {
			settingsHeight += 3;
		}
		if((this.yPos + ClickGUIHack.theme().yspacing) <= this.getParent().yPos + ClickGUIHack.theme().yspacing + ClickGUIHack.theme().titlebasediff) {
			this.shown = false;
		}else {
			this.shown = true;
		}
		
		
		ScaledResolution scaledResolution = new ScaledResolution(Client.mc.gameSettings, Client.mc.displayWidth, Client.mc.displayHeight);
		if(this.yPos + settingsHeight > scaledResolution.getScaledHeight()) {
			this.yPos -= settingsHeight - ClickGUIHack.theme().yspacing;
		}
		this.height = settingsHeight;
		
	}
	
	@Override
	public void render() {
		if(this.getParent().minimized) return;
		if(ClickGUIHack.theme().verticalSettings && this.settingProvider instanceof Hack) {
			((Hack)this.settingProvider).tab = null;
			ClickGUI.removeTab(this);
			return;
		}
		
		int setBord = ClickGUIHack.theme().settingBorder;
		
		int settingsHeight = this.height;
		int settingsWidth = this.width;
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glBlendFunc(770, 771);
		if(ClickGUIHack.theme() == Theme.CLIFF) {
			this.renderFrameBackGround((int)this.xPos, (int)this.yPos, (int)this.xPos + settingsWidth, (int)this.yPos + settingsHeight);
		}else if(ClickGUIHack.theme() == Theme.NODUS) {
			this.renderFrameBackGround(this.xPos-2, this.yPos-2, this.xPos + settingsWidth+2, this.yPos + settingsHeight+2, 1, 1, 1, 0x20/255f);
			this.renderFrameBackGround(this.xPos, this.yPos, this.xPos + settingsWidth, this.yPos + settingsHeight, 0, 0, 0, 0x80/255f);
		}else if(ClickGUIHack.theme() == Theme.HEPHAESTUS) {
			this.renderFrameBackGround((int)this.xPos, (int)this.yPos, (int)this.xPos + settingsWidth, (int)this.yPos + settingsHeight);
		}
		
		
		int height = (int)this.yPos;
		for(Setting set : this.settingProvider.getSettings()) {
			if(set.hidden) continue;
			int sHeight = set.getSettingHeight(this);
			set.renderElement(this, (int)this.xPos + setBord, height + setBord, (int)this.xPos + settingsWidth - setBord, height + sHeight - setBord);
			height += sHeight;
		}
		if(ClickGUIHack.theme() == Theme.CLIFF || ClickGUIHack.theme() == Theme.HEPHAESTUS) {
			this.renderFrameOutlines((int)this.xPos, (int)this.yPos, (int)this.xPos + settingsWidth, (int)this.yPos + settingsHeight);
		}
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
		
		height = (int)this.yPos;
		for(Setting set : this.settingProvider.getSettings()) {
			if(set.hidden) continue;
			int sHeight = set.getSettingHeight(this);
			set.renderText(this, (int)this.xPos + setBord, height + setBord, (int)this.xPos + settingsWidth - setBord, height + sHeight - setBord);
			height += sHeight;
		}
		
		
	}

	public Tab getParent() {
		return parent;
	}

	public void setParent(Tab parent) {
		this.parent = parent;
		this.preRender();
	}
}
