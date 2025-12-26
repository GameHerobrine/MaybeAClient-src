package net.skidcode.gh.maybeaclient.hacks;

import org.lwjgl.input.Keyboard;

import net.skidcode.gh.maybeaclient.hacks.category.Category;

public class NoClipHack extends Hack{
	
	public static NoClipHack instance;
	
	public NoClipHack() {
		super("NoClip", "Allows player to move through blocks", Keyboard.KEY_NONE, Category.MOVEMENT);
		instance = this;
	}

}
