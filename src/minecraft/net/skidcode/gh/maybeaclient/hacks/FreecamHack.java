package net.skidcode.gh.maybeaclient.hacks;

import org.lwjgl.input.Keyboard;

import net.minecraft.src.Packet0KeepAlive;
import net.minecraft.src.Packet10Flying;
import net.skidcode.gh.maybeaclient.events.Event;
import net.skidcode.gh.maybeaclient.events.EventListener;
import net.skidcode.gh.maybeaclient.events.EventRegistry;
import net.skidcode.gh.maybeaclient.events.impl.EventMPMovementUpdate;
import net.skidcode.gh.maybeaclient.events.impl.EventPacketSend;
import net.skidcode.gh.maybeaclient.events.impl.EventPlayerUpdatePost;
import net.skidcode.gh.maybeaclient.hacks.category.Category;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingBoolean;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingDouble;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingMode;

public class FreecamHack extends Hack implements EventListener{
	
	//TODO old/new mode maybe?
	//public SettingMode mode = new SettingMode(this, "Mode", "Old", "New");
	public SettingDouble speedMultiplier = new SettingDouble(this, "Speed Multiplier", 1, 0.1, 2.0);
	public SettingBoolean noclip = new SettingBoolean(this, "NoClip", true);
	public SettingBoolean resetPositonOnDisable = new SettingBoolean(this, "ResetPositionOnDisable", true);
	
	public double posX, posY, posZ;
	public static boolean hasXYZ = false;
	
	public static FreecamHack instance;
	
	public FreecamHack() {
		super("Freecam", "Allows players to fly around the world", Keyboard.KEY_G, Category.MISC);
		FreecamHack.instance = this;
		this.addSetting(this.speedMultiplier);
		this.addSetting(this.noclip);
		this.addSetting(this.resetPositonOnDisable);
		
		EventRegistry.registerListener(EventPacketSend.class, this);
		EventRegistry.registerListener(EventMPMovementUpdate.class, this);
		EventRegistry.registerListener(EventPlayerUpdatePost.class, this);
	}
	public void onEnable() {
		this.posX = mc.thePlayer.posX;
		this.posY = mc.thePlayer.posY;
		this.posZ = mc.thePlayer.posZ;
	}
	
	public void onDisable() {
		if(this.resetPositonOnDisable.getValue()) {
			mc.thePlayer.setPosition(this.posX, this.posY, this.posZ);
		}
		mc.thePlayer.motionX = 0.F;
		mc.thePlayer.motionY = 0.F;
		mc.thePlayer.motionZ = 0.F;
	}
	@Override
	public void handleEvent(Event event) {
		if(event instanceof EventMPMovementUpdate){
			mc.getSendQueue().addToSendQueue(new Packet0KeepAlive());
		}else if(event instanceof EventPacketSend) {
			EventPacketSend ev = (EventPacketSend) event;
			if(ev.packet instanceof Packet10Flying) { //packet is movement
				if(!hasXYZ) {
					this.posX = mc.thePlayer.posX;
					this.posY = mc.thePlayer.posY;
					this.posZ = mc.thePlayer.posZ;
					hasXYZ = true;
				}else {
					ev.cancelled = true;
				}
			}
		}else if(event instanceof EventPlayerUpdatePost) {
			FlyHack.handleFly(this.speedMultiplier.value); 
		}
	}
	public static boolean movementTaken() {
		return false; //instance.status && instance.mode.currentMode.equalsIgnoreCase("New");
	}
}
