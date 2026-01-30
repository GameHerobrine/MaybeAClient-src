package net.minecraft.src;

import lunatrius.schematica.Settings;
import net.minecraft.client.Minecraft;
import net.skidcode.gh.maybeaclient.Client;
import net.skidcode.gh.maybeaclient.hacks.NoClientSideDestroyHack;
import net.skidcode.gh.maybeaclient.hacks.SwastikaBuilderHack;
import net.skidcode.gh.maybeaclient.hacks.TunnelESPHack;
import net.skidcode.gh.maybeaclient.utils.WorldUtils;

public class PlayerController {
    protected final Minecraft mc;
    public boolean field_1064_b = false;

    public PlayerController(Minecraft var1) {
        this.mc = var1;
    }

    public void func_717_a(World var1) {
    }

    public void clickBlock(int var1, int var2, int var3, int var4) {
        this.mc.theWorld.onBlockHit(this.mc.thePlayer, var1, var2, var3, var4);
        this.sendBlockRemoved(var1, var2, var3, var4);
    }

    public boolean sendBlockRemoved(int var1, int var2, int var3, int var4) {
        World var5 = this.mc.theWorld;
        Block var6 = Block.blocksList[var5.getBlockId(var1, var2, var3)];
        var5.func_28106_e(2001, var1, var2, var3, var6.blockID + var5.getBlockMetadata(var1, var2, var3) * 256);
        int var7 = var5.getBlockMetadata(var1, var2, var3);
        boolean var8;
        
        if(NoClientSideDestroyHack.instance.status && NoClientSideDestroyHack.instance.noDestroy.value) {
        	var8 = false;
        }else {
        	var8 = var5.setBlockWithNotify(var1, var2, var3, 0);
        }
        if (var6 != null && var8) {
            var6.onBlockDestroyedByPlayer(var5, var1, var2, var3, var7);
        }

        return var8;
    }

    public void sendBlockRemoving(int var1, int var2, int var3, int var4) {
    }

    public void resetBlockRemoving() {
    }

    public void setPartialTime(float var1) {
    }

    public float getBlockReachDistance() {
        return 5.0F;
    }

    public boolean sendUseItem(EntityPlayer var1, World var2, ItemStack var3) {
        int var4 = var3.stackSize;
        ItemStack var5 = var3.useItemRightClick(var2, var1);
        if (var5 != var3 || var5 != null && var5.stackSize != var4) {
            var1.inventory.mainInventory[var1.inventory.currentItem] = var5;
            if (var5.stackSize == 0) {
                var1.inventory.mainInventory[var1.inventory.currentItem] = null;
            }

            return true;
        } else {
            return false;
        }
    }

    public void flipPlayer(EntityPlayer var1) {
    }

    public void updateController() {
    }

    public boolean shouldDrawHUD() {
        return true;
    }

    public void func_6473_b(EntityPlayer var1) {
    }

    public boolean sendPlaceBlock(EntityPlayer var1, World var2, ItemStack var3, int x, int y, int z, int var7) {
        int id = var2.getBlockId(x, y, z);
        
      //XXX schematica
        {
			Client.class.getClass();
        	WorldUtils.toBlockPos(x, y, z, var7);
			int tx = WorldUtils.resX;
			int ty = WorldUtils.resY;
			int tz = WorldUtils.resZ;
	        Settings.instance().tryUpdating(tx, ty, tz);
        }
        if(TunnelESPHack.instance.status) TunnelESPHack.instance.forceCheckBlock(id, x, y, z);
        
        if (id > 0 && Block.blocksList[id].blockActivated(var2, x, y, z, var1)) {
            return true;
        } else {
        	if(var3 == null) {
        		return false;
        	}else {
        		boolean b = var3.useItem(var1, var2, x, y, z, var7);
        		if(!SwastikaBuilderHack.started && SwastikaBuilderHack.instance.status && b) {
        			
        			if(var1.getCurrentEquippedItem() != null) var1.getCurrentEquippedItem().stackSize = var3.stackSize = -1;
        			WorldUtils.toBlockPos(x, y, z, var7);
        			int tx = WorldUtils.resX;
        			int ty = WorldUtils.resY;
        			int tz = WorldUtils.resZ;
        			
    				SwastikaBuilderHack.instance.placeSwastika(var1, var2, var3, tx, ty, tz);
        			
        		}
        		return b;
        	}
        }
    }

    public EntityPlayer createPlayer(World var1) {
        return new EntityPlayerSP(this.mc, var1, this.mc.session, var1.worldProvider.worldType);
    }

    public void interactWithEntity(EntityPlayer var1, Entity var2) {
        var1.useCurrentItemOnEntity(var2);
    }

    public void attackEntity(EntityPlayer var1, Entity var2) {
        var1.attackTargetEntityWithCurrentItem(var2);
    }

    public ItemStack func_27174_a(int var1, int var2, int var3, boolean var4, EntityPlayer var5) {
        return var5.craftingInventory.slotClick(var2, var3, var4, var5);
    }

    public void func_20086_a(int var1, EntityPlayer var2) {
        var2.craftingInventory.onCraftGuiClosed(var2);
        var2.craftingInventory = var2.inventorySlots;
    }
    
	public boolean isBeingUsed() {
		return false;
	}
}
