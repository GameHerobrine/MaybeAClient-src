package net.minecraft.src;

import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.skidcode.gh.maybeaclient.Client;

public class TextureWatchFX extends TextureFX {
    private Minecraft mc;
    private int[] watchIconImageData;
    private int[] dialImageData;
    private double field_4222_j;
    private double field_4221_k;

    public TextureWatchFX(Minecraft var1) {
        super(Item.pocketSundial.getIconFromDamage(0));
        this.mc = var1;
        this.tileImage = 1;

        try {
        	BufferedImage var2 = Client.getResource("/gui/items.png"); //XXX ImageIO.read(Client.mc.texturePackList.selectedTexturePack.func_6481_a("/gui/items.png"));
	        int tsize = this.textureRes = var2.getWidth() / 16;
	        int xoff = this.iconIndex % 16 * tsize;
	        int yoff = this.iconIndex / 16 * tsize;
	        this.watchIconImageData = new int[tsize*tsize];
	        this.imageData = new byte[tsize*tsize*4];
	        var2.getRGB(xoff, yoff, tsize, tsize, this.watchIconImageData, 0, tsize);
	        var2 = ImageIO.read(Client.mc.texturePackList.selectedTexturePack.getResourceAsStream("/misc/dial.png"));
	        var2 = Client.rescale(var2, this.textureRes);
	        this.dialImageData = new int[tsize*tsize];
	        var2.getRGB(0, 0, tsize, tsize, this.dialImageData, 0, tsize);
        } catch (IOException var5) {
            var5.printStackTrace();
        }

    }

    public void onTick() {
        double var1 = 0.0D;
        if (this.mc.theWorld != null && this.mc.thePlayer != null) {
            float var3 = this.mc.theWorld.getCelestialAngle(1.0F);
            var1 = (double)(-var3 * 3.1415927F * 2.0F);
            if (this.mc.theWorld.worldProvider.isNether) {
                var1 = Math.random() * 3.1415927410125732D * 2.0D;
            }
        }

        double var22;
        for(var22 = var1 - this.field_4222_j; var22 < -3.141592653589793D; var22 += 6.283185307179586D) {
        }

        while(var22 >= 3.141592653589793D) {
            var22 -= 6.283185307179586D;
        }

        if (var22 < -1.0D) {
            var22 = -1.0D;
        }

        if (var22 > 1.0D) {
            var22 = 1.0D;
        }

        this.field_4221_k += var22 * 0.1D;
        this.field_4221_k *= 0.8D;
        this.field_4222_j += this.field_4221_k;
        double var5 = Math.sin(this.field_4222_j);
        double var7 = Math.cos(this.field_4222_j);
        double tresminus1 = (double)this.textureRes - 1d;
        int tresmin1 = this.textureRes - 1;
        double tres = (double) this.textureRes;
        for(int var9 = 0; var9 < 256; ++var9) {
            int var10 = this.watchIconImageData[var9] >> 24 & 255;
            int var11 = this.watchIconImageData[var9] >> 16 & 255;
            int var12 = this.watchIconImageData[var9] >> 8 & 255;
            int var13 = this.watchIconImageData[var9] >> 0 & 255;
            if (var11 == var13 && var12 == 0 && var13 > 0) {
                double var14 = -((double)(var9 % this.textureRes) / tresminus1 - 0.5D);
                double var16 = (double)(var9 / this.textureRes) / tresminus1 - 0.5D;
                int var18 = var11;
                int var19 = (int)((var14 * var7 + var16 * var5 + 0.5D) * tres);
                int var20 = (int)((var16 * var7 - var14 * var5 + 0.5D) * tres);
                int var21 = (var19 & tresmin1) + (var20 & tresmin1) * this.textureRes;
                var10 = this.dialImageData[var21] >> 24 & 255;
                var11 = (this.dialImageData[var21] >> 16 & 255) * var11 / 255;
                var12 = (this.dialImageData[var21] >> 8 & 255) * var18 / 255;
                var13 = (this.dialImageData[var21] >> 0 & 255) * var18 / 255;
            }

            if (this.anaglyphEnabled) {
                int var23 = (var11 * 30 + var12 * 59 + var13 * 11) / 100;
                int var15 = (var11 * 30 + var12 * 70) / 100;
                int var24 = (var11 * 30 + var13 * 70) / 100;
                var11 = var23;
                var12 = var15;
                var13 = var24;
            }

            this.imageData[var9 * 4 + 0] = (byte)var11;
            this.imageData[var9 * 4 + 1] = (byte)var12;
            this.imageData[var9 * 4 + 2] = (byte)var13;
            this.imageData[var9 * 4 + 3] = (byte)var10;
        }

    }
}
