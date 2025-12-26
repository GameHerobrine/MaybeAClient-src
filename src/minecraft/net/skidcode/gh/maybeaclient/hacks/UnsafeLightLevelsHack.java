package net.skidcode.gh.maybeaclient.hacks;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.util.HashMap;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;

import net.minecraft.src.EnumCreatureType;
import net.minecraft.src.MathHelper;
import net.minecraft.src.RenderManager;
import net.minecraft.src.SpawnerAnimals;
import net.skidcode.gh.maybeaclient.events.Event;
import net.skidcode.gh.maybeaclient.events.EventListener;
import net.skidcode.gh.maybeaclient.events.EventRegistry;
import net.skidcode.gh.maybeaclient.events.impl.EventWorldRenderPreFog;
import net.skidcode.gh.maybeaclient.hacks.category.Category;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingColor;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingInteger;
import net.skidcode.gh.maybeaclient.utils.BlockPosHashSet;
import net.skidcode.gh.maybeaclient.utils.ChunkPos;
import net.skidcode.gh.maybeaclient.utils.RenderBuffer;

public class UnsafeLightLevelsHack extends Hack implements EventListener{
	public static UnsafeLightLevelsHack instance;
	
	public SettingInteger radius = new SettingInteger(this, "RadiusChunks", 4, 0, 8);
	public SettingColor color = new SettingColor(this, "Color", 255, 255, 0);
	public static HashMap<ChunkPos, RenderBuffer> renderBuffers = new HashMap<>();
	
	public UnsafeLightLevelsHack() {
		super("UnsafeLightLevels", "Shows places where mobs can spawn", Keyboard.KEY_NONE, Category.RENDER);
		instance = this;
		this.addSetting(this.color);
		this.addSetting(this.radius);
		EventRegistry.registerListener(EventWorldRenderPreFog.class, this);
	}

	public void checkBlock(int id, int x, int y, int z) {
		if(id != 0 && this.shouldRender(x, y, z)) {
			this.setRequireRehash(x, y, z, true);
		}else {
			this.setRequireRehash(x, y, z, false);
		}
	}
	
	@Override
	public void toggle() {
		super.toggle();
		renderBuffers.clear();
		mc.entityRenderer.updateRenderer();
        mc.theWorld.markBlocksDirty((int)mc.thePlayer.posX - 256, 0, (int)mc.thePlayer.posZ - 256, (int)mc.thePlayer.posX + 256, 127, (int)mc.thePlayer.posZ + 256);
	}
	
	public void setRequireRehash(int x, int y, int z, boolean b) {
		ChunkPos cp = new ChunkPos(x>>4, z>>4);
		RenderBuffer rb = renderBuffers.get(cp);
		if(rb == null) renderBuffers.put(cp, rb = new RenderBuffer(x, z));
		rb.recache = true;
		if(b) rb.blocks.add(x, y+1, z);
		else rb.blocks.remove(x, y+1, z);
	}
	
	public boolean shouldRender(int x, int y, int z) {
		if(SpawnerAnimals.canCreatureTypeSpawnAtLocation(EnumCreatureType.monster, mc.theWorld, x, y+1, z)) {
			if(true) {
				int var4 = mc.theWorld.getBlockLightValue(x, y+1, z);
				if (mc.theWorld.func_27160_B()) {
					int var5 = mc.theWorld.skylightSubtracted;
					mc.theWorld.skylightSubtracted = 10;
					var4 = mc.theWorld.getBlockLightValue(x, y+1, z);
					mc.theWorld.skylightSubtracted = var5;
				}
				return var4 <= 7;
			}
		}
		return false;
	}
	
	
	@Override
	public void handleEvent(Event event) {
		if(event instanceof EventWorldRenderPreFog) {
			int minX = (MathHelper.floor_double(mc.thePlayer.posX)>>4) - this.radius.value;
			int minZ = (MathHelper.floor_double(mc.thePlayer.posZ)>>4) - this.radius.value;
			int maxX = (MathHelper.floor_double(mc.thePlayer.posX)>>4) + this.radius.value;
			int maxZ = (MathHelper.floor_double(mc.thePlayer.posZ)>>4) + this.radius.value;
			
			GL11.glLineWidth(2);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glColor3f(this.color.red / 255f, this.color.green / 255f, this.color.blue / 255f);
			
			GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
			for(RenderBuffer rb : renderBuffers.values()) {
				if(rb.chunkX() < minX || rb.chunkZ() < minZ || rb.chunkX() > maxX || rb.chunkZ() > maxZ) continue;
				GL11.glPushMatrix();
				GL11.glTranslated(rb.startX-RenderManager.renderPosX, -RenderManager.renderPosY, rb.startZ-RenderManager.renderPosZ);
				if(rb.recache) {
					rb.beginRecache();
					for(int i = 0; i < rb.blocks.slots.length; ++i) {
						BlockPosHashSet.Element slot = rb.blocks.slots[i];
						BlockPosHashSet.Element el, prev;
						for(el = slot; el != null; el = prev)
						{
							double y = el.yPos+0.01;
							rb.checkResize(3*4);
							prev = el.prev;
							rb.vertex3(el.xPos+0.01, y, el.zPos+0.01);
							rb.vertex3(el.xPos+1-0.01, y, el.zPos+1-0.01);
							
							rb.vertex3(el.xPos+0.01, y, el.zPos+1-0.01);
							rb.vertex3(el.xPos+1-0.01, y, el.zPos+0.01);
						}
					}
					rb.recache = false;
				}
				rb.draw();
				GL11.glPopMatrix();
			}
			
			
			
			GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
		}
	}	
}
