package net.skidcode.gh.maybeaclient.hacks;

import org.lwjgl.input.Keyboard;

import net.minecraft.src.MathHelper;
import net.minecraft.src.Vec3D;
import net.skidcode.gh.maybeaclient.hacks.category.Category;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingMode;

public class CustomSkyHack extends Hack{
	public static CustomSkyHack instance;
	
	public SettingMode mode = new SettingMode(this, "Mode", "End", "Nether");
	
	public CustomSkyHack() {
		super("CustomSky", "Change sky color", Keyboard.KEY_NONE, Category.RENDER);
		instance = this;
		
		this.addSetting(this.mode);
	}
	
	@Override
	public String getPrefix() {
		return ""+this.mode.currentMode;
	}
	
	public static boolean isEnd() {
		return instance.status && instance.mode.currentMode.equalsIgnoreCase("End");
	}
	
	public static boolean isNether() {
		return instance.status && instance.mode.currentMode.equalsIgnoreCase("Nether");
	}
	
	public static float[] calcSunriseSunsetColors(float var1, float var2) {
		if(isEnd()) return null;
		if(isNether()) return null;
		
		return null;
	}

	public static float calculateCelestialAngle(long var1, float var3) {
		if(isEnd()) return 0;
		if(isNether()) return 0.5f;
		
		return 0;
	}

	public static Vec3D getFogColor(float var1, float var2) {
		//if(true) return Vec3D.createVector(1, 0, 0);
		if(isEnd()) {
			int i = 0x8080a0;
	        float f = MathHelper.cos(var1 * (float)Math.PI * 2.0F) * 2.0F + 0.5F;

	        if (f < 0.0F)
	        {
	            f = 0.0F;
	        }

	        if (f > 1.0F)
	        {
	            f = 1.0F;
	        }

	        float f1 = (float)(i >> 16 & 0xff) / 255F;
	        float f2 = (float)(i >> 8 & 0xff) / 255F;
	        float f3 = (float)(i & 0xff) / 255F;
	        f1 *= f * 0.0F + 0.15F;
	        f2 *= f * 0.0F + 0.15F;
	        f3 *= f * 0.0F + 0.15F;
	        return Vec3D.createVector(f1, f2, f3);
		}
		if(isNether()) {
			return Vec3D.createVector(0.20000000298023224D, 0.029999999329447746D, 0.029999999329447746D);
		}
		
		return Vec3D.createVector(1, 0, 0);
	}

	public static boolean hasSky() {
		return instance.status && (isEnd() || isNether());
	}
	

}
