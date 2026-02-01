package net.skidcode.gh.maybeaclient.gui.click;

import org.lwjgl.opengl.GL11;

import net.minecraft.src.EntitySlime;
import net.minecraft.src.MathHelper;
import net.minecraft.src.ScaledResolution;
import net.minecraft.src.Tessellator;
import net.skidcode.gh.maybeaclient.Client;
import net.skidcode.gh.maybeaclient.hacks.ClickGUIHack;
import net.skidcode.gh.maybeaclient.hacks.RadarHack;
import net.skidcode.gh.maybeaclient.hacks.SlimeChunkRadarHack;
import net.skidcode.gh.maybeaclient.hacks.ClickGUIHack.Theme;
import net.skidcode.gh.maybeaclient.utils.GUIUtils;
import net.skidcode.gh.maybeaclient.utils.RenderUtils;
import net.skidcode.gh.maybeaclient.utils.WorldUtils;

public class SlimeRadarTab extends Tab {

	public SlimeRadarTab() {
		super("Slimechunks Radar", 0, 24);
		this.xDefPos = this.startX = 160;
		this.yDefPos = this.startY = 24 + 14*7;
		this.isHUD = true;
	}
	
	public void preRender() {
		Theme theme = ClickGUIHack.theme();
		int width = Client.mc.fontRenderer.getStringWidth(this.getTabName()) + theme.titleXadd;
		if(width < 64) width = 64;
		int height = width + this.getYOffset();
		this.endX = this.startX + width;
		this.endY = this.startY + height;
		super.preRender();
	}
	
	public void renderIngame() {
		if(SlimeChunkRadarHack.instance.status) super.renderIngame();
	}
	
	public void render() {
		super.render();
		
		if(this.minimized.getValue()) return;
		
		SlimeChunkRadarHack hck = SlimeChunkRadarHack.instance;
		
		Theme theme = ClickGUIHack.theme();
		final int scale = 8;
		final int chunkScale = 16/scale;
		
		int xStart = (int)this.startX;
		int yStart = (int)this.startY + this.getYOffset();
		int xEnd = (int)this.endX;
		int yEnd = (int)this.endY;
		
		
		
		
		Tab.renderFrame(this, xStart, yStart, xEnd, yEnd);
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glBlendFunc(770, 771);
		ScaledResolution scaledResolution = new ScaledResolution(Client.mc.gameSettings, Client.mc.displayWidth, Client.mc.displayHeight);
		double scaledpx = 1d/scaledResolution.scaleFactor;
		
		GUIUtils.enableScissorTest();
		GUIUtils.scissorStart(xStart, yStart, xEnd - (theme == Theme.UWARE ? scaledpx : 0), yEnd);
		
		int yCenter = yStart + (yEnd - yStart) / 2;
		int xCenter = xStart + (xEnd - xStart) / 2;
		
		double plX = Client.mc.thePlayer.posX;
		double plZ = Client.mc.thePlayer.posZ;
		int plChunkX = MathHelper.floor_double(plX / 16);
		int plChunkZ = MathHelper.floor_double(plZ / 16);
		
		double plOffX = plX % 16;
		double plOffZ = plZ % 16;
		double mapOffX = -plOffX/chunkScale;
		double mapOffZ = plOffZ/chunkScale - scale;
		
		if(mapOffX > 0) mapOffX -= scale;
		if(mapOffZ < -scale) mapOffZ += scale;
		
		double playerMapX = xCenter+mapOffZ;
		double playerMapY = yCenter+mapOffX;
		
		int chunkOffsetXFromPlayer = (int)Math.ceil((playerMapX - xStart)/scale);
		int chunkOffsetYFromPlayer = (int)Math.ceil((playerMapY - yStart)/scale);
		
		double xBeginRendering = playerMapX - chunkOffsetXFromPlayer*scale;
		double yBeginRendering = playerMapY - chunkOffsetYFromPlayer*scale;
		
		int cz = 0;
		int cx = 0;
		for(double x = xBeginRendering; x < xEnd; x += scale) {
			double offMapX = (playerMapX - x) / scale;
			offMapX = ((int)(offMapX*100))/100d;
			cz = plChunkZ + MathHelper.floor_double(offMapX);
			
			for(double y = yBeginRendering; y < yEnd; y += scale) {
				
				double offMapY = (playerMapY - y) / scale;
				
				
				offMapY = ((int)(offMapY*100))/100d;
				
				cx = plChunkX - MathHelper.floor_double(offMapY);
				
				if(WorldUtils.isSlimeChunk(cx, cz)) {
					Tab.renderFrameBackGround(x, y, x+scale, y+scale, hck.slimeChunkColor.red/255f, hck.slimeChunkColor.green/255f, hck.slimeChunkColor.blue/255f, 0.5f);
				}
			}
		}
		
		if(hck.showChunkGrid.getValue()) {
			GL11.glColor4f(0, 0, 0, 1);
			GL11.glLineWidth(1);
			Tessellator.instance.startDrawing(GL11.GL_LINES);
			for(double x = xBeginRendering; x < xEnd; x += scale) {
				Tessellator.instance.addVertex(x, yBeginRendering, 0);
				Tessellator.instance.addVertex(x, yEnd, 0);
			}
			
			for(double y = yBeginRendering; y < yEnd; y += scale) {
				Tessellator.instance.addVertex(xBeginRendering, y, 0);
				Tessellator.instance.addVertex(xEnd, y, 0);
			}
			
			Tessellator.instance.draw();
		}
		
		//must be on top
		GL11.glEnable(GL11.GL_POLYGON_SMOOTH);
		GL11.glHint(GL11.GL_POLYGON_SMOOTH_HINT, GL11.GL_NICEST);
		GL11.glPushMatrix();
		GL11.glTranslatef(xCenter, yCenter, 0);
		GL11.glRotatef(Client.mc.thePlayer.rotationYaw + 90, 0, 0, 1);
		GL11.glColor4f(hck.playerColor.red/255f, hck.playerColor.green/255f, hck.playerColor.blue/255f, 1);
		Tessellator.instance.startDrawing(GL11.GL_TRIANGLES);
		Tessellator.instance.addVertex(-2, -3, 0);
		Tessellator.instance.addVertex(0, 3, 0);
		Tessellator.instance.addVertex(2, -3, 0);
		Tessellator.instance.draw();
		GL11.glPopMatrix();
		
		if(theme == Theme.CLIFF) {
			Tab.renderFrameOutlines(xStart, yStart, xEnd, yEnd);
		}
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_POLYGON_SMOOTH);
		GL11.glHint(GL11.GL_POLYGON_SMOOTH_HINT, GL11.GL_FASTEST);
		GUIUtils.scissorEnd();
		GUIUtils.disableScissorTest();
		Tab.renderFrameTop(this, xStart, yStart, xEnd, yEnd);
		
	}
}
