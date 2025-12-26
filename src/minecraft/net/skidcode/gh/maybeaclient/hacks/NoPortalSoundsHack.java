package net.skidcode.gh.maybeaclient.hacks;

import org.lwjgl.input.Keyboard;

import net.skidcode.gh.maybeaclient.hacks.category.Category;

public class NoPortalSoundsHack extends Hack{
	public static NoPortalSoundsHack instance;
	public NoPortalSoundsHack() {
		super("NoPortalSounds", "Removes portal sounds", Keyboard.KEY_NONE, Category.MISC);
		instance = this;
	}

}
