package net.skidcode.gh.maybeaclient.gui.mapart;

import org.lwjgl.opengl.GL11;
import net.minecraft.src.Block;
import net.minecraft.src.GuiIngame;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Tessellator;
import net.skidcode.gh.maybeaclient.Client;
import net.skidcode.gh.maybeaclient.gui.GuiSlotCustom;
import net.skidcode.gh.maybeaclient.utils.GUIUtils;

public class GuiColorSlot extends GuiSlotCustom{
	public GuiMapArtSelectBlocks parent;
	public GuiColorSlot(GuiMapArtSelectBlocks gui) {
		super(Client.mc, gui.width, gui.height, 32, gui.height - 55 + 4, 24);
		this.parent = gui;
	}

	@Override
	protected int getSize() {
		return Client.possibleColors.length;
	}

	@Override
	public int getSlotSize(int id, int minX, int maxX) {
		int width = maxX - minX - 24-18;
		int col = Client.possibleColors[id];
		int i = Client.color2block.get(col).size();
		if(width <= 0) return this.posZ * i;
		
		int a = (int) Math.ceil(((i*18f)/(width)));
		return a*this.posZ;
	}
	
	@Override
	protected void elementClicked(int id, boolean var2, int mx, int my, int minX, int minY, int maxX, int maxY) {
		int col = Client.possibleColors[id];
		int xo = minX + 24;
		int yo = minY;
		
		if(GUIUtils.isInsideRect(mx, my, xo, yo+2, xo+16, yo+24)) {
			GuiMapArtCreator.color2block.remove(col);
		}
		
		xo += 18;
		
		for(Block b : Client.color2block.get(col)) {
			if(GUIUtils.isInsideRect(mx, my, xo, yo+2, xo+16, yo+18)) GuiMapArtCreator.color2block.put(col, b);
			xo += 18;
			if(xo > maxX) {
				xo = minX+24+18;
				yo += 24;
			}
		}
		
	}

	@Override
	protected boolean isSelected(int var1) {
		return false;
	}

	@Override
	protected void drawBackground() {
		
	}

	public void drawSelection(int minx, int miny, int maxx, int maxy) {
		Tessellator tess = Tessellator.instance;
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		tess.startDrawing(GL11.GL_LINE_LOOP);
		tess.setColorOpaque_I(8421504);
		tess.addVertex(minx-1, miny-1, 0);
		tess.addVertex(minx-1, maxy+1, 0);
		tess.addVertex(maxx+1, maxy+1, 0);
		tess.addVertex(maxx+1, miny-1, 0);
		tess.draw();
	}
	
	@Override
	protected void drawSlot(int id, int x, int y, int h, Tessellator tess) {
		int col = Client.possibleColors[id];

		GL11.glDisable(GL11.GL_TEXTURE_2D);
		
		tess.startDrawingQuads();
		tess.setColorOpaque_I(col);
		tess.addVertex(x, y, 0);
		tess.addVertex(x, y+h, 0);
		tess.addVertex(x+20, y+h, 0);
		tess.addVertex(x+20, y, 0);
		tess.draw();

		
		int xo = x + 24;
		int yo = y;
		int maxX = this.width / 2 + 110;
		
		tess.startDrawing(GL11.GL_LINES);
		GL11.glLineWidth(2f);
		tess.setColorOpaque_I(0xff0000);
		tess.addVertex(xo+2, yo+4, 0);
		tess.addVertex(xo+14, yo+16, 0);
		
		tess.addVertex(xo+14, yo+4, 0);
		tess.addVertex(xo+2, yo+16, 0);
		tess.draw();
		
		
		if(!GuiMapArtCreator.color2block.containsKey(col)) drawSelection(xo, yo+2, xo+16, yo+18);
		
		xo += 18;
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		for(Block b : Client.color2block.get(col)) {
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GuiIngame.itemRenderer.renderItemIntoGUI(Client.mc.fontRenderer, Client.mc.renderEngine, new ItemStack(b.blockID, 1, 0), xo, yo+2);
			if(GuiMapArtCreator.color2block.containsValue(b)) {
				GL11.glDisable(GL11.GL_TEXTURE_2D);
				GL11.glDisable(GL11.GL_LIGHTING);
				drawSelection(xo, yo+2, xo+16, yo+18);
				GL11.glEnable(GL11.GL_TEXTURE_2D);
			}
			xo += 18;
			if(xo > maxX) {
				xo = x+24+18;
				yo += 24;
			}
			
			
		}
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		
	}
}
