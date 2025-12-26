package net.skidcode.gh.maybeaclient.hacks;

import org.lwjgl.input.Keyboard;
import net.skidcode.gh.maybeaclient.hacks.category.Category;

public class NoFriendlyFireHack extends Hack{
	public static NoFriendlyFireHack instance;
	public NoFriendlyFireHack() {
		super("NoFriendlyFire", "Prevents you from attacking friends", Keyboard.KEY_NONE, Category.COMBAT);
		instance = this;
	}
}
