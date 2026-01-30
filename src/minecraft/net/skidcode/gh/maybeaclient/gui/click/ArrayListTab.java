package net.skidcode.gh.maybeaclient.gui.click;

import java.util.ArrayList;
import net.skidcode.gh.maybeaclient.Client;
import net.skidcode.gh.maybeaclient.hacks.ArrayListHack;
import net.skidcode.gh.maybeaclient.hacks.ClickGUIHack;
import net.skidcode.gh.maybeaclient.hacks.Hack;
import net.skidcode.gh.maybeaclient.hacks.ArrayListHack.SorterAZ;
import net.skidcode.gh.maybeaclient.hacks.ArrayListHack.SorterZA;
import net.skidcode.gh.maybeaclient.utils.ChatColor;

public class ArrayListTab extends Tab{
	
	public static ArrayListTab instance;
	
	public ArrayListTab() {
		super("Enabled Modules");
		this.xDefPos = this.startX = 10;
		this.yDefPos = this.startY = 100;
		this.endY = this.startX+12;
		instance = this;
		this.isHUD = true;
	}
	
	public void renderIngame() {
		if(ArrayListHack.instance.status) super.renderIngame();
	}
	
	ArrayList<String> enabled;
	@Override
	public void preRender() {
		enabled = new ArrayList<>();
		int totalHeight = 0;
		int titleSize = Client.mc.fontRenderer.getStringWidth(this.getTabName());
		int totalWidth = titleSize + ClickGUIHack.theme().titleXadd;
		
		for(Hack h : Client.hacksByName.values()) {
			if(h.status) {
				totalHeight += ClickGUIHack.theme().yspacing;
				String ccolor = ChatColor.custom(ClickGUIHack.highlightedTextColor());
				String prefix = h.getPrefix().replace(ChatColor.BLACK.toString(), ccolor).replace(ChatColor.WHITE.toString(), ChatColor.EXP_RESET.toString());
				String name = h.getNameForArrayList();
				if(!prefix.equals("")) {
					name += "[";
					name += ccolor;
					name += prefix;
					name += ChatColor.EXP_RESET;
					name += "]";
				}
				int size = Client.mc.fontRenderer.getStringWidth(name) + 2;
				if(totalWidth < size) {
					totalWidth = size;
				}
				enabled.add(name);
			}
		}
		
		this.setPosition(ArrayListHack.instance.staticPositon.getValue(), ArrayListHack.instance.alignment.getValue(), ArrayListHack.instance.expand.getValue());
		this.isAlignedRight(ArrayListHack.instance.staticPositon.getValue(), ArrayListHack.instance.alignment.getValue());
		
		this.endX = this.startX + totalWidth;
		this.endY = this.startY + totalHeight + this.getYOffset();
		if(this.minimized.getValue()) this.endY = this.startY + ClickGUIHack.theme().yspacing;
		super.preRender();
	}
	
	@Override
	public void render() {
		if(this.minimized.getValue()) {
			this.renderMinimized();
			return;
		}
		
		this.renderName(alignRight);
		Tab.renderFrame(this, this.startX, this.startY + this.getYOffset(), this.endX, this.endY);
		
		if(ArrayListHack.instance.sortMode.currentMode.equalsIgnoreCase("Ascending")) enabled.sort(SorterAZ.inst);
		else if(ArrayListHack.instance.sortMode.currentMode.equalsIgnoreCase("Descending"))  enabled.sort(SorterZA.inst);
		for(int i = 0; i < enabled.size(); ++i) {
			String s = enabled.get(i);
			int rendX, rendY;
			if(alignRight) {
				rendX = (this.endX) - Client.mc.fontRenderer.getStringWidth(s);
			}else {
				rendX = this.startX + 2;
			}
			
			rendY = this.startY+i*ClickGUIHack.theme().yspacing + this.getYOffset() + 2;
			
			Client.mc.fontRenderer.drawString(s, rendX, rendY, ClickGUIHack.normTextColor());
		}
		Tab.renderFrameTop(this, this.startX, this.startY + this.getYOffset(), this.endX, this.endY);
		
	}
}
