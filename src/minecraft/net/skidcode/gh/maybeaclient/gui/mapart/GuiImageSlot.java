package net.skidcode.gh.maybeaclient.gui.mapart;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.src.GuiSlot;
import net.minecraft.src.Tessellator;
import net.skidcode.gh.maybeaclient.Client;
import net.skidcode.gh.maybeaclient.utils.ChatColor;

public class GuiImageSlot extends GuiSlot{

	public GuiImageSlot(GuiMapArtSelectImage gui) {
		super(Client.mc, gui.width, gui.height, 32, gui.height - 55, 36);
	}

	@Override
	protected int getSize() {
		return Client.maparts.size();
	}

	@Override
	protected void elementClicked(int var1, boolean var2) {
		File f = Client.maparts.get(var1);
		if(f == null) {
			GuiMapArtCreator.selectedImage = null;
			GuiMapArtCreator.selectedImageF = null;
		}else {
			ImageInputStream in = null;
			int width = 0, height = 0;
			try {
				in = ImageIO.createImageInputStream(f);
				Iterator<ImageReader> readers = ImageIO.getImageReaders(in);
				if(readers.hasNext()) {
					ImageReader reader = readers.next();
					reader.setInput(in);
					width = reader.getWidth(0);
					height = reader.getHeight(0);
				}
				
				if(width == 128 && height == 128) {
					GuiMapArtCreator.selectedImage = ImageIO.read(f);
					GuiMapArtCreator.selectedImageF = f;
				}else {
					GuiMapArtCreator.selectedImage = null;
					GuiMapArtCreator.selectedImageF = null;
				}
			}catch (IOException e) {
				e.printStackTrace();
			}finally {
				try {
					if(in != null) in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	protected boolean isSelected(int var1) {
		File f = Client.maparts.get(var1);
		return GuiMapArtCreator.selectedImageF != null && f != null && GuiMapArtCreator.selectedImageF.getName().equals(f.getName());
	}

	@Override
	protected void drawBackground() {
		
	}

	@Override
	protected void drawSlot(int id, int x, int y, int h, Tessellator tess) {
		File f = Client.maparts.get(id);
		int maxx = this.width / 2 + 110;
		if(f == null) {
			String s = "No maparts found.";
			Client.mc.fontRenderer.drawString(ChatColor.LIGHTRED+s, x + ((maxx-x) - Client.mc.fontRenderer.getStringWidth(s))/2, y, 0xffffff);
			s = "(Mapart must be a 128x128 PNG image)";
			Client.mc.fontRenderer.drawString(ChatColor.DARKGRAY+s, x + ((maxx-x) - Client.mc.fontRenderer.getStringWidth(s))/2, y+12, 0xffffff);
			return;
		}
		
		boolean formatCorrect = false;
		int width = 0, height = 0;
		int txt = 0;
		ImageInputStream in = null;
		try {
			in = ImageIO.createImageInputStream(f);
			Iterator<ImageReader> readers = ImageIO.getImageReaders(in);
			if(readers.hasNext()) {
				ImageReader reader = readers.next();
				reader.setInput(in);
				width = reader.getWidth(0);
				height = reader.getHeight(0);
			}
			
			formatCorrect = width == 128 && height == 128;
			if(formatCorrect) {
				txt = Client.mc.renderEngine.allocateAndSetupTexture(ImageIO.read(f));
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, txt);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			try {
				if(in != null) in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if(formatCorrect) {
			tess.startDrawingQuads();
			tess.addVertexWithUV(x, y, 0, 0, 0);
			tess.addVertexWithUV(x, y+32, 0, 0, 1);
			tess.addVertexWithUV(x+32, y+32, 0, 1, 1);
			tess.addVertexWithUV(x+32, y, 0, 1, 0);
			tess.draw();
			Client.mc.renderEngine.deleteTexture(txt);
		}else {
			Client.mc.fontRenderer.drawString(ChatColor.LIGHTRED+"Must be 128x128, got "+width+"x"+height, x + 32 + 2, y+12, 0xffffff);
		}
		
		
		Client.mc.fontRenderer.drawString(f.getName(), x + 32 + 2, y, 0xffffff);
		
		
	}

}
