package net.skidcode.gh.maybeaclient;

import net.minecraft.src.EntityClientPlayerMP;
import net.minecraft.src.Packet10Flying;
import net.minecraft.src.Packet12PlayerLook;
import net.minecraft.src.Packet13PlayerLookMove;
import net.skidcode.gh.maybeaclient.events.Event;
import net.skidcode.gh.maybeaclient.events.EventListener;
import net.skidcode.gh.maybeaclient.events.EventRegistry;
import net.skidcode.gh.maybeaclient.events.impl.EventPacketReceive;
import net.skidcode.gh.maybeaclient.events.impl.EventPacketSend;

public class MovePacketHook implements EventListener{
	public static final MovePacketHook INSTANCE = new MovePacketHook();
	
	public MovePacketHook() {
		EventRegistry.registerListener(EventPacketReceive.class, this);
		EventRegistry.registerListener(EventPacketSend.class, this);
	}
	
	public static float yaw;
	public static float pitch;
	public static boolean overrideYaw = false;
	public static boolean overridePitch = false;
	public static void setNextYawAndPitch(float yaw, float pitch) {
		if(!Client.mc.isMultiplayerWorld()) return;
		MovePacketHook.yaw = yaw;
		MovePacketHook.pitch = pitch;
		overrideYaw = overridePitch = true;
	}
	
	@Override
	public void handleEvent(Event event) {
		if(event instanceof EventPacketSend && ((EventPacketSend)event).packet instanceof Packet10Flying) {
			Packet10Flying pk = (Packet10Flying) ((EventPacketSend)event).packet;
			if(overrideYaw || overridePitch) {
				if(!pk.rotating) {
					event.cancelled = true;
					Packet10Flying npk = null;
					if(pk.moving) {
						npk = new Packet13PlayerLookMove(pk.xPosition, pk.yPosition, pk.stance, pk.zPosition, Client.mc.thePlayer.rotationYaw, Client.mc.thePlayer.rotationPitch, pk.onGround);
					}else {
						npk = new Packet12PlayerLook(Client.mc.thePlayer.rotationYaw, Client.mc.thePlayer.rotationPitch, pk.onGround);
					}
					Client.mc.getSendQueue().addToSendQueue(npk);
				}
				
				pk.rotating = true;
				if(overrideYaw) pk.yaw = yaw;
				else pk.yaw = Client.mc.thePlayer.rotationYaw;
				if(overridePitch) pk.pitch = pitch;
				else pk.pitch = Client.mc.thePlayer.rotationPitch;
				overrideYaw = overridePitch = false;
				if(Client.mc.thePlayer instanceof EntityClientPlayerMP) {
					EntityClientPlayerMP ep = (EntityClientPlayerMP) Client.mc.thePlayer;
					ep.oldRotationYaw = pk.yaw;
					ep.oldRotationPitch = pk.pitch;
				}
			}
		}
	}

	public static void setNextPitch(float f) {
		MovePacketHook.pitch = f;
		//Client.mc.thePlayer.rotationPitch = Client.mc.thePlayer.prevRotationPitch = f;
		overridePitch = true;
	}

	public static void setNextYaw(float f) {
		MovePacketHook.yaw = f;
		//Client.mc.thePlayer.rotationYaw = Client.mc.thePlayer.prevRotationYaw = f;
		overrideYaw = true;
	}
}
