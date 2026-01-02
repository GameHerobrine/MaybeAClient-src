package net.minecraft.src;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.skidcode.gh.maybeaclient.hacks.AutoMouseClickHack;
import net.skidcode.gh.maybeaclient.hacks.ChestContentHack;
import net.skidcode.gh.maybeaclient.hacks.InventoryWalkHack;
import net.skidcode.gh.maybeaclient.hacks.TooltipsHack;

public abstract class GuiContainer extends GuiScreen {
    private static RenderItem itemRenderer = new RenderItem();
    protected int xSize = 176;
    protected int ySize = 166;
    public Container inventorySlots;

    public GuiContainer(Container var1) {
        this.inventorySlots = var1;
    }

    public void initGui() {
        super.initGui();
        ChestContentHack.instance.locked=1;
        this.mc.thePlayer.craftingInventory = this.inventorySlots;
        this.controlList.add(new GuiButton(1337, 0, this.height - 20 - 1, 120, 20, "Disable AutoMouseClick") {
        	
        	@Override
        	public void drawButton(Minecraft var1, int var2, int var3) {
        		if(!AutoMouseClickHack.instance.status || !AutoMouseClickHack.instance.showDisableButton.value) return;
        		super.drawButton(var1, var2, var3);
        	}
        	
        	@Override
        	public boolean mousePressed(Minecraft mc, int x, int y) {
        		if(!AutoMouseClickHack.instance.status || !AutoMouseClickHack.instance.showDisableButton.value) return false;
        		return super.mousePressed(mc, x, y);
        	}
        });
    }
    
    //XXX check if update
    @Override
    public void actionPerformed(GuiButton gb) {
    	if(gb.id == 1337) {
    		if(AutoMouseClickHack.instance.status) AutoMouseClickHack.instance.toggle();
    	}else {
    		super.actionPerformed(gb);
    	}
    }
    
    public void drawScreen(int var1, int var2, float var3) {
        this.drawDefaultBackground();
        int var4 = (this.width - this.xSize) / 2;
        int var5 = (this.height - this.ySize) / 2;
        this.drawGuiContainerBackgroundLayer(var3);
        GL11.glPushMatrix();
        GL11.glRotatef(120.0F, 1.0F, 0.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslatef((float)var4, (float)var5, 0.0F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(32826 /*GL_RESCALE_NORMAL_EXT*/);
        Slot var6 = null;

        int txtX;
        int txtY;
        for(int var7 = 0; var7 < this.inventorySlots.slots.size(); ++var7) {
            Slot var8 = (Slot)this.inventorySlots.slots.get(var7);
            this.drawSlotInventory(var8);
            if (this.getIsMouseOverSlot(var8, var1, var2)) {
                var6 = var8;
                GL11.glDisable(2896 /*GL_LIGHTING*/);
                GL11.glDisable(2929 /*GL_DEPTH_TEST*/);
                txtX = var8.xDisplayPosition;
                txtY = var8.yDisplayPosition;
                this.drawGradientRect(txtX, txtY, txtX + 16, txtY + 16, -2130706433, -2130706433);
                GL11.glEnable(2896 /*GL_LIGHTING*/);
                GL11.glEnable(2929 /*GL_DEPTH_TEST*/);
            }
        }

        InventoryPlayer var12 = this.mc.thePlayer.inventory;
        if (var12.getItemStack() != null) {
            GL11.glTranslatef(0.0F, 0.0F, 32.0F);
            itemRenderer.renderItemIntoGUI(this.fontRenderer, this.mc.renderEngine, var12.getItemStack(), var1 - var4 - 8, var2 - var5 - 8);
            itemRenderer.renderItemOverlayIntoGUI(this.fontRenderer, this.mc.renderEngine, var12.getItemStack(), var1 - var4 - 8, var2 - var5 - 8);
        }

        GL11.glDisable(32826 /*GL_RESCALE_NORMAL_EXT*/);
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(2896 /*GL_LIGHTING*/);
        GL11.glDisable(2929 /*GL_DEPTH_TEST*/);
        this.drawGuiContainerForegroundLayer();
        if (var12.getItemStack() == null && var6 != null && var6.getHasStack()) {
            String var13 = ("" + StringTranslate.getInstance().translateNamedKey(var6.getStack().getItemName())).trim();
            ArrayList<String[]> toolTip = var6.getStack().getTooltip();
            if (var13.length() > 0) {
                txtX = var1 - var4 + 12;
                txtY = var2 - var5 - 12;
                int height = 8;
                if(TooltipsHack.instance.status) {
                	height += (toolTip.size() > 0 ? toolTip.size()*8+8 : 0);
                	for(int i = 0; i < toolTip.size(); ++i) {
                		String[] s = toolTip.get(i);
                		String tt = s[0]+": "+s[1];
                		int w = this.fontRenderer.getStringWidth(tt);
                		if(w > width) width = w;
                	}
                	
                	if(TooltipsHack.instance.showIdMeta.getValue()) {
                		var13 += " ("+var6.getStack().itemID+":"+var6.getStack().getItemDamage()+")";
                	}
                }
                
                int var11 = this.fontRenderer.getStringWidth(var13);
                this.drawGradientRect(txtX - 3, txtY - 3, txtX + var11 + 3, txtY + height + 3, -1073741824, -1073741824);
                this.fontRenderer.drawStringWithShadow(var13, txtX, txtY, -1);
                if(TooltipsHack.instance.status) {
                	txtY += 8;
                	for(int i = 0; i < toolTip.size(); ++i) {
                		txtY += 8;
                		String[] s = toolTip.get(i);
                		String tt = s[0]+": "+s[1];
                		this.fontRenderer.drawStringWithShadow(tt, txtX, txtY, 0xffffffff);
                	}
                }
            }
        }

        GL11.glPopMatrix();
        super.drawScreen(var1, var2, var3);
        GL11.glEnable(2896 /*GL_LIGHTING*/);
        GL11.glEnable(2929 /*GL_DEPTH_TEST*/);
    }

    protected void drawGuiContainerForegroundLayer() {
    }

    protected abstract void drawGuiContainerBackgroundLayer(float var1);

    private void drawSlotInventory(Slot var1) {
        int var2 = var1.xDisplayPosition;
        int var3 = var1.yDisplayPosition;
        ItemStack var4 = var1.getStack();
        if (var4 == null) {
            int var5 = var1.getBackgroundIconIndex();
            if (var5 >= 0) {
                GL11.glDisable(2896 /*GL_LIGHTING*/);
                this.mc.renderEngine.bindTexture(this.mc.renderEngine.getTexture("/gui/items.png"));
                this.drawTexturedModalRect(var2, var3, var5 % 16 * 16, var5 / 16 * 16, 16, 16);
                GL11.glEnable(2896 /*GL_LIGHTING*/);
                return;
            }
        }

        itemRenderer.renderItemIntoGUI(this.fontRenderer, this.mc.renderEngine, var4, var2, var3);
        itemRenderer.renderItemOverlayIntoGUI(this.fontRenderer, this.mc.renderEngine, var4, var2, var3);
    }

    private Slot getSlotAtPosition(int var1, int var2) {
        for(int var3 = 0; var3 < this.inventorySlots.slots.size(); ++var3) {
            Slot var4 = (Slot)this.inventorySlots.slots.get(var3);
            if (this.getIsMouseOverSlot(var4, var1, var2)) {
                return var4;
            }
        }

        return null;
    }

    private boolean getIsMouseOverSlot(Slot var1, int var2, int var3) {
        int var4 = (this.width - this.xSize) / 2;
        int var5 = (this.height - this.ySize) / 2;
        var2 -= var4;
        var3 -= var5;
        return var2 >= var1.xDisplayPosition - 1 && var2 < var1.xDisplayPosition + 16 + 1 && var3 >= var1.yDisplayPosition - 1 && var3 < var1.yDisplayPosition + 16 + 1;
    }

    protected void mouseClicked(int var1, int var2, int var3) {
        super.mouseClicked(var1, var2, var3);
        if (var3 == 0 || var3 == 1) {
            Slot var4 = this.getSlotAtPosition(var1, var2);
            int var5 = (this.width - this.xSize) / 2;
            int var6 = (this.height - this.ySize) / 2;
            boolean var7 = var1 < var5 || var2 < var6 || var1 >= var5 + this.xSize || var2 >= var6 + this.ySize;
            int var8 = -1;
            if (var4 != null) {
                var8 = var4.slotNumber;
            }

            if (var7) {
                var8 = -999;
            }

            if (var8 != -1) {
                boolean var9 = var8 != -999 && (Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54));
                this.mc.playerController.func_27174_a(this.inventorySlots.windowId, var8, var3, var9, this.mc.thePlayer);
            }
        }

    }
    
    @Override
    public void handleKeyboardInput()
    {
    	super.handleKeyboardInput();
    	
    	if(InventoryWalkHack.instance.status) {
    		mc.thePlayer.handleKeyPress(Keyboard.getEventKey(), Keyboard.getEventKeyState());
    	}
    }
    
    protected void mouseMovedOrUp(int var1, int var2, int var3) {
        if (var3 == 0) {
        }

    }

    protected void keyTyped(char var1, int var2) {
        if (var2 == 1 || var2 == this.mc.gameSettings.keyBindInventory.keyCode) {
            this.mc.thePlayer.closeScreen();
        }

    }

    public void onGuiClosed() {
    	ChestContentHack.instance.locked=0;
    	ChestContentHack.realOpened = false;
        if (this.mc.thePlayer != null) {
            this.mc.playerController.func_20086_a(this.inventorySlots.windowId, this.mc.thePlayer);
        }
    }

    public boolean doesGuiPauseGame() {
        return false;
    }

    public void updateScreen() {
        super.updateScreen();
        if (!this.mc.thePlayer.isEntityAlive() || this.mc.thePlayer.isDead) {
            this.mc.thePlayer.closeScreen();
        }

    }
}
