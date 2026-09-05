package net.skidcode.gh.maybeaclient.gui.click;

import net.minecraft.client.Minecraft;
import net.skidcode.gh.maybeaclient.hacks.ImageViewerHack;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ImageViewerTab extends Tab{
    public static final Path PATH = Paths.get(Minecraft.getMinecraftDir() + "/MaybeAClient/imageviewer");
    public static int textureId = -1;
    public static int[] frameTextureIds = null;
    public static int[] frameDelaysMs = null;
    public static int currentFrame = 0;
    public static long lastFrameTime = 0L;
    public static boolean isAnimated = false;

    public ImageViewerTab() {
        super("ImageViewer");
        this.canMinimize = false;
        this.svdWidth = 75;
        this.svdHeight = 75;
    }

    public static void resetImage() {
        ImageViewerTab.textureId = -1;
        try {
            Files.deleteIfExists(ImageViewerTab.PATH);
        } catch (Exception ignored) { }
    }

    public static int uploadTexture(BufferedImage image) {
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
        int id = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0,
                GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
        return id;
    }

    public static void loadTexture(File file) throws IOException {
        try {
            ImageInputStream stream = ImageIO.createImageInputStream(file);
            Iterator<ImageReader> readers = ImageIO.getImageReaders(stream);
            if (!readers.hasNext()) {
                stream.close();
                throw new IOException();
            }
            ImageReader reader = readers.next();
            reader.setInput(stream);
            boolean isGif = reader.getFormatName().equalsIgnoreCase("gif");
            int frameCount = isGif ? reader.getNumImages(true) : 1;
            if (isGif && frameCount > 1) {
                List<Integer> ids = new ArrayList<>();
                List<Integer> delays = new ArrayList<>();
                for (int i = 0; i < frameCount; i++) {
                    BufferedImage frame = reader.read(i);
                    ids.add(uploadTexture(frame));
                    int delay = 100;
                    try {
                        IIOMetadata metadata = reader.getImageMetadata(i);
                        String formatName = metadata.getNativeMetadataFormatName();
                        IIOMetadataNode root = (IIOMetadataNode) metadata.getAsTree(formatName);
                        for (int j = 0; j < root.getLength(); j++) {
                            IIOMetadataNode node = (IIOMetadataNode) root.item(j);
                            if (node.getNodeName().equals("GraphicControlExtension")) {
                                String delayTime = node.getAttribute("delayTime");
                                if (delayTime != null && !delayTime.isEmpty()) {
                                    delay = Integer.parseInt(delayTime) * 10;
                                }
                            }
                        }
                    } catch (Exception ignored) {
                    }
                    if (delay <= 0) delay = 100;
                    delays.add(delay);
                }
                reader.dispose();
                stream.close();
                frameTextureIds = new int[ids.size()];
                frameDelaysMs = new int[ids.size()];
                for (int i = 0; i < ids.size(); i++) {
                    frameTextureIds[i] = ids.get(i);
                    frameDelaysMs[i] = delays.get(i);
                }
                currentFrame = 0;
                lastFrameTime = System.currentTimeMillis();
                isAnimated = true;
                textureId = frameTextureIds[0];
            } else {
                BufferedImage frame = reader.read(0);
                reader.dispose();
                stream.close();
                if (frame == null) throw new IOException();
                frameTextureIds = null;
                frameDelaysMs = null;
                isAnimated = false;
                textureId = uploadTexture(frame);
            }
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
        int boundTexture = textureId;
        if (isAnimated && frameTextureIds != null && frameTextureIds.length > 0) {
            long now = System.currentTimeMillis();
            long elapsed = now - lastFrameTime;
            int delay = frameDelaysMs[currentFrame];
            if (elapsed >= delay) {
                currentFrame = (currentFrame + 1) % frameTextureIds.length;
                lastFrameTime = now;
            }
            boundTexture = frameTextureIds[currentFrame];
        }
        GL11.glColor3f(1.0f, 1.0f, 1.0f);
        GL11.glPushMatrix();
        GL11.glTranslatef(startX, startY, 0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, boundTexture);
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
