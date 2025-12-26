package net.skidcode.gh.maybeaclient.hacks;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import net.minecraft.src.Packet3Chat;
import net.skidcode.gh.maybeaclient.events.Event;
import net.skidcode.gh.maybeaclient.events.EventListener;
import net.skidcode.gh.maybeaclient.events.EventRegistry;
import net.skidcode.gh.maybeaclient.events.impl.EventPacketReceive;
import net.skidcode.gh.maybeaclient.events.impl.EventPlayerUpdatePre;
import net.skidcode.gh.maybeaclient.hacks.category.Category;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingInteger;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingTextBox;

public class GreeterHack extends Hack implements EventListener{
	public static GreeterHack instance;
	public SettingTextBox greetMessage = new SettingTextBox(this, "Greet message", "Welcome %username%", 150);
	public SettingInteger delay = new SettingInteger(this, "DelayTicks", 30, 0, 40);
	public GreeterHack() {
		super("Greeter", "Greets players when they join", Keyboard.KEY_NONE, Category.MISC);
		instance = this;
		
		this.addSetting(this.greetMessage);
		this.addSetting(this.delay);
		EventRegistry.registerListener(EventPacketReceive.class, this);
		EventRegistry.registerListener(EventPlayerUpdatePre.class, this);
	}
	
	public void greet(String player) {
		
	}
	public int timeout = 0;
	public ArrayList<String> scheduledGreets = new ArrayList<>();
	@Override
	public void handleEvent(Event event) {
		if(event instanceof EventPacketReceive) {
			if(((EventPacketReceive) event).packet instanceof Packet3Chat) {
				Packet3Chat pk = (Packet3Chat) ((EventPacketReceive) event).packet;
				if(pk.message.startsWith(PlayerlistHack.pp("&e"))) {
					String[] lst = pk.message.substring(2).split(" ");
					if(lst.length == 4) {
						if(lst[2].equalsIgnoreCase("the") && lst[3].equalsIgnoreCase("game.")) {
							String status = lst[1];
							String player = lst[0];
							
							if(status.equalsIgnoreCase("joined")) {
								if(!player.equals(mc.session.username)) {
									this.scheduledGreets.add(player);
								}
							}
						}
					}
				}

			}
		}else if(event instanceof EventPlayerUpdatePre) {
			if(this.timeout <= 0 && scheduledGreets.size() > 0) {
				String name = scheduledGreets.remove(0);
				String greet = this.greetMessage.value.replaceAll("%username%", name);
				mc.thePlayer.sendChatMessage(greet);
			}else if(this.timeout > 0) {
				--this.timeout;
			}
		}
	}

}
