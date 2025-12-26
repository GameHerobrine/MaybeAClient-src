package net.skidcode.gh.maybeaclient.hacks;

import org.lwjgl.input.Keyboard;

import net.skidcode.gh.maybeaclient.Client;
import net.skidcode.gh.maybeaclient.hacks.category.Category;
import net.skidcode.gh.maybeaclient.utils.ChatColor;

public class OnToggleMessageHack extends Hack{
	public static OnToggleMessageHack instance;
	public OnToggleMessageHack() {
		super("OnToggleMessage", "Displays in chat what module player enabled or disabled", Keyboard.KEY_NONE, Category.MISC);
		instance = this;
	}
	public static void toggled(Hack hack) {
		Client.addMessage(ChatColor.GOLD+hack.name+ChatColor.WHITE+" is "+(hack.status ? ChatColor.LIGHTGREEN+"Enabled" : ChatColor.LIGHTRED+"Disabled"));
	}

}
