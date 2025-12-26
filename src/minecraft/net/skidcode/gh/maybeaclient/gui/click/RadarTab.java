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
		this.isHUD = true;
	}
	public void renderIngame() {
		if(RadarHack.instance.status) super.renderIngame();
	}
	
	boolean first = true;
	boolean prevMinimized = this.minimized.getValue();
	HashMap<Integer, String> players;
	
	@Override
	public void preRender() {
		int ySpace = ClickGUIHack.theme().yspacing;
		int height = this.getYOffset();
		int width = Client.mc.fontRenderer.getStringWidth(this.getTabName()) + ClickGUIHack.theme().titleXadd;
		boolean showCoords = RadarHack.instance.showXYZ.value;
		
		if(!this.minimized.getValue()) {
			EntityPlayer local = Client.mc.thePlayer;
			players = new HashMap<>();
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
		}
		
		alignRight = this.isAlignedRight(RadarHack.instance.staticPositon.getValue(), RadarHack.instance.alignment.getValue());
		this.setPosition(RadarHack.instance.staticPositon.getValue(), RadarHack.instance.alignment.getValue(), RadarHack.instance.expand.getValue());
		
		this.endX = this.startX + width;
		this.endY = this.startY + height;
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
			
			int txtCol = ClickGUIHack.normTextColor();
			for(String s : players.values()) {
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
