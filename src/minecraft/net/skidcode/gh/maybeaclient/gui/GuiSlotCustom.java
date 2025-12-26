package net.skidcode.gh.maybeaclient.gui;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.GuiSlot;
import net.minecraft.src.Tessellator;

public abstract class GuiSlotCustom extends GuiSlot{

	public GuiSlotCustom(Minecraft var1, int var2, int var3, int var4, int var5, int var6) {
		super(var1, var2, var3, var4, var5, var6);
	}
	
	@Override
    protected int getContentHeight() {
		int yy = this.field_27261_r;
		int minX = this.width / 2 - 110;
        int maxX = this.width / 2 + 110;
        for(int i = 0; i < this.getSize(); ++i) {
    		int ss = this.getSlotSize(i, minX, maxX);
    		yy += ss;
        }
        return yy;
    }
	protected void elementClicked(int var1, boolean var2) {}
	protected abstract void elementClicked(int var1, boolean var2, int mx, int my, int minX, int minY, int maxX, int maxY);
	public void drawScreen(int mx, int my, float var3) {
        this.drawBackground();
        int size = this.getSize();
        int var5 = this.width / 2 + 124;
        int var6 = var5 + 6;
        int var9;
        int var10;
        int var11;
        int var13;
        int var19;
        
        int minX = this.width / 2 - 110;
        int maxX = this.width / 2 + 110;
        
        if (Mouse.isButtonDown(0)) {
            if (this.initialClickY == -1.0F) {
                boolean var7 = true;
                if (my >= this.top && my <= this.bottom) {
                	int startY = this.top+4 - this.field_27261_r - (int)this.amountScrolled;
                	int yy = startY;
                	for(int i = 0; i < size; ++i) {
                		int ss = this.getSlotSize(i, minX, maxX);
                		if (mx >= minX && mx <= maxX && my >= yy && my <= (yy+ss)) {
                			this.elementClicked(i, i == this.selectedElement && System.currentTimeMillis() - this.lastClicked < 250L, mx, my, minX, yy, maxX, yy+ss);
                			this.selectedElement = i;
                            this.lastClicked = System.currentTimeMillis();
                		}else if (mx >= minX && mx <= maxX && my <= startY) {
                			this.func_27255_a(mx - minX, my - this.top + (int)this.amountScrolled - 4);
                            var7 = false;
                		}
                		yy += ss;
                	}

                    if (mx >= var5 && mx <= var6) {
                        this.scrollMultiplier = -1.0F;
                        var19 = this.getContentHeight() - (this.bottom - this.top - 4);
                        if (var19 < 1) {
                            var19 = 1;
                        }

                        var13 = (int)((float)((this.bottom - this.top) * (this.bottom - this.top)) / (float)this.getContentHeight());
                        if (var13 < 32) {
                            var13 = 32;
                        }

                        if (var13 > this.bottom - this.top - 8) {
                            var13 = this.bottom - this.top - 8;
                        }

                        this.scrollMultiplier /= (float)(this.bottom - this.top - var13) / (float)var19;
                    } else {
                        this.scrollMultiplier = 1.0F;
                    }

                    if (var7) {
                        this.initialClickY = (float)my;
                    } else {
                        this.initialClickY = -2.0F;
                    }
                } else {
                    this.initialClickY = -2.0F;
                }
            } else if (this.initialClickY >= 0.0F) {
                this.amountScrolled -= ((float)my - this.initialClickY) * this.scrollMultiplier;
                this.initialClickY = (float)my;
            }
        } else {
            this.initialClickY = -1.0F;
        }

        this.bindAmountScrolled();
        GL11.glDisable(2896 /*GL_LIGHTING*/);
        GL11.glDisable(2912 /*GL_FOG*/);
        Tessellator var16 = Tessellator.instance;
        GuiScreen.drawGradientRect(this.left, this.top, this.right, this.bottom, -1072689136, -804253680);
        
        var9 = this.width / 2 - 92 - 16;
        var10 = this.top + 4 - (int)this.amountScrolled;
        if (this.field_27262_q) {
            this.func_27260_a(var9, var10, var16);
        }


        int yoff = 0;
        for(var11 = 0; var11 < size; ++var11) {
            var19 = var10 + yoff + this.field_27261_r;
            int ss = this.getSlotSize(var11, minX, maxX);
            var13 = ss-4;
            if (var19 <= this.bottom && var19 + var13 >= this.top) {
                if (this.field_25123_p && this.isSelected(var11)) {
                    
                    
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                    GL11.glDisable(3553 /*GL_TEXTURE_2D*/);
                    var16.startDrawingQuads();
                    var16.setColorOpaque_I(8421504);
                    var16.addVertexWithUV((double)minX, (double)(var19 + var13 + 2), 0.0D, 0.0D, 1.0D);
                    var16.addVertexWithUV((double)maxX, (double)(var19 + var13 + 2), 0.0D, 1.0D, 1.0D);
                    var16.addVertexWithUV((double)maxX, (double)(var19 - 2), 0.0D, 1.0D, 0.0D);
                    var16.addVertexWithUV((double)minX, (double)(var19 - 2), 0.0D, 0.0D, 0.0D);
                    var16.setColorOpaque_I(0);
                    var16.addVertexWithUV((double)(minX + 1), (double)(var19 + var13 + 1), 0.0D, 0.0D, 1.0D);
                    var16.addVertexWithUV((double)(maxX - 1), (double)(var19 + var13 + 1), 0.0D, 1.0D, 1.0D);
                    var16.addVertexWithUV((double)(maxX - 1), (double)(var19 - 1), 0.0D, 1.0D, 0.0D);
                    var16.addVertexWithUV((double)(minX + 1), (double)(var19 - 1), 0.0D, 0.0D, 0.0D);
                    var16.draw();
                    GL11.glEnable(3553 /*GL_TEXTURE_2D*/);
                }

                this.drawSlot(var11, var9, var19, var13, var16);
            }
            yoff += ss;
        }

        GL11.glDisable(2929 /*GL_DEPTH_TEST*/);
        byte var18 = 4;
        this.overlayBackground(0, this.top, 255, 255);
        this.overlayBackground(this.bottom, this.height, 255, 255);
        GL11.glEnable(3042 /*GL_BLEND*/);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3008 /*GL_ALPHA_TEST*/);
        GL11.glShadeModel(7425 /*GL_SMOOTH*/);
        GL11.glDisable(3553 /*GL_TEXTURE_2D*/);
        var16.startDrawingQuads();
        var16.setColorRGBA_I(0, 0);
        var16.addVertexWithUV((double)this.left, (double)(this.top + var18), 0.0D, 0.0D, 1.0D);
        var16.addVertexWithUV((double)this.right, (double)(this.top + var18), 0.0D, 1.0D, 1.0D);
        var16.setColorRGBA_I(0, 255);
        var16.addVertexWithUV((double)this.right, (double)this.top, 0.0D, 1.0D, 0.0D);
        var16.addVertexWithUV((double)this.left, (double)this.top, 0.0D, 0.0D, 0.0D);
        var16.draw();
        var16.startDrawingQuads();
        var16.setColorRGBA_I(0, 255);
        var16.addVertexWithUV((double)this.left, (double)this.bottom, 0.0D, 0.0D, 1.0D);
        var16.addVertexWithUV((double)this.right, (double)this.bottom, 0.0D, 1.0D, 1.0D);
        var16.setColorRGBA_I(0, 0);
        var16.addVertexWithUV((double)this.right, (double)(this.bottom - var18), 0.0D, 1.0D, 0.0D);
        var16.addVertexWithUV((double)this.left, (double)(this.bottom - var18), 0.0D, 0.0D, 0.0D);
        var16.draw();
        var19 = this.getContentHeight() - (this.bottom - this.top - 4);
        if (var19 > 0) {
            var13 = (this.bottom - this.top) * (this.bottom - this.top) / this.getContentHeight();
            if (var13 < 32) {
                var13 = 32;
            }

            if (var13 > this.bottom - this.top - 8) {
                var13 = this.bottom - this.top - 8;
            }

            int var14 = (int)this.amountScrolled * (this.bottom - this.top - var13) / var19 + this.top;
            if (var14 < this.top) {
                var14 = this.top;
            }

            var16.startDrawingQuads();
            var16.setColorRGBA_I(0, 255);
            var16.addVertexWithUV((double)var5, (double)this.bottom, 0.0D, 0.0D, 1.0D);
            var16.addVertexWithUV((double)var6, (double)this.bottom, 0.0D, 1.0D, 1.0D);
            var16.addVertexWithUV((double)var6, (double)this.top, 0.0D, 1.0D, 0.0D);
            var16.addVertexWithUV((double)var5, (double)this.top, 0.0D, 0.0D, 0.0D);
            var16.draw();
            var16.startDrawingQuads();
            var16.setColorRGBA_I(8421504, 255);
            var16.addVertexWithUV((double)var5, (double)(var14 + var13), 0.0D, 0.0D, 1.0D);
            var16.addVertexWithUV((double)var6, (double)(var14 + var13), 0.0D, 1.0D, 1.0D);
            var16.addVertexWithUV((double)var6, (double)var14, 0.0D, 1.0D, 0.0D);
            var16.addVertexWithUV((double)var5, (double)var14, 0.0D, 0.0D, 0.0D);
            var16.draw();
            var16.startDrawingQuads();
            var16.setColorRGBA_I(12632256, 255);
            var16.addVertexWithUV((double)var5, (double)(var14 + var13 - 1), 0.0D, 0.0D, 1.0D);
            var16.addVertexWithUV((double)(var6 - 1), (double)(var14 + var13 - 1), 0.0D, 1.0D, 1.0D);
            var16.addVertexWithUV((double)(var6 - 1), (double)var14, 0.0D, 1.0D, 0.0D);
            var16.addVertexWithUV((double)var5, (double)var14, 0.0D, 0.0D, 0.0D);
            var16.draw();
        }

        this.func_27257_b(mx, my);
        GL11.glEnable(3553 /*GL_TEXTURE_2D*/);
        GL11.glShadeModel(7424 /*GL_FLAT*/);
        GL11.glEnable(3008 /*GL_ALPHA_TEST*/);
        GL11.glDisable(3042 /*GL_BLEND*/);
    }

	public int getSlotSize(int slot, int minX, int maxX) {
		return this.posZ;
	}
}
