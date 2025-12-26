package net.skidcode.gh.maybeaclient.hacks;

import org.lwjgl.input.Keyboard;

import net.minecraft.src.Packet28EntityVelocity;
import net.skidcode.gh.maybeaclient.events.Event;
import net.skidcode.gh.maybeaclient.events.EventListener;
import net.skidcode.gh.maybeaclient.events.EventRegistry;
import net.skidcode.gh.maybeaclient.events.impl.EventPacketReceive;
import net.skidcode.gh.maybeaclient.hacks.category.Category;

public class NoPushHack extends Hack implements EventListener{
	
	public static NoPushHack instance;
	
	public NoPushHack() {
		super("NoPush", "Disables pushing", Keyboard.KEY_NONE, Category.MISC);
		instance = this;
		EventRegistry.registerListener(EventPacketReceive.class, this);
	}

	@Override
	public void handleEvent(Event event) {
		if(event instanceof EventPacketReceive) {
			EventPacketReceive e = (EventPacketReceive) event;
			if(e.packet instanceof Packet28EntityVelocity) {
				e.cancelled = true;
			}
		}
	}

}
