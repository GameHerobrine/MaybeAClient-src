package net.skidcode.gh.maybeaclient.console;

import java.util.Map;

import org.lwjgl.input.Keyboard;

import net.skidcode.gh.maybeaclient.Client;
import net.skidcode.gh.maybeaclient.gui.GuiBindModule;
import net.skidcode.gh.maybeaclient.hacks.Hack;
import net.skidcode.gh.maybeaclient.hacks.settings.Setting;
import net.skidcode.gh.maybeaclient.utils.ChatColor;

public class CommandModule extends Command{

	public CommandModule() {
		super("module", "Modules settings");
	}

	@Override
	public void onTyped(String[] args) {
		int argc = args.length;
		
		if(argc < 1 || args[0].equalsIgnoreCase("help")) {
			args = new String[] {"help"};
			
		}
		
		
		switch(args[0]) {
			case "list":
				String result = "Available modules: ";
				for(Map.Entry<String, Hack> entry : Client.hacksByName.entrySet()) {
					result += (entry.getValue().status ? ChatColor.LIGHTGREEN : ChatColor.LIGHTRED)+entry.getValue().name + ChatColor.WHITE+", ";
				}
				result = result.substring(0, result.length() - 2);
				Client.addMessage(result);
				break;
			case "toggle":
				if(argc < 2) {
					Client.addMessage("Not enough arguments!");
				}else {
					String name = args[1].toLowerCase();
					Hack h = Client.hacksByName.get(name);
					
					if(h == null) {
						Client.addMessage("Module "+ChatColor.GOLD+args[1]+ChatColor.WHITE+" is not found!");
					}else {
						h.toggle();
						Client.addMessage("Module "+ChatColor.GOLD+h.name+ChatColor.WHITE+" is now "+(h.status ? ChatColor.LIGHTGREEN+"Enabled" : ChatColor.LIGHTRED+"Disabled"));
					}
				}
				break;
			case "gbind":
				if(argc < 2) {
					Client.addMessage("Not enough arguments!");
				}else {
					String name = args[1].toLowerCase();
					Hack h = Client.hacksByName.get(name);
					
					if(h == null) {
						Client.addMessage("Module "+ChatColor.GOLD+args[1]+ChatColor.WHITE+" is not found!");
					}else {
						Client.mc.displayGuiScreen(new GuiBindModule(null, h));
					}
				}
				break;
			case "bind":
				if(argc < 3) {
					Client.addMessage("Not enough arguments!");
				}else {
					String name = args[1].toLowerCase();
					Hack h = Client.hacksByName.get(name);
					
					if(h == null) {
						Client.addMessage("Module "+ChatColor.GOLD+args[1]+ChatColor.WHITE+" is not found!");
					}else {
						String keybindRaw = args[2].toUpperCase();
						int bind;
						i_love_freecliff:
						try {
							bind = Integer.parseInt(keybindRaw);
						}catch(NumberFormatException e) {
							mouse_check:
							if(keybindRaw.startsWith("MOUSE_")) {
								try {
									bind = Client.getKeycodeForMouseButton(Integer.parseInt(keybindRaw.substring(6)));
									break i_love_freecliff;
								}catch(NumberFormatException ee) {}
							}
							if(!keybindRaw.startsWith("KEY_")) {
								Client.addMessage("Invalid keyname format.");
								break;
							}
							
							bind = Keyboard.getKeyIndex(keybindRaw.substring(4));
						}
						h.bind(bind);
					}
				}
				break;
			case "reset":
				if(argc < 3) {
					Client.addMessage("Not enough arguments!");
				}else {
					String name = args[1].toLowerCase();
					String settingName = args[2].toLowerCase();
					Hack h = Client.hacksByName.get(name);
					
					if(h == null) {
						Client.addMessage("Module "+ChatColor.GOLD+args[1]+ChatColor.WHITE+" is not found!");
					}else if(!h.hasSettings){
						Client.addMessage("Module "+ChatColor.GOLD+h.name+ChatColor.WHITE+" has no settings!");
					}else if(h.settings.get(settingName) == null){
						Client.addMessage("Module "+ChatColor.GOLD+h.name+ChatColor.WHITE+" doesn't have a setting named "+ChatColor.GOLD+args[2]+ChatColor.WHITE+"!");
					}else {
						Setting s = h.settings.get(settingName);
						s.reset();
						Client.addMessage("Setting "+ChatColor.GOLD+s.noWhitespacesName+ChatColor.WHITE+" was changed to "+ChatColor.GOLD+s.valueToString()+ChatColor.WHITE+"!");
					}
				}
				break;
			case "set":
				if(argc < 4) {
					Client.addMessage("Not enough arguments!");
				}else {
					String name = args[1].toLowerCase();
					String settingName = args[2].toLowerCase();
					Hack h = Client.hacksByName.get(name);
					
					if(h == null) {
						Client.addMessage("Module "+ChatColor.GOLD+args[1]+ChatColor.WHITE+" is not found!");
					}else if(!h.hasSettings){
						Client.addMessage("Module "+ChatColor.GOLD+h.name+ChatColor.WHITE+" has no settings!");
					}else if(h.settings.get(settingName) == null){
						Client.addMessage("Module "+ChatColor.GOLD+h.name+ChatColor.WHITE+" doesn't have a setting named "+ChatColor.GOLD+args[2]+ChatColor.WHITE+"!");
					}else {
						Setting s = h.settings.get(settingName);
						String valueRaw = args[3];
						for(int i = 4; i < args.length; ++i) valueRaw += " "+args[i];
						if(!s.validateValue(valueRaw)) {
							Client.addMessage("Invalid setting value!");
							break;
						}
						s.setValue_(valueRaw);
						Client.saveModules();
						Client.addMessage("Setting "+ChatColor.GOLD+s.noWhitespacesName+ChatColor.WHITE+" was changed to "+ChatColor.GOLD+s.valueToString()+ChatColor.WHITE+"!");
					}
				}
				break;
			case "info":
				if(argc < 2) {
					Client.addMessage("Not enough arguments!");
				}else {
					String name = args[1].toLowerCase();
					Hack h = Client.hacksByName.get(name);
					
					if(h == null) {
						Client.addMessage("Module "+ChatColor.GOLD+args[1]+ChatColor.WHITE+" is not found!");
					}else {
						Client.addMessage(ChatColor.GOLD+h.name);
						Client.addMessage("Description: "+ChatColor.GOLD+h.description);
						Client.addMessage("Status: "+(h.status ? ChatColor.LIGHTGREEN+"Enabled" : ChatColor.LIGHTRED+"Disabled"));
						//Client.addMessage("Keybind: "+ChatColor.GOLD+Keyboard.getKeyName(h.keybinding.));
						String s = "Settings: ";
						boolean hazSettings = false;
						for(Setting set : h.settings.values()) {
							if(!set.hidden) {
								s += ChatColor.GOLD+set.noWhitespacesName+ChatColor.WHITE+"["+set.valueToStringConsole()+ChatColor.WHITE+"], ";
								hazSettings = true;
							}
						}
						if(hazSettings){
							Client.addMessage(s.substring(0, s.length()-2));
						}
					}
				}
				break;
			default:
				Client.addMessage(".module list - List available modules");
				Client.addMessage(".module info <modulename> - Get information about module");	
				Client.addMessage(".module toggle <modulename> - Toggle module by name");
				Client.addMessage(".module set <modulename> <settingname> <value> - Change module setting value");
				Client.addMessage(".module reset <modulename> <settingname> - Reset module setting to default");
				Client.addMessage(".module bind <modulename> <keycode|keyname> - Bind module to some key. The keyboard key name must start with 'KEY_'. The mouse key name must start with 'MOUSE_'.");
				Client.addMessage(".module gbind <modulename> - Bind module to a key using gui.");	
				break;
		}
		
	}

}
