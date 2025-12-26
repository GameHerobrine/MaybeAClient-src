package net.minecraft.src;

import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.skidcode.gh.maybeaclient.Client;

public class TextureCompassFX extends TextureFX {
    private Minecraft mc;
    private int[] compassIconImageData;
    private double field_4229_i;
    private double field_4228_j;

    public TextureCompassFX(Minecraft var1) {
        super(Item.compass.getIconFromDamage(0));
        this.mc = var1;
        this.tileImage = 1;

        try {
        	BufferedImage var2 = Client.getResource("/gui/items.png"); //XXX ImageIO.read(mc.texturePackList.selectedTexturePack.func_6481_a("/gui/items.png"));
            int tsize = this.textureRes = var2.getWidth() / 16;
            
            int xoff = this.iconIndex % 16 * tsize;
            int yoff = this.iconIndex / 16 * tsize;
            this.compassIconImageData = new int[tsize*tsize];
            this.imageData = new byte[this.compassIconImageData.length*4];
            var2.getRGB(xoff, yoff, tsize, tsize, this.compassIconImageData, 0, 16);
        } catch (IOException var5) {
            var5.printStackTrace();
        }

    }

    public void onTick() {
        for(int var1 = 0; var1 < 256; ++var1) {
            int var2 = this.compassIconImageData[var1] >> 24 & 255;
            int var3 = this.compassIconImageData[var1] >> 16 & 255;
            int var4 = this.compassIconImageData[var1] >> 8 & 255;
            int var5 = this.compassIconImageData[var1] >> 0 & 255;
            if (this.anaglyphEnabled) {
                int var6 = (var3 * 30 + var4 * 59 + var5 * 11) / 100;
                int var7 = (var3 * 30 + var4 * 70) / 100;
                int var8 = (var3 * 30 + var5 * 70) / 100;
                var3 = var6;
                var4 = var7;
                var5 = var8;
            }

            this.imageData[var1 * 4 + 0] = (byte)var3;
            this.imageData[var1 * 4 + 1] = (byte)var4;
            this.imageData[var1 * 4 + 2] = (byte)var5;
            this.imageData[var1 * 4 + 3] = (byte)var2;
        }

        double var20 = 0.0D;
        if (this.mc.theWorld != null && this.mc.thePlayer != null) {
            ChunkCoordinates var21 = this.mc.theWorld.getSpawnPoint();
            double var23 = (double)var21.x - this.mc.thePlayer.posX;
            double var25 = (double)var21.z - this.mc.thePlayer.posZ;
            var20 = (double)(this.mc.thePlayer.rotationYaw - 90.0F) * 3.141592653589793D / 180.0D - Math.atan2(var25, var23);
            if (this.mc.theWorld.worldProvider.isNether) {
                var20 = Math.random() * 3.1415927410125732D * 2.0D;
            }
        }

        double var22;
        for(var22 = var20 - this.field_4229_i; var22 < -3.141592653589793D; var22 += 6.283185307179586D) {
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

        this.field_4228_j += var22 * 0.1D;
        this.field_4228_j *= 0.8D;
        this.field_4229_i += this.field_4228_j;
        double sin = Math.sin(this.field_4229_i);
        double cos = Math.cos(this.field_4229_i);

        int var9;
        int var10;
        int var11;
        int var12;
        int var13;
        int var14;
        int var15;
        short var16;
        int var17;
        int var18;
        int var19;
        //XXX HD Texture Fix start 
        int crossMin = this.textureRes / -4;
        int crossMax = this.textureRes / 4;
        double compassCenterMin = ((double)this.textureRes / 2.0d) - 0.5d;
        double compassCenterMax = ((double)this.textureRes / 2.0d) + 0.5d;
        int compassNeedleMin = this.textureRes / (-2);
        int compassNeedleMax = this.textureRes;
        //XXX HD Texture Fix end
        for(var9 = crossMin; var9 <= crossMax; ++var9) {
            var10 = (int)(compassCenterMax + cos * (double)var9 * 0.3D);
            var11 = (int)(compassCenterMin - sin * (double)var9 * 0.3D * 0.5D);
            var12 = var11 * this.textureRes + var10;
            var13 = 100;
            var14 = 100;
            var15 = 100;
            var16 = 255;
            if (this.anaglyphEnabled) {
                var17 = (var13 * 30 + var14 * 59 + var15 * 11) / 100;
                var18 = (var13 * 30 + var14 * 70) / 100;
                var19 = (var13 * 30 + var15 * 70) / 100;
                var13 = var17;
                var14 = var18;
                var15 = var19;
            }

            this.imageData[var12 * 4 + 0] = (byte)var13;
            this.imageData[var12 * 4 + 1] = (byte)var14;
            this.imageData[var12 * 4 + 2] = (byte)var15;
            this.imageData[var12 * 4 + 3] = (byte)var16;
        }

        for(var9 = compassNeedleMin; var9 <= compassNeedleMax; ++var9) {
            var10 = (int)(compassCenterMax + sin * (double)var9 * 0.3D);
            var11 = (int)(compassCenterMin + cos * (double)var9 * 0.3D * 0.5D);
            var12 = var11 * this.textureRes + var10;
            var13 = var9 >= 0 ? 255 : 100;
            var14 = var9 >= 0 ? 20 : 100;
            var15 = var9 >= 0 ? 20 : 100;
            var16 = 255;
            if (this.anaglyphEnabled) {
                var17 = (var13 * 30 + var14 * 59 + var15 * 11) / 100;
                var18 = (var13 * 30 + var14 * 70) / 100;
                var19 = (var13 * 30 + var15 * 70) / 100;
                var13 = var17;
                var14 = var18;
                var15 = var19;
            }

            this.imageData[var12 * 4 + 0] = (byte)var13;
            this.imageData[var12 * 4 + 1] = (byte)var14;
            this.imageData[var12 * 4 + 2] = (byte)var15;
            this.imageData[var12 * 4 + 3] = (byte)var16;
        }

    }
}
