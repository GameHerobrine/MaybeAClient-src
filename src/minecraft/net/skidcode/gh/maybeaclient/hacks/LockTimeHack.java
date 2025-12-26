package net.skidcode.gh.maybeaclient.hacks;

import org.lwjgl.input.Keyboard;

import net.skidcode.gh.maybeaclient.hacks.category.Category;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingLong;
import net.skidcode.gh.maybeaclient.utils.ChatColor;

public class LockTimeHack extends Hack{
	public SettingLong lockedTime = new SettingLong(this, "Time", 0, 0, 24000);

	public long realTime = 0;
	
	public static LockTimeHack INSTANCE;
	
	public LockTimeHack() {
		super("LockTime", "Lock time on specific value.", Keyboard.KEY_NONE, Category.MISC);
		INSTANCE = this;
		this.addSetting(this.lockedTime);
	}
	
	@Override
	public String getPrefix() {
		return ""+this.lockedTime.value;
	}
}
