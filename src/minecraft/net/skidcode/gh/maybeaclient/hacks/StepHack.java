package net.skidcode.gh.maybeaclient.hacks;

import org.lwjgl.input.Keyboard;

import net.skidcode.gh.maybeaclient.events.Event;
import net.skidcode.gh.maybeaclient.events.EventListener;
import net.skidcode.gh.maybeaclient.events.EventRegistry;
import net.skidcode.gh.maybeaclient.events.impl.EventPlayerUpdatePost;
import net.skidcode.gh.maybeaclient.hacks.category.Category;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingFloat;
import net.skidcode.gh.maybeaclient.utils.ChatColor;

public class StepHack extends Hack implements EventListener{
	
	public static StepHack instance;
	public SettingFloat stepHeight = new SettingFloat(this, "StepHeight", 1, 0, 5, 0.5f);
	
	public StepHack() {
		super("Step", "Automatically step up to multiple blocks", Keyboard.KEY_NONE, Category.MOVEMENT);
		this.addSetting(this.stepHeight);
		
		instance = this;
		EventRegistry.registerListener(EventPlayerUpdatePost.class, this);
	}
	
	public void onDisable() {
		mc.thePlayer.stepHeight = 0.5f;
	}
	
	@Override
	public String getPrefix() {
		return String.format("%.2f", this.stepHeight.value);
	}
	@Override
	public void handleEvent(Event event) {
		if(event instanceof EventPlayerUpdatePost) {
			mc.thePlayer.stepHeight = this.stepHeight.value;
		}
	}

}
