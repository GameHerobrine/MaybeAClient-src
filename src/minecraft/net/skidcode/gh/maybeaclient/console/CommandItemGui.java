package net.skidcode.gh.maybeaclient.console;

import net.skidcode.gh.maybeaclient.Client;
import net.skidcode.gh.maybeaclient.gui.GuiItemGive;
import net.skidcode.gh.maybeaclient.gui.ItemInventory;

public class CommandItemGui extends Command{

	public CommandItemGui() {
		super("igui", "Open item gui (SP only)");
	}

	@Override
	public void onTyped(String[] args) {
		Client.mc.displayGuiScreen(new GuiItemGive(Client.mc.thePlayer.inventory,new ItemInventory(1)));
	}

}
