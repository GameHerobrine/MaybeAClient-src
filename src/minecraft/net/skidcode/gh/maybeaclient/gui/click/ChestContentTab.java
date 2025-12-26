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
		this.xDefPos = this.startX = 255;
		this.yDefPos = this.startY = 24 + 14 + 14 + 14 + 14 + 14;
		instance = this;
		this.isHUD = true;
	}
	
	public void renderIngame() {
		if(ChestCheckerHack.instance.status) {
			super.renderIngame();
		}
	}
	
	ItemStack[] contents = null;
	@Override
	public void preRender() {
		contents = null;
		if(Client.mc.objectMouseOver != null && Client.mc.objectMouseOver.typeOfHit == EnumMovingObjectType.TILE && Client.mc.theWorld != null) {
			int x = Client.mc.objectMouseOver.blockX;
			int y = Client.mc.objectMouseOver.blockY;
			int z = Client.mc.objectMouseOver.blockZ;
			int id = Client.mc.theWorld.getBlockId(x, y, z);
			if(id == Block.chest.blockID && ChestCheckerHack.instance.status) {
				contents = ChestCheckerHack.instance.getChestContents(x, y, z);
			}
		}
		
		int width, height;
		if(this.minimized.getValue()) {
			height = ClickGUIHack.theme().yspacing;
			width = Client.mc.fontRenderer.getStringWidth(this.getTabName()) + ClickGUIHack.theme().titleXadd;
		}else if(contents == null) {
			width = 18*9 + 2;
			height = (int) (18*3 + this.getYOffset()) + 2;
		}else {
			width = 18*9 + 2;
			height = (int) (18*Math.ceil(contents.length/9) + this.getYOffset()) + 2;
		}
		
		this.endX = this.startX + width;
		this.endY = this.startY + height;
		super.preRender();
	}
	
	@Override
	public void render() {
		if(this.minimized.getValue()) {
			super.render();
			return;
		}
		
		super.render();
		Tab.renderFrame(this, (int)this.startX, (int)this.startY + this.getYOffset(), (int)this.endX, (int)this.endY);
		
		if(contents != null) {
			GL11.glPushMatrix();
			RenderHelper.enableStandardItemLighting();
			GL11.glColor4f(1, 1, 1, 1);
			
			int xo = 0;
			int yoff = this.getYOffset();
			int yo = 0;
			for(int i = 0; i < contents.length; ++i) {
				if(xo >= 9) {
					xo = 0;
					++yo;
				}
				GL11.glDisable(GL11.GL_LIGHTING);
				GL11.glDisable(GL11.GL_DEPTH_TEST);
				GuiIngame.itemRenderer.renderItemIntoGUI(Client.mc.fontRenderer, Client.mc.renderEngine, contents[i], (int)this.startX + xo*18, (int)this.startY + yoff + 2 + 18*yo);
				GuiIngame.itemRenderer.renderItemOverlayIntoGUI(Client.mc.fontRenderer, Client.mc.renderEngine, contents[i], (int)this.startX + xo*18, (int)this.startY + yoff + 2 + 18*yo);
				++xo;
			}
			
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			RenderHelper.disableStandardItemLighting();
			GL11.glPopMatrix();
		}
		
		Tab.renderFrameTop(this, (int)this.startX, (int)this.startY + this.getYOffset(), (int)this.endX, (int)this.endY);
		
	}
}
