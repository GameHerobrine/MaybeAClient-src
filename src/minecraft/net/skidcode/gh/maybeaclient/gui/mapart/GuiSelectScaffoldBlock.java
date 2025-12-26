package net.skidcode.gh.maybeaclient.gui.mapart;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.src.Block;
import net.minecraft.src.FontRenderer;
import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiIngame;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.Item;
import net.minecraft.src.Tessellator;
import net.skidcode.gh.maybeaclient.Client;
import net.skidcode.gh.maybeaclient.utils.ChatColor;

public class GuiSelectScaffoldBlock extends GuiScreen{
	public GuiScreen prev;
	public GuiSelectScaffoldBlock(GuiScreen s) {
		this.prev = s;
	}
	
	protected void keyTyped(char var1, int var2) {
        if (var2 == 1) {
            this.mc.displayGuiScreen(this.prev);
        }else {
        	super.keyTyped(var1, var2);
        }
    }
	
	public GuiButton cancel;

	@Override
	public void initGui() {
		this.controlList.clear();
		int midX = this.width / 2;
		int midY = this.height / 2;
		
		int x = midX - 140;
		int y = midY - 80;
		
		for(int i = 0; i < Block.blocksList.length; ++i) {
			if(Block.blocksList[i] == null) continue;
			final int j = i;
			this.controlList.add(new GuiButton(i, x, y, 20, 20, ""+i) {
				@Override
			    public void drawButton(Minecraft var1, int var2, int var3) {
			        if (this.enabled2) {
			            FontRenderer var4 = var1.fontRenderer;
			            GL11.glBindTexture(3553 /*GL_TEXTURE_2D*/, var1.renderEngine.getTexture("/gui/gui.png"));
			            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			            if(j == GuiMapArtCreator.scaffoldBlockId) {
			            	GL11.glColor4f(0.5F, 1.0F, 0.5F, 1.0F);
			            }
			            boolean var5 = var2 >= this.xPosition && var3 >= this.yPosition && var2 < this.xPosition + this.width && var3 < this.yPosition + this.height;
			            int var6 = this.getHoverState(var5);
			            this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 46 + var6 * 20, this.width / 2, this.height);
			            this.drawTexturedModalRect(this.xPosition + this.width / 2, this.yPosition, 200 - this.width / 2, 46 + var6 * 20, this.width / 2, this.height);
			            this.mouseDragged(var1, var2, var3);
			            
			            
			            GuiIngame.itemRenderer.drawItemIntoGui(Client.mc.fontRenderer, Client.mc.renderEngine, j, 0, Item.itemsList[j].getIconFromDamage(0), this.xPosition+2, this.yPosition + 2);
			            GL11.glDisable(GL11.GL_LIGHTING);
			        }
			    }
			});
			
			if(x > midX+100) {
				x = midX-140;
				y += 20;
			}else {
				x += 20;
			}
		}
		
		this.controlList.add(this.cancel = new GuiButton(-1, midX-80/2, this.height-20-2, 80, 20, "Done"));
	}
	
	@Override
	public void actionPerformed(GuiButton gb) {
		if(gb.id >= 0 && gb.id < Block.blocksList.length) {
			GuiMapArtCreator.scaffoldBlockId = gb.id;
		}
		if(gb == this.cancel) {
			this.mc.displayGuiScreen(this.prev);
		}
    }
	
	@Override
	public void drawScreen(int var1, int var2, float var3) {
		this.drawDefaultBackground();
		int midX = this.width / 2;
		int midY = this.height / 2;
		
		String s = "Select Scaffold Block";
		this.fontRenderer.drawString(s, midX - this.fontRenderer.getStringWidth(s) / 2, 12, 0xffffff);

		super.drawScreen(var1, var2, var3);
	}
}
