package net.skidcode.gh.maybeaclient;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import javax.imageio.ImageIO;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.src.Block;
import net.minecraft.src.BlockBed;
import net.minecraft.src.BlockButton;
import net.minecraft.src.BlockCake;
import net.minecraft.src.BlockChest;
import net.minecraft.src.BlockDispenser;
import net.minecraft.src.BlockDoor;
import net.minecraft.src.BlockFurnace;
import net.minecraft.src.BlockJukeBox;
import net.minecraft.src.BlockLever;
import net.minecraft.src.BlockNote;
import net.minecraft.src.BlockRedstoneOre;
import net.minecraft.src.BlockRedstoneRepeater;
import net.minecraft.src.BlockTrapDoor;
import net.minecraft.src.BlockWorkbench;
import net.minecraft.src.MapColor;
import net.minecraft.src.Material;
import net.minecraft.src.NBTBase;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NBTTagInt;
import net.minecraft.src.NBTTagList;
import net.minecraft.src.NBTTagString;
import net.minecraft.src.RenderManager;
import net.minecraft.src.ScaledResolution;
import net.skidcode.gh.maybeaclient.console.*;
import net.skidcode.gh.maybeaclient.gui.altman.AccountInfo;
import net.skidcode.gh.maybeaclient.gui.altman.GuiAccManager;
import net.skidcode.gh.maybeaclient.gui.altman.PasswordInfo;
import net.skidcode.gh.maybeaclient.gui.altman.PasswordInfo.MatchMode;
import net.skidcode.gh.maybeaclient.gui.click.ClickGUI;
import net.skidcode.gh.maybeaclient.gui.click.Tab;
import net.skidcode.gh.maybeaclient.gui.server.GuiServerSelector;
import net.skidcode.gh.maybeaclient.gui.server.ServerInfo;
import net.skidcode.gh.maybeaclient.hacks.*;
import net.skidcode.gh.maybeaclient.hacks.settings.Setting;
import net.skidcode.gh.maybeaclient.shaders.Framebuffer;
import net.skidcode.gh.maybeaclient.shaders.Shaders;
import net.skidcode.gh.maybeaclient.utils.ChatColor;
import net.skidcode.gh.maybeaclient.utils.TextureUtils;

public class Client {
	public static HashMap<String, Hack> hacksByName = new HashMap<>();
	public static HashMap<String, Command> commands = new HashMap<>();
	
	public static HashMap<String, String> aliases = new HashMap<>();
	public static ArrayList<String> aliasesList = new ArrayList<>();
	
	public static String cmdPrefix = ".";
	public static Minecraft mc;
	public static final String clientName = "MaybeAClient";
	public static final String clientVersion = "4.0.5";
	
	public static final int saveVersion = 4;
	/*
	 * Save format 2(1.3):
	 *  * SettingColor saves the data as CompoundTag instead of ByteArray
	 * Save format 3(1.5):
	 *  * ClickGUI tabs save priority into the tag.
	 * Save format 4(3.0):
	 *  * Tabs save all settings
	 */
	
	public static int convertingVersion = 0;
	
	public static void registerHack(Hack hack) {
		Client.hacksByName.put(hack.name.toLowerCase(), hack);
	}
	
	public static void registerCommand(Command cmd) {
		Client.commands.put(cmd.name.toLowerCase(), cmd);
	}
	
	public static void addMessage(String msg) {
		addMessageRaw(String.format("%s[%s]:%s %s", ChatColor.CYAN, ClientNameHack.instance.overrideInChat() ? ClientNameHack.instance.clientName() : Client.clientName, ChatColor.WHITE, msg));
	}
	
	public static void addMessageRaw(String msg) {
		mc.thePlayer.addChatMessage(msg);
	}
	public static void handleCommand(String command) {
		handleCommand(command, true);
	}
	public static void handleCommand(String command, boolean sendToChat) {
		String[] cmds = command.split(" ");
		int parcnt = cmds.length;
		if(sendToChat) mc.thePlayer.addChatMessageWithMoreOpacityBG(ChatColor.LIGHTGRAY+Client.cmdPrefix+command);
		Command cmd = Client.commands.get(cmds[0].toLowerCase());
		
		if(cmd == null) {
			
			String cmdalias = Client.aliases.get(cmds[0].toLowerCase());
			if(cmdalias != null) {
				String newcommand = cmdalias;
				for(int i = 1; i < cmds.length; ++i) {
					newcommand += " " + cmds[i];
				}
				
				Client.handleCommand(newcommand, false);
			}else {
				Client.addMessage("Invalid command: "+ChatColor.GOLD+cmds[0]+ChatColor.WHITE+". Use "+ChatColor.GOLD+Client.cmdPrefix+"help"+ChatColor.WHITE+" to view the available commands.");
			}
		
		}else {
			String[] args = Arrays.copyOfRange(cmds, 1, cmds.length);
			cmd.onTyped(args);
		}
		
	}
	
	public static String getAlias(String alias) {
		return Client.aliases.get(alias.toLowerCase());
	}
	public static void removeAlias(String alias) {
		alias = alias.toLowerCase();
		Client.aliases.remove(alias);
		if(Client.aliasesList.contains(alias)) {
			Client.aliasesList.remove(alias);
		}
		
	}
	public static void addAlias(String alias, String command) {
		alias = alias.toLowerCase();
		Client.aliases.put(alias, command);
		
		if(!Client.aliasesList.contains(alias)) {
			Client.aliasesList.add(alias);
		}
	}
	
	public static void overrideHackInfo(Hack hack, NBTTagCompound input) {
		hack.status = input.getBoolean("Status");
		if(input.hasKey("Keybind")) { //XXX older versions
			hack.keybinding.setValue(input.getInteger("Keybind"));
		}
		NBTTagCompound settings = input.getCompoundTag("Settings");
		if(hack.equals(EntityESPHack.instance)) {
			if(settings.hasKey("Mode")) {
				EntityESPHack.instance.animalsMode.setValue(settings.getString("Mode"));
				EntityESPHack.instance.hostileMode.setValue(settings.getString("Mode"));
				EntityESPHack.instance.playersMode.setValue(settings.getString("Mode"));
			}
		}
		
		for(Map.Entry<String, Setting> entry : hack.settings.entrySet()) {
			entry.getValue().readFromNBT(settings);
		}
	}
	
	public static void writeHackInfo(Hack hack, NBTTagCompound output) {
		NBTTagCompound hacc = (NBTTagCompound) NBTBase.createTagOfType((byte) 10);
		hacc.setBoolean("Status", hack.status);
		
		NBTTagCompound settings = (NBTTagCompound) NBTBase.createTagOfType((byte) 10);
		for(Map.Entry<String, Setting> entry : hack.settings.entrySet()) {
			entry.getValue().writeToNBT(settings);
		}
		hacc.setCompoundTag("Settings", settings);
		
		output.setCompoundTag(hack.name.toLowerCase(), hacc);
	}
	
	public static void writeModuleSettings(File f) throws IOException {
		NBTTagCompound comp = (NBTTagCompound) NBTBase.createTagOfType((byte) 10);
		
		for(Map.Entry<String, Hack> entry : Client.hacksByName.entrySet()) {
			Hack hack = entry.getValue();
			writeHackInfo(hack, comp);
		}
		
		
		NBTTagCompound write = (NBTTagCompound) NBTBase.createTagOfType((byte) 10);
		write.setCompoundTag("Modules", comp);
		write.setInteger("FormatVersion", Client.saveVersion);
		write.writeTagContents(new DataOutputStream(new FileOutputStream(f)));
	}
	
	public static void saveModules() {
		try {
			File f = new File(Minecraft.getMinecraftDir()+"/MaybeAClient/");
			f.mkdirs();
			File ms = new File(f, "module-settings");
			if(!ms.exists()) ms.createNewFile();
			writeModuleSettings(ms);
		}catch(Exception e) {
			e.printStackTrace();
			System.out.println("Failed to save module settings!");
		}
	}
	
	public static void saveClickGUI() {
		try {
			File f = new File(Minecraft.getMinecraftDir()+"/MaybeAClient/");
			f.mkdirs();
			File ms = new File(f, "clickgui-settings");
			if(!ms.exists()) ms.createNewFile();
			writeClickGUISettings(ms);
		}catch(Exception e) {
			e.printStackTrace();
			System.out.println("Failed to save clickgui!");
		}
	}
	
	public static void saveAliases() {
		try {
			File f = new File(Minecraft.getMinecraftDir()+"/MaybeAClient/");
			f.mkdirs();
			File ms = new File(f, "aliases");
			if(!ms.exists()) ms.createNewFile();
			writeCommandAliases(ms);
		}catch(Exception e) {
			e.printStackTrace();
			System.out.println("Failed to save aliases!");
		}
	}
	
	public static void readModuleSettings(File f) throws IOException {
		NBTTagCompound modules = null;
		NBTTagInt version = null;
		DataInputStream dis = new DataInputStream(new FileInputStream(f));
		while(true) {
			NBTBase base = NBTBase.readTag(dis);
			
			if(base instanceof NBTTagInt) {
				version = (NBTTagInt) base;
			}else if(base instanceof NBTTagCompound) {//TODO rework
				modules = (NBTTagCompound) base;
			}else {
				if(modules == null) {
					System.out.println("No module information was found!");
				}
				if(version == null) {
					System.out.println("No version was found!");
				}
				break;
			}
		}
		if(modules == null) return;
		
		if(version.intValue != Client.saveVersion) {
			System.out.println(String.format("Module settings save version does not match client(%d != %d). Converting...", version.intValue, Client.saveVersion));
			Client.convertingVersion = version.intValue;
		}
		
		for(Map.Entry<String, Hack> entry : Client.hacksByName.entrySet()) {
			Hack hack = entry.getValue();
			if(!modules.hasKey(hack.name.toLowerCase())) {
				if(hack.name.equalsIgnoreCase(ClientInfoHack.instance.name) && modules.hasKey("coords")) {
					NBTTagCompound hacc = modules.getCompoundTag("coords");
					overrideHackInfo(hack, hacc);
				}else if(hack.name.equalsIgnoreCase(NoClientSideDestroyHack.instance.name) && modules.hasKey("noclientsidedestroy")) {
					NBTTagCompound hacc = modules.getCompoundTag("noclientsidedestroy");
					overrideHackInfo(hack, hacc);
				}else if(hack.name.equalsIgnoreCase(ThirdPersonTweaksHack.instance.name) && modules.hasKey("noclipthirdperson")){
					NBTTagCompound hacc = modules.getCompoundTag("noclipthirdperson");
					overrideHackInfo(hack, hacc);
					ThirdPersonTweaksHack.instance.noclip.setValue(true);
				}else {
					System.out.println("Information about "+hack.name+" was not found");
				}
			}else {
				NBTTagCompound hacc = modules.getCompoundTag(hack.name.toLowerCase());
				overrideHackInfo(hack, hacc);
			}
			writeHackInfo(hack, modules);
		}
		
		NBTTagCompound write = (NBTTagCompound) NBTBase.createTagOfType((byte) 10);
		write.setCompoundTag("Modules", modules);
		write.setInteger("FormatVersion", Client.saveVersion);
		write.writeTagContents(new DataOutputStream(new FileOutputStream(f)));
		Client.convertingVersion = 0;
	}
	
	
	
	public static void readClickGUISettings(File f) throws IOException {
		DataInputStream dis = new DataInputStream(new FileInputStream(f));
		NBTBase base = NBTBase.readTag(dis);
		NBTTagCompound cc = (NBTTagCompound) base;
		int version = cc.getInteger("FormatVersion");
		if(version != Client.saveVersion) {
			System.out.println(String.format("Gui settings save version does not match client(%d != %d). Converting...", version, Client.saveVersion));
			Client.convertingVersion = version;
		}
		
		HashMap<Integer, Tab> priority = new HashMap<>();
		NBTTagCompound clickgui = cc.getCompoundTag("ClickGUI");
		int lowest = Integer.MIN_VALUE;
		for(Tab tab : ClickGUI.tabs) {
			NBTTagCompound tg = clickgui.getCompoundTag(tab.getTabName().toLowerCase());
			if(!clickgui.hasKey(tab.getTabName().toLowerCase())) {
				System.out.println("Information about "+tab.name+" not found.");
				priority.put(lowest++, tab);
			}else {
				tab.readFromNBT(tg);
				if(tg.hasKey("Priority")) {
					int tabprio = tg.getInteger("Priority");
					if(priority.containsKey(tabprio)) {
						System.out.println("Priortity "+tabprio+" was set to another tab already!");
						priority.put(lowest++, tab);
					}else {
						priority.put(tabprio, tab);
					}
				}else {
				priority.put(lowest++, tab);
				}
			}
		}
		
		ClickGUI.tabs.clear();
		ArrayList<Map.Entry<Integer, Tab>> arr = new ArrayList<>(priority.entrySet());
		arr.sort(Map.Entry.comparingByKey());
		for(Entry<Integer, Tab> entry : arr) {
			ClickGUI.tabs.add(entry.getValue());
		}
	}
	
	public static void writeClickGUISettings(File f) throws IOException {
		NBTTagCompound clickgui = (NBTTagCompound) NBTBase.createTagOfType((byte) 10);
		
		for(Tab tab : ClickGUI.tabs) {
			NBTTagCompound tb = (NBTTagCompound) NBTBase.createTagOfType((byte) 10);
			clickgui.setCompoundTag(tab.getTabName().toLowerCase(), tb);
			tab.writeToNBT(tb);
		}
		
		NBTTagCompound write1 = (NBTTagCompound) NBTBase.createTagOfType((byte) 10);
		NBTTagCompound write2 = (NBTTagCompound) NBTBase.createTagOfType((byte) 10);
		write1.setCompoundTag("ClickGUI", clickgui);
		write1.setInteger("FormatVersion", Client.saveVersion);
		write2.setCompoundTag("", write1);
		write2.writeTagContents(new DataOutputStream(new FileOutputStream(f)));
	}
	
	public static void readCommandAliases(File f) throws IOException {
		
		NBTTagCompound aliases = null;
		NBTTagInt version = null;
		DataInputStream in = new DataInputStream(new FileInputStream(f));
		while(true) {
			NBTBase base = NBTBase.readTag(in);
			
			if(base instanceof NBTTagInt) {
				version = (NBTTagInt) base;
			}else if(base instanceof NBTTagCompound) {
				aliases = (NBTTagCompound) base;
			}else {
				if(aliases == null) {
					System.out.println("No aliases information was found!");
				}
				if(version == null) {
					System.out.println("No version was found!");
				}
				break;
			}
		}
		
		for(Map.Entry<String, NBTBase> entry : aliases.tagMap.entrySet()) {
			String alias = entry.getKey();
			NBTBase bs = entry.getValue();
			if(bs instanceof NBTTagString) {
				NBTTagString cmd = (NBTTagString) bs;
				Client.addAlias(alias, cmd.stringValue);
			}else {
				System.out.println("Alias "+alias+" has wrong nbttag type("+bs.getType()+")!");
			}
		}
		
		NBTTagCompound write = (NBTTagCompound) NBTBase.createTagOfType((byte) 10);
		write.setCompoundTag("Aliases", aliases);
		write.setInteger("FormatVersion", Client.saveVersion);
		write.writeTagContents(new DataOutputStream(new FileOutputStream(f)));
	}
	
	public static void writeCommandAliases(File f) throws IOException {
		NBTTagCompound comp = (NBTTagCompound) NBTBase.createTagOfType((byte) 10);
		
		for(Map.Entry<String, String> entry : Client.aliases.entrySet()) {
			comp.setString(entry.getKey().toLowerCase(), entry.getValue());
		}
		
		
		NBTTagCompound write = (NBTTagCompound) NBTBase.createTagOfType((byte) 10);
		write.setCompoundTag("Aliases", comp);
		write.setInteger("FormatVersion", Client.saveVersion);
		write.writeTagContents(new DataOutputStream(new FileOutputStream(f)));
	}
	
	public static void readCurrentServers(File f) throws IOException {
		DataInputStream dis = new DataInputStream(new FileInputStream(f));
		NBTBase base = NBTBase.readTag(dis);
		NBTTagCompound cc = (NBTTagCompound) base;
		NBTTagList servers = cc.getTagList("Servers");
		for(int i = 0; i < servers.tagCount(); ++i) {
			NBTBase tag = servers.tagAt(i);
			if(tag instanceof NBTTagCompound) {
				NBTTagCompound server = (NBTTagCompound) tag;
				String name = server.getString("Name");
				String ip = server.getString("IP");
				int port = server.getInteger("Port");
				GuiServerSelector.servers.add(new ServerInfo(name, ip, port));
			}else {
				System.out.println("Server entry is not compound! "+tag);
			}
		}
	}
	public static void writeCurrentServers(){
		try {
			File fpr = new File(Minecraft.getMinecraftDir()+"/MaybeAClient_USER/");
			File servers = new File(fpr, "servers");
			writeCurrentServers(servers);
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	public static void writeCurrentServers(File f) throws IOException {
		NBTTagList comp = (NBTTagList) NBTBase.createTagOfType(NBTBase.LIST);
		for(ServerInfo server : GuiServerSelector.servers) {
			NBTTagCompound info = (NBTTagCompound)NBTBase.createTagOfType(NBTBase.COMPOUND);
			info.setString("Name", server.name);
			info.setString("IP", server.ip);
			info.setInteger("Port", server.port);
			comp.setTag(info);
		}
		NBTTagCompound write = (NBTTagCompound) NBTBase.createTagOfType((byte) 10);
		NBTTagCompound write2 = (NBTTagCompound) NBTBase.createTagOfType((byte) 10);
		write.setTag("Servers", comp);
		write.setInteger("FormatVersion", Client.saveVersion);
		write2.setCompoundTag("", write);
		write2.writeTagContents(new DataOutputStream(new FileOutputStream(f)));
	}
	
	public static int possibleColors[] = new int[MapColor.mapColorArray.length];
	public static HashMap<Integer, Integer> containedColors = new HashMap<>();
	public static HashMap<Integer, Integer> color2default = new HashMap<Integer, Integer>();
	
	public static HashMap<Integer, ArrayList<Block>> color2block = new HashMap<>();
	
	public static int color2blocks(MapColor mc, int col) {
		color2default.put(col, mc.colorValue);
		ArrayList<Block> blocks = color2block.getOrDefault(col, new ArrayList<>());
		for(Block b : Block.blocksList) {
			if(b != null && b.blockMaterial.materialMapColor == mc && b.blockMaterial != Material.fire) {
				blocks.add(b);
			}
		}
		
		color2block.put(col, blocks);
		return col;
	}
	
	public static void postClientLoad() throws IOException {
		System.out.println("Loaded "+Client.hacksByName.size()+" modules, "+Client.commands.size()+" commands.");
		
		ClickGUI.registerTabs();
		int j = 0;
		for(int i = 0; i < MapColor.mapColorArray.length; ++i) {
			MapColor mc = MapColor.mapColorArray[i];
			if(mc != null && mc != MapColor.airColor) { //black not possible
				int col = mc.colorValue;
				if(!containedColors.containsKey(col)) {
					possibleColors[j] = col;
					containedColors.put(col, j);
					++j;
				}
				
				{ //1 & 3 - default
					int r = (col >> 16 & 255) * 220 / 255;
	                int g = (col >> 8 & 255) * 220 / 255;
	                int b = (col & 255) * 220 / 255;
	                color2blocks(mc, (r << 16) | (g << 8) | b);
				}
				
				{ //2 - * 255/255 laddering up
					color2blocks(mc, col);
				}
				
				{ //0 laddering down
					int r = (col >> 16 & 255) * 180 / 255;
	                int g = (col >> 8 & 255) * 180 / 255;
	                int b = (col & 255) * 180 / 255;
	                
	                color2blocks(mc, (r << 16) | (g << 8) | b);
				}
				
			}
		}
		int[] realpossible = new int[j];
		System.arraycopy(possibleColors, 0, realpossible, 0, j);
		possibleColors = realpossible;
		
		
		File f = new File(Minecraft.getMinecraftDir()+"/MaybeAClient/");
		File fpr = new File(Minecraft.getMinecraftDir()+"/MaybeAClient_USER/");
		f.mkdirs();
		fpr.mkdirs();
		System.out.println("Getting module settings");
		File ms = new File(f, "module-settings");
		if(ms.exists()) {
			readModuleSettings(ms);
		}else {
			ms.createNewFile();
			writeModuleSettings(ms);
		}
		
		System.out.println("Getting command aliases");
		File aliases = new File(f, "aliases");
		if(aliases.exists()) {
			readCommandAliases(aliases);
		}else {
			aliases.createNewFile();
			writeCommandAliases(aliases);
		}
		
		System.out.println("Getting clickgui settings");
		File clickgui = new File(f, "clickgui-settings");
		if(clickgui.exists()) {
			readClickGUISettings(clickgui);
			writeClickGUISettings(clickgui);
		}else {
			clickgui.createNewFile();
			writeClickGUISettings(clickgui);
		}
		System.out.println("Getting servers");
		File servers = new File(fpr, "servers");
		if(servers.exists()) {
			readCurrentServers(servers);
			writeCurrentServers(servers);
		}else {
			servers.createNewFile();
			writeCurrentServers(servers);
		}
		
		System.out.println("Getting accounts");
		File accounts = new File(fpr, "accounts");
		if(accounts.exists()) {
			readCurrentAccounts(accounts);
			writeCurrentAccounts(accounts);
		}else {
			accounts.createNewFile();
			writeCurrentAccounts(accounts);
		}
		
		
		ClickGUIHack.instance.status = false;
		//generateOutlinedTextures();
		new MovePacketHook();
	}
	
	public static void writeCurrentAccounts(){
		try {
			File fpr = new File(Minecraft.getMinecraftDir()+"/MaybeAClient_USER/");
			File servers = new File(fpr, "accounts");
			writeCurrentAccounts(servers);
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void readCurrentAccounts(File f) throws IOException {
		DataInputStream dis = new DataInputStream(new FileInputStream(f));
		NBTBase base = NBTBase.readTag(dis);
		NBTTagCompound cc = (NBTTagCompound) base;
		NBTTagList accounts = cc.getTagList("Accounts");
		for(int i = 0; i < accounts.tagCount(); ++i) {
			NBTBase tag = accounts.tagAt(i);
			if(tag instanceof NBTTagCompound) {
				NBTTagCompound account = (NBTTagCompound) tag;
				String username = account.getString("Username");
				AccountInfo info = new AccountInfo(username);
				
				NBTTagList passwords = account.getTagList("Passwords");
				for(int j = 0; j < passwords.tagCount(); ++j) {
					NBTBase pwdtag = passwords.tagAt(j);
					if(pwdtag instanceof NBTTagCompound) {
						NBTTagCompound pwdtag2 = (NBTTagCompound) pwdtag;
						String password = pwdtag2.getString("Password");
						String prompt = pwdtag2.getString("Prompt");
						String loginCmd = pwdtag2.getString("LoginCommand");
						String ipAddress = pwdtag2.getString("IP");
						MatchMode[] vals = MatchMode.values();
						int ii = pwdtag2.getInteger("Mode");
						if(ii >= vals.length) {
							System.out.println("Invalid mode! ("+ii+"/"+vals.length+")");
							ii = 0;
						}
						PasswordInfo passInfo = new PasswordInfo(password, ipAddress);
						passInfo.loginPrompt = prompt;
						passInfo.loginCommand = loginCmd;
						passInfo.mode = vals[ii];
						
						info.addPassword(passInfo);
					}
				}
				GuiAccManager.addAccount(info);
			}else {
				System.out.println("Account entry is not compound! "+tag);
			}
		}
	}
	
	public static void writeCurrentAccounts(File f) throws IOException {
		NBTTagList comp = (NBTTagList) NBTBase.createTagOfType(NBTBase.LIST);
		for(AccountInfo acc : GuiAccManager.accounts) {
			NBTTagCompound info = (NBTTagCompound)NBTBase.createTagOfType(NBTBase.COMPOUND);
			info.setString("Username", acc.name);
			
			NBTTagList passwds = (NBTTagList) NBTBase.createTagOfType(NBTBase.LIST);
			for(PasswordInfo pass : acc.pwds) {
				NBTTagCompound ps = (NBTTagCompound)NBTBase.createTagOfType(NBTBase.COMPOUND);
				ps.setString("Password", pass.password);
				ps.setString("Prompt", pass.loginPrompt);
				ps.setString("LoginCommand", pass.loginCommand);
				ps.setString("IP", pass.serverIP);
				ps.setInteger("Mode", pass.mode.ordinal());
				passwds.setTag(ps);
			}
			
			info.setTag("Passwords", passwds);
			comp.setTag(info);
		}
		NBTTagCompound write = (NBTTagCompound) NBTBase.createTagOfType((byte) 10);
		NBTTagCompound write2 = (NBTTagCompound) NBTBase.createTagOfType((byte) 10);
		write.setTag("Accounts", comp);
		write.setInteger("FormatVersion", Client.saveVersion);
		write2.setCompoundTag("", write);
		write2.writeTagContents(new DataOutputStream(new FileOutputStream(f)));
	}
	public static HashMap<String, BufferedImage> path2bufImg = new HashMap<>();
	
	public static BufferedImage rescale(BufferedImage img, int width) {
		if(img.getWidth() == width) return img;
		int height = (img.getHeight() * width) / img.getWidth();
		BufferedImage newImage = new BufferedImage(width, height, 2);
		Graphics2D graphics2D = newImage.createGraphics();
		graphics2D.drawImage(img, 0, 0, width, height, (ImageObserver) null);
		return newImage;
	}
	public static BufferedImage getRescaledResource(String src, int size) throws IOException {
		if(Client.path2bufImg.containsKey(src)) {
			return Client.path2bufImg.get(src);
		}
		
		InputStream res = Client.mc.texturePackList.selectedTexturePack.getResourceAsStream(src);
		if(res == null) return null;
		
		
		Client.path2bufImg.put(src, rescale(ImageIO.read(res), size));
		return Client.path2bufImg.get(src);
	}
	public static BufferedImage getResource(String src) throws IOException {
		if(Client.path2bufImg.containsKey(src)) {
			return Client.path2bufImg.get(src);
		}
		InputStream res = Client.mc.texturePackList.selectedTexturePack.getResourceAsStream(src);
		if(res == null) return null;
		
		
		Client.path2bufImg.put(src, ImageIO.read(res));
		return Client.path2bufImg.get(src);
	}
	
	public static boolean textureExists(String src) {
		return Client.mc.texturePackList.selectedTexturePack.getResourceAsStream(src) != null;
	}
	
	public static byte[] itemsTextureSides;
	public static byte[] terrainTextureSides;
	
	
	public static final boolean FORCE_STACK_DRAW = true;
	public static final boolean BETTER_CHAT_CONTROLS = true;
	
	public static final int STENCIL_REF_ELDRAW = 123;
	public static final int STENCIL_REF_TBDRAW = STENCIL_REF_ELDRAW+1; //MUST BE +1 OR TEXTBOXES WILL BREAK
	
	public static final File mapartsDirectory = new File(Minecraft.getMinecraftDir(), "/maparts/");
	
	/**Future?
	 * Better Schematica placement controls
	 * Resizeable tabs? (currently has some problems with scrolling + bad multimc uses applets)
	 * PacketRecorder
	 * Custom font support
	 * BlockNotifier (notify if some block is close to the player)
	 * Better AutoTunnel, Diagonal AutoTunnel?
	 * Bred, CatHack themes
	 * AutoMiner
	 * Added tab manager(special tab in clickgui, .tab [...] in console) TODO console, heph & nodus theme, more options, rewrite saving
	 * Added ability to create custom module categories and modify existent TODO
	 * hai my digga can u add inventory clicker like when u hold shift and left button its moves in container with auto clicking every item to move item
	 * - Steal button for invs?
	*/
	
	/**
	 * 4.0.6
	 * Fixed huge schematicas causing huge lags when autoplace is enabled
	 * Fixed instantmine crashing the game when breaking and placing zero-hardness blocks very fast
	 */
	
	static {
		Client.mapartsDirectory.mkdirs();
		//1.0
		registerHack(new FlyHack());
		registerHack(new JesusHack());
		registerHack(new NoFallHack());
		registerHack(new XRayHack());
		registerHack(new FullBrightHack());
		registerHack(new ForceFieldHack());
		registerHack(new EntityESPHack());
		registerHack(new FreecamHack());
		registerHack(new RadarHack());
		registerHack(new LockTimeHack());
		registerHack(new LiquidInteractHack());
		registerHack(new InstantHack());
		registerHack(new NoClipHack());
		registerHack(new ClientInfoHack());
		registerHack(new KeybindingsHack());
		registerHack(new ClockspeedHack());
		registerHack(new AntiKnockbackHack());
		registerHack(new ServerStatusHack());
		//1.1
		registerHack(new AutoWalkHack());
		registerHack(new SafeWalkHack());
		registerHack(new StrafeHack());
		registerHack(new CameraLockHack());
		registerHack(new SpeedMineHack());
		registerHack(new AutoTunnelHack());
		registerHack(new ArrayListHack());
		//1.2
		registerHack(new StepHack());
		registerHack(new ClickGUIHack());
		registerHack(new ClientNameHack());
		registerHack(new Packet19SenderHack());
		registerHack(new TracersHack());
		//1.3
		registerHack(new BlockESPHack());
		registerHack(new NameTagsHack());
		registerHack(new NoClientSideDestroyHack());
		//1.4
		registerHack(new SchematicaHack());
		//1.5
		registerHack(new FastPlaceHack());
		registerHack(new ReachHack());
		registerHack(new FastLadderHack());
		registerHack(new NoPushHack());
		registerHack(new WorldDLHack());
		registerHack(new FOVHack());
		registerHack(new ScaffoldHack());
		registerHack(new InventoryViewHack());
		registerHack(new PlayerViewHack());
		registerHack(new CombatLogHack());
		//1.6
		registerHack(new AutoEatHack());
		registerHack(new AutoMouseClickHack());
		registerHack(new InventoryWalkHack());
		registerHack(new XCarryHack());
		registerHack(new AutoToolHack());
		//1.7
		registerHack(new AutoReconnectHack());
		registerHack(new AutoLoginHack());
		registerHack(new ItemNameTagsHack());
		registerHack(new AntiSlowdownHack());
		//1.7.1
		registerHack(new NoRenderHack());
		//1.8
		registerHack(new AutoScreenshotCopyHack());
		registerHack(new ThirdPersonTweaksHack());
		registerHack(new FastPortalHack());
		registerHack(new AutoFishHack());
		registerHack(new ChestContentHack());
		registerHack(new NewChunksHack());
		//1.9
		registerHack(new HideChatHack());
		registerHack(new PlayerlistHack());
		registerHack(new LastSeenSpotsHack());
		//2.0
		registerHack(new CustomHandPositionHack());
		registerHack(new DogOwnerHack());
		//2.2
		registerHack(new TunnelESPHack());
		registerHack(new NoPortalSoundsHack());
		registerHack(new TooltipsHack());
		//useless registerHack(new InventoryTweaksHack());
		//2.2.1
		registerHack(new LowHopSpeedHack());
		registerHack(new AFKDisconnectHack());
		//2.3
		registerHack(new CharSelectorHack());
		registerHack(new AimBotHack());
		registerHack(new TrajectoriesHack());
		//2.4
		registerHack(new SwastikaBuilderHack());
		registerHack(new PacketMineHack());
		registerHack(new AutoSignHack());
		registerHack(new OnToggleMessageHack());
		registerHack(new AutoSaplingHack());
		registerHack(new ClimbGappedLadderHack());
		registerHack(new BlockDestroyerHack());
		registerHack(new CustomSkyHack());
		//2.5
		registerHack(new ChunkBordersHack());
		registerHack(new SlimeChunkRadarHack());
		registerHack(new GreeterHack());
		registerHack(new UnsafeLightLevelsHack());
		//2.6
		registerHack(new ZoomHack());
		//2.6.2
		registerHack(new AutoBoneMealHack());
		//3.0.0
		registerHack(new NoFriendlyFireHack());
		registerHack(new FireExtinguisherHack());
		//3.0.2
		registerHack(new WeatherLockHack());
		//4.0.3 (freehij)
		registerHack(new TNTBomberHack());
		//4.0.4 (freehij)
		registerHack(new AutoShearHack());
		registerHack(new ImageViewerHack());
		registerHack(new FastCraftHack());

		/*registerHack(new Hack("Test", "test", org.lwjgl.input.Keyboard.KEY_NONE, net.skidcode.gh.maybeaclient.hacks.category.Category.RENDER) {
			public Hack init() {
				this.addSetting(new net.skidcode.gh.maybeaclient.hacks.settings.SettingChooser(this, "ColorSettingVithVeryVeryVeryVeryLongName", new String[] {"Ae", "Clef"}, new boolean[] {false, false}));
				return this;
			}
		}.init());*/
		
		//1.0
		registerCommand(new CommandModule()); 
		registerCommand(new CommandHelp());
		registerCommand(new CommandSlot9());
		//1.1
		registerCommand(new CommandAlias());
		//1.2
		registerCommand(new CommandTab());
		//2.1
		registerCommand(new CommandTeleport());
		//3.1
		registerCommand(new CommandItemGui());
		registerCommand(new CommandInstantBuild());
		registerCommand(new CommandSetMapScale());
		
		try {
			postClientLoad();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	public static boolean isActiveable[] = new boolean[Block.blocksList.length];
	
    static {
    	for(int i = 0; i < Block.blocksList.length; ++i) {
    		Block b = Block.blocksList[i];
    		isActiveable[i] = 
    			b instanceof BlockBed || 
    			b instanceof BlockButton || 
    			b instanceof BlockCake || 
    			b instanceof BlockChest || 
    			b instanceof BlockDispenser || 
    			b instanceof BlockDoor ||
    			b instanceof BlockFurnace ||
    			b instanceof BlockJukeBox ||
    			b instanceof BlockLever ||
    			b instanceof BlockNote ||
    			//b instanceof BlockPistonBase ||
    			//b instanceof BlockPistonMoving ||
    			b instanceof BlockRedstoneOre ||
    			b instanceof BlockRedstoneRepeater ||
    			//b instanceof BlockStairs ||
    			//b instanceof BlockTNT ||
    			b instanceof BlockTrapDoor ||
    			b instanceof BlockWorkbench;
    	}
    }
    
	public static Framebuffer fb_entityTop;
	
	public static int outlinesDisplayLists = 0;
	public static int blockDisplayLists = -1;
	public static int itemDisplayLists = -1;
	
	public static boolean initializedDisplayLists = false;
	public static int itemsTexSize = 16;
	public static int terrainTexSize = 16;
	public static boolean debug = false;
	public static void deleteDisplayLists() {
		if(initializedDisplayLists) {
			System.out.println("Deleting outline displaylists");
			initializedDisplayLists = false;
			GL11.glDeleteLists(outlinesDisplayLists, 256 + 256);
			blockDisplayLists = itemDisplayLists = -1;
		}
	}
	
	public static void initTextures() {
		Shaders.init();
		if(fbEnabled) {
			fb_entityTop = new Framebuffer();
		}
		
		System.out.println("Intializing outlined textures");
		long time = System.currentTimeMillis();
		if(!initializedDisplayLists) {
			System.out.println("Creating new outline display lists");
			initializedDisplayLists = true;
			outlinesDisplayLists = GL11.glGenLists(256 + 256); //block sprite count, item sprite count
			blockDisplayLists = outlinesDisplayLists;
			itemDisplayLists = outlinesDisplayLists + 256;
		}
		
		try {
			itemsTextureSides = TextureUtils.getOutliningSides(getResource("/gui/items.png"));
			terrainTextureSides = TextureUtils.getOutliningSides(getResource("/terrain.png"));
			itemsTexSize = (int) Math.sqrt(itemsTextureSides.length) / 16;
			terrainTexSize = (int) Math.sqrt(terrainTextureSides.length) / 16;
			
			bakeSides(itemsTextureSides);
			bakeSides(terrainTextureSides);
		}catch(Exception e) {
			e.printStackTrace();
		}
		System.out.println("Done initializing. Took "+(System.currentTimeMillis()-time)+"ms");
	}
	public static void bakeSides(byte[] sides) {
    	int tsize = 16;
    	int callsbase = 0;
    	if(sides == terrainTextureSides) {
    		tsize = terrainTexSize;
    		callsbase = blockDisplayLists;
    	}else {
    		tsize = itemsTexSize;
    		callsbase = itemDisplayLists;
    	}
    	double px = 1d/(double)tsize;
    	
    	for(int gx = 0; gx < 16; ++gx) {
			for(int gy = 0; gy < 16; ++gy) {
				GL11.glNewList(callsbase + (gx + gy*16), GL11.GL_COMPILE);
				for(int x = gx*tsize; x < gx*tsize+tsize; ++x) {
					boolean begin = false;
					int beginX = 0;
					int beginY = 0;
					
					for(int y = gy*tsize; y < gy*tsize+tsize; ++y) {
						byte b = sides[x*(tsize*16) + y];
	
						if((b & 1) > 0) {
							if(!begin) {
								begin = true;
								beginX = x % tsize;
								beginY = y % tsize;
							}
						}else {
							if(begin) {
								TextureUtils.line(1, beginX, beginY, x%tsize, y%tsize, px);
								begin = false;
							}
						}
					}
					
					if(begin) {
						TextureUtils.line(1, beginX, beginY, x%tsize, tsize, px);
						begin = false;
					}
					
					for(int y = gy*tsize; y < gy*tsize+tsize; ++y) {
						byte b = sides[x*(tsize*16) + y];
	
						if((b & 2) > 0) {
							if(!begin) {
								begin = true;
								beginX = x % tsize;
								beginY = y % tsize;
							}
						}else {
							if(begin) {
								TextureUtils.line(2, beginX, beginY, x%tsize, y%tsize, px);
								begin = false;
							}
						}
					}
					
					if(begin) {
						TextureUtils.line(2, beginX, beginY, x%tsize, tsize, px);
						begin = false;
					}
				}
				
				for(int y = gy*tsize; y < gy*tsize+tsize; ++y) {
					boolean begin = false;
					int beginX = 0;
					int beginY = 0;
					for(int x = gx*tsize; x < gx*tsize+tsize; ++x) {
						byte b = sides[x*(tsize*16) + y];
						if((b & 4) > 0) {
							if(!begin) {
								begin = true;
								beginX = x % tsize;
								beginY = y % tsize;
							}
						}else {
							if(begin) {
								TextureUtils.line(4, beginX, beginY, x%tsize, y%tsize, px);
								begin = false;
							}
						}
					}
					if(begin) {
						TextureUtils.line(4, beginX, beginY, tsize, y%tsize, px);
						begin = false;
					}
					
					for(int x = gx*tsize; x < gx*tsize+tsize; ++x) {
						byte b = sides[x*(tsize*16) + y];
						if((b & 8) > 0) {
							if(!begin) {
								begin = true;
								beginX = x % tsize;
								beginY = y % tsize;
							}
						}else {
							if(begin) {
								TextureUtils.line(8, beginX, beginY, x%tsize, y%tsize, px);
								begin = false;
							}
						}
					}
					
					if(begin) {
						TextureUtils.line(8, beginX, beginY, tsize, y%tsize, px);
						begin = false;
					}
				}
				GL11.glEndList();
			}
		}
    }
	

	public static Hack findHack(String name) {
		return Client.hacksByName.get(name.toLowerCase());
	}
	
	public static void onKeyPress(int keycode, boolean pressed) {
		if(keycode == 0) return;
		
		if(pressed) {
			if(RenderManager.instance.livingPlayer == null) return;
			
			for(Hack h : Client.hacksByName.values()) {
				if(h.keybinding.value == keycode){
					h.toggleByKeybind();
				}
			}
		}else if(!pressed && keycode == ZoomHack.instance.keybinding.value && ZoomHack.instance.status) {
			ZoomHack.onStopHolding();
		}
	}

	public static String getKeyName(int value) {
		if(value < 0) return Mouse.getButtonName((-value)-1);
		return Keyboard.getKeyName(value);
	}

	public static int getKeycodeForMouseButton(int key) {
		if(key < 0) return 0;
		if(Mouse.getButtonName(key) == null) return 0;
		
		return -(key+1); //0 - 15 -> -1 - -16
	}
	public static Hack disconnectCausedBy = null;
	public static boolean renderEdgeLines = false;
	public static void forceDisconnect(Hack h) {
		if(disconnectCausedBy == null) {
			mc.theWorld.sendQuittingDisconnectingPacket();
			disconnectCausedBy = h;
		}
	}

	public static final boolean fbEnabled = false;
	
	public static void onScreenResize(ScaledResolution sr) {
		if(fbEnabled) Client.fb_entityTop.setup(sr.getScaledWidth(), sr.getScaledHeight());
	}
	
	public static void renderFramebuffers() {
		if(Client.fbEnabled) Client.fb_entityTop.renderOnScreen();
		
	}

	
	public static ArrayList<File> maparts;
	public static ArrayList<File> getMaparts(){
		maparts = new ArrayList<File>();
		File[] files = mapartsDirectory.listFiles();
		for(File f : files) {
			if(!f.getName().toLowerCase().endsWith(".png")) continue;
			maparts.add(f);
		}
		if(files.length <= 0) maparts.add(null);
		return maparts;
	}
}
