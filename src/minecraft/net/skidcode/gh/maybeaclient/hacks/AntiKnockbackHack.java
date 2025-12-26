package net.skidcode.gh.maybeaclient.hacks;

import org.lwjgl.input.Keyboard;

import net.minecraft.src.Packet28EntityVelocity;
import net.skidcode.gh.maybeaclient.events.Event;
import net.skidcode.gh.maybeaclient.events.EventListener;
import net.skidcode.gh.maybeaclient.events.EventRegistry;
import net.skidcode.gh.maybeaclient.events.impl.EventPacketReceive;
import net.skidcode.gh.maybeaclient.hacks.category.Category;

public class AntiKnockbackHack extends Hack implements EventListener{

	public AntiKnockbackHack() {
		super("AntiKnockback", "Prevents player from being knocked back", Keyboard.KEY_NONE, Category.COMBAT);
		
		EventRegistry.registerListener(EventPacketReceive.class, this);
	}

	@Override
	public void handleEvent(Event event) {
		if(event instanceof EventPacketReceive) {
			EventPacketReceive ev = (EventPacketReceive) event;
			if(ev.packet instanceof Packet28EntityVelocity) {
				event.cancelled = true;
			}
		}
	}

}
