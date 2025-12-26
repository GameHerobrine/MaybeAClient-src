package net.skidcode.gh.maybeaclient.gui.click;

import java.util.HashMap;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.NBTBase;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.ScaledResolution;
import net.skidcode.gh.maybeaclient.Client;
import net.skidcode.gh.maybeaclient.hacks.ArrayListHack;
import net.skidcode.gh.maybeaclient.hacks.ClickGUIHack;
import net.skidcode.gh.maybeaclient.hacks.KeybindingsHack;
import net.skidcode.gh.maybeaclient.hacks.RadarHack;
import net.skidcode.gh.maybeaclient.hacks.settings.enums.EnumAlign;
import net.skidcode.gh.maybeaclient.utils.ChatColor;

public class RadarTab extends Tab{
	
	public static RadarTab instance;
	
	public RadarTab() {
		super("Radar", 0, 12);
		instance = this;
	}
	public void renderIngame() {
		if(RadarHack.instance.status) super.renderIngame();
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
		this.renderName(this.isAlignedRight(RadarHack.instance.staticPositon.getValue(), RadarHack.instance.alignment.getValue()));
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
		HashMap<Integer, String> players = new HashMap<>();
		
		int height = ySpace + prevSpace;
		int width = this.width;
		boolean showCoords = RadarHack.instance.showXYZ.value;
		
		for(Object o : Client.mc.theWorld.playerEntities) {
			EntityPlayer player = (EntityPlayer) o;
			if(player.entityId != local.entityId || Client.mc.currentScreen instanceof ClickGUI) {
				String d = String.format("%.2f", player.getDistance(local.posX, local.posY, local.posZ));
				String hlcol = ChatColor.custom(ClickGUIHack.highlightedTextColor());
				String dist = player.username+" ["+hlcol+d+ChatColor.EXP_RESET+"]";
				if(showCoords) {
					dist += " XYZ: "+ String.format("%s%.2f %.2f %.2f", hlcol, player.posX, player.posY, player.posZ);
				}
				players.put(player.entityId, dist);
				int w = Client.mc.fontRenderer.getStringWidth(dist) + 2;
				if(w > width) width = w;
				height += ySpace;
			}
		}
		
		this.height = height;
		this.width = width;
		
		boolean alignRight = this.isAlignedRight(RadarHack.instance.staticPositon.getValue(), RadarHack.instance.alignment.getValue());
		boolean expandTop = this.setPosition(RadarHack.instance.staticPositon.getValue(), RadarHack.instance.alignment.getValue(), RadarHack.instance.expand.getValue());
		
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
			int txtCol = ClickGUIHack.normTextColor();
			for(String s : players.values()) {
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
	}
	
	public void writeToNBT(NBTTagCompound tag) {
		NBTTagCompound comp = (NBTTagCompound) NBTBase.createTagOfType((byte) 10);
		comp.setInteger("xPos", RadarHack.instance.alignment.getValue() == EnumAlign.RIGHT ? this.xPos + this.width : this.xPos);
		comp.setInteger("yPos", this.yPos);
		comp.setBoolean("Minimized", this.minimized);
		tag.setCompoundTag("Position", comp);
	}

}
