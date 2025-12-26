package net.skidcode.gh.maybeaclient.hacks;

import org.lwjgl.input.Keyboard;

import net.minecraft.src.GuiInventory;
import net.minecraft.src.Packet101CloseWindow;
import net.skidcode.gh.maybeaclient.events.EventListener;
import net.skidcode.gh.maybeaclient.events.EventRegistry;
import net.skidcode.gh.maybeaclient.events.impl.EventPacketSend;
import net.skidcode.gh.maybeaclient.hacks.category.Category;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingBoolean;

public class XCarryHack extends Hack implements EventListener<EventPacketSend>{
	public SettingBoolean alwaysCancelPacket = new SettingBoolean(this, "Always cancel packet", false);
	public XCarryHack() {
		super("XCarry", "Allows to use crafting slots to store items", Keyboard.KEY_NONE, Category.MISC);
		this.addSetting(this.alwaysCancelPacket);
		EventRegistry.registerListener(EventPacketSend.class, this);
	}
	
	@Override
	public void handleEvent(EventPacketSend e) {
		if(e.packet instanceof Packet101CloseWindow && (this.alwaysCancelPacket.value || mc.currentScreen instanceof GuiInventory)) {
			e.cancelled = true;
		}
	}
}
