package lunatrius.schematica;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import lunatrius.schematica.util.SchematicaRenderBuffer;
import lunatrius.schematica.util.Vector3f;
import lunatrius.schematica.util.Vector3i;
import net.minecraft.client.Minecraft;
import net.minecraft.src.Block;
import net.minecraft.src.ChunkCache;
import net.minecraft.src.CompressedStreamTools;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.KeyBinding;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.RenderBlocks;
import net.minecraft.src.RenderManager;
import net.minecraft.src.StringTranslate;
import net.minecraft.src.TileEntity;
import net.skidcode.gh.maybeaclient.Client;
import net.skidcode.gh.maybeaclient.hacks.SchematicaHack;
import net.skidcode.gh.maybeaclient.utils.MiniChunkPos;

import org.lwjgl.input.Keyboard;


public class Settings {
	private final static Settings instance = new Settings();
	private final StringTranslate strTranslate = StringTranslate.getInstance();

	public BlockStat stats_blocksToPlace[] = new BlockStat[Block.blocksList.length];
	public boolean stats_initialized = false;
	
	// loaded from config
	//public boolean enableAlpha = false;
	//public float alpha = 1.0f;
	//public boolean highlight = true;
	public float blockDelta = 0.005f;
	//public Vector3i renderRange = new Vector3i(20, 15, 20);

	public KeyBinding[] keyBindings = new KeyBinding[] {
			new KeyBinding("key.schematic.load", Keyboard.KEY_DIVIDE),
			new KeyBinding("key.schematic.save", Keyboard.KEY_MULTIPLY),
			new KeyBinding("key.schematic.control", Keyboard.KEY_SUBTRACT)
	};

	public static final File schematicDirectory = new File(Minecraft.getMinecraftDir(), "/schematics/");
	public static final File textureDirectory = new File(Minecraft.getMinecraftDir(), "/resources/mod/schematica/");
	public Minecraft minecraft = Client.mc;
	public ChunkCache mcWorldCache = null;
	public SchematicWorld schematic = null;
	public Vector3f playerPosition = new Vector3f();
	public RenderBlocks renderBlocks = null;
	public RenderTileEntity renderTileEntity = null;
	public int selectedSchematic = 0;
	public Vector3i pointA = new Vector3i();
	public Vector3i pointB = new Vector3i();
	public Vector3i pointMin = new Vector3i();
	public Vector3i pointMax = new Vector3i();
	public int rotationRender = 0;
	public Vector3i offset = new Vector3i();
	public boolean isRenderingSchematic = false;
	public int renderingLayer = -1;
	public boolean isRenderingGuide = false;
	public int[] increments = {
			1, 5, 15, 50, 250
	};

	private Settings() {
		for(int i = 0; i < stats_blocksToPlace.length; ++i) {
			stats_blocksToPlace[i] = new BlockStat(i);
		}
	}
	public void tryUpdating(int x, int y, int z) {
		if(this.schematic == null) return;
		int minX = this.offset.x;
		int minY = this.offset.y;
		int minZ = this.offset.z;
		int maxX = minX + this.schematic.width();
		int maxY = minY + this.schematic.height();
		int maxZ = minZ + this.schematic.length();
		
		if(x >= minX && x <= maxX && y >= minY && y <= maxY && z >= minZ && z <= maxZ) {
			MiniChunkPos mcp = new MiniChunkPos((x-minX)/16, (y-minY)/16, (z-minZ)/16);
			SchematicaHack.instance.render.getRenderBuffer(mcp).update = true;
		}
	}
	
	public void requestFullUpdate() {
		for(SchematicaRenderBuffer rb : SchematicaHack.instance.render.renderBuffers.values()) {
			rb.update = true;
		}
	}
	
	public void recalculateStats() {
		IBlockAccess mcWorld = this.minecraft.theWorld;
		SchematicWorld world = this.schematic;

		int x, y, z;
		int blockId = 0, mcBlockId = 0;
		int minX = 0, minY = 0, minZ = 0;
		int maxX = this.schematic.width();
		int maxY = this.schematic.height();
		int maxZ = this.schematic.length();
		for (x = minX; x < maxX; x++) {
			for (y = minY; y < maxY; y++) {
				for (z = minZ; z < maxZ; z++) {
					try {
						blockId = world.getBlockId(x, y, z);
						mcBlockId = mcWorld.getBlockId(x + this.offset.x, y + this.offset.y, z + this.offset.z);

						if (mcBlockId != 0) {
							if (blockId != mcBlockId) {
								this.incrementStat(mcBlockId, BlockStat.REMOVE);
								this.incrementStat(blockId, BlockStat.PLACE);
							} else if (world.getBlockMetadata(x, y, z) != mcWorld.getBlockMetadata(x + this.offset.x, y + this.offset.y, z + this.offset.z)) {
								this.incrementStat(blockId, BlockStat.METAINVALID);
							}else if(mcBlockId == blockId){
								this.incrementStat(blockId, BlockStat.DONEPLACE);
							}
						} else if (mcBlockId == 0 && blockId > 0 && blockId < 0x1000) {
							this.incrementStat(blockId, BlockStat.PLACE);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		stats_initialized = true;
	}
	
	public void incrementStat(int blockId, byte stat) {
		if(blockId >= this.stats_blocksToPlace.length || blockId < 0) {
			System.out.println("Trying to increment invalid block id stat! "+blockId+"  "+stat);
			return;
		}
		if(blockId == 0) return;
		BlockStat st = this.stats_blocksToPlace[blockId];
		
		switch(stat) {
			case BlockStat.PLACE:
				++st.toPlace;
				break;
			case BlockStat.DONEPLACE:
				++st.donePlace;
				break;
			case BlockStat.METAINVALID:
				//++st.metaInvalid;
				break;
			case BlockStat.REMOVE:
				++st.toRemove;
				break;
			default:
				System.out.println("Invalid stat! "+stat);
		}
	}
	public void emptyStats() {
		for(int i = 0; i < stats_blocksToPlace.length; ++i) {
			stats_blocksToPlace[i].empty();
		}
		stats_initialized = false;
	}
	
	public static Settings instance() {
		return instance;
	}

	public List<String> getSchematicFiles() {
		ArrayList<String> schematicFiles = new ArrayList<String>();
		schematicFiles.add("-- No schematic --");

		File[] files = schematicDirectory.listFiles(new FileFilterSchematic());
		for (int i = 0; i < files.length; i++) {
			schematicFiles.add(files[i].getName());
		}
		return schematicFiles;
	}

	public void unloadSchematic() {
		this.schematic = null;
		
		for(SchematicaRenderBuffer buf : SchematicaHack.instance.render.renderBuffers.values()) {
			buf.destroy();
		}
		SchematicaHack.instance.render.renderBuffers.clear();
		
		this.renderBlocks = null;
		this.renderTileEntity = null;
		this.isRenderingSchematic = false;
		SchematicaHack.instance.openStats.hidden = true;
		this.emptyStats();
	}
	
	public boolean loadSchematic(String filename) {
		if(this.schematic != null) {
			this.unloadSchematic();
		}
		try {
			InputStream stream = new FileInputStream(filename);
			NBTTagCompound tagCompound = CompressedStreamTools.func_1138_a(stream);

			if (tagCompound != null) {
				this.schematic = new SchematicWorld();
				this.schematic.readFromNBT(tagCompound);

				this.renderBlocks = new RenderBlocks(this.schematic);
				this.renderTileEntity = new RenderTileEntity(this.schematic);

				this.isRenderingSchematic = true;
				this.emptyStats();
				SchematicaHack.instance.openStats.hidden = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.unloadSchematic();
			return false;
		}

		return true;
	}

	public boolean saveSchematic(String filename, Vector3i from, Vector3i to) {
		try {
			NBTTagCompound tagCompound = new NBTTagCompound();

			int minX = Math.min(from.x, to.x);
			int maxX = Math.max(from.x, to.x);
			int minY = Math.min(from.y, to.y);
			int maxY = Math.max(from.y, to.y);
			int minZ = Math.min(from.z, to.z);
			int maxZ = Math.max(from.z, to.z);
			short width = (short) (Math.abs(maxX - minX) + 1);
			short height = (short) (Math.abs(maxY - minY) + 1);
			short length = (short) (Math.abs(maxZ - minZ) + 1);

			int[][][] blocks = new int[width][height][length];
			int[][][] metadata = new int[width][height][length];
			List<TileEntity> tileEntities = new ArrayList<TileEntity>();

			for (int x = minX; x <= maxX; x++) {
				for (int y = minY; y <= maxY; y++) {
					for (int z = minZ; z <= maxZ; z++) {
						blocks[x - minX][y - minY][z - minZ] = this.minecraft.theWorld.getBlockId(x, y, z);
						metadata[x - minX][y - minY][z - minZ] = this.minecraft.theWorld.getBlockMetadata(x, y, z);
						if (this.minecraft.theWorld.getBlockTileEntity(x, y, z) != null) {
							NBTTagCompound te = new NBTTagCompound();
							this.minecraft.theWorld.getBlockTileEntity(x, y, z).writeToNBT(te);

							TileEntity tileEntity = TileEntity.createAndLoadEntity(te);
							tileEntity.xCoord -= minX;
							tileEntity.yCoord -= minY;
							tileEntity.zCoord -= minZ;
							tileEntities.add(tileEntity);
						}
					}
				}
			}
			SchematicWorld schematicOut = new SchematicWorld(blocks, metadata, tileEntities, width, height, length);
			schematicOut.writeToNBT(tagCompound);
			OutputStream stream = new FileOutputStream(filename);
			CompressedStreamTools.writeGzippedCompoundToOutputStream(tagCompound, stream);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public float getTranslationX() {
		return (float) (RenderManager.renderPosX - this.offset.x);
	}

	public float getTranslationY() {
		return (float) (RenderManager.renderPosY - this.offset.y);
	}

	public float getTranslationZ() {
		return (float) (RenderManager.renderPosZ - this.offset.z);
	}

	public void updatePoints() {
		this.pointMin.x = Math.min(this.pointA.x, this.pointB.x);
		this.pointMin.y = Math.min(this.pointA.y, this.pointB.y);
		this.pointMin.z = Math.min(this.pointA.z, this.pointB.z);

		this.pointMax.x = Math.max(this.pointA.x, this.pointB.x);
		this.pointMax.y = Math.max(this.pointA.y, this.pointB.y);
		this.pointMax.z = Math.max(this.pointA.z, this.pointB.z);

		this.requestFullUpdate();
	}

	public void moveHere(Vector3i point) {
		point.x = (int) Math.floor(this.playerPosition.x);
		point.y = (int) Math.floor(this.playerPosition.y - 1);
		point.z = (int) Math.floor(this.playerPosition.z);

		switch (this.rotationRender) {
		case 0:
			point.x -= 1;
			point.z += 1;
			break;
		case 1:
			point.x -= 1;
			point.z -= 1;
			break;
		case 2:
			point.x += 1;
			point.z -= 1;
			break;
		case 3:
			point.x += 1;
			point.z += 1;
			break;
		}
	}

	public void moveHere() {
		this.offset.x = (int) Math.floor(this.playerPosition.x);
		this.offset.y = (int) Math.floor(this.playerPosition.y) - 1;
		this.offset.z = (int) Math.floor(this.playerPosition.z);

		if (this.schematic != null) {
			switch (this.rotationRender) {
			case 0:
				this.offset.x -= this.schematic.width();
				this.offset.z += 1;
				break;
			case 1:
				this.offset.x -= this.schematic.width();
				this.offset.z -= this.schematic.length();
				break;
			case 2:
				this.offset.x += 1;
				this.offset.z -= this.schematic.length();
				break;
			case 3:
				this.offset.x += 1;
				this.offset.z += 1;
				break;
			}

			reloadChunkCache();
		}
	}

	public void toggleRendering() {
		this.isRenderingSchematic = !this.isRenderingSchematic && (this.schematic != null);
	}

	public void reloadChunkCache() {
		if(this.schematic == null) return;
		this.mcWorldCache = new ChunkCache(this.minecraft.theWorld, this.offset.x - 1, this.offset.y - 1, this.offset.z - 1, this.offset.x + this.schematic.width() + 1, this.offset.y + this.schematic.height() + 1, this.offset.z + this.schematic.length() + 1);
		this.requestFullUpdate();
	}

	public void flipWorld() {
		if (this.schematic != null) {
			this.schematic.flip();
			this.requestFullUpdate();
		}
	}

	public void rotateWorld() {
		if (this.schematic != null) {
			this.schematic.rotate();
			this.requestFullUpdate();
		}
	}
}
