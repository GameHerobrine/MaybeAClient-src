package net.skidcode.gh.maybeaclient.hacks;

import org.lwjgl.input.Keyboard;

import net.skidcode.gh.maybeaclient.hacks.category.Category;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingBoolean;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingColor;

public class SlimeChunkRadarHack extends Hack {
	public static SlimeChunkRadarHack instance;
	public SettingColor playerColor = new SettingColor(this, "Player Color", 255, 0, 0);
	public SettingColor slimeChunkColor = new SettingColor(this, "Slime Chunk Color", 0, 255, 0);
	public SettingBoolean showChunkGrid = new SettingBoolean(this, "Show Chunk Grid", true);
	public SlimeChunkRadarHack() {
		super("SlimeChunkRadar", "Shows slime chunks around the player", Keyboard.KEY_NONE, Category.UI);
		instance = this;
		this.addSetting(this.playerColor);
		this.addSetting(this.slimeChunkColor);
		this.addSetting(this.showChunkGrid);
	}
	
	
}
