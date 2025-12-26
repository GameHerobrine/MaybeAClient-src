package net.skidcode.gh.maybeaclient.gui.click;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import net.minecraft.src.ScaledResolution;
import net.skidcode.gh.maybeaclient.Client;
import net.skidcode.gh.maybeaclient.hacks.ArrayListHack;
import net.skidcode.gh.maybeaclient.hacks.ClickGUIHack;
import net.skidcode.gh.maybeaclient.hacks.Hack;
import net.skidcode.gh.maybeaclient.hacks.KeybindingsHack;
import net.skidcode.gh.maybeaclient.hacks.ArrayListHack.SorterAZ;
import net.skidcode.gh.maybeaclient.hacks.ArrayListHack.SorterZA;
import net.skidcode.gh.maybeaclient.hacks.ClickGUIHack.Theme;
import net.skidcode.gh.maybeaclient.utils.ChatColor;

public class ArrayListTab extends Tab{
	
	public static ArrayListTab instance;
	
	public ArrayListTab() {
		super("Enabled Modules");
		this.xDefPos = this.xPos = 10;
		this.yDefPos = this.yPos = 100;
		this.height = 12;
		
		instance = this;
	}
	public int yOffset = 0;
	
	public int oldEnabledCnt = 0;
	boolean first = true;
	public void renderIngame() {
		if(ArrayListHack.instance.status) super.renderIngame();
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
	
	@Override
	public void renderMinimized() {
		this.height = ClickGUIHack.theme().yspacing;
		this.renderName(this.isAlignedRight(ArrayListHack.instance.staticPositon.getValue(), ArrayListHack.instance.alignment.getValue()));
	}
	
	public void render() {
		Client.debug = true;
		ArrayList<String> enabled = new ArrayList<>();
		int savdHeight = this.height;
		int savdWidth = this.width;
		int totalHeight = 0;
		int titleSize = Client.mc.fontRenderer.getStringWidth(this.name);
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
		this.width = totalWidth;
		this.height = totalHeight + 14;
		if(this.minimized) this.height = ClickGUIHack.theme().yspacing;
		
		boolean expandTop = this.setPosition(ArrayListHack.instance.staticPositon.getValue(), ArrayListHack.instance.alignment.getValue(), ArrayListHack.instance.expand.getValue());
		boolean alignRight = this.isAlignedRight(ArrayListHack.instance.staticPositon.getValue(), ArrayListHack.instance.alignment.getValue());
		
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
			this.renderMinimized();
			return;
		}
		
		this.renderName(alignRight);
		this.renderFrame(
			this.xPos, this.yPos + ClickGUIHack.theme().yspacing + ClickGUIHack.theme().titlebasediff, 
			this.xPos + totalWidth, this.yPos + ClickGUIHack.theme().yspacing + ClickGUIHack.theme().titlebasediff + totalHeight + this.yOffset
		);
		
		if(ArrayListHack.instance.sortMode.currentMode.equalsIgnoreCase("Ascending")) enabled.sort(SorterAZ.inst);
		else if(ArrayListHack.instance.sortMode.currentMode.equalsIgnoreCase("Descending"))  enabled.sort(SorterZA.inst);
		for(int i = 0; i < enabled.size(); ++i) {
			String s = enabled.get(i);
			int rendX, rendY;
			if(alignRight) {
				rendX = (this.xPos + this.width) - Client.mc.fontRenderer.getStringWidth(s);
			}else {
				rendX = this.xPos + 2;
			}
			
			rendY = this.yPos+i*ClickGUIHack.theme().yspacing + ClickGUIHack.theme().yspacing + ClickGUIHack.theme().titlebasediff + 2;
			
			Client.mc.fontRenderer.drawString(s, rendX, rendY, ClickGUIHack.normTextColor());
		}
		Client.debug = false;
	}
	
	@Override
	public void wheelMoved(int wheel, int x, int y) {

	}
}
