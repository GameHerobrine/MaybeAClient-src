package net.minecraft.src;

import net.skidcode.gh.maybeaclient.hacks.InventoryTweaksHack;

public class ContainerPlayer extends Container {
    public InventoryCrafting craftMatrix;
    public IInventory craftResult;
    public boolean isSinglePlayer;

    public ContainerPlayer(InventoryPlayer var1) {
        this(var1, true);
    }

    public ContainerPlayer(InventoryPlayer var1, boolean var2) {
        this.craftMatrix = new InventoryCrafting(this, 2, 2);
        this.craftResult = new InventoryCraftResult();
        this.isSinglePlayer = false;
        this.isSinglePlayer = var2;
        this.addSlot(new SlotCrafting(var1.player, this.craftMatrix, this.craftResult, 0, 144, 36));

        int var3;
        int var4;
        for(var3 = 0; var3 < 2; ++var3) {
            for(var4 = 0; var4 < 2; ++var4) {
                this.addSlot(new Slot(this.craftMatrix, var4 + var3 * 2, 88 + var4 * 18, 26 + var3 * 18));
            }
        }

        for(var3 = 0; var3 < 4; ++var3) {
            this.addSlot(new SlotArmor(this, var1, var1.getSizeInventory() - 1 - var3, 8, 8 + var3 * 18, var3));
        }

        for(var3 = 0; var3 < 3; ++var3) {
            for(var4 = 0; var4 < 9; ++var4) {
                this.addSlot(new Slot(var1, var4 + (var3 + 1) * 9, 8 + var4 * 18, 84 + var3 * 18));
            }
        }

        for(var3 = 0; var3 < 9; ++var3) {
            this.addSlot(new Slot(var1, var3, 8 + var3 * 18, 142));
        }

        this.onCraftMatrixChanged(this.craftMatrix);
    }

    public void onCraftMatrixChanged(IInventory var1) {
        this.craftResult.setInventorySlotContents(0, CraftingManager.getInstance().findMatchingRecipe(this.craftMatrix));
    }
    
    @Override
    public ItemStack func_27279_a(int slt) {
    	if(!InventoryTweaksHack.instance.status) return super.func_27279_a(slt);
    	ItemStack itemstack = null;
        Slot slot = (Slot)slots.get(slt);
        if(slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if(slt == 0)
            {
                invtweaks_func28125a(itemstack1, 9, 45, true);
            } else
            if(slt >= 9 && slt < 36)
            {
                invtweaks_func28125a(itemstack1, 36, 45, false);
            } else
            if(slt >= 36 && slt < 45)
            {
                invtweaks_func28125a(itemstack1, 9, 36, false);
            } else
            {
                invtweaks_func28125a(itemstack1, 9, 45, false);
            }
            if(itemstack1.stackSize == 0)
            {
                slot.putStack(null);
            } else
            {
                slot.onSlotChanged();
            }
            if(itemstack1.stackSize != itemstack.stackSize)
            {
                slot.onPickupFromSlot(itemstack1);
            } else
            {
                return null;
            }
        }
        return itemstack;
    }
    
    public void onCraftGuiClosed(EntityPlayer var1) {
        super.onCraftGuiClosed(var1);

        for(int var2 = 0; var2 < 4; ++var2) {
            ItemStack var3 = this.craftMatrix.getStackInSlot(var2);
            if (var3 != null) {
                var1.dropPlayerItem(var3);
                this.craftMatrix.setInventorySlotContents(var2, (ItemStack)null);
            }
        }

    }

    public boolean isUsableByPlayer(EntityPlayer var1) {
        return true;
    }
}
