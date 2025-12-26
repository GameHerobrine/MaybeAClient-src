package net.skidcode.gh.maybeaclient.hacks;

import org.lwjgl.input.Keyboard;

import net.skidcode.gh.maybeaclient.hacks.category.Category;

public class InventoryTweaksHack extends Hack{
	public static InventoryTweaksHack instance;
	public InventoryTweaksHack() {
		super("InventoryTweaks", "Some tweaks for inventory(might work only in Uberbukkit)", Keyboard.KEY_NONE , Category.MISC);
		instance = this;
	}

}
