package net.skidcode.gh.maybeaclient.console;

import net.minecraft.src.ItemMap;
import net.skidcode.gh.maybeaclient.Client;

public class CommandSetMapScale extends Command{

	public CommandSetMapScale() {
		super("setmapscale", "Change map scale (SP only)");
	}

	@Override
	public void onTyped(String[] args) {

		int argc = args.length;
		if(argc < 1) {
			Client.addMessage("Usage: .setmapscale <n>");
			return;
		}
		try {
			int scale = Integer.parseInt(args[0]);
			ItemMap.scale = scale;
			Client.addMessage("Changed map scale to "+scale);
		}catch(Exception e) {
			Client.addMessage("Usage: .setmapscale <n>");
		}
		
	}

}
