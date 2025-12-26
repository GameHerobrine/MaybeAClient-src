package net.skidcode.gh.maybeaclient.hacks;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraft.src.Entity;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Packet20NamedEntitySpawn;
import net.minecraft.src.Packet29DestroyEntity;
import net.minecraft.src.RenderManager;
import net.minecraft.src.Tessellator;
import net.minecraft.src.Vec3D;
import net.skidcode.gh.maybeaclient.Client;
import net.skidcode.gh.maybeaclient.events.Event;
import net.skidcode.gh.maybeaclient.events.EventListener;
import net.skidcode.gh.maybeaclient.events.EventRegistry;
import net.skidcode.gh.maybeaclient.events.impl.EventPacketReceive;
import net.skidcode.gh.maybeaclient.events.impl.EventWorldRenderPreFog;
import net.skidcode.gh.maybeaclient.hacks.category.Category;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingBoolean;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingColor;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingEnum;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingMode;
import net.skidcode.gh.maybeaclient.hacks.settings.enums.EnumAlign;
import net.skidcode.gh.maybeaclient.hacks.settings.enums.EnumExpand;
import net.skidcode.gh.maybeaclient.utils.ChatColor;
import net.skidcode.gh.maybeaclient.utils.RenderUtils;

public class LastSeenSpotsHack extends Hack implements EventListener{
	
	public class PlayerInfo{
		public double x, y, z;
		public ItemStack currItem;
		public ItemStack armor[];
		
		public PlayerInfo(EntityPlayer player) {
			this.x = player.posX;
			this.y = player.posY;
			this.z = player.posZ;
			this.currItem = player.inventory.getCurrentItem();
			this.armor = player.inventory.armorInventory;
		}
	}
	public SettingEnum<EnumAlign> alignment = new SettingEnum<>(this, "Alignment", EnumAlign.LEFT);
	public SettingEnum<EnumExpand> expand = new SettingEnum<>(this, "Expand", EnumExpand.BOTTOM);
	
	public SettingBoolean render;
	public SettingColor boxColor = new SettingColor(this, "Box Color", 255, 255, 0);
	public SettingBoolean tracers;
	public SettingColor tracerColor = new SettingColor(this, "Tracers Color", 255, 255, 0);
	public SettingBoolean notifyInChat = new SettingBoolean(this, "NotifyInChat", false);
	public static LastSeenSpotsHack instance;
	public LastSeenSpotsHack() {
		super("LastSeenSpots", "Shows last player positions", Keyboard.KEY_NONE, Category.RENDER);
		instance = this;
		this.addSetting(this.alignment);
		this.addSetting(this.expand);
		
		this.addSetting(this.render = new SettingBoolean(this, "RenderInWorld", true) {
			public void setValue(boolean d) {
				super.setValue(d);
				LastSeenSpotsHack.instance.boxColor.hidden = !this.value;
			}
		});
		this.addSetting(this.boxColor);
		
		this.addSetting(this.tracers = new SettingBoolean(this, "Tracers", false) {
			public void setValue(boolean d) {
				super.setValue(d);
				LastSeenSpotsHack.instance.tracerColor.hidden = !this.value;
			}
		});
		this.addSetting(this.tracerColor);
		
		this.addSetting(this.notifyInChat);
		EventRegistry.registerListener(EventPacketReceive.class, this);
		EventRegistry.registerListener(EventWorldRenderPreFog.class, this);
	}
	public HashMap<String, PlayerInfo> players = new HashMap<String, PlayerInfo>();
	
	@Override
	public void handleEvent(Event event) {
		if(event instanceof EventPacketReceive) {
			if(((EventPacketReceive) event).packet instanceof Packet29DestroyEntity) {
				Packet29DestroyEntity pk = (Packet29DestroyEntity) ((EventPacketReceive) event).packet;
				Entity e = mc.getSendQueue().worldClient.func_709_b(pk.entityId);
				if(e instanceof EntityPlayer) {
					if(this.notifyInChat.value) Client.addMessage(ChatColor.GOLD+((EntityPlayer) e).username+ChatColor.WHITE+" was removed!");
					
					this.players.put(((EntityPlayer) e).username, new PlayerInfo((EntityPlayer) e));
				}
			}
			
			if(((EventPacketReceive) event).packet instanceof Packet20NamedEntitySpawn) {
				Packet20NamedEntitySpawn pk = (Packet20NamedEntitySpawn) ((EventPacketReceive) event).packet;
				
				if(this.players.containsKey(pk.name)) {
					String name = pk.name;
					if(this.notifyInChat.value) Client.addMessage(ChatColor.GOLD+name+ChatColor.WHITE+" reappeared!");
					this.players.remove(name);
				}
			}
		}
		
		if(event instanceof EventWorldRenderPreFog) {
			EventWorldRenderPreFog ev = (EventWorldRenderPreFog) event;
			GL11.glPushMatrix();
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glDisable(GL11.GL_FOG);
			GL11.glLineWidth(1.0f);
			for(Map.Entry<String, PlayerInfo> ent : this.players.entrySet()) {
				String name = ent.getKey();
				PlayerInfo pos = ent.getValue();
				
	            if(this.tracers.value) {
	            	Vec3D forward = new Vec3D(0.0d, 0.0d, 1.0d);
	                forward.rotateAroundX((float) -Math.toRadians(mc.thePlayer.rotationPitch));
	                forward.rotateAroundY((float) -Math.toRadians(mc.thePlayer.rotationYaw));
	                
	                boolean bobbing = mc.gameSettings.viewBobbing;
            		mc.gameSettings.viewBobbing = false;
                	mc.entityRenderer.setupCameraTransform(ev.param, 0);
                    double d2 = pos.x;
                    double d3 = pos.y + 1;
                    double d4 = pos.z;
                    double diffX = d2 - RenderManager.renderPosX;
                    double diffY = d3 - RenderManager.renderPosY;
                    double diffZ = d4 - RenderManager.renderPosZ;
                    
                    
                    GL11.glPushMatrix();
                    GL11.glDisable(GL11.GL_TEXTURE_2D);
                    GL11.glDisable(GL11.GL_ALPHA_TEST);
                    GL11.glDisable(GL11.GL_BLEND);
                    GL11.glDisable(GL11.GL_DEPTH_TEST);
                    GL11.glColor4f(this.tracerColor.red / 255f, this.tracerColor.green / 255f, this.tracerColor.blue / 255f, 1);
                    GL11.glLineWidth(1);
                    
                    Tessellator.instance.startDrawing(1);
                    Tessellator.instance.addVertex(forward.xCoord, forward.yCoord, forward.zCoord);
                    Tessellator.instance.addVertex(diffX, diffY, diffZ);
                    Tessellator.instance.draw();
                    
                    GL11.glEnable(GL11.GL_ALPHA_TEST);
                    GL11.glDisable(GL11.GL_BLEND);
                    GL11.glDisable(GL11.GL_TEXTURE_2D);
        			GL11.glDisable(GL11.GL_DEPTH_TEST);
                    GL11.glPopMatrix();
                    mc.gameSettings.viewBobbing = bobbing;
                    mc.entityRenderer.setupCameraTransform(ev.param, 0);
	            }
				if(this.render.value) {
					GL11.glColor4f(this.boxColor.red / 255f, this.boxColor.green / 255f, this.boxColor.blue / 255f, 1);
					RenderUtils.drawOutlinedBB(pos.x - RenderManager.renderPosX - 0.5, pos.y - RenderManager.renderPosY, pos.z - RenderManager.renderPosZ - 0.5, pos.x - RenderManager.renderPosX + 0.5, pos.y - RenderManager.renderPosY + 2, pos.z - RenderManager.renderPosZ + 0.5);
					RenderUtils.drawString(name, pos.x, pos.y, pos.z);
					GL11.glDisable(GL11.GL_TEXTURE_2D);
					GL11.glDisable(GL11.GL_LIGHTING);
				}
			}
			GL11.glEnable(GL11.GL_BLEND);
			
			GL11.glColor4f(1f, 1f, 1f, 1f);
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glPopAttrib();
			GL11.glPopMatrix();
		}
	}

	public static void reset() {
		instance.players.clear();
	}
}
