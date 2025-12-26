package net.skidcode.gh.maybeaclient.gui;
// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

import net.minecraft.src.ContainerChest;
import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiContainer;
import net.minecraft.src.IInventory;
import org.lwjgl.opengl.GL11;

public class GuiItemGive extends GuiContainer
{
	public static int page = 0;
    public GuiItemGive(IInventory iinventory, IInventory iinventory1)
    {
        super(new ContainerChest(iinventory,iinventory1));
    	inventoryRows = 0;
        upperChestInventory = iinventory;
        lowerChestInventory = iinventory1;
        field_948_f = false;
        char c = '\336';
        int i = c - 108;
        inventoryRows = iinventory1.getSizeInventory() / 9;
        ySize = i + inventoryRows * 18;
        int j = (inventoryRows - 4) * 18;

    }
    protected void drawGuiContainerForegroundLayer()
    {
    	fontRenderer.drawString("The Great Dupe Gui.", 8, 6, 0x404040);
        fontRenderer.drawString("Your Inventory", 8, (ySize - 96) + 2, 0x404040);
    }
    public void actionPerformed(GuiButton guibutton)
    {
    	if(guibutton.id == 1) {
    		page+=1;
    	}
    	if(guibutton.id == 2) {
    		page -=1;
    		if(page < 0) {
    			page = 0;
    		}
    	}
    }
    protected void drawGuiContainerBackgroundLayer(float f)
    {
        controlList.clear();
    	int i = mc.renderEngine.getTexture("/gui/container.png");
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(i);
        int j = (width - xSize) / 2;
        int k = (height - ySize) / 2;
        drawTexturedModalRect(j, k, 0, 0, xSize, inventoryRows * 18 + 17);
        drawTexturedModalRect(j, k + inventoryRows * 18 + 17, 0, 126, xSize, 96);
    	controlList.add(new GuiButton(1,j+150,k+3,15,12,">"));
    	controlList.add(new GuiButton(2,j+135,k+3,15,12,"<"));
    }

    private IInventory upperChestInventory;
    private IInventory lowerChestInventory;
    private int inventoryRows;
}
