package net.skidcode.gh.maybeaclient.hacks;

import org.lwjgl.input.Keyboard;

import net.skidcode.gh.maybeaclient.hacks.category.Category;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingBoolean;

public class ClimbGappedLadderHack extends Hack{
	public SettingBoolean allowTwoBlockGaps = new SettingBoolean(this, "AllowTwoBlockGaps", true);
	public static ClimbGappedLadderHack instance;
	public ClimbGappedLadderHack() {
		super("ClimbGappedLadder", "Allows to climb ladders with gaps between them", Keyboard.KEY_NONE, Category.MOVEMENT);
		instance = this;
		this.addSetting(this.allowTwoBlockGaps);
	}

	@Override
	public String getPrefix() {
		return ""+(this.allowTwoBlockGaps.getValue() ? 2 : 1);
	}
	
}
