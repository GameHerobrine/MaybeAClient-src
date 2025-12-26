package net.skidcode.gh.maybeaclient.hacks;

import org.lwjgl.input.Keyboard;

import net.skidcode.gh.maybeaclient.hacks.category.Category;

public class CharSelectorHack extends Hack{
	
	public static CharSelectorHack instance;
	
	
	public CharSelectorHack() {
		super("CharSelector", "Shows char selector in some gui screens", Keyboard.KEY_NONE, Category.UI);
		instance = this;
	}

	
	
}
