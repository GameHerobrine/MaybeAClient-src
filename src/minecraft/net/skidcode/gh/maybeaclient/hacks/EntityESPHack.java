package net.skidcode.gh.maybeaclient.hacks;

import org.lwjgl.input.Keyboard;

import net.minecraft.src.Entity;
import net.minecraft.src.EntityAnimal;
import net.minecraft.src.EntityGhast;
import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityMob;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntitySlime;
import net.minecraft.src.EntitySquid;
import net.minecraft.src.EntityWolf;
import net.minecraft.src.Tessellator;
import net.skidcode.gh.maybeaclient.events.Event;
import net.skidcode.gh.maybeaclient.events.EventListener;
import net.skidcode.gh.maybeaclient.events.EventRegistry;
import net.skidcode.gh.maybeaclient.hacks.category.Category;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingBoolean;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingChooser;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingColor;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingMode;
import net.skidcode.gh.maybeaclient.utils.ChatColor;

public class EntityESPHack extends Hack{
	public SettingChooser chooser;
	//public SettingMode mode = new SettingMode(this, "Mode", "Box", "Fill", "Outline");
	public SettingMode playersMode = new SettingMode(this, "Players Mode", "Box", "Fill", "Outline", "Glow");
	public SettingMode animalsMode = new SettingMode(this, "Animals Mode", "Box", "Fill", "Outline", "Glow");
	public SettingMode hostileMode = new SettingMode(this, "Hostiles Mode", "Box", "Fill", "Outline", "Glow");
	public SettingMode neutralsMode = new SettingMode(this, "Neutrals Mode", "Box", "Fill", "Outline", "Glow");
	public SettingMode itemsMode = new SettingMode(this, "Items Mode", "Box", "Fill", "Outline");
	
	public SettingColor playerColor = new SettingColor(this, "Players Color", 255, 0, 0);
	public SettingBoolean drawOnLocalPlayer = new SettingBoolean(this, "Render on local player", true);
	public SettingColor animalColor = new SettingColor(this, "Animals Color", 255, 255, 0);
	public SettingColor hostileColor = new SettingColor(this, "Hostiles Color", 255, 0, 255);
	public SettingColor neutralsColor = new SettingColor(this, "Neutrals Color", 0, 255, 255);
	public SettingColor itemColor = new SettingColor(this, "Item Color", 0, 0, 255);
	//XXX useless public SettingBoolean renderingOrder = new SettingBoolean(this, "Vanilla rendering order", true);
	
	public static EntityESPHack instance;
	public static boolean allowRendering = false;
	public static boolean currentlyRendering = false;
	
	@Override
	public String getPrefix() {
		String s = "";
		if(this.chooser.getValue("Players")) s += "P";
		if(this.chooser.getValue("Animals")) s += "A";
		if(this.chooser.getValue("Hostiles")) s += "H";
		if(this.chooser.getValue("Neutrals")) s += "N";
		if(this.chooser.getValue("Items")) s += "I";
		return s;
	}
	public EntityESPHack() {
		super("EntityESP", "Highlights entities", Keyboard.KEY_NONE, Category.RENDER);
		
		instance = this;
		this.chooser = new SettingChooser(
			this, 
			"Entity Chooser",
			new String[] {"Players", "Animals", "Hostiles", "Items", "Neutrals"},
			new boolean[] {true, true, true, false, true}
		) {
			public void setValue(String name, boolean value) {
				super.setValue(name, value);
				
				if(name.equalsIgnoreCase("Players")) {
					EntityESPHack.instance.playerColor.hidden = !value;
					EntityESPHack.instance.drawOnLocalPlayer.hidden = !value;
					EntityESPHack.instance.playersMode.hidden = !value;
				}
				if(name.equalsIgnoreCase("Animals")) {
					EntityESPHack.instance.animalColor.hidden = !value;
					EntityESPHack.instance.animalsMode.hidden = !value;
				}
				if(name.equalsIgnoreCase("Hostiles")) {
					EntityESPHack.instance.hostileColor.hidden = !value;
					EntityESPHack.instance.hostileMode.hidden = !value;
				}
				if(name.equalsIgnoreCase("Items")) {
					EntityESPHack.instance.itemColor.hidden = !value;
					EntityESPHack.instance.itemsMode.hidden = !value;
				}
				if(name.equalsIgnoreCase("Neutrals")) {
					EntityESPHack.instance.neutralsColor.hidden = !value;
					EntityESPHack.instance.neutralsMode.hidden = !value;
				}
			}
		};
		this.addSetting(this.chooser);
		
		this.addSetting(this.playerColor);
		this.addSetting(this.playersMode);
		this.addSetting(this.drawOnLocalPlayer);
		
		this.addSetting(this.animalColor);
		this.addSetting(this.animalsMode);
		
		this.addSetting(this.hostileColor);
		this.addSetting(this.hostileMode);
		
		this.addSetting(this.itemColor);
		this.addSetting(this.itemsMode);
		
		this.addSetting(this.neutralsColor);
		this.addSetting(this.neutralsMode);
		
		//this.addSetting(this.renderingOrder);
	}
	public String getRenderingMode(Entity e) {
		if(e instanceof EntityPlayer) return this.playersMode.currentMode;
		if(e instanceof EntityAnimal && !(e instanceof EntitySquid || e instanceof EntityWolf)) return this.animalsMode.currentMode;
		if(e instanceof EntityMob || e instanceof EntityGhast || e instanceof EntitySlime) return this.hostileMode.currentMode;
		if(e instanceof EntitySquid || e instanceof EntityWolf) return this.neutralsMode.currentMode; 
		if(e instanceof EntityItem) return this.itemsMode.currentMode;
		return "";
	}
	
	public SettingColor getRenderColor(Entity e) {
		if(e instanceof EntityPlayer) return this.playerColor;
		if(e instanceof EntityAnimal && !(e instanceof EntitySquid || e instanceof EntityWolf)) return this.animalColor;
		if(e instanceof EntityMob || e instanceof EntityGhast || e instanceof EntitySlime) return this.hostileColor;
		if(e instanceof EntitySquid || e instanceof EntityWolf) return this.neutralsColor; 
		if(e instanceof EntityItem) return this.itemColor;
		return null;
	}
	
	public boolean shouldRender(Entity e) {
		return allowRendering && 
			((chooser.getValue("Players") && e instanceof EntityPlayer && (e != mc.thePlayer || this.drawOnLocalPlayer.value)) ||
			(chooser.getValue("Animals") && e instanceof EntityAnimal && !(e instanceof EntitySquid || e instanceof EntityWolf)) ||
			(chooser.getValue("Hostiles") && e instanceof EntityMob || e instanceof EntityGhast || e instanceof EntitySlime) ||
			(chooser.getValue("Neutrals") && e instanceof EntitySquid || e instanceof EntityWolf) ||
			(chooser.getValue("Items") && e instanceof EntityItem));
	}
}
