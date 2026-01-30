package net.skidcode.gh.maybeaclient.gui.click;

import java.util.ArrayList;

import net.minecraft.src.ScaledResolution;
import net.skidcode.gh.maybeaclient.Client;
import net.skidcode.gh.maybeaclient.gui.click.element.TextElement;
import net.skidcode.gh.maybeaclient.gui.click.element.VerticalContainer;
import net.skidcode.gh.maybeaclient.hacks.ArrayListHack;
import net.skidcode.gh.maybeaclient.hacks.ClickGUIHack;
import net.skidcode.gh.maybeaclient.hacks.ClientInfoHack;
import net.skidcode.gh.maybeaclient.hacks.settings.enums.EnumAlign;
import net.skidcode.gh.maybeaclient.hacks.settings.enums.EnumStaticPos;
import net.skidcode.gh.maybeaclient.utils.ChatColor;
import net.skidcode.gh.maybeaclient.utils.PlayerUtils;

public class ClientInfoTab extends ElementTab{
	
	public static ClientInfoTab instance;
	
	
	public TextElement coordX, coordY, coordZ, yaw, pitch, biome;
	public TextElement facing, fps, username, walkingSpeed;
	public VerticalContainer vc = new VerticalContainer();
	
	public ClientInfoTab() {
		super("Player info");
		this.xDefPos = this.startX = 255;
		this.yDefPos = this.startY = 10;
		instance = this;
		this.addElement(vc);
		vc.addElement(this.coordX = new TextElement(""));
		vc.addElement(this.coordY = new TextElement(""));
		vc.addElement(this.coordZ = new TextElement(""));
        vc.addElement(this.yaw = new TextElement(""));
        vc.addElement(this.pitch = new TextElement(""));
		vc.addElement(this.facing = new TextElement(""));
		vc.addElement(this.biome = new TextElement(""));
		vc.addElement(this.fps = new TextElement(""));
		vc.addElement(this.username = new TextElement(""));
		vc.addElement(this.walkingSpeed = new TextElement(""));
		this.isHUD = true;
	}
	
	public void renderIngame() {
		if(ClientInfoHack.instance.status) super.renderIngame();
	}
	
	boolean first = true;
	public ArrayList<String> toRender;
	
	@Override
	public void preRender() {
		//this.width = Client.mc.fontRenderer.getStringWidth(this.name) + ClickGUIHack.theme().titleXadd;
		this.coordX.shown = this.coordY.shown = this.coordZ.shown = ClientInfoHack.instance.coords.value;
        this.yaw.shown = this.pitch.shown = ClientInfoHack.instance.rotation.value;
		this.facing.shown = ClientInfoHack.instance.facing.value;
		this.biome.shown = ClientInfoHack.instance.biome.value;
		this.fps.shown = ClientInfoHack.instance.fps.value;
		this.username.shown = ClientInfoHack.instance.username.value;
		this.walkingSpeed.shown = ClientInfoHack.instance.walkingSpeed.value;

        this.yaw.text = String.format("Yaw: %.1f", Client.mc.thePlayer.rotationYaw);
        this.pitch.text = String.format("Pitch: %.1f", Client.mc.thePlayer.rotationPitch);
		this.facing.text = String.format("Facing: %s", PlayerUtils.getDirection());
		this.biome.text = "Biome: " + Client.mc.theWorld.getWorldChunkManager().getBiomeGenAt(
				(int) Client.mc.thePlayer.posX, (int) Client.mc.thePlayer.posZ).biomeName;
		this.fps.text = String.format("FPS: %s", Client.mc.fps);
		this.username.text = String.format("Username: %s", Client.mc.session.username);
		this.walkingSpeed.text = String.format("Speed: %.4f BPS", PlayerUtils.getSpeed(ClientInfoHack.instance.useHorizontal.value));	
		
		//this.height = baseOff;
		if(this.coordX.shown) {
			if(ClientInfoHack.instance.showNetherCoords.value) {
				boolean inNether = Client.mc.theWorld.worldProvider.isHellWorld;
				if(inNether) {
					this.coordX.text = String.format("X: %.2f %s%.2f", Client.mc.thePlayer.posX, ChatColor.LIGHTGREEN, Client.mc.thePlayer.posX*8);
					this.coordY.text = String.format("Y: %.2f %s%.2f", Client.mc.thePlayer.posY, ChatColor.LIGHTGREEN, Client.mc.thePlayer.posY);
					this.coordZ.text = String.format("Z: %.2f %s%.2f", Client.mc.thePlayer.posZ, ChatColor.LIGHTGREEN, Client.mc.thePlayer.posZ*8);
				}else {
					this.coordX.text = String.format("X: %.2f %s%.2f", Client.mc.thePlayer.posX, ChatColor.LIGHTRED, Client.mc.thePlayer.posX/8);
					this.coordY.text = String.format("Y: %.2f %s%.2f", Client.mc.thePlayer.posY, ChatColor.LIGHTRED, Client.mc.thePlayer.posY);
					this.coordZ.text = String.format("Z: %.2f %s%.2f", Client.mc.thePlayer.posZ, ChatColor.LIGHTRED, Client.mc.thePlayer.posZ/8);
				}
			}else {
				this.coordX.text = String.format("X: %.2f", Client.mc.thePlayer.posX);
				this.coordY.text = String.format("Y: %.2f", Client.mc.thePlayer.posY);
				this.coordZ.text = String.format("Z: %.2f", Client.mc.thePlayer.posZ);
			}
		}
		
		this.setPosition(ClientInfoHack.instance.staticPositon.getValue(), ClientInfoHack.instance.alignment.getValue());
		alignRight = this.isAlignedRight(ClientInfoHack.instance.staticPositon.getValue(), ClientInfoHack.instance.alignment.getValue());
		
		super.preRender();
		
	}
}
