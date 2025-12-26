package net.skidcode.gh.maybeaclient.hacks;

import org.lwjgl.input.Keyboard;

import net.minecraft.src.WorldInfo;
import net.skidcode.gh.maybeaclient.Client;
import net.skidcode.gh.maybeaclient.hacks.category.Category;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingEnum;

public class WeatherLockHack extends Hack{
	public static enum Weather{
		SUN("Sun"),
		RAIN("Rain");
		
		public final String name;
		Weather(String s) {
			this.name = s;
		}
		
		public String toString() {
			return this.name;
		}
	};
	
	public SettingEnum<Weather> weather = new SettingEnum<Weather>(this, "Weather", Weather.SUN);
	public static WeatherLockHack instance;
	public static boolean raining = false;
	public WeatherLockHack() {
		super("WeatherLock", "Locks weather", Keyboard.KEY_NONE, Category.MISC);
		this.addSetting(this.weather);
		instance = this;
	}
	
	@Override
	public void onEnable() {
		if(Client.mc.isMultiplayerWorld()) {
			raining = Client.mc.theWorld.getWorldInfo().getRaining();
		}
	}
	
	@Override
	public void onDisable() {
		if(Client.mc.isMultiplayerWorld()) {
			WorldInfo wi = Client.mc.theWorld.getWorldInfo();
			wi.setRaining(raining);
			if(wi.getRaining()) {
				Client.mc.theWorld.func_27158_h(1.0F);
			}else {
				Client.mc.theWorld.func_27158_h(0.0F);
			}
		}
	}
}
