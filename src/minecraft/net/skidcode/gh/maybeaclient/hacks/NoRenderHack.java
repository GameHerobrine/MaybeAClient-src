package net.skidcode.gh.maybeaclient.hacks;

import org.lwjgl.input.Keyboard;

import net.minecraft.src.EntityDiggingFX;
import net.minecraft.src.EntityExplodeFX;
import net.minecraft.src.EntityFX;
import net.minecraft.src.EntityRainFX;
import net.skidcode.gh.maybeaclient.hacks.category.Category;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingBoolean;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingChooser;

public class NoRenderHack extends Hack{
	public static NoRenderHack instance;
	
	public SettingBoolean signText = new SettingBoolean(this, "SignText", false);
	public SettingBoolean itemEntities = new SettingBoolean(this, "ItemEntities", false);
	public SettingBoolean clouds = new SettingBoolean(this, "Clouds", false);
	public SettingBoolean fog = new SettingBoolean(this, "Fog", false);
	public SettingBoolean boats = new SettingBoolean(this, "Boats", false);
	public SettingBoolean waterAnim = new SettingBoolean(this, "Water Animation", false);
	public SettingBoolean lavaAnim = new SettingBoolean(this, "Lava Animation", false);
	public SettingChooser particles = new SettingChooser(
			this, "Particles",
			new String[] {"Break", "Explosion", "Other"},
			new boolean[] {false, false, false}
	);
	public SettingBoolean weather = new SettingBoolean(this, "Weather", false);
	public SettingBoolean vignette = new SettingBoolean(this, "Vignette", false);
	public SettingBoolean players = new SettingBoolean(this, "Players", false);
	
	public NoRenderHack() {
		super("NoRender", "Disables some rendering", Keyboard.KEY_NONE, Category.RENDER);
		instance = this;
		this.addSetting(this.signText);
		this.addSetting(this.itemEntities);
		this.addSetting(this.clouds);
		this.addSetting(this.fog);
		this.addSetting(this.boats);
		this.addSetting(this.waterAnim);
		this.addSetting(this.lavaAnim);
		this.addSetting(this.particles);
		this.addSetting(this.weather);
		this.addSetting(this.vignette);
		this.addSetting(this.players);
	}
	public static boolean shouldRender(EntityFX e) {
		if(!instance.status) return true;
		if(instance.particles.minimized) return true;
		//, "Break", "Explosion", "Other"
		//if(instance.particles.getValue("Rain") && e instanceof EntityRainFX) return false;
		if(e instanceof EntityDiggingFX) return !instance.particles.getValue("Break");
		if(e instanceof EntityExplodeFX) return !instance.particles.getValue("Explosion");
		if(instance.particles.getValue("Other")) return false;
		return true;
	}

}
