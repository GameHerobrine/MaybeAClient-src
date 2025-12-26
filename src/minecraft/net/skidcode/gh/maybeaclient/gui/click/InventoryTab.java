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
		this.xDefPos = this.startX = 160;
		this.yDefPos = this.startY = 24 + 14 + 14 + 14 + 14 + 14;
		this.minimized.setValue(true);
		instance = this;
		this.isHUD = true;
	}
	
	public void renderIngame() {
		if(InventoryViewHack.instance.status && RenderManager.instance.livingPlayer != null) super.renderIngame();
	}
	
	public void preRender() {
		int ySpace = ClickGUIHack.theme().yspacing;
		this.endX = this.startX + 18*9 + 2;
		this.endY = this.startY + 18*3 + this.getYOffset() + 2;
		if(this.minimized.getValue()) this.endY = this.startY + ySpace;
		super.preRender();
	}
	
	public void render() {
		super.render();
		if(this.minimized.getValue()) return;
		Tab.renderFrame(this, (int)this.startX, (int)this.startY + this.getYOffset(), (int)this.endX, (int)this.endY);
		GL11.glPushMatrix();
		RenderHelper.enableStandardItemLighting();
		GL11.glColor4f(1, 1, 1, 1);
		
		int slot = 9;
		int yoff = this.getYOffset();
		for(int yo = 0; yo < 3; ++yo) {
			for(int xo = 0; xo < 9; ++xo) {
				GL11.glDisable(GL11.GL_LIGHTING);
				GL11.glDisable(GL11.GL_DEPTH_TEST);
				GuiIngame.itemRenderer.renderItemIntoGUI(Client.mc.fontRenderer, Client.mc.renderEngine, Client.mc.thePlayer.inventory.mainInventory[slot], (int)this.startX + xo*18, (int)this.startY + yoff + 2 + 18*yo);
				GuiIngame.itemRenderer.renderItemOverlayIntoGUI(Client.mc.fontRenderer, Client.mc.renderEngine, Client.mc.thePlayer.inventory.mainInventory[slot], (int)this.startX + xo*18, (int)this.startY + yoff + 2 + 18*yo);
				++slot;
			}
		}
		
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		RenderHelper.disableStandardItemLighting();
		GL11.glPopMatrix();
		Tab.renderFrameTop(this, (int)this.startX, (int)this.startY + this.getYOffset(), (int)this.endX, (int)this.endY);
	}
}
