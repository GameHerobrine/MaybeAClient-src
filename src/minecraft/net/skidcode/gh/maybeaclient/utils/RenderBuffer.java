package net.skidcode.gh.maybeaclient.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import org.lwjgl.opengl.GL11;

public class RenderBuffer {
	public int cacheBufSize = 0xfffff;
	public FloatBuffer cacheBuf = createBuffer();
	public static final int bufelementsize = 4; 
	public int cacheBufElems = 0;
	public boolean recache = false;
	public int startX, startZ;
	public BlockPosHashSet blocks = new BlockPosHashSet();
	
	public RenderBuffer(int x, int z) {
		this.startX = x;
		this.startZ = z;
	}

	public int chunkX() {
		return this.startX>>4;
	}
	public int chunkZ() {
		return this.startZ>>4;
	}
	
	public FloatBuffer createBuffer() {
		return ByteBuffer.allocateDirect(cacheBufSize).order(ByteOrder.nativeOrder()).asFloatBuffer();
	}
	
	public void beginRecache() {
		cacheBuf.position(0);
		cacheBufElems = 0;
		scale = 0;
	}
	
	public void checkResize(int vertexes) {
		if((cacheBufElems+4)*3*bufelementsize > cacheBufSize) {
			cacheBufSize += 0xffff;
			FloatBuffer buf = createBuffer();
			cacheBuf.limit((cacheBufElems)*3);
			cacheBuf.position(0);
			buf.put(cacheBuf);
			cacheBuf = buf;
		}
	}
	
	public double scale;
	public void vertex3(double x, double y, double z) {
		cacheBuf.put((float)(x-startX)).put((float)y).put((float)(z-startZ));
		
		cacheBufElems += 1;
	}

	public void draw() {
		if(recache) return;
		cacheBuf.position(0);
		
		GL11.glVertexPointer(3, 0, cacheBuf);
		GL11.glDrawArrays(GL11.GL_LINES, 0, cacheBufElems);
	}

	public void scale(double d) {
		this.scale = d;
	}
}
