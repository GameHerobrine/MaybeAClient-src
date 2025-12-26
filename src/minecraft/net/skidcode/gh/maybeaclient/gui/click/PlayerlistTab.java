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
		this.xDefPos = this.startX = 160;
		this.yDefPos = this.startY = 24 + 14*6;
		instance = this;
		this.isHUD = true;
	}
	public void renderIngame() {
		if(PlayerlistHack.instance.status) super.renderIngame();
	}
	
	boolean first = true;
	boolean prevMinimized = this.minimized.getValue();
	
	public void preRender() {
		int ySpace = ClickGUIHack.theme().yspacing;
		int width = Client.mc.fontRenderer.getStringWidth(this.getTabName()) + ClickGUIHack.theme().titleXadd;
		int height = this.getYOffset();
		
		EntityPlayer local = Client.mc.thePlayer;
		players = new ArrayList<String>();
		
		
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
		
		if(this.minimized.getValue()) {
			width = Client.mc.fontRenderer.getStringWidth(this.getTabName()) + ClickGUIHack.theme().titleXadd;
		}
		
		alignRight = PlayerlistHack.instance.alignment.currentMode.equalsIgnoreCase("Right");
		expandTop = PlayerlistHack.instance.expand.currentMode.equalsIgnoreCase("Top");
		this.tabMinimize.alignRight = alignRight;
		
		
		
		
		this.endY = this.startY + height;
		this.endX = this.startX + width;
		super.preRender();
	}
	boolean alignRight, expandTop;
	ArrayList<String> players;
	@Override
	public void render() {
		int ySpace = ClickGUIHack.theme().yspacing;
		int txtCenter = ClickGUIHack.theme().yaddtocenterText;
		
		if(this.minimized.getValue()) {
			this.renderMinimized();
			return;
		}
		
		if(players.size() > 0) {
			int txtCol = ClickGUIHack.normTextColor();
			int h = this.getYOffset();
			Tab.renderFrame(this, this.startX, this.startY + h, this.endX, this.endY);
			for(String s : players) {
				if(alignRight) {
					Client.mc.fontRenderer.drawString(s, this.endX - Client.mc.fontRenderer.getStringWidth(s), this.startY + h + txtCenter, txtCol);
				}else {
					Client.mc.fontRenderer.drawString(s, this.startX + 2, this.startY + h + txtCenter, txtCol);
				}
				
				h += ySpace;
			}
			Tab.renderFrameTop(this, this.startX, this.startY + this.getYOffset(), this.endX, this.endY);
		}
		
		this.renderName(alignRight);
		prevMinimized = this.minimized.getValue();
	}

}
