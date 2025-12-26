package net.skidcode.gh.maybeaclient.hacks;

import org.lwjgl.input.Keyboard;

import net.minecraft.src.Block;
import net.skidcode.gh.maybeaclient.Client;
import net.skidcode.gh.maybeaclient.hacks.category.Category;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingBlockChooser;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingFloat;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingMode;
import net.skidcode.gh.maybeaclient.utils.ChatColor;

public class XRayHack extends Hack{
	
	public static XRayHack INSTANCE;

	public static boolean applyOpacity = true;
	
	public SettingFloat opacity;
	public SettingMode mode;
	
	public XRayHack() {
		super("xRay", "Force disables certain block rendering", Keyboard.KEY_X, Category.RENDER);
		INSTANCE = this;
		
		this.opacity = new SettingFloat(this, "Opacity", 0.5f, 0, 1) {
			@Override
			public void setValue(float value) {
				super.setValue(value);
				if(this.hack.status && mc.theWorld != null) {
					mc.entityRenderer.updateRenderer();
			        mc.theWorld.markBlocksDirty((int)mc.thePlayer.posX - 256, 0, (int)mc.thePlayer.posZ - 256, (int)mc.thePlayer.posX + 256, 127, (int)mc.thePlayer.posZ + 256);
				}
			}
		};
		
		this.mode = new SettingMode(this, "Mode", "Normal", "Opacity") {
			public void setValue(String value) {
				boolean toggle = false;
				
				if(this.currentMode != null && !this.currentMode.equalsIgnoreCase(value) && XRayHack.INSTANCE.status) {
					toggle = true;
				}
				
				super.setValue(value);
				if(toggle && mc.theWorld != null) {
					mc.entityRenderer.updateRenderer();
			        mc.theWorld.markBlocksDirty((int)mc.thePlayer.posX - 256, 0, (int)mc.thePlayer.posZ - 256, (int)mc.thePlayer.posX + 256, 127, (int)mc.thePlayer.posZ + 256);
				}
				
				if(this.currentMode.equalsIgnoreCase("Opacity")) {
					XRayHack.INSTANCE.opacity.show();
				}else {
					XRayHack.INSTANCE.opacity.hide();
				}
			}
		};
		this.addSetting(this.opacity);
		this.addSetting(this.mode);
		this.addSetting(this.blockChooser);
	}
	
	@Override
	public String getPrefix() {
		return this.mode.currentMode;
	}
	@Override
	public void toggle() {
		super.toggle();
		mc.entityRenderer.updateRenderer();
        mc.theWorld.markBlocksDirty((int)mc.thePlayer.posX - 256, 0, (int)mc.thePlayer.posZ - 256, (int)mc.thePlayer.posX + 256, 127, (int)mc.thePlayer.posZ + 256);
	}
	public SettingBlockChooser blockChooser = new SettingBlockChooser(
			this, "Blocks", 
			Block.oreCoal.blockID,
			Block.oreDiamond.blockID,
			Block.oreGold.blockID,
			Block.oreIron.blockID,
			Block.oreLapis.blockID,
			Block.oreRedstone.blockID,
			Block.oreRedstoneGlowing.blockID,
			Block.blockDiamond.blockID,
			Block.blockSteel.blockID,
			Block.blockGold.blockID,
			Block.blockLapis.blockID
	) {
		@Override
		public void blockChanged(int id) {
			mc.entityRenderer.updateRenderer();
	        mc.theWorld.markBlocksDirty((int)mc.thePlayer.posX - 256, 0, (int)mc.thePlayer.posZ - 256, (int)mc.thePlayer.posX + 256, 127, (int)mc.thePlayer.posZ + 256);
		}
	};
}
