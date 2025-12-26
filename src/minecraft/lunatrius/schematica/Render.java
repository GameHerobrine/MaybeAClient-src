package lunatrius.schematica;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import javax.imageio.ImageIO;
import lunatrius.schematica.util.SchematicaRenderBuffer;
import lunatrius.schematica.util.Vector3f;
import lunatrius.schematica.util.Vector3i;
import net.minecraft.src.Block;
import net.minecraft.src.EntityPlayerSP;
import net.minecraft.src.EntityRenderer;
import net.minecraft.src.GLAllocation;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.RenderBlocks;
import net.minecraft.src.Tessellator;
import net.minecraft.src.TexturePackBase;
import net.minecraft.src.TileEntity;
import net.minecraft.src.TileEntityRenderer;
import net.minecraft.src.TileEntitySign;
import net.minecraft.src.TileEntitySpecialRenderer;
import net.skidcode.gh.maybeaclient.hacks.SchematicaHack;
import net.skidcode.gh.maybeaclient.utils.MiniChunkPos;
import org.lwjgl.opengl.GL11;

public class Render {
	public final Settings settings = Settings.instance();
	public final List<String> textures = new ArrayList<String>();
	public final BufferedImage missingTextureImage = new BufferedImage(64, 64, 2);
	public HashMap<MiniChunkPos, SchematicaRenderBuffer> renderBuffers = new HashMap<>();
	public SchematicaRenderBuffer globalRenderBuffer = new SchematicaRenderBuffer(false);
	public boolean useGlobalRenderBuffer = true;
	public SchematicaHack schematica;
	
	public Render(SchematicaHack insatnce) {
		this.schematica = insatnce;
		initTexture();
	}

	public void initTexture() {
		Graphics graphics = this.missingTextureImage.getGraphics();
		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, 64, 64);
		graphics.setColor(Color.BLACK);
		graphics.drawString("missingtex", 1, 10);
		graphics.dispose();
	}

	public void onRender(EntityRenderer event, float f) {
		if (this.settings.minecraft != null) {
			EntityPlayerSP player = this.settings.minecraft.thePlayer;
			if (player != null) {
				this.settings.playerPosition.x = (float) (player.lastTickPosX + (player.posX - player.lastTickPosX) * f);
				this.settings.playerPosition.y = (float) (player.lastTickPosY + (player.posY - player.lastTickPosY) * f);
				this.settings.playerPosition.z = (float) (player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * f);

				this.settings.rotationRender = (int) (((player.rotationYaw / 90) % 4 + 4) % 4);

				render();
			}
		}
	}

	public void render() {
		GL11.glPushMatrix();
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(true);
		int minX = 0, maxX = 0, minY = 0, maxY = 0, minZ = 0, maxZ = 0;
		HashSet<MiniChunkPos> toRender = null;
		
		this.globalRenderBuffer.clear();
		if (this.settings.schematic != null) {
			maxX = this.settings.schematic.width();
			maxY = this.settings.schematic.height();
			maxZ = this.settings.schematic.length();

			if (this.settings.renderingLayer >= 0) {
				minY = this.settings.renderingLayer;
				maxY = this.settings.renderingLayer + 1;
			}
		
			toRender = new HashSet<MiniChunkPos>();
			useGlobalRenderBuffer = false;
			for(int x = minX/16; x <= maxX/16; ++x) {
				for(int z = minZ/16; z <= maxZ/16; ++z) {
					for(int y = minY/16; y <= maxY/16; ++y) {
						MiniChunkPos mcp = new MiniChunkPos(x, y, z);
						SchematicaRenderBuffer rb = this.getRenderBuffer(mcp);
						toRender.add(mcp);
						if(rb.update) {
							rb.clear();
							GL11.glNewList(rb.blockDisplayList, GL11.GL_COMPILE);
							int minBX = x*16;
							int maxBX = (x+1)*16;
							int minBY = y*16;
							int maxBY = (y+1)*16;
							int minBZ = z*16;
							int maxBZ = (z+1)*16;
							if(minBX < minX) minBX = minX;
							if(minBY < minY) minBY = minY;
							if(minBZ < minZ) minBZ = minZ;
							if(maxBX > maxX) maxBX = maxX;
							if(maxBY > maxY) maxBY = maxY;
							if(maxBZ > maxZ) maxBZ = maxZ;
							
							this.renderBlocks(minBX, minBY, minBZ, maxBX, maxBY, maxBZ);
							
							GL11.glEndList();
							rb.update = false;
						}
					}
				}
			}
			
			useGlobalRenderBuffer = true;
			SchematicWorld world = Settings.instance().schematic;
			drawCuboidLine(Vector3i.ZERO, new Vector3i(world.width(), world.height(), world.length()), 0x3F, 0.75f, 0.0f, 0.75f, 0.25f);
		}
		
		if (this.settings.isRenderingGuide) {
			renderGuide();
		}
		
		GL11.glTranslatef(-this.settings.getTranslationX(), -this.settings.getTranslationY(), -this.settings.getTranslationZ());
		if (this.settings.isRenderingSchematic && this.settings.schematic != null) {
			ArrayList<MiniChunkPos> toRemove = new ArrayList<>();
			for(Map.Entry<MiniChunkPos, SchematicaRenderBuffer> kp : this.renderBuffers.entrySet()) {
				if(!toRender.contains(kp.getKey())) {
					kp.getValue().destroy();
					toRemove.add(kp.getKey());
					continue;
				}
				kp.getValue().drawBlocks();
			}
			while(toRemove.size() > 0) this.renderBuffers.remove(toRemove.remove(toRemove.size()-1));
			
			for(Map.Entry<MiniChunkPos, SchematicaRenderBuffer> kp : this.renderBuffers.entrySet()) {
				kp.getValue().drawHighlight();
			}
		}
		
		this.globalRenderBuffer.drawHighlight();

		
		if (this.settings.isRenderingSchematic && this.settings.schematic != null) {
			renderTileEntities(minX, minY, minZ, maxX, maxY, maxZ);
		}

		//GL11.glTranslatef(this.settings.getTranslationX(), +this.settings.getTranslationY(), +this.settings.getTranslationZ());

		GL11.glDisable(GL11.GL_BLEND);
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		GL11.glPopMatrix();
	}

	public void renderBlocks(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
		IBlockAccess mcWorld = this.settings.minecraft.theWorld;
		SchematicWorld world = this.settings.schematic;
		RenderBlocks renderBlocks = this.settings.renderBlocks;

		int x, y, z;
		int blockId = 0, mcBlockId = 0;
		int sides = 0;
		Block block = null;

		boolean ambientOcclusion = this.settings.minecraft.gameSettings.ambientOcclusion;
		this.settings.minecraft.gameSettings.ambientOcclusion = false;
		Tessellator.instance.schematicaRendering = true;
		
		Tessellator.instance.startDrawingQuads();
		
		for (x = minX; x < maxX; x++) {
			for (y = minY; y < maxY; y++) {
				for (z = minZ; z < maxZ; z++) {
					try {
						blockId = world.getBlockId(x, y, z);
						block = Block.blocksList[blockId];
						mcBlockId = mcWorld.getBlockId(x + this.settings.offset.x, y + this.settings.offset.y, z + this.settings.offset.z);
						
						sides = 0;
						if (block != null) {
							if (block.shouldSideBeRendered(world, x, y - 1, z, 0)) sides |= 0x01;
							if (block.shouldSideBeRendered(world, x, y + 1, z, 1)) sides |= 0x02;
							if (block.shouldSideBeRendered(world, x, y, z - 1, 2)) sides |= 0x04;
							if (block.shouldSideBeRendered(world, x, y, z + 1, 3)) sides |= 0x08;
							if (block.shouldSideBeRendered(world, x - 1, y, z, 4)) sides |= 0x10;
							if (block.shouldSideBeRendered(world, x + 1, y, z, 5)) sides |= 0x20;
						}

						if (mcBlockId != 0) {
							if (blockId != mcBlockId) {
								//if(incrStats) this.settings.incrementStat(mcBlockId, BlockStat.REMOVE);
								//if(incrStats) this.settings.incrementStat(blockId, BlockStat.PLACE);
								if(this.schematica.highlight.value) {
									Vector3i tmp = new Vector3i(x, y, z);
									drawCuboidQuad(tmp, tmp.clone().add(1), sides, 1.0f, 0.0f, 0.0f, 0.25f);
									drawCuboidLine(tmp, tmp.clone().add(1), sides, 1.0f, 0.0f, 0.0f, 0.25f);
								}
							} else if (world.getBlockMetadata(x, y, z) != mcWorld.getBlockMetadata(x + this.settings.offset.x, y + this.settings.offset.y, z + this.settings.offset.z)) {
								//if(incrStats) this.settings.incrementStat(blockId, BlockStat.METAINVALID);
								if(this.schematica.highlight.value) {
									Vector3i tmp = new Vector3i(x, y, z);
									drawCuboidQuad(tmp, tmp.clone().add(1), sides, 0.75f, 0.35f, 0.0f, 0.45f);
									drawCuboidLine(tmp, tmp.clone().add(1), sides, 0.75f, 0.35f, 0.0f, 0.45f);
								}
							}//else if(mcBlockId == blockId){
								//if(incrStats) this.settings.incrementStat(blockId, BlockStat.DONEPLACE);
							//}
						} else if (mcBlockId == 0 && blockId > 0 && blockId < 0x1000) {
							if (this.schematica.highlight.value) {
								Vector3i tmp = new Vector3i(x, y, z);

								drawCuboidQuad(tmp, tmp.clone().add(1), sides, 0.0f, 0.75f, 1.0f, 0.25f);
								drawCuboidLine(tmp, tmp.clone().add(1), sides, 0.0f, 0.75f, 1.0f, 0.25f);
							}
							
							//if(incrStats) this.settings.incrementStat(blockId, BlockStat.PLACE);

							if (block != null) renderBlocks.renderBlockByRenderType(block, x, y, z);
						}
					} catch (Exception e) {
						//e.printStackTrace();
					}
				}
			}
		}
		Tessellator.instance.draw();
		Tessellator.instance.schematicaRendering = false;
		this.settings.minecraft.gameSettings.ambientOcclusion = ambientOcclusion;
	}

	public void renderTileEntities(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
		IBlockAccess mcWorld = this.settings.minecraft.theWorld;
		SchematicWorld world = this.settings.schematic;
		RenderTileEntity renderTileEntity = this.settings.renderTileEntity;

		int x, y, z;
		int mcBlockId = 0;

		GL11.glColor4f(1.0f, 1.0f, 1.0f, (255f - this.schematica.alpha.value)/255f);

		try {
			for (TileEntity tileEntity : world.getTileEntities()) {
				x = tileEntity.xCoord;
				y = tileEntity.yCoord;
				z = tileEntity.zCoord;

				if (x < minX || x >= maxX) {
					continue;
				} else if (z < minZ || z >= maxZ) {
					continue;
				} else if (y < minY || y >= maxY) {
					continue;
				}

				mcBlockId = mcWorld.getBlockId(x + this.settings.offset.x, y + this.settings.offset.y, z + this.settings.offset.z);

				if (mcBlockId == 0) {
					if (tileEntity instanceof TileEntitySign) {
						renderTileEntity.renderTileEntitySignAt((TileEntitySign) tileEntity);
					}else {
						TileEntitySpecialRenderer tileEntitySpecialRenderer = TileEntityRenderer.instance.getSpecialRendererForEntity(tileEntity);
						if (tileEntitySpecialRenderer != null) {
							tileEntitySpecialRenderer.renderTileEntityAt(tileEntity, x, y, z, 0);
							GL11.glColor4f(1.0f, 1.0f, 1.0f, (255f - this.schematica.alpha.value)/255f);
						}
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void renderGuide() {
		Vector3i start = null;
		Vector3i end = null;

		start = this.settings.pointMin.clone().sub(this.settings.offset);
		end = this.settings.pointMax.clone().sub(this.settings.offset).add(1);
		drawCuboidLine(start, end, 0x3F, 0.0f, 0.75f, 0.0f, 0.25f);

		start = this.settings.pointA.clone().sub(this.settings.offset);
		end = start.clone().add(1);
		drawCuboidLine(start, end, 0x3F, 0.75f, 0.0f, 0.0f, 0.25f);
		drawCuboidQuad(start, end, 0x3F, 0.75f, 0.0f, 0.0f, 0.25f);

		start = this.settings.pointB.clone().sub(this.settings.offset);
		end = start.clone().add(1);
		drawCuboidLine(start, end, 0x3F, 0.0f, 0.0f, 0.75f, 0.25f);
		drawCuboidQuad(start, end, 0x3F, 0.0f, 0.0f, 0.75f, 0.25f);
	}
	
	public SchematicaRenderBuffer getRenderBuffer(MiniChunkPos cp) {
		SchematicaRenderBuffer rb = this.renderBuffers.get(cp);
		if(rb == null) this.renderBuffers.put(cp, rb = new SchematicaRenderBuffer(true));
		return rb;
	}

	public void drawCuboidQuad(Vector3i a, Vector3i b, int sides, float red, float green, float blue, float alpha) {
		MiniChunkPos cp = a.miniChunk();
		SchematicaRenderBuffer rb = useGlobalRenderBuffer ? this.globalRenderBuffer : this.renderBuffers.get(cp);
		Vector3f zero = new Vector3f(a.x, a.y, a.z).sub(this.settings.blockDelta);
		Vector3f size = new Vector3f(b.x, b.y, b.z).add(this.settings.blockDelta);

		int total = 0;
		rb.colorq(red, green, blue, alpha);
		// left
		if ((sides & 0x10) != 0) {
			rb.vertexq(zero.x, zero.y, zero.z);
			rb.vertexq(zero.x, zero.y, size.z);
			rb.vertexq(zero.x, size.y, size.z);
			rb.vertexq(zero.x, size.y, zero.z);
			total += 4;
		}

		// right
		if ((sides & 0x20) != 0) {
			rb.vertexq(size.x, zero.y, size.z);
			rb.vertexq(size.x, zero.y, zero.z);
			rb.vertexq(size.x, size.y, zero.z);
			rb.vertexq(size.x, size.y, size.z);

			total += 4;
		}

		// near
		if ((sides & 0x04) != 0) {
			rb.vertexq(size.x, zero.y, zero.z);
			rb.vertexq(zero.x, zero.y, zero.z);
			rb.vertexq(zero.x, size.y, zero.z);
			rb.vertexq(size.x, size.y, zero.z);

			total += 4;
		}

		// far
		if ((sides & 0x08) != 0) {
			rb.vertexq(zero.x, zero.y, size.z);
			rb.vertexq(size.x, zero.y, size.z);
			rb.vertexq(size.x, size.y, size.z);
			rb.vertexq(zero.x, size.y, size.z);

			total += 4;
		}

		// bottom
		if ((sides & 0x01) != 0) {
			rb.vertexq(size.x, zero.y, zero.z);
			rb.vertexq(size.x, zero.y, size.z);
			rb.vertexq(zero.x, zero.y, size.z);
			rb.vertexq(zero.x, zero.y, zero.z);

			total += 4;
		}

		// top
		if ((sides & 0x02) != 0) {
			rb.vertexq(size.x, size.y, zero.z);
			rb.vertexq(zero.x, size.y, zero.z);
			rb.vertexq(zero.x, size.y, size.z);
			rb.vertexq(size.x, size.y, size.z);

			total += 4;
		}
	}

	public void drawCuboidLine(Vector3i a, Vector3i b, int sides, float red, float green, float blue, float alpha) {
		Vector3f zero = new Vector3f(a.x, a.y, a.z).sub(this.settings.blockDelta);
		Vector3f size = new Vector3f(b.x, b.y, b.z).add(this.settings.blockDelta);
		MiniChunkPos cp = a.miniChunk();
		SchematicaRenderBuffer rb = useGlobalRenderBuffer ? this.globalRenderBuffer : this.renderBuffers.get(cp);
		
		int total = 0;
		rb.colorl(red, green, blue, alpha);
		// bottom left
		if ((sides & 0x11) != 0) {
			rb.vertexl(zero.x, zero.y, zero.z);
			rb.vertexl(zero.x, zero.y, size.z);
			total += 2;
		}

		// top left
		if ((sides & 0x12) != 0) {
			rb.vertexl(zero.x, size.y, zero.z);
			rb.vertexl(zero.x, size.y, size.z);

			total += 2;
		}

		// bottom right
		if ((sides & 0x21) != 0) {
			rb.vertexl(size.x, zero.y, zero.z);
			rb.vertexl(size.x, zero.y, size.z);

			total += 2;
		}

		// top right
		if ((sides & 0x22) != 0) {
			rb.vertexl(size.x, size.y, zero.z);
			rb.vertexl(size.x, size.y, size.z);

			total += 2;
		}

		// bottom near
		if ((sides & 0x05) != 0) {
			rb.vertexl(zero.x, zero.y, zero.z);
			rb.vertexl(size.x, zero.y, zero.z);

			total += 2;
		}

		// top near
		if ((sides & 0x06) != 0) {
			rb.vertexl(zero.x, size.y, zero.z);
			rb.vertexl(size.x, size.y, zero.z);

			total += 2;
		}

		// bottom far
		if ((sides & 0x09) != 0) {
			rb.vertexl(zero.x, zero.y, size.z);
			rb.vertexl(size.x, zero.y, size.z);

			total += 2;
		}

		// top far
		if ((sides & 0x0A) != 0) {
			rb.vertexl(zero.x, size.y, size.z);
			rb.vertexl(size.x, size.y, size.z);

			total += 2;
		}

		// near left
		if ((sides & 0x14) != 0) {
			rb.vertexl(zero.x, zero.y, zero.z);
			rb.vertexl(zero.x, size.y, zero.z);

			total += 2;
		}

		// near right
		if ((sides & 0x24) != 0) {
			rb.vertexl(size.x, zero.y, zero.z);
			rb.vertexl(size.x, size.y, zero.z);

			total += 2;
		}

		// far left
		if ((sides & 0x18) != 0) {
			rb.vertexl(zero.x, zero.y, size.z);
			rb.vertexl(zero.x, size.y, size.z);

			total += 2;
		}

		// far right
		if ((sides & 0x28) != 0) {
			rb.vertexl(size.x, zero.y, size.z);
			rb.vertexl(size.x, size.y, size.z);

			total += 2;
		}
	}

	public String getTextureName(String texture) {
		if (!this.schematica.enableAlpha.value) {
			return texture;
		}

		String textureName = "/" + (int) ((255f - this.schematica.alpha.value)/255f * 255) + texture.replace('/', '-');

		if (this.textures.contains(textureName)) {
			return textureName;
		}

		try {
			TexturePackBase texturePackBase = this.settings.minecraft.texturePackList.selectedTexturePack;
			File newTextureFile = new File(Settings.textureDirectory, texturePackBase.texturePackFileName.replace(".zip", "") + textureName);
			
			if (!newTextureFile.exists()) {
				BufferedImage bufferedImage = readTextureImage(texturePackBase.func_6481_a(texture));
				if (bufferedImage == null) {
					return texture;
				}

				int x, y, color;
				for (x = 0; x < bufferedImage.getWidth(); x++) {
					for (y = 0; y < bufferedImage.getHeight(); y++) {
						color = bufferedImage.getRGB(x, y);
						bufferedImage.setRGB(x, y, (((int) (((color >> 24) & 0xFF) * ((255f - this.schematica.alpha.value) / 255f))) << 24) | (color & 0x00FFFFFF));
					}
				}

				newTextureFile.getParentFile().mkdirs();
				ImageIO.write(bufferedImage, "png", newTextureFile);
			}

			loadTexture(textureName, readTextureImage(new BufferedInputStream(new FileInputStream(newTextureFile))));

			this.textures.add(textureName);
			return textureName;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		return texture;
	}

	public int loadTexture(String texture, BufferedImage textureImage) throws IllegalArgumentException, IllegalAccessException {
		HashMap<String, Integer> textureMap = (HashMap<String, Integer>) this.settings.minecraft.renderEngine.textureMap;
		IntBuffer singleIntBuffer = this.settings.minecraft.renderEngine.singleIntBuffer;

		Integer textureId = textureMap.get(texture);

		if (textureId != null) {
			return textureId.intValue();
		}

		try {
			singleIntBuffer.clear();
			GLAllocation.generateTextureNames(singleIntBuffer);
			int glTextureId = singleIntBuffer.get(0);
			this.settings.minecraft.renderEngine.setupTexture(textureImage, glTextureId);
			textureMap.put(texture, Integer.valueOf(glTextureId));
			return glTextureId;
		} catch (Exception e) {
			e.printStackTrace();
			GLAllocation.generateTextureNames(singleIntBuffer);
			int glTextureId = singleIntBuffer.get(0);
			this.settings.minecraft.renderEngine.setupTexture(this.missingTextureImage, glTextureId);
			textureMap.put(texture, Integer.valueOf(glTextureId));
			return glTextureId;
		}
	}

	public BufferedImage readTextureImage(InputStream inputStream) throws IOException {
		BufferedImage bufferedImage = ImageIO.read(inputStream);
		inputStream.close();
		return bufferedImage;
	}
}
