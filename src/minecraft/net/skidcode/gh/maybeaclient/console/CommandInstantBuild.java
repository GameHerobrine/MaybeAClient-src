package net.skidcode.gh.maybeaclient.console;

import lunatrius.schematica.SchematicWorld;
import lunatrius.schematica.Settings;
import net.skidcode.gh.maybeaclient.Client;

public class CommandInstantBuild extends Command{

	public CommandInstantBuild() {
		super("ibuild", "instantly builds schematica (SP only)");
	}

	@Override
	public void onTyped(String[] args) {
		if(Settings.instance().schematic != null) {
			SchematicWorld w = Settings.instance().schematic;
			for(int y = 0; y < w.height; ++y) {
				for(int x = 0; x < w.width; ++x) {
					for(int z = 0; z < w.length; ++z) {
					
						int id = w.getBlockId(x, y, z);
						int meta = w.getBlockMetadata(x, y, z);
						
						int wx = Settings.instance().offset.x;
						int wy = Settings.instance().offset.y;
						int wz = Settings.instance().offset.z;
						Client.mc.theWorld.setBlockAndMetadata(wx+x, wy+y, wz+z, id, meta);
						
					}
				}
			}
			Client.addMessage("Built successfully!");
		}else {
			Client.addMessage("No schematica is loaded");
		}
		
	}

}
