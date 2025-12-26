package lunatrius.schematica.util;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import net.minecraft.src.GLAllocation;

public class SchematicaRenderBuffer {
	public int capQ = 0, capL = 0;
	public FloatBuffer vBufferLine, vBufferQuad;
	public ByteBuffer cBufferLine, cBufferQuad;
	
	public int blockDisplayList = 0;
	public int sizeQ = 0, sizeL = 0;
	public boolean update = true;
	
	public SchematicaRenderBuffer(boolean dl) {
		this.expandq();
		this.expandl();
		
		if(dl) blockDisplayList = GL11.glGenLists(1);
	}
	
	public void expandq() {
		int oldCap = capQ;
		capQ += 256;
		ByteBuffer cbuf = BufferUtils.createByteBuffer(this.capQ * 4);
		FloatBuffer vbuf = BufferUtils.createFloatBuffer(this.capQ * 3);
		cbuf.position(0);
		vbuf.position(0);
		if(cBufferQuad != null) {
			cBufferQuad.limit(oldCap*4);
			cBufferQuad.position(0);
			cbuf.put(cBufferQuad);
		}
		if(vBufferQuad != null) {
			vBufferQuad.limit(oldCap*3);
			vBufferQuad.position(0);
			vbuf.put(vBufferQuad);
		}
		cBufferQuad = cbuf;
		vBufferQuad = vbuf;
		
		cBufferQuad.position(this.sizeQ*4);
		vBufferQuad.position(this.sizeQ*3);
		cBufferQuad.limit(this.capQ * 4);
		vBufferQuad.limit(this.capQ * 3);
	}
	
	public void expandl() {
		int oldCap = capL;
		capL += 256;
		ByteBuffer cbuf = BufferUtils.createByteBuffer(this.capL * 4);
		FloatBuffer vbuf = BufferUtils.createFloatBuffer(this.capL * 3);
		cbuf.position(0);
		vbuf.position(0);
		if(cBufferLine != null) {
			cBufferLine.limit(oldCap*4);
			cBufferLine.position(0);
			cbuf.put(cBufferLine);
		}
		if(vBufferLine != null) {
			vBufferLine.limit(oldCap*3);
			vBufferLine.position(0);
			vbuf.put(vBufferLine);
		}
		cBufferLine = cbuf;
		vBufferLine = vbuf;
		
		cBufferLine.position(this.sizeL*4);
		vBufferLine.position(this.sizeL*3);
		cBufferLine.limit(this.capL * 4);
		vBufferLine.limit(this.capL * 3);
	}
	
	public void vertexl(float x, float y, float z) {
		if(sizeL+1 > capL) {
			this.expandl();
		}
		
		this.vBufferLine.put(x).put(y).put(z);
		this.cBufferLine.put(lred).put(lgreen).put(lblue).put(lalpha);
		++sizeL;
	}

	public byte lred, lgreen, lblue, lalpha;
	public void colorl(float red, float green, float blue, float alpha) {
		this.lred = (byte)(int)(red*255);
		this.lgreen = (byte)(int)(green*255);
		this.lblue = (byte)(int)(blue*255);
		this.lalpha = (byte)(int)(alpha*255);
	}
	
	public void vertexq(float x, float y, float z) {
		if(sizeQ+1 > capQ) {
			this.expandq();
		}
		
		this.vBufferQuad.put(x).put(y).put(z);
		this.cBufferQuad.put(qred).put(qgreen).put(qblue).put(qalpha);
		++sizeQ;
	}

	public byte qred, qgreen, qblue, qalpha;
	public void colorq(float red, float green, float blue, float alpha) {
		this.qred = (byte)(int)(red*255);
		this.qgreen = (byte)(int)(green*255);
		this.qblue = (byte)(int)(blue*255);
		this.qalpha = (byte)(int)(alpha*255);
	}

	public void clear() {
		sizeQ = 0;
		sizeL = 0;
		
		this.cBufferLine.position(0);
		this.vBufferLine.position(0);
		
		this.cBufferQuad.position(0);
		this.vBufferQuad.position(0);
	}
	
	public void destroy() {
		GL11.glDeleteLists(this.blockDisplayList, 1);
	}

	public void drawBlocks() {
		GL11.glCallList(this.blockDisplayList);
	}

	public void drawHighlight() {
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glLineWidth(1.5f);
		GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
		GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
		
		this.cBufferQuad.position(0);
		this.vBufferQuad.position(0);
		GL11.glColorPointer(4, true, 0, this.cBufferQuad);
		GL11.glVertexPointer(3, 0, this.vBufferQuad);
		GL11.glDrawArrays(GL11.GL_QUADS, 0, this.sizeQ);
		
		this.cBufferLine.position(0);
		this.vBufferLine.position(0);
		GL11.glColorPointer(4, true, 0, this.cBufferLine);
		GL11.glVertexPointer(3, 0, this.vBufferLine);
		GL11.glDrawArrays(GL11.GL_LINES, 0, this.sizeL);
		
		GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);
		GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}
}
