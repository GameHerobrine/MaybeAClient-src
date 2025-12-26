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
		this.yDefPos = this.yPos = 15;
		instance = this;
	}
	public void renderIngame() {
		if(KeybindingsHack.instance.status) super.renderIngame();
	}
	public void renderName(boolean alignRight) {
		if(alignRight) {
			int xStart = this.xPos;
			int yStart = this.yPos;
			this.renderNameBG();
			this.renderNameAt(xStart + this.width - Client.mc.fontRenderer.getStringWidth(this.name) - ClickGUIHack.theme().headerXAdd, yStart); //XXX - 2 is needed
		}else {
			super.renderName();
		}
	}
	
	public void renderMinimized() {
		this.height = ClickGUIHack.theme().yspacing;
		this.width = Client.mc.fontRenderer.getStringWidth(this.name) + ClickGUIHack.theme().titleXadd;
		this.renderName(this.isAlignedRight(EnumStaticPos.DISABLED, KeybindingsHack.instance.alignment.getValue()));
	}
	
	boolean first = true;
	public void render() {
		int ySpace = ClickGUIHack.theme().yspacing;
		int prevSpace = ClickGUIHack.theme().titlebasediff;
		int txtCenter = ClickGUIHack.theme().yaddtocenterText;
		
		int height = ySpace + prevSpace;
		int savdHeight = this.height;
		int savdWidth = this.width;
		int width = Client.mc.fontRenderer.getStringWidth(this.name) + ClickGUIHack.theme().titleXadd;
		boolean hasBinds = false;
		for(Hack h : Client.hacksByName.values()) {
        	if(h.keybinding.value != 0) {
        		height += ySpace;
        		int w = Client.mc.fontRenderer.getStringWidth("["+ChatColor.custom(ClickGUIHack.highlightedTextColor())+h.keybinding.valueToString()+ChatColor.EXP_RESET+"] "+h.name) + 2;
        		if(w > width) width = w;
        		hasBinds = true;
        	}
		}
		
		boolean expandTop = this.setPosition(EnumStaticPos.DISABLED, KeybindingsHack.instance.alignment.getValue(), KeybindingsHack.instance.expand.getValue());
		boolean alignRight = this.isAlignedRight(EnumStaticPos.DISABLED, KeybindingsHack.instance.alignment.getValue());
		this.tabMinimize.alignRight = alignRight;
		this.height = height;
		this.width = width;
		
		if(!this.minimized) {
			if(first) {
				first = false;
			}else {
				boolean sav = false;
				if(expandTop && savdHeight != this.height) {
					this.yPos -= (this.height - savdHeight);
					sav = true;
				}
				
				if(alignRight && savdWidth != this.width){
					this.xPos -= (this.width - savdWidth);
					sav = true;
				}
				
				if(sav) Client.saveClickGUI();
			}
		}
		
		if(this.minimized) {
			this.width = Client.mc.fontRenderer.getStringWidth(this.name) + ClickGUIHack.theme().titleXadd;
			if(alignRight && savdWidth != this.width){
				this.xPos -= (this.width - savdWidth);
			}
			this.renderMinimized();
			return;
		}
		
		this.renderName(alignRight);
		if(!hasBinds) return;
		
		this.renderFrame(this.xPos, this.yPos + ySpace + prevSpace, this.xPos + this.width, this.yPos + this.height);
		
		int i = 1;
		int tcolor = ClickGUIHack.normTextColor();
        for(Hack h : Client.hacksByName.values()) {
        	if(h.keybinding.value != 0) {
        		String s = "["+ChatColor.custom(ClickGUIHack.highlightedTextColor())+h.keybinding.valueToString()+ChatColor.EXP_RESET+"] "+h.name;
        		
        		if(alignRight) {
        			Client.mc.fontRenderer.drawString(s, this.xPos + this.width - Client.mc.fontRenderer.getStringWidth(s), this.yPos + i*ySpace + prevSpace + txtCenter, tcolor);
        		}else {
        			Client.mc.fontRenderer.drawString(s, this.xPos + 2, this.yPos + i*ySpace + prevSpace + txtCenter, tcolor);
        		}
	        	++i;
        	}
        }
	}
	
}
