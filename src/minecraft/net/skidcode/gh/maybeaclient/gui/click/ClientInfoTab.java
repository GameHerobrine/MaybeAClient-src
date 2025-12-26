package net.skidcode.gh.maybeaclient.gui.click;

import java.util.ArrayList;

import net.minecraft.src.ScaledResolution;
import net.skidcode.gh.maybeaclient.Client;
import net.skidcode.gh.maybeaclient.hacks.ArrayListHack;
import net.skidcode.gh.maybeaclient.hacks.ClickGUIHack;
import net.skidcode.gh.maybeaclient.hacks.ClientInfoHack;
import net.skidcode.gh.maybeaclient.hacks.settings.enums.EnumAlign;
import net.skidcode.gh.maybeaclient.hacks.settings.enums.EnumStaticPos;
import net.skidcode.gh.maybeaclient.utils.ChatColor;
import net.skidcode.gh.maybeaclient.utils.PlayerUtils;

public class ClientInfoTab extends Tab{
	
	public static ClientInfoTab instance;
	
	public ClientInfoTab() {
		super("Player info", 0, 12);
		this.xDefPos = this.xPos = 255;
		this.yDefPos = this.yPos = 10;
		instance = this;
	}
	
	public void renderIngame() {
		if(ClientInfoHack.instance.status) super.renderIngame();
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
		this.height = 12;
		this.renderName(this.isAlignedRight(ClientInfoHack.instance.staticPositon.getValue(), ClientInfoHack.instance.alignment.getValue()));
	}
	
	boolean first = true;
	public ArrayList<String> toRender;
	
	public void addRenderString(String s) {
		int wid = Client.mc.fontRenderer.getStringWidth(s) + 2;
		if(wid > this.width) this.width = wid;
		toRender.add(s);
		this.height += ClickGUIHack.theme().yspacing;
	}
	
	public void render() {
		int savdWidth = this.width;
		this.width = Client.mc.fontRenderer.getStringWidth(this.name) + ClickGUIHack.theme().titleXadd;
		
		
		boolean renderCoords = ClientInfoHack.instance.coords.value;
		boolean renderFacing = ClientInfoHack.instance.facing.value;
		boolean renderFPS = ClientInfoHack.instance.fps.value;
		boolean renderUsername = ClientInfoHack.instance.username.value;
		boolean renderSpeed = ClientInfoHack.instance.walkingSpeed.value;
		int ySpace = ClickGUIHack.theme().yspacing;
		int prevSpace = ClickGUIHack.theme().titlebasediff;
		int txtCenter = ClickGUIHack.theme().yaddtocenterText;
		
		String coordX = "", coordY = "", coordZ = "";
		String facing = String.format("Facing: %s", PlayerUtils.getDirection());
		String fps = String.format("FPS: %s", Client.mc.fps);
		String username = String.format("Username: %s", Client.mc.session.username);
		String walkingSpeed = String.format("Speed: %.4f BPS", PlayerUtils.getSpeed(ClientInfoHack.instance.useHorizontal.value));	
		int baseOff = ySpace + prevSpace;
		this.height = baseOff;
		toRender = new ArrayList<>();
		if(this.minimized) this.height = ySpace;
		else {
			if(renderCoords) {
				if(ClientInfoHack.instance.showNetherCoords.value) {
					boolean inNether = Client.mc.theWorld.worldProvider.isHellWorld;
					if(Client.mc.isMultiplayerWorld()) {
						if(ClientInfoHack.instance.isInNether.currentMode.equals("Detect")) {
							inNether = PlayerUtils.isInNether();
						}else if(ClientInfoHack.instance.isInNether.currentMode.equals("Nether")) {
							inNether = true;
						}else if(ClientInfoHack.instance.isInNether.currentMode.equals("Overworld")) {
							inNether = false;
						}
					}
					
					
					if(inNether) {
						coordX = String.format("X: %.2f %s%.2f", Client.mc.thePlayer.posX, ChatColor.LIGHTGREEN, Client.mc.thePlayer.posX*8);
						coordY = String.format("Y: %.2f %s%.2f", Client.mc.thePlayer.posY, ChatColor.LIGHTGREEN, Client.mc.thePlayer.posY);
						coordZ = String.format("Z: %.2f %s%.2f", Client.mc.thePlayer.posZ, ChatColor.LIGHTGREEN, Client.mc.thePlayer.posZ*8);
					}else {
						coordX = String.format("X: %.2f %s%.2f", Client.mc.thePlayer.posX, ChatColor.LIGHTRED, Client.mc.thePlayer.posX/8);
						coordY = String.format("Y: %.2f %s%.2f", Client.mc.thePlayer.posY, ChatColor.LIGHTRED, Client.mc.thePlayer.posY);
						coordZ = String.format("Z: %.2f %s%.2f", Client.mc.thePlayer.posZ, ChatColor.LIGHTRED, Client.mc.thePlayer.posZ/8);
					}
				}else {
					coordX = String.format("X: %.2f", Client.mc.thePlayer.posX);
					coordY = String.format("Y: %.2f", Client.mc.thePlayer.posY);
					coordZ = String.format("Z: %.2f", Client.mc.thePlayer.posZ);
				}
				
				this.addRenderString(coordX);
				this.addRenderString(coordY);
				this.addRenderString(coordZ);
			}
			if(renderFacing) this.addRenderString(facing);
			if(renderFPS) this.addRenderString(fps);
			if(renderUsername) this.addRenderString(username);
			if(renderSpeed) this.addRenderString(walkingSpeed);
		}
		
		this.setPosition(ClientInfoHack.instance.staticPositon.getValue(), ClientInfoHack.instance.alignment.getValue());
		boolean alignRight = this.isAlignedRight(ClientInfoHack.instance.staticPositon.getValue(), ClientInfoHack.instance.alignment.getValue());
		int rendX = this.xPos + 2; 
		
		if(!this.minimized) {
			if(first) {
				first = false;
			}else {
				boolean sav = false;
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
		this.renderFrame(this.xPos, this.yPos + baseOff, this.xPos + this.width, this.yPos + this.height);
		int tcolor = ClickGUIHack.normTextColor();
		
		for(int i = 0; i < toRender.size(); ++i) {
			String s = toRender.get(i);
			if(alignRight) rendX = (this.xPos + this.width) - Client.mc.fontRenderer.getStringWidth(s);
			Client.mc.fontRenderer.drawString(s, rendX, this.yPos + (i+1)*ySpace + prevSpace + txtCenter, tcolor);
		}
	}
}
