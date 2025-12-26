package net.skidcode.gh.maybeaclient.hacks;

import java.util.Comparator;
import java.util.List;

import org.lwjgl.input.Keyboard;

import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityAnimal;
import net.minecraft.src.EntityGhast;
import net.minecraft.src.EntityMob;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntitySlime;
import net.minecraft.src.EntitySquid;
import net.minecraft.src.EntityWolf;
import net.minecraft.src.Packet7UseEntity;
import net.skidcode.gh.maybeaclient.events.Event;
import net.skidcode.gh.maybeaclient.events.EventListener;
import net.skidcode.gh.maybeaclient.events.EventRegistry;
import net.skidcode.gh.maybeaclient.events.impl.EventMPMovementUpdate;
import net.skidcode.gh.maybeaclient.events.impl.EventPlayerUpdatePost;
import net.skidcode.gh.maybeaclient.events.impl.EventPlayerUpdatePre;
import net.skidcode.gh.maybeaclient.hacks.category.Category;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingBoolean;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingChooser;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingDouble;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingIgnoreList;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingInteger;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingMode;
import net.skidcode.gh.maybeaclient.utils.ChatColor;
import net.skidcode.gh.maybeaclient.utils.PlayerUtils;

public class ForceFieldHack extends Hack implements EventListener{
	static class DistEntitySorter implements Comparator<Entity>{
		public static final DistEntitySorter instance = new DistEntitySorter();
		@Override
		public int compare(Entity o1, Entity o2) {
			double d1 = o1.getDistanceToEntity(mc.thePlayer);
			double d2 = o2.getDistanceToEntity(mc.thePlayer);
			if(d1 == d2) return 0;
			return d1 > d2 ? 1 : -1;
		}
		
	}
	public SettingChooser chooser = new SettingChooser(
		this,
		"Entity Chooser",
		new String[] {"Players", "Animals", "Hostiles", "Neutrals"},
		new boolean[] {true, false, true, false}
	);
	public SettingMode mode = new SettingMode(this, "Mode", "Multi", "Single");
	public SettingIgnoreList ignoreList = new SettingIgnoreList(this, "Ignore");
	public SettingBoolean notifyInChat = new SettingBoolean(this, "NotifyInChat", false);
	public SettingBoolean middleClickFriend = new SettingBoolean(this, "MiddleClickFriend", false) {
		public void setValue(boolean d) {
			super.setValue(d);
			((ForceFieldHack) this.hack).notifyInChat.hidden = !this.getValue();
		}
	};
	public SettingBoolean swing = new SettingBoolean(this, "Swing", false);
	public SettingDouble radius = new SettingDouble(this, "Radius", 6.0f, 0, 10);			
	public SettingInteger ticksBetweenHits = new SettingInteger(this, "TicksBetweenHits", 0, 0, 20);
	
	public static ForceFieldHack instance;
	
	public ForceFieldHack() {
		super("KillAura", "Damages the entities around the player", Keyboard.KEY_NONE, Category.COMBAT);
		instance = this;
		
		this.addSetting(this.mode);
		this.addSetting(this.swing);
		this.addSetting(this.chooser);
		this.addSetting(this.radius);
		this.addSetting(this.ticksBetweenHits);
		this.addSetting(this.ignoreList);
		this.addSetting(this.middleClickFriend);
		this.addSetting(this.notifyInChat);
		
		EventRegistry.registerListener(EventPlayerUpdatePost.class, this);
	}
	
	@Override
	public String getPrefix() {
		String s = "";
		if(this.chooser.getValue("Players")) s += "P";
		if(this.chooser.getValue("Animals")) s += "A";
		if(this.chooser.getValue("Hostiles")) s += "H";
		if(this.chooser.getValue("Neutrals")) s += "N";
		return s;
	}
	public boolean canBeAttacked(Entity e) {
		return 
			(chooser.getValue("Players") && e instanceof EntityPlayer) ||
			(chooser.getValue("Animals") && e instanceof EntityAnimal && !(e instanceof EntitySquid || e instanceof EntityWolf)) ||
			(chooser.getValue("Hostiles") && e instanceof EntityMob || e instanceof EntityGhast || e instanceof EntitySlime) ||
			(chooser.getValue("Neutrals") && (e instanceof EntitySquid || e instanceof EntityWolf));
	}
	
	public boolean isFriend(EntityPlayer e) {
		if(!this.ignoreList.enabled) return false;
		
		return this.ignoreList.names.contains(e.username.toLowerCase());
	}
	
	@Override
	public void handleEvent(Event event) {
		if(event instanceof EventPlayerUpdatePost) {
			double rad = this.radius.getValue();
			List entitiesNearby = mc.theWorld.getEntitiesWithinAABBExcludingEntity(
				mc.thePlayer,
				AxisAlignedBB.getBoundingBox(
					mc.thePlayer.posX - rad, mc.thePlayer.posY - rad, mc.thePlayer.posZ - rad,
					mc.thePlayer.posX + rad, mc.thePlayer.posY + rad, mc.thePlayer.posZ + rad
				)
			);
			entitiesNearby.sort(DistEntitySorter.instance);
			for(Object o : entitiesNearby) {
				Entity e = (Entity)o;
				if(this.canBeAttacked(e)) {
					if(e instanceof EntityPlayer && this.isFriend((EntityPlayer)e)) {
						continue;
					}
					if(e.ff_delay > 0) continue;
					
					if(this.swing.getValue()) {
						mc.thePlayer.swingItem();
					}
					PlayerUtils.hitEntity(e);
					e.ff_delay = this.ticksBetweenHits.getValue();
					if(this.mode.currentMode.equalsIgnoreCase("Single")) break;
				}
			}
		}
	}
}
