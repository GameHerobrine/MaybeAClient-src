package net.skidcode.gh.maybeaclient.hacks;

import org.lwjgl.input.Keyboard;

import net.skidcode.gh.maybeaclient.hacks.category.Category;

public class DogOwnerHack extends Hack{
	public static DogOwnerHack instance;
	public DogOwnerHack() {
		super("DogOwner", "Shows dog owner", Keyboard.KEY_NONE, Category.RENDER);
		instance = this;
	}

}
