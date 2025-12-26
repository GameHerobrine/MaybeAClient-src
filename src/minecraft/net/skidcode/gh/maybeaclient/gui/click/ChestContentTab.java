package net.skidcode.gh.maybeaclient.gui.click;

import org.lwjgl.opengl.GL11;

import net.minecraft.src.Block;
import net.minecraft.src.EnumMovingObjectType;
import net.minecraft.src.GuiIngame;
import net.minecraft.src.ItemStack;
import net.minecraft.src.RenderHelper;
import net.minecraft.src.RenderManager;
import net.skidcode.gh.maybeaclient.Client;
import net.skidcode.gh.maybeaclient.hacks.ChestCheckerHack;
import net.skidcode.gh.maybeaclient.hacks.ClickGUIHack;
import net.skidcode.gh.maybeaclient.hacks.ClientNameHack;
import net.skidcode.gh.maybeaclient.hacks.InventoryViewHack;

public class ChestContentTab extends Tab{
	public static ChestContentTab instance;
	public ChestContentTab() {
		super("ChestContent");
		this.xDefPos = this.xPos = 255;
		this.yDefPos = this.yPos = 24 + 14 + 14 + 14 + 14 + 14;
		this.minimized = false;
		instance = this;
	}
	
	public void renderIngame() {
		if(ChestCheckerHack.instance.status) {
			super.renderIngame();
		}
	}
	
	public void render() {
		ItemStack[] contents = null;
		if(Client.mc.objectMouseOver != null && Client.mc.objectMouseOver.typeOfHit == EnumMovingObjectType.TILE && Client.mc.theWorld != null) {
			int x = Client.mc.objectMouseOver.blockX;
			int y = Client.mc.objectMouseOver.blockY;
			int z = Client.mc.objectMouseOver.blockZ;
			int id = Client.mc.theWorld.getBlockId(x, y, z);
			if(id == Block.crate.blockID) {
				contents = ChestCheckerHack.instance.getChestContents(x, y, z);
			}
		}
		
		if(this.minimized) {
			this.height = ClickGUIHack.theme().yspacing;
			this.width = Client.mc.fontRenderer.getStringWidth(this.name) + ClickGUIHack.theme().titleXadd;
			super.render();
			return;
		}
		
		if(contents == null) {
			this.width = 18*9 + 2;
			this.height = (int) (18*3 + (ClickGUIHack.theme().yspacing + ClickGUIHack.theme().titlebasediff));
			super.render();
			this.renderFrame(
				(int)this.xPos, (int)this.yPos + ClickGUIHack.theme().yspacing + ClickGUIHack.theme().titlebasediff, 
				(int)this.xPos + this.width, (int)this.yPos + this.height
			);
			return;
		}
		
		super.render();
		this.width = 18*9 + 2;
		this.height = (int) (18*Math.ceil(contents.length/9) + (ClickGUIHack.theme().yspacing + ClickGUIHack.theme().titlebasediff));
		this.renderFrame(
			(int)this.xPos, (int)this.yPos + ClickGUIHack.theme().yspacing + ClickGUIHack.theme().titlebasediff, 
			(int)this.xPos + this.width, (int)this.yPos + this.height
		);
		GL11.glPushMatrix();
		RenderHelper.enableStandardItemLighting();
		GL11.glColor4f(1, 1, 1, 1);
		
		int xo = 0;
		int yo = 0;
		for(int i = 0; i < contents.length; ++i) {
			if(xo >= 9) {
				xo = 0;
				++yo;
			}
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GuiIngame.itemRenderer.renderItemIntoGUI(Client.mc.fontRenderer, Client.mc.renderEngine, contents[i], (int)this.xPos + xo*18, (int)this.yPos + 14 + 2 + 18*yo);
			GuiIngame.itemRenderer.renderItemOverlayIntoGUI(Client.mc.fontRenderer, Client.mc.renderEngine, contents[i], (int)this.xPos + xo*18, (int)this.yPos + 14 + 2 + 18*yo);
			++xo;
		}
		
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		RenderHelper.disableStandardItemLighting();
		GL11.glPopMatrix();
	}
}
