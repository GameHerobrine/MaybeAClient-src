package net.skidcode.gh.maybeaclient.gui.click;

import org.lwjgl.opengl.GL11;

import net.minecraft.src.GuiIngame;
import net.minecraft.src.RenderHelper;
import net.minecraft.src.RenderManager;
import net.skidcode.gh.maybeaclient.Client;
import net.skidcode.gh.maybeaclient.hacks.ClickGUIHack;
import net.skidcode.gh.maybeaclient.hacks.ClientNameHack;
import net.skidcode.gh.maybeaclient.hacks.InventoryViewHack;

public class InventoryTab extends Tab{
	public static InventoryTab instance;
	public InventoryTab() {
		super("Inventory");
		this.xDefPos = this.xPos = 160;
		this.yDefPos = this.yPos = 24 + 14 + 14 + 14 + 14 + 14;
		this.minimized = true;
		instance = this;
	}
	
	public void renderIngame() {
		if(InventoryViewHack.instance.status && RenderManager.instance.livingPlayer != null) super.renderIngame();
	}
	
	public void render() {
		int ySpace = ClickGUIHack.theme().yspacing;
		int prevSpace = ClickGUIHack.theme().titlebasediff;
		int txtCenter = ClickGUIHack.theme().yaddtocenterText;
		if(this.minimized) {
			this.height = ySpace;
			this.width = Client.mc.fontRenderer.getStringWidth(this.name) + ClickGUIHack.theme().titleXadd;
			super.render();
			return;
		}
		super.render();
		this.width = 18*9 + 2;
		this.height = 18*3 + ySpace + prevSpace;
		this.renderFrame((int)this.xPos, (int)this.yPos + ySpace + prevSpace, (int)this.xPos + this.width, (int)this.yPos + this.height);
		GL11.glPushMatrix();
		RenderHelper.enableStandardItemLighting();
		GL11.glColor4f(1, 1, 1, 1);
		
		int slot = 9;
		for(int yo = 0; yo < 3; ++yo) {
			for(int xo = 0; xo < 9; ++xo) {
				GL11.glDisable(GL11.GL_LIGHTING);
				GL11.glDisable(GL11.GL_DEPTH_TEST);
				GuiIngame.itemRenderer.renderItemIntoGUI(Client.mc.fontRenderer, Client.mc.renderEngine, Client.mc.thePlayer.inventory.mainInventory[slot], (int)this.xPos + xo*18, (int)this.yPos + 14 + 2 + 18*yo);
				GuiIngame.itemRenderer.renderItemOverlayIntoGUI(Client.mc.fontRenderer, Client.mc.renderEngine, Client.mc.thePlayer.inventory.mainInventory[slot], (int)this.xPos + xo*18, (int)this.yPos + 14 + 2 + 18*yo);
				++slot;
			}
		}
		
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		RenderHelper.disableStandardItemLighting();
		GL11.glPopMatrix();
	}
}
