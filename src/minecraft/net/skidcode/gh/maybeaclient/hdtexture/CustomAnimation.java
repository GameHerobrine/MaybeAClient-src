package net.skidcode.gh.maybeaclient.hdtexture;

import net.minecraft.src.Block;
import net.minecraft.src.TextureFX;
import net.skidcode.gh.maybeaclient.Client;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import javax.imageio.ImageIO;

public class CustomAnimation extends TextureFX {
	private Delegate delegate;
	private static Random rand = new Random();
	
	public CustomAnimation(int iconIndex, int tileImage, int tileSize, String name, int minScrollDelay, int maxScrollDelay) {
		super(iconIndex);

		this.iconIndex = iconIndex;
		this.tileImage = tileImage;
		this.tileSize = tileSize;
		
		BufferedImage custom = null;
		String imageName = (tileImage == 0 ? "/terrain.png" : "/gui/items.png");
		try {
			
			BufferedImage bi = Client.getResource(imageName);
			this.textureRes = bi.getWidth() / 16;
			
			String customSrc = "/custom_" + name + ".png";
			custom = Client.getRescaledResource(customSrc, this.textureRes);
			if (custom != null) {
				this.textureRes = custom.getWidth();
				imageName = customSrc;
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		System.out.printf("new CustomAnimation %s, src=%s, buffer size=0x%x, tile=%d res=%d\n",
			name, imageName, imageData.length, this.iconIndex, this.textureRes
		);

		this.imageData = new byte[textureRes*textureRes*4];
		if (custom == null) {
			delegate = new Tile(imageName, iconIndex, minScrollDelay, maxScrollDelay);
		} else {
			delegate = new Strip(custom);
		}
	}

	static private void ARGBtoRGBA(int[] src, byte[] dest) {
		for (int i = 0; i < src.length; ++i) {
			int v = src[i];
			dest[(i * 4) + 3] = (byte) ((v >> 24) & 0xff);
			dest[(i * 4) + 0] = (byte) ((v >> 16) & 0xff);
			dest[(i * 4) + 1] = (byte) ((v >> 8) & 0xff);
			dest[(i * 4) + 2] = (byte) ((v >> 0) & 0xff);
		}
	}

	@Override
	public void onTick() {
		delegate.onTick();
	}

	private interface Delegate {
		public void onTick();
	}

	private class Tile implements Delegate {
		private final int allButOneRow;
		private final int oneRow;
		private final int minScrollDelay;
		private final int maxScrollDelay;
		private final boolean isScrolling;
		private final byte[] temp;

		private int timer;

		Tile(String imageName, int iconIndex, int minScrollDelay, int maxScrollDelay) {
			oneRow = textureRes * 4;
			allButOneRow = (textureRes - 1) * oneRow;
			this.minScrollDelay = minScrollDelay;
			this.maxScrollDelay = maxScrollDelay;
			isScrolling = (this.minScrollDelay >= 0);
			if (isScrolling) {
				temp = new byte[oneRow];
			} else {
				temp = null;
			}

			BufferedImage tiles;
			try {
				tiles = Client.getResource(imageName);
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}

			int tileX = (iconIndex % 16) * textureRes;
			int tileY = (iconIndex / 16) * textureRes;
			int imageBuf[] = new int[textureRes*textureRes];
			tiles.getRGB(tileX, tileY, textureRes, textureRes, imageBuf, 0, textureRes);
			ARGBtoRGBA(imageBuf, imageData);
		}

		public void onTick() {
			if (isScrolling && (maxScrollDelay <= 0 || --timer <= 0)) {
				if (maxScrollDelay > 0) {
					timer = rand.nextInt(maxScrollDelay - minScrollDelay + 1) + minScrollDelay;
				}
				System.arraycopy(imageData, allButOneRow, temp, 0, oneRow);
				System.arraycopy(imageData, 0, imageData, oneRow, allButOneRow);
				System.arraycopy(temp, 0, imageData, 0, oneRow);
			}
		}
	}

	private class Strip implements Delegate {
		private final int oneFrame;
		private final byte[] src;
		private final int numFrames;

		private int currentFrame;

		Strip(BufferedImage custom) {
			oneFrame = textureRes * textureRes * 4;
			numFrames = custom.getHeight() / custom.getWidth();
			int imageBuf[] = null;
			imageBuf = custom.getRGB(0, 0, custom.getWidth(), custom.getHeight(), imageBuf, 0, textureRes);
			src = new byte[imageBuf.length * 4];
			ARGBtoRGBA(imageBuf, src);
		}

		public void onTick() {
			if (++currentFrame >= numFrames) {
				currentFrame = 0;
			}
			System.arraycopy(src, currentFrame * oneFrame, imageData, 0, oneFrame);
		}
	}
}