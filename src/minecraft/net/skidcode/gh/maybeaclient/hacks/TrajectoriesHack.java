package net.skidcode.gh.maybeaclient.hacks;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraft.src.EntityArrow;
import net.minecraft.src.EnumMovingObjectType;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.MovingObjectPosition;
import net.minecraft.src.RenderManager;
import net.minecraft.src.Tessellator;
import net.skidcode.gh.maybeaclient.events.Event;
import net.skidcode.gh.maybeaclient.events.EventListener;
import net.skidcode.gh.maybeaclient.events.EventRegistry;
import net.skidcode.gh.maybeaclient.events.impl.EventWorldRenderPreFog;
import net.skidcode.gh.maybeaclient.hacks.category.Category;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingBoolean;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingColor;
import net.skidcode.gh.maybeaclient.utils.RenderUtils;

public class TrajectoriesHack extends Hack implements EventListener{
	
	public SettingColor color = new SettingColor(this, "Color", 0, 255, 0);
	public SettingBoolean renderOnlyWithItem = new SettingBoolean(this, "Render only if holding bow/projectile", true);
	
	public TrajectoriesHack() {
		super("Trajectories", "Shows projectile trajectories", Keyboard.KEY_NONE, Category.RENDER);
		EventRegistry.registerListener(EventWorldRenderPreFog.class, this);
		this.addSetting(this.color);
		this.addSetting(this.renderOnlyWithItem);
	}
	public static double posX, posY, posZ;
	public static float yaw, pitch;
	public static MovingObjectPosition hit;
	public static int MAX_TICKS = 1000;
	
	@Override
	public void handleEvent(Event event) {
		if(event instanceof EventWorldRenderPreFog) {
			ItemStack is = mc.thePlayer.getCurrentEquippedItem();
			
			if(this.renderOnlyWithItem.value) {
				if(is == null) return;
				if(is.itemID != Item.bow.shiftedIndex && is.itemID != Item.snowball.shiftedIndex && is.itemID != Item.egg.shiftedIndex) return;
			}
			EventWorldRenderPreFog wrp = (EventWorldRenderPreFog) event;
			posX = mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * (double)wrp.param;
			posY = mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * (double)wrp.param;
			posZ = mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * (double)wrp.param;
			yaw = mc.thePlayer.prevRotationYaw + (mc.thePlayer.rotationYaw - mc.thePlayer.prevRotationYaw) * wrp.param;
            pitch = mc.thePlayer.prevRotationPitch + (mc.thePlayer.rotationPitch - mc.thePlayer.prevRotationPitch) * wrp.param;
            
			EntityArrow arrow = new EntityArrow(mc.theWorld, mc.thePlayer, true);
			boolean light = GL11.glGetBoolean(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glLineWidth(2);
			GL11.glColor4f(this.color.red/255f, this.color.green/255f, this.color.blue/255f, 1);
			Tessellator.instance.startDrawing(GL11.GL_LINE_STRIP);
			double rx = RenderManager.renderPosX;
			double ry = RenderManager.renderPosY;
			double rz = RenderManager.renderPosZ;
			//Tessellator.instance.addVertex(0, 0, 0);
			for(int i = 0; i < MAX_TICKS; ++i) {
				arrow.onUpdate();
				Tessellator.instance.addVertex(arrow.posX - rx, arrow.posY - ry, arrow.posZ - rz);
				if(arrow.isDead) break;
			}
			Tessellator.instance.draw();
			
			if(hit != null) {
				if(hit.typeOfHit == EnumMovingObjectType.ENTITY) {
					double diffMaxY = hit.entityHit.boundingBox.maxY - hit.entityHit.posY;
					double diffMinY = hit.entityHit.boundingBox.minY - hit.entityHit.posY;
					double radius = hit.entityHit.width / 2;
					
					double x = hit.entityHit.lastTickPosX + (hit.entityHit.posX - hit.entityHit.lastTickPosX) * (double)wrp.param;
					double y = hit.entityHit.lastTickPosY + (hit.entityHit.posY - hit.entityHit.lastTickPosY) * (double)wrp.param;
					double z = hit.entityHit.lastTickPosZ + (hit.entityHit.posZ - hit.entityHit.lastTickPosZ) * (double)wrp.param;
					double minX = x - radius;
					double minY = y + diffMinY;
					double minZ = z - radius;
					double maxX = x + radius;
					double maxY = y + diffMaxY;
					double maxZ = z + radius;
					RenderUtils.drawOutlinedBB(minX - rx, minY - ry, minZ - rz, maxX - rx, maxY - ry, maxZ - rz);
				}else if(hit.typeOfHit == EnumMovingObjectType.TILE) {
					double bx = hit.blockX;
					double by = hit.blockY;
					double bz = hit.blockZ;
					
					RenderUtils.drawOutlinedBlockBB(bx - rx, by - ry, bz - rz, 1, 1, 1);
				}
			}
			
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			if(light) {
				GL11.glEnable(GL11.GL_LIGHTING);
			}
			
			GL11.glEnable(GL11.GL_TEXTURE_2D);
		}
		
	}

}
