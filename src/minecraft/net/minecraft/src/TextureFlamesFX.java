package net.minecraft.src;

import java.io.IOException;

import net.skidcode.gh.maybeaclient.Client;

public class TextureFlamesFX extends TextureFX {
    protected float[] field_1133_g;
    protected float[] field_1132_h;

    public TextureFlamesFX(int var1) {
        super(Block.fire.blockIndexInTexture + var1 * 16);
        try {
			this.textureRes = Client.getResource("/terrain.png").getWidth() / 16;
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        int fireHeight = this.textureRes + 4;
        this.field_1133_g = new float[this.textureRes*fireHeight];
        this.field_1132_h = new float[this.textureRes*fireHeight];
        this.imageData = new byte[this.textureRes*this.textureRes*4];
    }

    public void onTick() {
        int var2;
        float var4;
        int var5;
        int var6;
        float float_flameNudge;
        
        if (this.textureRes < 64) {
            float_flameNudge = 1.0f + (0.96f / (float)this.textureRes);
        } else {
            float_flameNudge = 1.0f + (1.28f / (float)this.textureRes);
        }
        
        int fireHeight = this.textureRes + 4;
        for(int var1 = 0; var1 < this.textureRes; ++var1) {
            for(var2 = 0; var2 < fireHeight; ++var2) {
                int var3 = 18;
                var4 = this.field_1133_g[var1 + (var2 + 1) % fireHeight * this.textureRes] * (float)var3;

                for(var5 = var1 - 1; var5 <= var1 + 1; ++var5) {
                    for(var6 = var2; var6 <= var2 + 1; ++var6) {
                        if (var5 >= 0 && var6 >= 0 && var5 < this.textureRes && var6 < fireHeight) {
                            var4 += this.field_1133_g[var5 + var6 * this.textureRes];
                        }

                        ++var3;
                    }
                }

                this.field_1132_h[var1 + var2 * this.textureRes] = var4 / ((float)var3 * float_flameNudge);
                if (var2 >= fireHeight-1) {
                    this.field_1132_h[var1 + var2 * this.textureRes] = (float)(Math.random() * Math.random() * Math.random() * 4.0D + Math.random() * 0.10000000149011612D + 0.20000000298023224D);
                }
            }
        }

        float[] var12 = this.field_1132_h;
        this.field_1132_h = this.field_1133_g;
        this.field_1133_g = var12;

        for(var2 = 0; var2 < this.textureRes*this.textureRes; ++var2) {
            float var13 = this.field_1133_g[var2] * 1.8F;
            if (var13 > 1.0F) {
                var13 = 1.0F;
            }

            if (var13 < 0.0F) {
                var13 = 0.0F;
            }

            var5 = (int)(var13 * 155.0F + 100.0F);
            var6 = (int)(var13 * var13 * 255.0F);
            int var7 = (int)(var13 * var13 * var13 * var13 * var13 * var13 * var13 * var13 * var13 * var13 * 255.0F);
            short var8 = 255;
            if (var13 < 0.5F) {
                var8 = 0;
            }

            var4 = (var13 - 0.5F) * 2.0F;
            if (this.anaglyphEnabled) {
                int var9 = (var5 * 30 + var6 * 59 + var7 * 11) / 100;
                int var10 = (var5 * 30 + var6 * 70) / 100;
                int var11 = (var5 * 30 + var7 * 70) / 100;
                var5 = var9;
                var6 = var10;
                var7 = var11;
            }

            this.imageData[var2 * 4 + 0] = (byte)var5;
            this.imageData[var2 * 4 + 1] = (byte)var6;
            this.imageData[var2 * 4 + 2] = (byte)var7;
            this.imageData[var2 * 4 + 3] = (byte)var8;
        }

    }
}
