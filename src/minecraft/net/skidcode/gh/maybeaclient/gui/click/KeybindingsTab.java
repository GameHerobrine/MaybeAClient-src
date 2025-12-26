package net.skidcode.gh.maybeaclient.gui.click;

import net.skidcode.gh.maybeaclient.Client;
import net.skidcode.gh.maybeaclient.hacks.ClickGUIHack;
import net.skidcode.gh.maybeaclient.hacks.Hack;
import net.skidcode.gh.maybeaclient.hacks.KeybindingsHack;
import net.skidcode.gh.maybeaclient.hacks.settings.enums.EnumStaticPos;
import net.skidcode.gh.maybeaclient.utils.ChatColor;

public class KeybindingsTab extends Tab{
	
	public static KeybindingsTab instance;
	
	public KeybindingsTab() {
		super("Keybindings", 0, 14);
		this.yDefPos = this.startY = 15;
		instance = this;
		this.isHUD = true;
	}
	public void renderIngame() {
		if(KeybindingsHack.instance.status) super.renderIngame();
	}
	
	@Override
	public void preRender() {
		int ySpace = ClickGUIHack.theme().yspacing;
		
		int height = this.getYOffset();
		int width = Client.mc.fontRenderer.getStringWidth(this.getTabName()) + ClickGUIHack.theme().titleXadd;
		
		for(Hack h : Client.hacksByName.values()) {
        	if(h.keybinding.value != 0) {
        		height += ySpace;
        		int w = Client.mc.fontRenderer.getStringWidth("["+ChatColor.custom(ClickGUIHack.highlightedTextColor())+h.keybinding.valueToString()+ChatColor.EXP_RESET+"] "+h.name) + 2;
        		if(w > width) width = w;
        		hasBinds = true;
        	}
		}
		if(this.minimized.getValue()) {
			width = Client.mc.fontRenderer.getStringWidth(this.getTabName()) + ClickGUIHack.theme().titleXadd;
		}
		expandTop = this.setPosition(EnumStaticPos.DISABLED, KeybindingsHack.instance.alignment.getValue(), KeybindingsHack.instance.expand.getValue());
		alignRight = this.isAlignedRight(EnumStaticPos.DISABLED, KeybindingsHack.instance.alignment.getValue());
		this.tabMinimize.alignRight = alignRight;
		this.endY = this.startY + height;
		this.endX = this.startX + width;
		super.preRender();
	}
	boolean expandTop, alignRight;
	boolean first = true;
	boolean hasBinds = false;
	@Override
	public void render() {
		int ySpace = ClickGUIHack.theme().yspacing;
		int txtCenter = ClickGUIHack.theme().yaddtocenterText;
		int headoff = this.getYOffset();
		
		
		if(this.minimized.getValue()) {
			this.renderMinimized();
			return;
		}
		
		this.renderName(alignRight);
		if(!hasBinds) return;
		
		Tab.renderFrame(this, this.startX, this.startY + headoff, this.endX, this.endY);
		
		int i = 0;
		int tcolor = ClickGUIHack.normTextColor();
        for(Hack h : Client.hacksByName.values()) {
        	if(h.keybinding.value != 0) {
        		String s = "["+ChatColor.custom(ClickGUIHack.highlightedTextColor())+h.keybinding.valueToString()+ChatColor.EXP_RESET+"] "+h.name;
        		
        		if(alignRight) {
        			Client.mc.fontRenderer.drawString(s, this.endX - Client.mc.fontRenderer.getStringWidth(s), this.startY + i*ySpace + headoff + txtCenter, tcolor);
        		}else {
        			Client.mc.fontRenderer.drawString(s, this.startX + 2, this.startY + i*ySpace + headoff + txtCenter, tcolor);
        		}
	        	++i;
        	}
        }
        Tab.renderFrameTop(this, this.startX, this.startY + headoff, this.endX, this.endY);
	}
	
}
