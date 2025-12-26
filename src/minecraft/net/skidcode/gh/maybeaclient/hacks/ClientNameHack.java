package net.skidcode.gh.maybeaclient.hacks;

import org.lwjgl.input.Keyboard;

import net.skidcode.gh.maybeaclient.Client;
import net.skidcode.gh.maybeaclient.hacks.category.Category;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingBoolean;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingTextBox;

public class ClientNameHack extends Hack{

	public static ClientNameHack instance;
	public SettingBoolean overrideName;
	public SettingBoolean overrideInChat = new SettingBoolean(this, "Override in chat", false);
	public SettingTextBox nameOverride = new SettingTextBox(this, "Name", Client.clientName+" "+Client.clientVersion, 100);
	
	public ClientNameHack() {
		super("ClientName", "Show client name", Keyboard.KEY_NONE, Category.UI);
		instance = this;
		this.status = true;
		this.addSetting(this.overrideName = new SettingBoolean(this, "OverrideName", false) {
			@Override
			public void setValue(boolean d) {
				super.setValue(d);
				ClientNameHack.instance.nameOverride.hidden = !this.getValue();
				ClientNameHack.instance.overrideInChat.hidden = !this.getValue();
			}
		});
		this.addSetting(this.nameOverride);
		this.addSetting(this.overrideInChat);
	}
	public String getPrefix() {
		return this.overrideName.getValue() ? "Overriden" : super.getPrefix();
	}
	public String clientName() {
		if(this.overrideName.getValue()) {
			return this.nameOverride.value;
		}
		return Client.clientName+" "+Client.clientVersion;
	}
	public boolean overrideInChat() {
		return this.overrideName.getValue() && this.overrideInChat.getValue();
	}
}
