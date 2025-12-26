package net.skidcode.gh.maybeaclient.gui.click;

import java.util.ArrayList;
import java.util.Map;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.NBTBase;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.ScaledResolution;
import net.skidcode.gh.maybeaclient.Client;
import net.skidcode.gh.maybeaclient.hacks.ClickGUIHack;
import net.skidcode.gh.maybeaclient.hacks.LastSeenSpotsHack;
import net.skidcode.gh.maybeaclient.hacks.settings.enums.EnumAlign;
import net.skidcode.gh.maybeaclient.hacks.settings.enums.EnumStaticPos;
import net.skidcode.gh.maybeaclient.utils.ChatColor;

public class LastSeenSpotsTab extends Tab{
	
	public static LastSeenSpotsTab instance;
	
	public LastSeenSpotsTab() {
		super("Last Seen Spots", 0, 12);
		this.minimized = false;
		this.xDefPos = this.xPos = 160;
		this.yDefPos = this.yPos = 24 + 14*7;
		instance = this;
	}
	public void renderIngame() {
		if(LastSeenSpotsHack.instance.status) super.renderIngame();
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
		this.renderName(this.isAlignedRight(EnumStaticPos.DISABLED, LastSeenSpotsHack.instance.alignment.getValue()));
	}
	
	boolean first = true;
	boolean prevMinimized = this.minimized;
	public void render() {
		int ySpace = ClickGUIHack.theme().yspacing;
		int prevSpace = ClickGUIHack.theme().titlebasediff;
		int txtCenter = ClickGUIHack.theme().yaddtocenterText;
		
		int savdWidth = this.width;
		this.width = Client.mc.fontRenderer.getStringWidth(this.name) + ClickGUIHack.theme().titleXadd;
		
		int savdHeight = this.height;
		
		EntityPlayer local = Client.mc.thePlayer;
		ArrayList<String> players = new ArrayList<String>();
		
		int height = ySpace + prevSpace;
		int width = this.width;
		for(Map.Entry<String, LastSeenSpotsHack.PlayerInfo> n_pi : LastSeenSpotsHack.instance.players.entrySet()) {
			String s = n_pi.getKey();
			LastSeenSpotsHack.PlayerInfo pi = n_pi.getValue();
			s += " XYZ: "+ String.format("%s%.2f %.2f %.2f", ChatColor.custom(ClickGUIHack.highlightedTextColor()), pi.x, pi.y, pi.z);
			players.add(s);
			int w = Client.mc.fontRenderer.getStringWidth(s) + 2;
			if(w > width) width = w;
			height += ySpace;
		}
		
		this.height = height;
		this.width = width;
		
		boolean alignRight = this.isAlignedRight(EnumStaticPos.DISABLED, LastSeenSpotsHack.instance.alignment.getValue());
		boolean expandTop = this.setPosition(EnumStaticPos.DISABLED, LastSeenSpotsHack.instance.alignment.getValue(), LastSeenSpotsHack.instance.expand.getValue());
		this.tabMinimize.alignRight = alignRight;

		if(!this.minimized) {
			if(first) {
				first = false;
				if(alignRight && savdWidth != this.width){
					this.xPos -= (this.width - savdWidth);
					Client.saveClickGUI();
				}
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
		
		if(players.size() > 0) {
			this.renderFrame(this.xPos, this.yPos + ySpace + prevSpace, this.xPos + this.width, this.yPos + this.height);
			int h = ySpace + prevSpace;
			for(String s : players) {
				if(alignRight) {
					Client.mc.fontRenderer.drawString(s, this.xPos + this.width - Client.mc.fontRenderer.getStringWidth(s), this.yPos + h + txtCenter, ClickGUIHack.normTextColor());
				}else {
					Client.mc.fontRenderer.drawString(s, this.xPos + 2, this.yPos + h + txtCenter, ClickGUIHack.normTextColor());
				}
				
				h += ySpace;
			}
		}
		
		this.renderName(alignRight);
		prevMinimized = this.minimized;
		//String d = String.format("%.2f", player.getDistance(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ));
		//String s = ChatColor.LIGHTCYAN+"["+d+"] "+ChatColor.GOLD+player.username+ChatColor.LIGHTCYAN+" XYZ: "+ChatColor.GOLD+String.format("%.2f %.2f %.2f", player.posX, player.posY, player.posZ);
		
		
	}
	
	public void writeToNBT(NBTTagCompound tag) {
		NBTTagCompound comp = (NBTTagCompound) NBTBase.createTagOfType((byte) 10);
		comp.setInteger("xPos", LastSeenSpotsHack.instance.alignment.getValue() == EnumAlign.RIGHT ? this.xPos + this.width : this.xPos);
		comp.setInteger("yPos", this.yPos);
		comp.setBoolean("Minimized", this.minimized);
		tag.setCompoundTag("Position", comp);
	}

}
