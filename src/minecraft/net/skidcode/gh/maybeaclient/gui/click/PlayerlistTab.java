package net.skidcode.gh.maybeaclient.gui.click;

import java.util.ArrayList;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.NBTBase;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.ScaledResolution;
import net.skidcode.gh.maybeaclient.Client;
import net.skidcode.gh.maybeaclient.hacks.ClickGUIHack;
import net.skidcode.gh.maybeaclient.hacks.PlayerlistHack;

public class PlayerlistTab extends Tab{
	
	public static PlayerlistTab instance;
	
	public PlayerlistTab() {
		super("Player List", 0, 12);
		this.minimized = false;
		this.xDefPos = this.xPos = 160;
		this.yDefPos = this.yPos = 24 + 14*6;
		instance = this;
	}
	public void renderIngame() {
		if(PlayerlistHack.instance.status) super.renderIngame();
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
		this.renderName(PlayerlistHack.instance.alignment.currentMode.equalsIgnoreCase("Right"));
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
		if(Client.mc.isMultiplayerWorld()) {
			for(String s : PlayerlistHack.detectedPlayers) {
				players.add(s);
				int w = Client.mc.fontRenderer.getStringWidth(s) + 2;
				if(w > width) width = w;
				height += ySpace;
			}
		}else {
			players.add(Client.mc.thePlayer.username);
			int w = Client.mc.fontRenderer.getStringWidth(Client.mc.thePlayer.username) + 2;
			if(w > width) width = w;
			height += ySpace;
		}
		
		
		this.height = height;
		this.width = width;
		
		boolean alignRight = PlayerlistHack.instance.alignment.currentMode.equalsIgnoreCase("Right");
		boolean expandTop = PlayerlistHack.instance.expand.currentMode.equalsIgnoreCase("Top");
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
			int txtCol = ClickGUIHack.normTextColor();
			this.renderFrame(this.xPos, this.yPos + ySpace + prevSpace, this.xPos + this.width, this.yPos + this.height);
			int h = ySpace + prevSpace;
			for(String s : players) {
				if(alignRight) {
					Client.mc.fontRenderer.drawString(s, this.xPos + this.width - Client.mc.fontRenderer.getStringWidth(s), this.yPos + h + txtCenter, txtCol);
				}else {
					Client.mc.fontRenderer.drawString(s, this.xPos + 2, this.yPos + h + txtCenter, txtCol);
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
		comp.setInteger("xPos", PlayerlistHack.instance.alignment.currentMode.equalsIgnoreCase("Right") ? this.xPos + this.width : this.xPos);
		comp.setInteger("yPos", this.yPos);
		comp.setBoolean("Minimized", this.minimized);
		tag.setCompoundTag("Position", comp);
	}

}
