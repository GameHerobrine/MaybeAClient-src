package net.minecraft.src;

import lunatrius.schematica.Settings;
import net.minecraft.client.Minecraft;
import net.skidcode.gh.maybeaclient.hacks.AutoToolHack;
import net.skidcode.gh.maybeaclient.hacks.ForceFieldHack;
import net.skidcode.gh.maybeaclient.hacks.InstantHack;
import net.skidcode.gh.maybeaclient.hacks.NoFriendlyFireHack;
import net.skidcode.gh.maybeaclient.hacks.PacketMineHack;
import net.skidcode.gh.maybeaclient.hacks.ReachHack;
import net.skidcode.gh.maybeaclient.hacks.SpeedMineHack;
import net.skidcode.gh.maybeaclient.hacks.TunnelESPHack;

public class PlayerControllerMP extends PlayerController {
    private int currentBlockX = -1;
    private int currentBlockY = -1;
    private int currentblockZ = -1;
    private float curBlockDamageMP = 0.0F;
    private float prevBlockDamageMP = 0.0F;
    private float field_9441_h = 0.0F;
    private int blockHitDelay = 0;
    private boolean isHittingBlock = false;
    private NetClientHandler netClientHandler;
    private int currentPlayerItem = 0;

    public PlayerControllerMP(Minecraft var1, NetClientHandler var2) {
        super(var1);
        this.netClientHandler = var2;
    }

    public void flipPlayer(EntityPlayer var1) {
        var1.rotationYaw = -180.0F;
    }

    public boolean sendBlockRemoved(int x, int y, int z, int var4) {
        int id = this.mc.theWorld.getBlockId(x, y, z);
        boolean var6 = super.sendBlockRemoved(x, y, z, var4);
        ItemStack var7 = this.mc.thePlayer.getCurrentEquippedItem();
        if (var7 != null) {
            var7.onDestroyBlock(id, x, y, z, this.mc.thePlayer);
            if (var7.stackSize == 0) {
                var7.func_1097_a(this.mc.thePlayer);
                this.mc.thePlayer.destroyCurrentEquippedItem();
            }
        }

        if(TunnelESPHack.instance.status) TunnelESPHack.instance.forceCheckBlock(id, x, y, z);
        //XXX schematica
        Settings.instance().tryUpdating(x, y, z);
        return var6;
    }

    public void clickBlock(int var1, int var2, int var3, int var4) {
        if (!this.isHittingBlock || var1 != this.currentBlockX || var2 != this.currentBlockY || var3 != this.currentblockZ) {
            this.netClientHandler.addToSendQueue(new Packet14BlockDig(0, var1, var2, var3, var4));
            int var5 = this.mc.theWorld.getBlockId(var1, var2, var3);
            
            if(var5 > 0 && PacketMineHack.instance.status) {
            	PacketMineHack.instance.packetMine(var1, var2, var3, var5);
            }
            
            if (var5 > 0 && this.curBlockDamageMP == 0.0F) {
                Block.blocksList[var5].onBlockClicked(this.mc.theWorld, var1, var2, var3, this.mc.thePlayer);
            }
            float progress = SpeedMineHack.instance.status ? SpeedMineHack.instance.sendDestroyAfter.value : 1.0f;
            
            if (var5 > 0 && Block.blocksList[var5].blockStrength(this.mc.thePlayer) >= progress) {
                this.sendBlockRemoved(var1, var2, var3, var4);
            } else {
                this.isHittingBlock = true;
                this.currentBlockX = var1;
                this.currentBlockY = var2;
                this.currentblockZ = var3;
                this.curBlockDamageMP = 0.0F;
                this.prevBlockDamageMP = 0.0F;
                this.field_9441_h = 0.0F;
            }
        }

    }

    public void resetBlockRemoving() {
        this.curBlockDamageMP = 0.0F;
        this.isHittingBlock = false;
    }

    public void sendBlockRemoving(int var1, int var2, int var3, int var4) {
        if (this.isHittingBlock) {
        	
        	if(InstantHack.instance.status) {
	            this.netClientHandler.addToSendQueue(new Packet14BlockDig(0, var1, var2, var3, var4)); //new Packet16BlockItemSwitch(this.field_1075_l));
	            this.netClientHandler.addToSendQueue(new Packet14BlockDig(2, var1, var2, var3, var4));
	            this.sendBlockRemoved(var1, var2, var3, var4);
            }
            this.syncCurrentPlayItem();
            if (this.blockHitDelay > 0) {
                --this.blockHitDelay;
            } else {
                if (var1 == this.currentBlockX && var2 == this.currentBlockY && var3 == this.currentblockZ) {
                    int var5 = this.mc.theWorld.getBlockId(var1, var2, var3);
                    if (var5 == 0) {
                        this.isHittingBlock = false;
                        return;
                    }

                    Block var6 = Block.blocksList[var5];
                    
                    if(AutoToolHack.instance.status) {
                    	mc.thePlayer.inventory.currentItem = AutoToolHack.getBestSlot(var6);
                    }
                    
                    this.curBlockDamageMP += var6.blockStrength(this.mc.thePlayer);
                    if (this.field_9441_h % 4.0F == 0.0F && var6 != null) {
                        this.mc.sndManager.playSound(var6.stepSound.func_1145_d(), (float)var1 + 0.5F, (float)var2 + 0.5F, (float)var3 + 0.5F, (var6.stepSound.getVolume() + 1.0F) / 8.0F, var6.stepSound.getPitch() * 0.5F);
                    }

                    ++this.field_9441_h;
                    float progress = SpeedMineHack.instance.status ? SpeedMineHack.instance.sendDestroyAfter.value : 1.0f;
                    if (this.curBlockDamageMP >= progress) {
                        this.isHittingBlock = false;
                        this.netClientHandler.addToSendQueue(new Packet14BlockDig(2, var1, var2, var3, var4));
                        this.sendBlockRemoved(var1, var2, var3, var4);
                        this.curBlockDamageMP = 0.0F;
                        this.prevBlockDamageMP = 0.0F;
                        this.field_9441_h = 0.0F;
                        this.blockHitDelay = 5;
                    }
                } else {
                    this.clickBlock(var1, var2, var3, var4);
                }

            }
        }
    }

    public void setPartialTime(float var1) {
        if (this.curBlockDamageMP <= 0.0F) {
            this.mc.ingameGUI.damageGuiPartialTime = 0.0F;
            this.mc.renderGlobal.damagePartialTime = 0.0F;
        } else {//CLIENT: visuals
        	float div = SpeedMineHack.instance.status ? SpeedMineHack.instance.sendDestroyAfter.value : 1.0f;
            float var2 = this.prevBlockDamageMP + (this.curBlockDamageMP - this.prevBlockDamageMP) * var1;
            this.mc.ingameGUI.damageGuiPartialTime = var2/div;
            this.mc.renderGlobal.damagePartialTime = var2/div;
        }

    }

    public float getBlockReachDistance() {
    	if(ReachHack.instance.status) return ReachHack.instance.radius.getValue();
        return 4.0F;
    }

    public void func_717_a(World var1) {
        super.func_717_a(var1);
    }

    public void updateController() {
        this.syncCurrentPlayItem();
        this.prevBlockDamageMP = this.curBlockDamageMP;
        this.mc.sndManager.playRandomMusicIfReady();
    }

    public void syncCurrentPlayItem() {
        int var1 = this.mc.thePlayer.inventory.currentItem;
        if (var1 != this.currentPlayerItem) {
            this.currentPlayerItem = var1;
            this.netClientHandler.addToSendQueue(new Packet16BlockItemSwitch(this.currentPlayerItem));
        }

    }

    public boolean isBeingUsed() {
    	return this.isHittingBlock; //no need to check others
    }
    
    public boolean sendPlaceBlock(EntityPlayer var1, World var2, ItemStack var3, int var4, int var5, int var6, int var7) {
        this.syncCurrentPlayItem();
        this.netClientHandler.addToSendQueue(new Packet15Place(var4, var5, var6, var7, var1.inventory.getCurrentItem()));
        boolean var8 = super.sendPlaceBlock(var1, var2, var3, var4, var5, var6, var7);
        if(TunnelESPHack.instance.status) TunnelESPHack.instance.forceCheckBlock(0, var4, var5, var6);
        //XXX schematica
        Settings.instance().tryUpdating(var4, var5, var6);
        return var8;
    }

    public boolean sendUseItem(EntityPlayer var1, World var2, ItemStack var3) {
        this.syncCurrentPlayItem();
        this.netClientHandler.addToSendQueue(new Packet15Place(-1, -1, -1, 255, var1.inventory.getCurrentItem()));
        boolean var4 = super.sendUseItem(var1, var2, var3);
        return var4;
    }

    public EntityPlayer createPlayer(World var1) {
        return new EntityClientPlayerMP(this.mc, var1, this.mc.session, this.netClientHandler);
    }

    public void attackEntity(EntityPlayer var1, Entity var2) {
        this.syncCurrentPlayItem();
        if(NoFriendlyFireHack.instance.status && var2 instanceof EntityPlayer) {
        	if(ForceFieldHack.instance.isFriend((EntityPlayer) var2)) {
        		return;
        	}
        }
        this.netClientHandler.addToSendQueue(new Packet7UseEntity(var1.entityId, var2.entityId, 1));
        var1.attackTargetEntityWithCurrentItem(var2);
    }

    public void interactWithEntity(EntityPlayer var1, Entity var2) {
        this.syncCurrentPlayItem();
        this.netClientHandler.addToSendQueue(new Packet7UseEntity(var1.entityId, var2.entityId, 0));
        var1.useCurrentItemOnEntity(var2);
    }

    public ItemStack func_27174_a(int var1, int var2, int var3, boolean var4, EntityPlayer var5) {
        short var6 = var5.craftingInventory.func_20111_a(var5.inventory);
        ItemStack var7 = super.func_27174_a(var1, var2, var3, var4, var5);
        this.netClientHandler.addToSendQueue(new Packet102WindowClick(var1, var2, var3, var4, var7, var6));
        return var7;
    }

    public void func_20086_a(int var1, EntityPlayer var2) {
        if (var1 != -9999) {
            ;
        }
    }
}
