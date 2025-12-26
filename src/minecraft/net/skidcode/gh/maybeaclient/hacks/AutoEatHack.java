package net.skidcode.gh.maybeaclient.hacks;

import org.lwjgl.input.Keyboard;

import net.minecraft.src.ItemFood;
import net.minecraft.src.ItemStack;
import net.skidcode.gh.maybeaclient.events.Event;
import net.skidcode.gh.maybeaclient.events.EventListener;
import net.skidcode.gh.maybeaclient.events.EventRegistry;
import net.skidcode.gh.maybeaclient.events.impl.EventPlayerUpdatePre;
import net.skidcode.gh.maybeaclient.hacks.category.Category;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingBoolean;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingChooser;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingInteger;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingMode;
import net.skidcode.gh.maybeaclient.utils.ChatColor;

public class AutoEatHack extends Hack implements EventListener{
	public SettingInteger maxHPOverflow = new SettingInteger(this, "MaxOverflow", 19, 0, 19);
	public SettingInteger delayTicks = new SettingInteger(this, "DelayTicks", 10, 0, 20);
	public SettingInteger health = new SettingInteger(this, "Health", 19, 0, 19);
	public SettingMode mode = new SettingMode(this, "Mode", "Original", "Simple") {
		@Override
		public void setValue(String value) {
			super.setValue(value);
			AutoEatHack h = (AutoEatHack) this.hack;
			h.maxHPOverflow.hidden = !this.currentMode.equalsIgnoreCase("Original");
			h.health.hidden = !this.currentMode.equalsIgnoreCase("Simple");
		}
	};
	
	public long ticksPassed = 0;
	public AutoEatHack() {
		super("AutoEat", "Automatically eats food", Keyboard.KEY_NONE, Category.COMBAT);
		this.addSetting(this.mode);
		this.addSetting(this.maxHPOverflow);
		this.addSetting(this.health);
		
		this.addSetting(this.delayTicks);
		EventRegistry.registerListener(EventPlayerUpdatePre.class, this);
	}
	
	@Override
	public String getPrefix() {
		if(this.mode.currentMode.equalsIgnoreCase("Simple")) return ""+this.health.getValue();
		return super.getPrefix();
	}
	public void handleEvent(Event e) {
        if (e instanceof EventPlayerUpdatePre) {
        	++ticksPassed;
        	boolean isSimpleMode = this.mode.currentMode.equalsIgnoreCase("Simple");
        	int healthLimit = isSimpleMode ? this.health.getValue() : 19;
        	if(mc.thePlayer.health <= healthLimit) {
        		int prevItem = mc.thePlayer.inventory.currentItem;
				for (int i = 0; i < 9; i++) {
					ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
					if (stack != null && (stack.getItem() instanceof ItemFood) && ticksPassed > this.delayTicks.value) {
						
						ItemFood food = (ItemFood) stack.getItem();
						int newHealth = food.getHealAmount();
						boolean needsToEat = isSimpleMode ? true : ((newHealth + mc.thePlayer.health) - 20 <= this.maxHPOverflow.value);
						if(needsToEat) {
							mc.thePlayer.inventory.currentItem = i;
							mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, stack);
							mc.thePlayer.inventory.currentItem = prevItem;
							ticksPassed = 0;
							break;
						}
					}
				}
        	}
        }
	}
}
