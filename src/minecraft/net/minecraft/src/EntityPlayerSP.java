package net.minecraft.src;

import net.minecraft.client.Minecraft;
import net.skidcode.gh.maybeaclient.Client;
import net.skidcode.gh.maybeaclient.events.EventRegistry;
import net.skidcode.gh.maybeaclient.events.impl.EventPlayerUpdatePost;
import net.skidcode.gh.maybeaclient.events.impl.EventPlayerUpdatePre;
import net.skidcode.gh.maybeaclient.hacks.AutoTunnelHack;
import net.skidcode.gh.maybeaclient.hacks.AutoWalkHack;
import net.skidcode.gh.maybeaclient.hacks.CombatLogHack;
import net.skidcode.gh.maybeaclient.hacks.FreecamHack;
import net.skidcode.gh.maybeaclient.hacks.StrafeHack;

public class EntityPlayerSP extends EntityPlayer {
    public MovementInput movementInput;
    protected Minecraft mc;
    public int field_9373_b = 20;
    private boolean inPortal = false;
    public float timeInPortal;
    public float prevTimeInPortal;
    private MouseFilter field_21903_bJ = new MouseFilter();
    private MouseFilter field_21904_bK = new MouseFilter();
    private MouseFilter field_21902_bL = new MouseFilter();

    public EntityPlayerSP(Minecraft var1, World var2, Session var3, int var4) {
        super(var2);
        this.mc = var1;
        this.dimension = var4;
        if (var3 != null && var3.username != null && var3.username.length() > 0) {
            this.skinUrl = "http://s3.amazonaws.com/MinecraftSkins/" + var3.username + ".png";
        }

        this.username = var3.username;
    }

    public void moveEntity(double var1, double var3, double var5) {
        super.moveEntity(var1, var3, var5);
    }
    
    @Override
    public boolean attackEntityFrom(Entity var1, int var2) {
    	boolean b = super.attackEntityFrom(var1, var2);
    	if(this == mc.thePlayer && CombatLogHack.instance.status && CombatLogHack.instance.mode.currentMode.equalsIgnoreCase("OnHit")) {
    		if(mc.isMultiplayerWorld()) {
    			Client.forceDisconnect(CombatLogHack.instance);
    		}else {
    			CombatLogHack.shouldQuit = true;
    		}
    	}
    	return b;
    }
    
    public void updatePlayerActionState() {
        super.updatePlayerActionState();
        
        if(!FreecamHack.movementTaken()) {
	        this.moveStrafing = this.movementInput.moveStrafe;
	        this.moveForward = this.movementInput.moveForward;
	        this.isJumping = this.movementInput.jump;
        }
        if(AutoTunnelHack.autoWalking()) {
        	this.moveForward = 1;
        }
        if(AutoWalkHack.instance.status) {
        	this.moveForward = 1;
        }
        
        if(StrafeHack.instance.enabled()) {
        	this.moveForward = this.moveStrafing = 0;
        }
        
    }

    public void onLivingUpdate() {
        if (!this.mc.field_25001_G.func_27183_a(AchievementList.field_25195_b)) {
            this.mc.field_25002_t.func_27101_b(AchievementList.field_25195_b);
        }
        
        this.prevTimeInPortal = this.timeInPortal;
        
        EventPlayerUpdatePre e = new EventPlayerUpdatePre();
        EventRegistry.handleEvent(e);
        
        if (this.inPortal) {
            if (this.timeInPortal == 0.0F) {
                this.mc.sndManager.func_337_a("portal.trigger", 1.0F, this.rand.nextFloat() * 0.4F + 0.8F);
            }

            this.timeInPortal += 0.0125F;
            if (this.timeInPortal >= 1.0F) {
                this.timeInPortal = 1.0F;
                this.field_9373_b = 10;
                this.mc.sndManager.func_337_a("portal.travel", 1.0F, this.rand.nextFloat() * 0.4F + 0.8F);
                this.mc.usePortal();
            }

            this.inPortal = false;
        } else {
            if (this.timeInPortal > 0.0F) {
                this.timeInPortal -= 0.05F;
            }

            if (this.timeInPortal < 0.0F) {
                this.timeInPortal = 0.0F;
            }
        }

        if (this.field_9373_b > 0) {
            --this.field_9373_b;
        }

        if(!FreecamHack.movementTaken()) {
        	this.movementInput.updatePlayerMoveState(this);
        	if (this.movementInput.sneak && this.ySize < 0.2F) {
                this.ySize = 0.2F;
            }
        }
        

        EventPlayerUpdatePost ee = new EventPlayerUpdatePost();
        EventRegistry.handleEvent(ee);
        
        super.onLivingUpdate();
    }

    public void resetPlayerKeyState() {
    	this.movementInput.resetKeyState();
    }

    public void handleKeyPress(int var1, boolean var2) {
    	this.movementInput.checkKeyForMovementInput(var1, var2);
    }

    public void writeEntityToNBT(NBTTagCompound var1) {
        super.writeEntityToNBT(var1);
        var1.setInteger("Score", this.score);
    }

    public void readEntityFromNBT(NBTTagCompound var1) {
        super.readEntityFromNBT(var1);
        this.score = var1.getInteger("Score");
    }

    public void func_20059_m() {
        super.func_20059_m();
        this.mc.displayGuiScreen((GuiScreen)null);
    }

    public void displayGUIEditSign(TileEntitySign var1) {
        this.mc.displayGuiScreen(new GuiEditSign(var1));
    }

    public void displayGUIChest(IInventory var1) {
        this.mc.displayGuiScreen(new GuiChest(this.inventory, var1));
    }

    public void displayWorkbenchGUI(int var1, int var2, int var3) {
        this.mc.displayGuiScreen(new GuiCrafting(this.inventory, this.worldObj, var1, var2, var3));
    }

    public void displayGUIFurnace(TileEntityFurnace var1) {
        this.mc.displayGuiScreen(new GuiFurnace(this.inventory, var1));
    }

    public void displayGUIDispenser(TileEntityDispenser var1) {
        this.mc.displayGuiScreen(new GuiDispenser(this.inventory, var1));
    }

    public void onItemPickup(Entity var1, int var2) {
        this.mc.effectRenderer.addEffect(new EntityPickupFX(this.mc.theWorld, var1, this, -0.5F));
    }

    public int getPlayerArmorValue() {
        return this.inventory.getTotalArmorValue();
    }

    public void sendChatMessage(String var1) {
    }

    public boolean isSneaking() {
        return this.movementInput.sneak && !this.sleeping;
    }

    public void setInPortal() {
        if (this.field_9373_b > 0) {
            this.field_9373_b = 10;
        } else {
            this.inPortal = true;
        }
    }

    public void setHealth(int var1) {
        int var2 = this.health - var1;
        if (var2 <= 0) {
            this.health = var1;
        } else {
            this.field_9346_af = var2;
            this.prevHealth = this.health;
            this.field_9306_bj = this.field_9366_o;
            this.damageEntity(var2);
            this.hurtTime = this.maxHurtTime = 10;
        }

    }

    public void respawnPlayer() {
        this.mc.respawn(false);
    }

    public void func_6420_o() {
    }

    public void addChatMessage(String var1) {
        this.mc.ingameGUI.func_22064_c(var1);
    }
    public void addChatMessageWithMoreOpacityBG(String var1) {
        this.mc.ingameGUI.addChatMessageWithMoreOpacityBG(var1);
    }
    public void addStat(StatBase var1, int var2) {
        if (var1 != null) {
            if (var1.func_25067_a()) {
                Achievement var3 = (Achievement)var1;
                if (var3.field_25076_c == null || this.mc.field_25001_G.func_27183_a(var3.field_25076_c)) {
                    if (!this.mc.field_25001_G.func_27183_a(var3)) {
                        this.mc.field_25002_t.func_27102_a(var3);
                    }

                    this.mc.field_25001_G.func_25100_a(var1, var2);
                }
            } else {
                this.mc.field_25001_G.func_25100_a(var1, var2);
            }

        }
    }

	
}
