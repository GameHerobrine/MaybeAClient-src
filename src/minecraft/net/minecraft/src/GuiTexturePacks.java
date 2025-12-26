package net.minecraft.src;

import java.io.File;
import net.minecraft.client.Minecraft;
import org.lwjgl.Sys;

public class GuiTexturePacks extends GuiScreen {
    protected GuiScreen guiScreen;
    private int field_6454_o = -1;
    private String fileLocation = "";
    private GuiTexturePackSlot guiTexturePackSlot;
    public GuiTexturePacks(GuiScreen var1) {
    	this(var1, true);
    }
    //XXX Modifed
    public boolean fillBg = true;
    public GuiTexturePacks(GuiScreen var1, boolean fillBg) {
        this.guiScreen = var1;
        this.fillBg = fillBg;
    }

    public void initGui() {
        StringTranslate var1 = StringTranslate.getInstance();
        this.controlList.add(new GuiSmallButton(5, this.width / 2 - 154, this.height - 48, var1.translateKey("texturePack.openFolder")));
        this.controlList.add(new GuiSmallButton(6, this.width / 2 + 4, this.height - 48, var1.translateKey("gui.done")));
        this.mc.texturePackList.updateAvaliableTexturePacks();
        this.fileLocation = (new File(Minecraft.getMinecraftDir(), "texturepacks")).getAbsolutePath();
        this.guiTexturePackSlot = new GuiTexturePackSlot(this);
        this.guiTexturePackSlot.registerScrollButtons(this.controlList, 7, 8);
    }

    protected void actionPerformed(GuiButton var1) {
        if (var1.enabled) {
            if (var1.id == 5) {
                Sys.openURL("file://" + this.fileLocation);
            } else if (var1.id == 6) {
                //this.mc.renderEngine.refreshTextures();
                this.mc.displayGuiScreen(this.guiScreen);
            } else {
                this.guiTexturePackSlot.actionPerformed(var1);
            }

        }
    }

    protected void mouseClicked(int var1, int var2, int var3) {
        super.mouseClicked(var1, var2, var3);
    }

    protected void mouseMovedOrUp(int var1, int var2, int var3) {
        super.mouseMovedOrUp(var1, var2, var3);
    }

    public void drawScreen(int var1, int var2, float var3) {
        this.guiTexturePackSlot.drawScreen(var1, var2, var3);
        if (this.field_6454_o <= 0) {
            this.mc.texturePackList.updateAvaliableTexturePacks();
            this.field_6454_o += 20;
        }

        StringTranslate var4 = StringTranslate.getInstance();
        this.drawCenteredString(this.fontRenderer, var4.translateKey("texturePack.title"), this.width / 2, 16, 16777215);
        this.drawCenteredString(this.fontRenderer, var4.translateKey("texturePack.folderInfo"), this.width / 2 - 77, this.height - 26, 8421504);
        super.drawScreen(var1, var2, var3);
    }

    public void updateScreen() {
        super.updateScreen();
        --this.field_6454_o;
    }
}
