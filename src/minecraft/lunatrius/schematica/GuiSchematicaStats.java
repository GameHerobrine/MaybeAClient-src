package lunatrius.schematica;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiIngame;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.GuiSlot;
import net.minecraft.src.ItemStack;
import net.minecraft.src.RenderHelper;
import net.minecraft.src.Tessellator;
import net.skidcode.gh.maybeaclient.Client;

public class GuiSchematicaStats extends GuiScreen{
	
	public static class GuiBlockRequired extends GuiSlot{
		public GuiSchematicaStats parent;
		public GuiBlockRequired(GuiSchematicaStats gui) {
			super(Client.mc, gui.width, gui.height, 20, gui.height - 32 + 4, 20);
			this.parent = gui;
		}
		
		ArrayList<BlockStat> generated;
		@Override
		protected int getSize() {
			if(!Settings.instance().stats_initialized) {
				Settings.instance().recalculateStats();
				
			}
			generated = new ArrayList<BlockStat>();
			for(int i = 0; i < Settings.instance().stats_blocksToPlace.length; ++i) {
				BlockStat st = Settings.instance().stats_blocksToPlace[i];
				if(st.isEmpty()) continue;
				generated.add(st);
			}
			return generated.size() + 1;
		}

		@Override
		protected void elementClicked(int var1, boolean var2) {}

		@Override
		protected boolean isSelected(int var1) {
			return false;
		}

		@Override
		protected int getContentHeight() {
			if(this.getSize() == 0) return 1;
			return this.getSize() * 20;
		}

		@Override
		protected void drawBackground() {
			this.parent.drawDefaultBackground();
		}

		@Override
		protected void drawSlot(int index, int x, int y, int par4, Tessellator tessellator) {
			if(index == 0) {
				int min = this.width / 2 - 92 - 16;
				int max = this.width / 2 + 92 + 16;
				int add = (max - min)/4;
				y += 6;
				mc.fontRenderer.drawString("Block", x, y, 0xffffff);
				mc.fontRenderer.drawString("Place", x + add, y, 0xffffff);
				mc.fontRenderer.drawString("Break", x + add*2, y, 0xffffff);
				mc.fontRenderer.drawString("Done", x + add*3, y, 0xffffff);
				return;
			}
			BlockStat i = this.generated.get(index-1);
			//GL11.glPushMatrix();
	        //GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
	        //RenderHelper.enableStandardItemLighting();
	        //GL11.glPopMatrix();
			GL11.glDisable(GL11.GL_LIGHTING);
	        GL11.glEnable(GL11.GL_DEPTH_TEST);
			GuiIngame.itemRenderer.renderItemIntoGUI(mc.fontRenderer, mc.renderEngine, new ItemStack(i.id, 1, i.meta), x, y);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glDisable(GL11.GL_LIGHTING);
			//RenderHelper.disableStandardItemLighting();
			
			int min = this.width / 2 - 92 - 16;
			int max = this.width / 2 + 92 + 16;
			int add = (max - min)/4;
			y += 6;
			mc.fontRenderer.drawString(""+i.toPlace, x + add, y, 0xffffff);
			mc.fontRenderer.drawString(""+i.toRemove, x + add*2, y, 0xffffff);
			mc.fontRenderer.drawString(""+i.donePlace, x + add*3, y, 0xffffff);
		}
	}
	
	@Override
	protected void keyTyped(char var1, int var2) {
        if (var2 == Keyboard.KEY_ESCAPE) {
            this.mc.displayGuiScreen(this.parent);
        }
    }
	
	public GuiScreen parent;
	public GuiBlockRequired slot;
	public GuiSchematicaStats(GuiScreen parent) {
		this.parent = parent;
	}
	public void initGui() {
		Settings.instance().emptyStats();
		Settings.instance().recalculateStats();
		this.slot = new GuiBlockRequired(this);
		this.controlList.add(new GuiButton(1, this.width / 2 - 200 / 2, this.height - 25, "Back"));
	}
	
	public void actionPerformed(GuiButton b) {
		if(b.id == 1) {
			this.mc.displayGuiScreen(this.parent);
		}
    }
	
	@Override
	public void drawScreen(int x, int y, float partialTicks) {
		this.slot.drawScreen(x, y, partialTicks);
		
		
		this.drawCenteredString(this.fontRenderer, "Schematica Stats ", this.width / 2, 7, 0x00FFFFFF);
		
		
		
		super.drawScreen(x, y, partialTicks);
	}
}
