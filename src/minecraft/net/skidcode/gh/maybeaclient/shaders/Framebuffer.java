package net.skidcode.gh.maybeaclient.shaders;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.ARBFramebufferObject;
import org.lwjgl.opengl.GL11;

import net.minecraft.src.Tessellator;
import net.skidcode.gh.maybeaclient.Client;

public class Framebuffer {
	
	public int fb;
	public int color;
	public int depth;
	public Framebuffer() {
		fb = ARBFramebufferObject.glGenFramebuffers();
    	color = GL11.glGenTextures();
    	depth = ARBFramebufferObject.glGenRenderbuffers();
	}
	
	public int width, height;
	public void setup(int width, int height) {
		this.bind();
    	GL11.glBindTexture(GL11.GL_TEXTURE_2D,  color);
    	GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, Client.mc.displayWidth, Client.mc.displayHeight, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer)null);
    	GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
    	GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
    	
    	
    	ARBFramebufferObject.glFramebufferTexture2D(ARBFramebufferObject.GL_DRAW_FRAMEBUFFER, ARBFramebufferObject.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, color, 0);
    	ARBFramebufferObject.glBindRenderbuffer(ARBFramebufferObject.GL_RENDERBUFFER, depth);
    	ARBFramebufferObject.glRenderbufferStorage(ARBFramebufferObject.GL_RENDERBUFFER, GL11.GL_DEPTH_COMPONENT, Client.mc.displayWidth, Client.mc.displayHeight);
    	ARBFramebufferObject.glFramebufferRenderbuffer(ARBFramebufferObject.GL_DRAW_FRAMEBUFFER, ARBFramebufferObject.GL_DEPTH_ATTACHMENT, ARBFramebufferObject.GL_RENDERBUFFER, depth);
    	
    	int i = ARBFramebufferObject.glCheckFramebufferStatus(ARBFramebufferObject.GL_DRAW_FRAMEBUFFER);
    	
    	
    	
    	this.width = width;
    	this.height = height;
    	
    	this.unbind();
	}
	
	public void bind() {
		ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_FRAMEBUFFER, fb);
		//GL11.glViewport(0, 0, width, height);
		GL11.glViewport(0, 0, Client.mc.displayWidth, Client.mc.displayHeight);
	}
	
	public void unbind() {
		ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_FRAMEBUFFER, 0);
		//GL11.glViewport(0, 0, Client.mc.displayWidth, Client.mc.displayHeight);
	}
	
	public void clear() {
		this.bind();
		GL11.glClearColor(0, 0, 0, 0f);
    	GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    	this.unbind();
	}
	
	public void startRendering() {
		//GL11.glEnable(GL11.GL_TEXTURE_2D);
		//GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		this.bind();
	}
	
	public void endRendering() {
		this.unbind();
	}
	
	public void renderOnScreen() {
		GL11.glEnable(GL11.GL_TEXTURE_2D);
    	GL11.glBindTexture(GL11.GL_TEXTURE_2D, color);
        //GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        //GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    	Tessellator.instance.startDrawingQuads();
    	Tessellator.instance.addVertexWithUV(0, 0, 0, 0, 1);
    	Tessellator.instance.addVertexWithUV(0, this.height, 0, 0, 0);
    	Tessellator.instance.addVertexWithUV(this.width, this.height, 0, 1, 0);
    	Tessellator.instance.addVertexWithUV(this.width, 0, 0, 1, 1);
    	Tessellator.instance.draw();
	}
}
