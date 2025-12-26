package net.skidcode.gh.maybeaclient.hacks;

import org.lwjgl.input.Keyboard;

import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NBTTagLong;
import net.skidcode.gh.maybeaclient.Client;
import net.skidcode.gh.maybeaclient.events.Event;
import net.skidcode.gh.maybeaclient.events.EventListener;
import net.skidcode.gh.maybeaclient.events.EventRegistry;
import net.skidcode.gh.maybeaclient.events.impl.EventPlayerUpdatePre;
import net.skidcode.gh.maybeaclient.hacks.category.Category;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingBoolean;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingDouble;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingLong;

public class AFKDisconnectHack extends Hack implements EventListener{
	public static AFKDisconnectHack instance;
	public static long afkStart = 0;
	public static boolean afkStarted = false;
	public SettingBoolean preventReconnect = new SettingBoolean(this, "PreventAutoReconnect", true);
	public SettingDouble minutesToDisconnect = new SettingDouble(this, "DisconnectAfterMinutes", 60, 1, 120, 1) {
		@Override
		public void readFromNBT(NBTTagCompound input) {
			if(input.hasKey(this.name)) {
				if(input.tagMap.get(this.name) instanceof NBTTagLong) {
					this.setValue(input.getLong(this.name));
				}else {
					this.setValue(input.getDouble(this.name));
				}
			}
		}
	};
	
	public AFKDisconnectHack() {
		super("AFKDisconnect", "Disconnects the player if no inputs were made after specific amount of time", Keyboard.KEY_NONE, Category.MISC);
		instance = this;
		
		this.addSetting(this.minutesToDisconnect);
		this.addSetting(this.preventReconnect);
		EventRegistry.registerListener(EventPlayerUpdatePre.class, this);
	}
	
	public static void startAFKing() {
		if(!afkStarted) {
			afkStart = System.currentTimeMillis();
			afkStarted = true;
		}
	}
	public static void stopAFKing() {
		afkStarted = false;
	}
	@Override
	public void onDisable() {
		afkStart = 0;
		afkStarted = false;
	}
	
	@Override
	public void handleEvent(Event event) {
		if(event instanceof EventPlayerUpdatePre) {
			long diff = (System.currentTimeMillis() - afkStart) / 1000;
			if(diff >= this.minutesToDisconnect.getValue()*60) {
				Client.forceDisconnect(this);
			}
		}
	}

}
