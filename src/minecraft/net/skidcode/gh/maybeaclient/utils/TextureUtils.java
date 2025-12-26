package net.skidcode.gh.maybeaclient.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;

public class TextureUtils {
	
	public static int getAlpha(BufferedImage tex, int x, int y) {
		if(x >= tex.getWidth() || x < 0) return 0;
		if(y >= tex.getHeight() || y < 0) return 0;
		
		return (tex.getRGB(x, y) >> 24) & 0xff;
	}
	
	public static void line(int side, int xStart, int yStart, int xEnd, int yEnd, double px) {
		if(side == 2) {
			xStart += 1;
			xEnd += 1;
		}
		if(side == 8) {
			yStart += 1;
			yEnd += 1;
		}
		double bX = -0.5 + (xStart * px);
		double bY = 0.75 - (yStart * px);
		double eX = -0.5 + (xEnd * px);
		double eY = 0.75 - (yEnd * px);

		GL11.glVertex3d(bX, bY, 0);
		GL11.glVertex3d(eX, eY, 0);
	}

	public static byte[] getOutliningSides(BufferedImage tex) {
		byte[] arr = new byte[tex.getWidth()*tex.getHeight()];
		
		int m = (int) tex.getWidth() / 16;
		int n = m - 1;
		
		for(int tx = 0; tx < tex.getWidth(); ++tx) {
			for(int ty = 0; ty < tex.getHeight(); ++ty) {
				int a = getAlpha(tex, tx, ty);
				
				if(a != 0) {
					int xn = getAlpha(tex, tx-1, ty);
					int xp = getAlpha(tex, tx+1, ty);
					int yn = getAlpha(tex, tx, ty-1);
					int yp = getAlpha(tex, tx, ty+1);
					
					byte bm = 0;
					if(xn == 0 || tx % m == 0) bm |= 1; //needs left outline
					if(xp == 0 || tx % m == n) bm |= 2; //needs right outline
					if(yn == 0 || ty % m == 0) bm |= 4; //needs top outline
					if(yp == 0 || ty % m == n) bm |= 8; //needs top outline
					
					arr[tx*tex.getWidth() + ty] = bm;
				}
			}
		}
		
		
		return arr;
	}
}
