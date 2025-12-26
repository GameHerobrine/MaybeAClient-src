package net.skidcode.gh.maybeaclient.hacks;

import org.lwjgl.input.Keyboard;

import net.minecraft.src.NBTTagCompound;
import net.skidcode.gh.maybeaclient.hacks.category.Category;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingFloat;
import net.skidcode.gh.maybeaclient.utils.ChatColor;

public class FOVHack extends Hack{
	
	public SettingFloat fov = new SettingFloat(this, "Screen FOV", 70, 30, 110, 1) {
		@Override
		public void readFromNBT(NBTTagCompound input) {
			if(input.hasKey("FOV")) this.setValue(input.getFloat("FOV"));
			super.readFromNBT(input);
		}
	};
	
	public SettingFloat fovHand = new SettingFloat(this, "Hand FOV", 70, 30, 110, 1);
	
	public static FOVHack instance;
	public FOVHack() {
		super("Fov", "Change field of view", Keyboard.KEY_NONE, Category.RENDER);
		this.addSetting(this.fov);
		this.addSetting(this.fovHand);
		instance = this;
	}
	@Override
	public String getPrefix() {
		return ""+this.fov.value;
	}
}
