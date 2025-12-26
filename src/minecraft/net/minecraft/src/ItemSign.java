package net.minecraft.src;

import net.skidcode.gh.maybeaclient.Client;
import net.skidcode.gh.maybeaclient.hacks.AutoSignHack;

public class ItemSign extends Item {
    public ItemSign(int var1) {
        super(var1);
        this.maxStackSize = 1;
    }

    public boolean onItemUse(ItemStack var1, EntityPlayer var2, World var3, int var4, int var5, int var6, int var7) {
        if (var7 == 0) {
            return false;
        } else if (!var3.getBlockMaterial(var4, var5, var6).isSolid()) {
            return false;
        } else {
            if (var7 == 1) {
                ++var5;
            }

            if (var7 == 2) {
                --var6;
            }

            if (var7 == 3) {
                ++var6;
            }

            if (var7 == 4) {
                --var4;
            }

            if (var7 == 5) {
                ++var4;
            }

            if (!Block.signPost.canPlaceBlockAt(var3, var4, var5, var6)) {
                return false;
            } else {
                if (var7 == 1) {
                    var3.setBlockAndMetadataWithNotify(var4, var5, var6, Block.signPost.blockID, MathHelper.floor_double((double)((var2.rotationYaw + 180.0F) * 16.0F / 360.0F) + 0.5D) & 15);
                } else {
                    var3.setBlockAndMetadataWithNotify(var4, var5, var6, Block.signWall.blockID, var7);
                }

                --var1.stackSize;
                TileEntitySign var8 = (TileEntitySign)var3.getBlockTileEntity(var4, var5, var6);
                if (var8 != null) {
                	
                	if(AutoSignHack.instance.status) {
                		var8.signText[0] = AutoSignHack.instance.text[0];
                		var8.signText[1] = AutoSignHack.instance.text[1];
                		var8.signText[2] = AutoSignHack.instance.text[2];
                		var8.signText[3] = AutoSignHack.instance.text[3];
                		
                		if (Client.mc.theWorld.multiplayerWorld) {
                			Client.mc.getSendQueue().addToSendQueue(new Packet130UpdateSign(var8.xCoord, var8.yCoord, var8.zCoord, var8.signText));
                        }
                	}else {
                		var2.displayGUIEditSign(var8);
                	}
                }

                return true;
            }
        }
    }
}
