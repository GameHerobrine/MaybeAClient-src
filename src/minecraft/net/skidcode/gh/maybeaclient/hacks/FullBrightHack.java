package net.skidcode.gh.maybeaclient.hacks;

import org.lwjgl.input.Keyboard;

import net.skidcode.gh.maybeaclient.hacks.category.Category;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingBoolean;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingFloat;

public class FullBrightHack extends Hack{
	public static FullBrightHack INSTANCE;
	
	public SettingFloat brightness = new SettingFloat(this, "Brightness", 1f, 0.1f, 1f, 0.1f) {
		@Override
		public void setValue(float d) {
			super.setValue(d);
			if(mc.entityRenderer != null) mc.entityRenderer.updateRenderer();
			if(mc.theWorld != null && mc.thePlayer != null) mc.theWorld.markBlocksDirty((int)mc.thePlayer.posX - 256, 0, (int)mc.thePlayer.posZ - 256, (int)mc.thePlayer.posX + 256, 127, (int)mc.thePlayer.posZ + 256);
		};
	};
	public SettingBoolean smooth = new SettingBoolean(this, "SmoothLight", false) {
		@Override
		public void setValue(boolean b) {
			super.setValue(b);
			if(mc.entityRenderer != null) mc.entityRenderer.updateRenderer();
			if(mc.theWorld != null && mc.thePlayer != null) mc.theWorld.markBlocksDirty((int)mc.thePlayer.posX - 256, 0, (int)mc.thePlayer.posZ - 256, (int)mc.thePlayer.posX + 256, 127, (int)mc.thePlayer.posZ + 256);
		};
	};
	
	public FullBrightHack() {
		super("FullBright", "Makes the world brighter", Keyboard.KEY_C, Category.RENDER);
		INSTANCE = this;
		this.addSetting(this.brightness);
		this.addSetting(this.smooth);
	}
	public void toggle() {
		super.toggle();
		mc.entityRenderer.updateRenderer();
        mc.theWorld.markBlocksDirty((int)mc.thePlayer.posX - 256, 0, (int)mc.thePlayer.posZ - 256, (int)mc.thePlayer.posX + 256, 127, (int)mc.thePlayer.posZ + 256);
	}
}
