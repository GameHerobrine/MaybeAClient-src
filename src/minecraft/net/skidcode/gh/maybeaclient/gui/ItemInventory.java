package net.skidcode.gh.maybeaclient.gui;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IInventory;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;

public class ItemInventory implements IInventory {
	int offset;

	public ItemInventory(int offset) {
		this.offset = offset;
	}

	public int getSizeInventory() {
		return 54;
	}

	public ItemStack getStackInSlot(int i) {
		try {
			for (int f = 0, i1 = 0; f < Item.itemsList.length; f++) {
				if (Item.itemsList[f] == null) {
					continue;
				} else {
					itemsList2[i1] = Item.itemsList[f];
					i1++;
				}
			}
			if (itemsList2[+i + GuiItemGive.page * 54] != null)
				return new ItemStack(itemsList2[i + GuiItemGive.page * 54], -1);
			else
				return null;
		} catch (Exception e) {
			return null;
		}
	}

	public ItemStack decrStackSize(int i, int j) {
		if (itemsList2[i + GuiItemGive.page * 54] != null)
			return new ItemStack(itemsList2[+i + GuiItemGive.page * 54], -1);
		return null;
	}

	public void setInventorySlotContents(int i, ItemStack itemstack) {
	}

	public String getInvName() {
		return "Item Give GUI";
	}

	public int getInventoryStackLimit() {
		return 64;
	}

	public static Item itemsList2[] = new Item[32000];

	public void onInventoryChanged() {
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return true;
	}
}
