package net.skidcode.gh.maybeaclient.console;

import net.skidcode.gh.maybeaclient.Client;

public class CommandTeleport extends Command{

	public CommandTeleport() {
		super("teleport", "Teleport player to some position");
	}

	@Override
	public void onTyped(String[] args) {
		int argc = args.length;
		if(argc < 3) {
			Client.addMessage("Usage: .teleport <x> <y> <z>");
		}else {
			try {
				double x = Double.parseDouble(args[0]);
				double y = Double.parseDouble(args[1]);
				double z = Double.parseDouble(args[2]);
				Client.mc.thePlayer.setPosition(x, y, z);
			}catch(NumberFormatException e) {
				Client.addMessage("Incorrect coordinates!");
			}
		}
	}

}
