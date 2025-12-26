package net.skidcode.gh.maybeaclient.gui.click;

import net.skidcode.gh.maybeaclient.gui.click.element.Element;
import net.skidcode.gh.maybeaclient.hacks.ClickGUIHack;
import net.skidcode.gh.maybeaclient.hacks.settings.Setting;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingsProvider;

public class SettingsTab extends ScrollableTab{
	
	public SettingsProvider settingProvider;
	
	public Setting selected = null;
	public int selectedMinX = 0, selectedMinY = 0, selectedMaxX = 0, selectedMaxY = 0;
	public boolean vertical = false;
	
	public SettingsTab(Element parent, SettingsProvider sp) {
		super("");
		showninmanager = false;
		this.settingProvider = sp;
		this.renderHeader = false;
		this.setParent(parent);
	}
	
	@Override
	public boolean isPointInside(float x, float y) {
		if(!this.parent.isShown()) return false;
		return super.isPointInside(x, y);
	}
	
	public void renderIngame() {}
	
	public int getInitialYPos() {
		return this.parent.startY;
	}
	
	
	@Override
	public void preRender() {
		if(!this.isShown()) return;
		this.startX = this.parent.getParentEndX() + ClickGUIHack.theme().titlebasediff;
		this.startY = this.parent.startY;
		this.clearElements();
		for(Setting s : this.settingProvider.getSettings()) {
			if(!s.hidden) this.addElement(s.guielement);
		}
		
		super.preRender();
	}
	
	@Override
	public void render() {
		if(!this.isShown()) return;
		super.render();
	}
	
	@Override
	public boolean isShown() {
		if(!parent.isInRenderBounds(parent)) return false;
		if(!this.shown) return false;
		return parent.isShown();
	}

	public void setParent(Element parent) {
		this.parent = parent;
		this.preRender();
	}
}
