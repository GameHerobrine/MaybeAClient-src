package net.skidcode.gh.maybeaclient.gui.click;

import org.lwjgl.opengl.EXTRescaleNormal;
import org.lwjgl.opengl.GL11;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.GuiIngame;
import net.minecraft.src.ItemStack;
import net.minecraft.src.RenderHelper;
import net.minecraft.src.RenderManager;
import net.skidcode.gh.maybeaclient.Client;
import net.skidcode.gh.maybeaclient.hacks.ClickGUIHack;
import net.skidcode.gh.maybeaclient.hacks.KeybindingsHack;
import net.skidcode.gh.maybeaclient.hacks.PlayerViewHack;
import net.skidcode.gh.maybeaclient.utils.PlayerUtils;

public class PlayerViewTab extends Tab{
	public static PlayerViewTab instance;
	public PlayerViewTab() {
		super("Player View");
		this.xDefPos = this.xPos = 160;
		this.yDefPos = this.yPos = 24 + 14 + 14 + 14 + 14;
		this.minimized = true;
		instance = this;
	}
	
	public void renderIngame() {
		if(PlayerViewHack.instance.status && RenderManager.instance.livingPlayer != null) super.renderIngame();
	}
	
	public void render() {
		int ySpace = ClickGUIHack.theme().yspacing;
		int prevSpace = ClickGUIHack.theme().titlebasediff;
		int txtCenter = ClickGUIHack.theme().yaddtocenterText;
		if(this.minimized) {
			this.height = ySpace;
			this.width = Client.mc.fontRenderer.getStringWidth(this.name) + ClickGUIHack.theme().titleXadd;
			super.render();
			return;
		}
		EntityPlayer player = Client.mc.thePlayer;
		this.height = ySpace + prevSpace + 90; //cliff checked
		this.width = Client.mc.fontRenderer.getStringWidth(this.name) + 2 + 16*2;
		super.render();
		this.renderFrame((int)this.xPos, (int)this.yPos + ySpace + prevSpace, (int)this.xPos + this.width, (int)this.yPos + this.height);
		
		GL11.glEnable(EXTRescaleNormal.GL_RESCALE_NORMAL_EXT);
		GL11.glEnable(GL11.GL_COLOR_MATERIAL);
		GL11.glPushMatrix();
		int centerX = (this.width - 16) / 2;
		int centerY = this.height - ySpace;
		GL11.glTranslatef((float)(centerX + this.xPos), (float)(centerY + this.yPos), 50.0F);
		GL11.glScalef(-35, 35, 35);
		GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
		float lastRYaw = player.rotationYaw;
		float prevLastRYaw = player.prevRotationYaw;
		float lastBYaw = player.renderYawOffset;
		float prevLastBYaw = player.prevRenderYawOffset;
		
		player.rotationYaw = -PlayerUtils.wrapAngle180(((lastRYaw % 360 + 360) % 360));
		player.prevRotationYaw = -PlayerUtils.wrapAngle180(((prevLastRYaw % 360 + 360) % 360));
		player.renderYawOffset = -PlayerUtils.wrapAngle180(((lastBYaw % 360 + 360) % 360));
		player.prevRenderYawOffset = -PlayerUtils.wrapAngle180(((prevLastBYaw % 360 + 360) % 360));
		
		if(PlayerViewHack.instance.dontRotate.value) {
			player.rotationYaw = player.prevRotationYaw = 0;
			player.renderYawOffset = player.prevRenderYawOffset = 0;
		}
		
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		float var10 = (float)(centerY + 75 - 50);
		GL11.glRotatef(135.0F, 0.0F, 1.0F, 0.0F);
		RenderHelper.enableStandardItemLighting();
		GL11.glRotatef(-135.0F, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(-((float)Math.atan((double)(var10 / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);
		GL11.glTranslatef(0.0F, Client.mc.thePlayer.yOffset, 0.0F);
		GL11.glColor4f(1, 1, 1, 1);
		//GL11.glCullFace(GL11.GL_FRONT_AND_BACK);
		RenderManager.instance.renderEntityWithPosYaw(player, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F);
		player.rotationYaw = lastRYaw;
		player.prevRotationYaw = prevLastRYaw;
		player.renderYawOffset = lastBYaw;
		player.prevRenderYawOffset = prevLastBYaw;
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(EXTRescaleNormal.GL_RESCALE_NORMAL_EXT);
		GL11.glPopMatrix();
		
		
		
		GL11.glPushMatrix();
		
		int yOff = 0;
		for(;yOff < 4; ++yOff) {
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GuiIngame.itemRenderer.renderItemIntoGUI(Client.mc.fontRenderer, Client.mc.renderEngine, player.inventory.armorInventory[3-yOff], (int)this.xPos + this.width-16, (int)this.yPos + ySpace + prevSpace + 18*yOff);
			GuiIngame.itemRenderer.renderItemOverlayIntoGUI(Client.mc.fontRenderer, Client.mc.renderEngine, player.inventory.armorInventory[3-yOff], (int)this.xPos + this.width-16, (int)this.yPos + ySpace + prevSpace + 18*yOff);
		}
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_LIGHTING);
		GuiIngame.itemRenderer.renderItemIntoGUI(Client.mc.fontRenderer, Client.mc.renderEngine, player.inventory.getCurrentItem(), (int)this.xPos + this.width-16, (int)this.yPos + ySpace + prevSpace + 18*yOff);
		GuiIngame.itemRenderer.renderItemOverlayIntoGUI(Client.mc.fontRenderer, Client.mc.renderEngine, player.inventory.getCurrentItem(), (int)this.xPos + this.width-16, (int)this.yPos + ySpace + prevSpace + 18*yOff);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		RenderHelper.disableStandardItemLighting();
		GL11.glPopMatrix();
	}
}
