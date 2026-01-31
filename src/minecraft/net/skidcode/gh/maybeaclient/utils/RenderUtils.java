package net.skidcode.gh.maybeaclient.utils;

import org.lwjgl.opengl.GL11;

import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.FontRenderer;
import net.minecraft.src.MathHelper;
import net.minecraft.src.RenderManager;
import net.minecraft.src.Tessellator;
import net.skidcode.gh.maybeaclient.Client;

public class RenderUtils {
	public static void drawOutlinedBlockBB(double x, double y, double z, int w, int h, int d) {
		Tessellator tessellator = Tessellator.instance;
		
        tessellator.startDrawing(GL11.GL_LINE_STRIP);
        tessellator.addVertex(x, y, z);
        tessellator.addVertex(x + w, y, z);
        tessellator.addVertex(x + w, y, z + d);
        tessellator.addVertex(x, y, z + d);
        tessellator.addVertex(x, y, z);
        
        tessellator.addVertex(x, y + h, z);
        tessellator.addVertex(x + w, y + h, z);
        tessellator.addVertex(x + w, y + h, z + d);
        tessellator.addVertex(x, y + h, z + d);
        tessellator.addVertex(x, y + h, z);
        
        tessellator.draw();
        
        tessellator.startDrawing(GL11.GL_LINES);
        tessellator.addVertex(x, y, z);
        tessellator.addVertex(x, y + h, z);
        tessellator.addVertex(x + w, y, z);
        tessellator.addVertex(x + w, y + h, z);
        tessellator.addVertex(x + w, y, z + d);
        tessellator.addVertex(x + w, y + h, z + d);
        tessellator.addVertex(x, y, z + d);
        tessellator.addVertex(x, y + h, z + d);
        tessellator.draw();
	}
	public static void drawOutlinedBlockBB(double x, double y, double z) {
		drawOutlinedBlockBB(x, y, z, 1, 1, 1);
	}
	public static void drawOutlinedBB(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawing(3);
        tessellator.addVertex(minX, minY, minZ);
        tessellator.addVertex(maxX, minY, minZ);
        tessellator.addVertex(maxX, minY, maxZ);
        tessellator.addVertex(minX, minY, maxZ);
        tessellator.addVertex(minX, minY, minZ);
        tessellator.draw();
        tessellator.startDrawing(3);
        tessellator.addVertex(minX, maxY, minZ);
        tessellator.addVertex(maxX, maxY, minZ);
        tessellator.addVertex(maxX, maxY, maxZ);
        tessellator.addVertex(minX, maxY, maxZ);
        tessellator.addVertex(minX, maxY, minZ);
        tessellator.draw();
        tessellator.startDrawing(1);
        tessellator.addVertex(minX, minY, minZ);
        tessellator.addVertex(minX, maxY, minZ);
        tessellator.addVertex(maxX, minY, minZ);
        tessellator.addVertex(maxX, maxY, minZ);
        tessellator.addVertex(maxX, minY, maxZ);
        tessellator.addVertex(maxX, maxY, maxZ);
        tessellator.addVertex(minX, minY, maxZ);
        tessellator.addVertex(minX, maxY, maxZ);
        tessellator.draw();
	}
	public static void drawOutlinedBB(AxisAlignedBB bb) {
		drawOutlinedBB(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ);
	}
	
	public static void renderChunk(int x, int z, float r, float g, float b, boolean depthTest) {
		x *= 16;
		z *= 16;
		GL11.glPushMatrix();
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		if(!depthTest) GL11.glDisable(GL11.GL_DEPTH_TEST);
		else GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDepthMask(false);
		GL11.glColor4f(r, g, b, 1);
		Tessellator.instance.setTranslationD(-RenderManager.renderPosX, -RenderManager.renderPosY, -RenderManager.renderPosZ);
		Tessellator.instance.startDrawing(GL11.GL_LINES);
		
		
		int ppos = MathHelper.floor_double(Client.mc.thePlayer.posY);
		if(ppos > 128) ppos = 128;
		if(ppos < 0) ppos = 0;
		for(int i = 0; i <= 16; i += 1) {
			if(ppos < 128) {
				Tessellator.instance.addVertex(x+i, 128, z);
				Tessellator.instance.addVertex(x+i, ppos, z);
				Tessellator.instance.addVertex(x+i, 128, z+16);
				Tessellator.instance.addVertex(x+i, ppos, z+16);
				Tessellator.instance.addVertex(x, 128, z+i);
				Tessellator.instance.addVertex(x, ppos, z+i);
				Tessellator.instance.addVertex(x+16, 128, z+i);
				Tessellator.instance.addVertex(x+16, ppos, z+i);
			}
			
			if(ppos > 0) {
				Tessellator.instance.addVertex(x+i, ppos, z);
				Tessellator.instance.addVertex(x+i, 0, z);
				Tessellator.instance.addVertex(x+i, ppos, z+16);
				Tessellator.instance.addVertex(x+i, 0, z+16);
				Tessellator.instance.addVertex(x, ppos, z+i);
				Tessellator.instance.addVertex(x, 0, z+i);
				Tessellator.instance.addVertex(x+16, ppos, z+i);
				Tessellator.instance.addVertex(x+16, 0, z+i);
			}
		}
		
		for(int i = 0; i <= 128; i += 1) {
			Tessellator.instance.addVertex(x, i, z);
			Tessellator.instance.addVertex(x, i, z+16);
			
			Tessellator.instance.addVertex(x, i, z+16);
			Tessellator.instance.addVertex(x+16, i, z+16);
			
			Tessellator.instance.addVertex(x+16, i, z+16);
			Tessellator.instance.addVertex(x+16, i, z);
			
			Tessellator.instance.addVertex(x+16, i, z);
			Tessellator.instance.addVertex(x, i, z);
		}
		
		Tessellator.instance.draw();
		Tessellator.instance.setTranslationD(0, 0, 0);
		GL11.glPopAttrib();
		GL11.glPopMatrix();
	}

	public static void glColor(int col) {
		glColor(col, 0xff);
	}
	public static void glColor(int col, int opacity) {
		float a = opacity / 255f;
		float r = ((col & 0x00ff0000) >>> 16) / 255f;
		float g = ((col & 0x0000ff00) >>> 8) / 255f;
		float b = ((col & 0x000000ff)) / 255f;
		GL11.glColor4f(r, g, b, a);
	}
	
	public static void drawString(String s, double d, double d1, double d2)
    {
        double f = Math.sqrt(
        	(d - Client.mc.renderViewEntity.posX)*(d - Client.mc.renderViewEntity.posX) + 
        	(d1 - Client.mc.renderViewEntity.posY)*(d1 - Client.mc.renderViewEntity.posY) + 
        	(d2 - Client.mc.renderViewEntity.posZ)*(d2 - Client.mc.renderViewEntity.posZ)
        );
        FontRenderer fontrenderer = Client.mc.fontRenderer;
        float f1 = 1.6F;
        float f2 = 0.01666667F * f1;
        
			float scale = 1;
			if(f > 200) f = 200; 
			f2 *= scale * 0.1f * f;
			
			if(0.016666668F * f1 > f2) f2 = 0.016666668F * f1;
        
        GL11.glPushMatrix();
        GL11.glTranslated(d - RenderManager.renderPosX, d1 - RenderManager.renderPosY + 2.3D, d2 - RenderManager.renderPosZ);
        GL11.glNormal3f(0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-RenderManager.instance.playerViewY, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(RenderManager.instance.playerViewX, 1.0F, 0.0F, 0.0F);
        GL11.glScalef(-f2, -f2, f2);
        GL11.glDisable(2896 /*GL_LIGHTING*/);
        GL11.glDepthMask(false);
        GL11.glDisable(2929 /*GL_DEPTH_TEST*/);
        GL11.glEnable(3042 /*GL_BLEND*/);
        GL11.glBlendFunc(770, 771);
        Tessellator tessellator = Tessellator.instance;
        byte byte0 = 0;
        if(s.equals("deadmau5"))
        {
            byte0 = -10;
        }
        GL11.glDisable(3553 /*GL_TEXTURE_2D*/);
        tessellator.startDrawingQuads();
        int j = fontrenderer.getStringWidth(s) / 2;
        tessellator.setColorRGBA_F(0.0F, 0.0F, 0.0F, 0.25F);
        tessellator.addVertex(-j - 1, -1 + byte0, 0.0D);
        tessellator.addVertex(-j - 1, 8 + byte0, 0.0D);
        tessellator.addVertex(j + 1, 8 + byte0, 0.0D);
        tessellator.addVertex(j + 1, -1 + byte0, 0.0D);
        tessellator.draw();
        GL11.glEnable(3553 /*GL_TEXTURE_2D*/);
        fontrenderer.drawString(s, -fontrenderer.getStringWidth(s) / 2, byte0, 0x20ffffff);
        GL11.glDepthMask(true);
        fontrenderer.drawString(s, -fontrenderer.getStringWidth(s) / 2, byte0, -1);
        GL11.glEnable(2896 /*GL_LIGHTING*/);
        GL11.glDisable(3042 /*GL_BLEND*/);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(2929 /*GL_DEPTH_TEST*/);
        GL11.glPopMatrix();
    }
	
}
