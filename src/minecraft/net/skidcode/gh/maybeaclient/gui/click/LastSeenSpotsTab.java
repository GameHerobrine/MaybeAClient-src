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
		this.minimized.setValue(false);
		this.xDefPos = this.startX = 160;
		this.yDefPos = this.startY = 24 + 14*7;
		instance = this;
		this.isHUD = true;
	}
	public void renderIngame() {
		if(LastSeenSpotsHack.instance.status) super.renderIngame();
	}
	
	boolean first = true;
	boolean prevMinimized = this.minimized.getValue();
	
	ArrayList<String> players;
	public void preRender() {
		int ySpace = ClickGUIHack.theme().yspacing;
		
		EntityPlayer local = Client.mc.thePlayer;
		players = new ArrayList<String>();
		
		int height = this.getYOffset();
		int width =  Client.mc.fontRenderer.getStringWidth(this.getTabName()) + ClickGUIHack.theme().titleXadd;
		for(Map.Entry<String, LastSeenSpotsHack.PlayerInfo> n_pi : LastSeenSpotsHack.instance.players.entrySet()) {
			String s = n_pi.getKey();
			LastSeenSpotsHack.PlayerInfo pi = n_pi.getValue();
			s += " XYZ: "+ String.format("%s%.2f %.2f %.2f", ChatColor.custom(ClickGUIHack.highlightedTextColor()), pi.x, pi.y, pi.z);
			players.add(s);
			int w = Client.mc.fontRenderer.getStringWidth(s) + 2;
			if(w > width) width = w;
			height += ySpace;
		}

		if(this.minimized.getValue()) {
			width = Client.mc.fontRenderer.getStringWidth(this.getTabName()) + ClickGUIHack.theme().titleXadd;
		}
		
		alignRight = this.isAlignedRight(EnumStaticPos.DISABLED, LastSeenSpotsHack.instance.alignment.getValue());
		boolean expandTop = this.setPosition(EnumStaticPos.DISABLED, LastSeenSpotsHack.instance.alignment.getValue(), LastSeenSpotsHack.instance.expand.getValue());
		this.tabMinimize.alignRight = alignRight;
		
		this.endY = this.startY +  height;
		this.endX = this.startX + width;
		super.preRender();
	}
	boolean alignRight;
	@Override
	public void render() {
		int ySpace = ClickGUIHack.theme().yspacing;
		int txtCenter = ClickGUIHack.theme().yaddtocenterText;
		if(this.minimized.getValue()) {
			this.renderMinimized();
			return;
		}
		
		if(players.size() > 0) {
			int h = this.getYOffset();
			Tab.renderFrame(this, this.startX, this.startY + h, this.endX, this.endY);
			for(String s : players) {
				if(alignRight) {
					Client.mc.fontRenderer.drawString(s, this.endX - Client.mc.fontRenderer.getStringWidth(s), this.startY + h + txtCenter, ClickGUIHack.normTextColor());
				}else {
					Client.mc.fontRenderer.drawString(s, this.startX + 2, this.startY + h + txtCenter, ClickGUIHack.normTextColor());
				}
				
				h += ySpace;
			}
			Tab.renderFrameTop(this, this.startX, this.startY + this.getYOffset(), this.endX, this.endY);
		}
		
		this.renderName(alignRight);
		prevMinimized = this.minimized.getValue();
	}
}
