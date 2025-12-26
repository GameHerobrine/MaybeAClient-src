package net.skidcode.gh.maybeaclient.gui.click;

import net.skidcode.gh.maybeaclient.Client;
import net.skidcode.gh.maybeaclient.hacks.ClickGUIHack;
import net.skidcode.gh.maybeaclient.hacks.ClientInfoHack;
import net.skidcode.gh.maybeaclient.hacks.ClientNameHack;

public class ClientNameTab extends Tab{
	public static ClientNameTab instance;
	public ClientNameTab() {
		super("ClientName");
		this.xDefPos = this.xPos = 0;
		this.yDefPos = this.yPos = 0;
		this.height = 12;
		this.canMinimize = false;
		instance = this;
	}
	public void renderName() {
		String name = this.name;
		this.name = ClientNameHack.instance.clientName();
		super.renderName();
		this.name = name;
	}
	public void render() {
		this.width = Client.mc.fontRenderer.getStringWidth(ClientNameHack.instance.clientName()) + ClickGUIHack.theme().titleXadd;
		this.renderName();
	}
	
	public void renderIngame() {
		if(ClientNameHack.instance.status) super.renderIngame();
	}
}
