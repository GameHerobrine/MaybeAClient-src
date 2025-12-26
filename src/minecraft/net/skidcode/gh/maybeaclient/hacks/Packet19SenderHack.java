package net.skidcode.gh.maybeaclient.hacks;

import org.lwjgl.input.Keyboard;

import net.minecraft.src.Packet19EntityAction;
import net.skidcode.gh.maybeaclient.events.Event;
import net.skidcode.gh.maybeaclient.events.EventListener;
import net.skidcode.gh.maybeaclient.events.EventRegistry;
import net.skidcode.gh.maybeaclient.events.impl.EventPlayerUpdatePost;
import net.skidcode.gh.maybeaclient.hacks.category.Category;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingFloat;

public class Packet19SenderHack extends Hack implements EventListener{
	public SettingFloat delayS = new SettingFloat(this, "DelaySeconds", 0, 0, 2, 0.05f);
	
	
	public Packet19SenderHack() {	
		super("Packet19Sender", "Sends packet19", Keyboard.KEY_NONE, Category.MISC);
		
		this.addSetting(this.delayS);
		
		EventRegistry.registerListener(EventPlayerUpdatePost.class, this);
	}

	public long nextSend = 0;

	@Override
	public void handleEvent(Event event) {
		if(event instanceof EventPlayerUpdatePost && mc.isMultiplayerWorld()) {
			long l = System.currentTimeMillis();
			long addr = (long) (this.delayS.value*1000);
			if(l > nextSend) {
				mc.getSendQueue().addToSendQueue(new Packet19EntityAction(mc.thePlayer, 3));
				this.nextSend = l + addr;
			}
			
		}
	}
	
	

}
