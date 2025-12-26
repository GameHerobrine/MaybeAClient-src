package net.skidcode.gh.maybeaclient.hacks;

import org.lwjgl.input.Keyboard;

import net.minecraft.src.Packet10Flying;
import net.skidcode.gh.maybeaclient.events.Event;
import net.skidcode.gh.maybeaclient.events.EventListener;
import net.skidcode.gh.maybeaclient.events.EventRegistry;
import net.skidcode.gh.maybeaclient.events.impl.EventMPMovementUpdate;
import net.skidcode.gh.maybeaclient.hacks.category.Category;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingInteger;

public class FastPortalHack extends Hack implements EventListener{
	public static FastPortalHack instance;
	public SettingInteger packets = new SettingInteger(this, "Packets", 20, 10, 30);
	public FastPortalHack() {
		super("FastPortal", "Decreases time player has to wait before getting teleported", Keyboard.KEY_NONE, Category.MISC);
		instance = this;
		this.addSetting(this.packets);
		EventRegistry.registerListener(EventMPMovementUpdate.class, this);
	}

	@Override
	public void handleEvent(Event event) {
		if(event instanceof EventMPMovementUpdate) {
			if (mc.thePlayer.timeInPortal > 0.0f && mc.thePlayer.field_28024_y == 0) {
				for(int i = 0; i < this.packets.getValue(); ++i) mc.getSendQueue().addToSendQueue(new Packet10Flying(true));
			}
		}
	}

}
