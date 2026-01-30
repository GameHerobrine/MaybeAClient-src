package net.skidcode.gh.maybeaclient.hacks;

import org.lwjgl.input.Keyboard;

import net.skidcode.gh.maybeaclient.hacks.category.Category;

public class FastCraftHack extends Hack{
	public static FastCraftHack instance;
	public FastCraftHack() {
		super("FastCraft", "Allows to craft faster", Keyboard.KEY_NONE, Category./*NOT_*/MISC);
		instance = this;
	}

}
