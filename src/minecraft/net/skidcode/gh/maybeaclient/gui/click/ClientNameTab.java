package net.skidcode.gh.maybeaclient.gui.click;

import org.lwjgl.opengl.GL11;

import net.minecraft.src.GuiIngame;
import net.minecraft.src.RenderHelper;
import net.skidcode.gh.maybeaclient.Client;
import net.skidcode.gh.maybeaclient.hacks.ClickGUIHack;
import net.skidcode.gh.maybeaclient.hacks.ClientInfoHack;
import net.skidcode.gh.maybeaclient.hacks.ClientNameHack;

public class ClientNameTab extends Tab{
	public static ClientNameTab instance;
	public ClientNameTab() {
		super("ClientName");
		this.xDefPos = this.startX = 0;
		this.yDefPos = this.startY = 0;
		this.canMinimize = false;
		instance = this;
		this.isHUD = true;
	}
	@Override
	public void renderName(boolean right) {
		String name = this.getTabName();
		this.name.setValue(ClientNameHack.instance.clientName());
		super.renderName(right);
		this.name.setValue(name);
	}
	@Override
	public void preRender() {
		this.endX = this.startX + Client.mc.fontRenderer.getStringWidth(ClientNameHack.instance.clientName()) + ClickGUIHack.theme().titleXadd;
		this.endY = this.startY + ClickGUIHack.theme().yspacing; //12
		super.preRender();
	}
	
	public void render() {
		super.render();
		if(!ClickGUIHack.renderHeader(this)) {
			Tab.renderFrame(this, (int)this.startX, (int)this.startY + this.getYOffset(), (int)this.endX, (int)this.endY);
			String name = this.getTabName();
			this.name.setValue(ClientNameHack.instance.clientName());
			this.renderNameAt(this.startX, this.startY);
			this.name.setValue(name);
			
			Tab.renderFrameTop(this, (int)this.startX, (int)this.startY + this.getYOffset(), (int)this.endX, (int)this.endY);
		}
	}
	
	@Override
	public void renderIngame() {
		if(ClientNameHack.instance.status) super.renderIngame();
	}
}
