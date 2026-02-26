package net.skidcode.gh.maybeaclient.hacks;

import org.lwjgl.input.Keyboard;

import net.minecraft.src.GuiChest;
import net.minecraft.src.ItemStack;
import net.minecraft.src.MathHelper;
import net.minecraft.src.Packet;
import net.minecraft.src.Packet103SetSlot;
import net.minecraft.src.Packet22Collect;
import net.skidcode.gh.maybeaclient.events.Event;
import net.skidcode.gh.maybeaclient.events.EventListener;
import net.skidcode.gh.maybeaclient.events.EventRegistry;
import net.skidcode.gh.maybeaclient.events.impl.EventPacketReceive;
import net.skidcode.gh.maybeaclient.events.impl.EventPlayerUpdatePost;
import net.skidcode.gh.maybeaclient.hacks.category.Category;
import net.skidcode.gh.maybeaclient.utils.BlockPos;

public class AutoStoreHack extends Hack implements EventListener{
	public static AutoStoreHack instance;
	public AutoStoreHack() {
		super("AutoStore", "Automatically stores picked up items into the opened chest", Keyboard.KEY_NONE, Category.MISC);
		instance = this;
		EventRegistry.registerListener(EventPacketReceive.class, this);
		
	}
	
	public boolean awaiting = false;
	
	
	@Override
	public void handleEvent(Event event) {
		if(event instanceof EventPacketReceive) {
			Packet pk = ((EventPacketReceive) event).packet;
			if(pk instanceof Packet22Collect) {
				awaiting = true;
			}
		}
	}
	
	
	public void handlePickup(Packet103SetSlot var1) {
		
		if(mc.currentScreen instanceof GuiChest) {
			GuiChest chest = (GuiChest) mc.currentScreen;
			if (var1.windowId == -1) {
				//ignore: should never occur when picking up items
	        } else if (var1.windowId == 0 && var1.itemSlot >= 36 && var1.itemSlot < 45) {
	        	//ignore: doesnt seem to happen when chest gui is opened
	        } else if (var1.windowId == mc.thePlayer.craftingInventory.windowId) {
				mc.playerController.func_27174_a(chest.inventorySlots.windowId, var1.itemSlot, 0, true, mc.thePlayer);
	        }
		}
		
		
	}
}
