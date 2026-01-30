package net.skidcode.gh.maybeaclient.gui.click;

import net.minecraft.client.Minecraft;
import net.skidcode.gh.maybeaclient.hacks.ImageViewerHack;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ImageViewerTab extends Tab{
    public static final Path PATH = Paths.get(Minecraft.getMinecraftDir() + "/MaybeAClient/imageviewer");
    public static int textureId = -1;

    public ImageViewerTab() {
        super("ImageViewer");
        this.canMinimize = false;
        this.svdWidth = 75;
        this.svdHeight = 75;
    }

    public static void loadTexture(File file) throws IOException { //kareliq skid
        try {
            BufferedImage image = ImageIO.read(file);
            if (image == null) throw new IOException();

            int width = image.getWidth();
            int height = image.getHeight();
            int[] pixels = new int[width * height];
            image.getRGB(0, 0, width, height, pixels, 0, width);

            ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * 4);
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int pixel = pixels[y * width + x];
                    buffer.put((byte) ((pixel >> 16) & 0xFF));
                    buffer.put((byte) ((pixel >> 8) & 0xFF));
                    buffer.put((byte) (pixel & 0xFF));
                    buffer.put((byte) ((pixel >> 24) & 0xFF));
                }
            }
            buffer.flip();

            textureId = GL11.glGenTextures();
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0,
                    GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onSelect(int click, int x, int y) {
        if(click == 0) {
            this.selectedMouseX = x;
            this.selectedMouseY = y;
            this.dragging = true;
            return true;
        }
        return false;
    }

    @Override
    public void preRender() {
        this.endX = startX + ImageViewerHack.instance.width.value;
        this.endY = startY + ImageViewerHack.instance.height.value;
    }

    @Override
    public void render() {
        if (!ImageViewerHack.instance.status) return;
        if (textureId == -1 && Files.exists(PATH))
            try {
                loadTexture(new File(PATH.toAbsolutePath().toString()));
            } catch (IOException ignored) {
            }
        GL11.glColor3f(1.0f, 1.0f, 1.0f);
        GL11.glPushMatrix();
        GL11.glTranslatef(startX, startY, 0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(0, 1);
        GL11.glVertex2f(0, ImageViewerHack.instance.height.value);
        GL11.glTexCoord2f(1, 1);
        GL11.glVertex2f(ImageViewerHack.instance.width.value, ImageViewerHack.instance.height.value);
        GL11.glTexCoord2f(1, 0);
        GL11.glVertex2f(ImageViewerHack.instance.width.value, 0);
        GL11.glTexCoord2f(0, 0);
        GL11.glVertex2f(0, 0);
        GL11.glEnd();
        GL11.glPopMatrix();
    }

    @Override
    public void renderIngame() {
        if(ImageViewerHack.instance.status) super.renderIngame();
    }
}
