package net.minecraft.src;

public class ItemBlock extends Item {
    public int blockID;

    public ItemBlock(int var1) {
        super(var1);
        this.blockID = var1 + 256;
        this.setIconIndex(Block.blocksList[var1 + 256].getBlockTextureFromSide(2));
    }
    
    public boolean onItemUse(ItemStack var1, EntityPlayer var2, World var3, int x, int y, int z, int var7) {
        if (var3.getBlockId(x, y, z) == Block.snow.blockID) {
            var7 = 0;
        } else {
            if (var7 == 0) {
                --y;
            }

            if (var7 == 1) {
                ++y;
            }

            if (var7 == 2) {
                --z;
            }

            if (var7 == 3) {
                ++z;
            }

            if (var7 == 4) {
                --x;
            }

            if (var7 == 5) {
                ++x;
            }
        }

        if (var1.stackSize == 0) {
            return false;
        } else if (var3.canBlockBePlacedAt(this.blockID, x, y, z, false)) {
            Block var8 = Block.blocksList[this.blockID];
            if (var3.setBlockAndMetadataWithNotify(x, y, z, this.blockID, this.func_21012_a(var1.getItemDamage()))) {
                Block.blocksList[this.blockID].onBlockPlaced(var3, x, y, z, var7);
                Block.blocksList[this.blockID].onBlockPlacedBy(var3, x, y, z, var2);
                var3.playSoundEffect((double)((float)x + 0.5F), (double)((float)y + 0.5F), (double)((float)z + 0.5F), var8.stepSound.func_1145_d(), (var8.stepSound.func_1147_b() + 1.0F) / 2.0F, var8.stepSound.func_1144_c() * 0.8F);
                --var1.stackSize;
            }

            return true;
        } else {
            return false;
        }
    }

    public String getItemNameIS(ItemStack var1) {
        return Block.blocksList[this.blockID].getBlockName();
    }

    public String getItemName() {
        return Block.blocksList[this.blockID].getBlockName();
    }
}
