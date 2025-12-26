package net.skidcode.gh.maybeaclient.hacks;

import org.lwjgl.input.Keyboard;

import net.minecraft.src.NBTTagByte;
import net.minecraft.src.NBTTagCompound;
import net.skidcode.gh.maybeaclient.events.Event;
import net.skidcode.gh.maybeaclient.events.EventListener;
import net.skidcode.gh.maybeaclient.events.EventRegistry;
import net.skidcode.gh.maybeaclient.events.impl.EventPlayerUpdatePost;
import net.skidcode.gh.maybeaclient.events.impl.EventRenderIngameNoDebug;
import net.skidcode.gh.maybeaclient.hacks.category.Category;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingBoolean;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingFloat;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingInteger;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingMode;
import net.skidcode.gh.maybeaclient.utils.ChatColor;

public class CameraLockHack extends Hack implements EventListener{
	
	public static enum LockMode{
		STATIC,
		SNAP,
		OFF
	};
	
	public static CameraLockHack instance;
	public SettingMode lockYaw;
	public SettingBoolean lockPitch = new SettingBoolean(this, "LockPitch", false);
	
	public SettingInteger snapYawBy = new SettingInteger(this, "Snap yaw by", 45, 0, 90, 5);
	
	public SettingFloat yaw = new SettingFloat(this, "Yaw", 0, 0, 359, 5);
	public SettingFloat pitch = new SettingFloat(this, "Pitch", 0, -90, 90, 5);
	
	public CameraLockHack() {
		super("CameraLock", "Allows players to lock yaw and pitch of the camera", Keyboard.KEY_NONE, Category.MISC);
		instance = this;
		this.addSetting(lockYaw = new SettingMode(this, "LockYaw", "Static", "Snap", "Off") {
			
			@Override
			public void setValue(String value) {
				super.setValue(value);
				boolean snap = this.currentMode.equalsIgnoreCase("Snap");
				boolean stat = this.currentMode.equalsIgnoreCase("Static");
				
				snapYawBy.hidden = !snap;
				yaw.hidden = !stat;
			}
			
			@Override
			public void readFromNBT(NBTTagCompound input) {
				if(input.hasKey(this.name) && input.tagMap.get(this.name) instanceof NBTTagByte) {
					//compat
					boolean b = input.getBoolean(this.name);
					if(b) lockYaw.setValue("Static");
					else lockYaw.setValue("Off");
				}else {
					super.readFromNBT(input);
				}
			}
		});
		this.addSetting(yaw);
		this.addSetting(this.snapYawBy);
		
		this.addSetting(lockPitch);
		this.addSetting(pitch);
		
		EventRegistry.registerListener(EventPlayerUpdatePost.class, this);
		//EventRegistry.registerListener(EventRenderIngameNoDebug.class, this);
	}
	
	@Override
	public String getPrefix() {
		String s = "";
		LockMode yawlock = this.lockYaw();
		if(yawlock != LockMode.OFF) {
			s += ChatColor.BLACK;
			if(yawlock == LockMode.STATIC) s += this.yaw.value;
			else s += "Snap";
			s += ChatColor.WHITE;
			if(this.lockPitch.value) s += ";";
		}
		
		if(this.lockPitch.value) {
			s += ChatColor.BLACK;
			s += this.pitch.value;
			s += ChatColor.WHITE;
		}
		return s;
	}
	
	double additionalYaw;
	public void addYaw(double d) {
		additionalYaw += d;
		if(Math.abs(additionalYaw) >= this.snapYawBy.value) {
			mc.thePlayer.rotationYaw += (additionalYaw < 0 ? -this.snapYawBy.value : this.snapYawBy.value);
			additionalYaw = 0;
		}else if(this.snapYawBy.value != 0){
			mc.thePlayer.rotationYaw = ((int)mc.thePlayer.rotationYaw / this.snapYawBy.value) * this.snapYawBy.value;
			mc.thePlayer.rotationYaw %= 360;
		}
	}
	
	public LockMode lockYaw() {
		if(this.lockYaw.currentMode.equalsIgnoreCase("off")) return LockMode.OFF;
		if(this.lockYaw.currentMode.equalsIgnoreCase("static")) return LockMode.STATIC;
		if(this.lockYaw.currentMode.equalsIgnoreCase("snap")) return LockMode.SNAP;
		return LockMode.OFF;
	}
	
	@Override
	public void handleEvent(Event event) {
		if(event instanceof EventPlayerUpdatePost) {
			LockMode yawlock = this.lockYaw();
			if(yawlock != LockMode.OFF) {
				if(yawlock == LockMode.STATIC) mc.thePlayer.rotationYaw = mc.thePlayer.prevRotationYaw = this.yaw.value % 360;
			}
			if(this.lockPitch.value) mc.thePlayer.rotationPitch = mc.thePlayer.prevRotationPitch = this.pitch.value % 91;
		}/*else if(event instanceof EventRenderIngameNoDebug) {
			EventRenderIngameNoDebug ev = (EventRenderIngameNoDebug) event;
			if(lockYaw() == LockMode.SNAP) {
				int w = ev.resolution.getScaledWidth();
				int h = ev.resolution.getScaledHeight();
				int yw = ((int)mc.thePlayer.rotationYaw);
				String s;
				if(Math.abs(additionalYaw) > 0) {
					s = ""+yw;
					mc.fontRenderer.drawStringWithShadow(s, (w-mc.fontRenderer.getStringWidth(s))/2+1, h/2 + 16, 0xffffff);
					s = ""+(yw-45);
					mc.fontRenderer.drawStringWithShadow(s, (w-mc.fontRenderer.getStringWidth(s))/2+1-45, h/2 + 16, 0xffffff);
					s = ""+(yw+45);
					mc.fontRenderer.drawStringWithShadow(s, (w-mc.fontRenderer.getStringWidth(s))/2+1+45, h/2 + 16, 0xffffff);
				}
			}
		}*/
	}
}
