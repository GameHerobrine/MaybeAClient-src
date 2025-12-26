package net.minecraft.src;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ChunkProviderClient implements IChunkProvider {
    private Chunk blankChunk;
    private Map chunkMapping = new HashMap();
    private List field_889_c = new ArrayList();
    private World worldObj;

    public ChunkProviderClient(World var1) {
        this.blankChunk = new EmptyChunk(var1, new byte['\u8000'], 0, 0);
        this.worldObj = var1;
    }

    private void saveChunk(Chunk chunk)
	{
		if(!((WorldClient)worldObj).downloadThisWorld)
		{
			return;
		}
		chunk.lastSaveTime = worldObj.getWorldTime();
		chunk.isTerrainPopulated = true;
		try
		{
			Iterator iterator = chunk.newChunkTileEntityMap.keySet().iterator();
			do
			{
				if(!iterator.hasNext())
				{
					break;
				}
				Object obj = iterator.next();
				TileEntity tileentity = (TileEntity)chunk.newChunkTileEntityMap.get(obj);
				if(tileentity != null)
				{
					Block block = Block.blocksList[worldObj.getBlockId(tileentity.xCoord, tileentity.yCoord, tileentity.zCoord)];
					if((block instanceof BlockChest) || (block instanceof BlockDispenser) || (block instanceof BlockFurnace) || (block instanceof BlockNote))
					{
						chunk.chunkTileEntityMap.put(obj, tileentity);
					}
				}
			} while(true);
			((WorldClient)worldObj).downloadChunkLoader.saveChunk(worldObj, chunk);
		}
		catch(IOException ioexception)
		{
			ioexception.printStackTrace();
		}
	}
    
    public boolean chunkExists(int var1, int var2) {
        if (this != null) {
            return true;
        } else {
            ChunkCoordIntPair var3 = new ChunkCoordIntPair(var1, var2);
            return this.chunkMapping.containsKey(var3);
        }
    }

    public void func_539_c(int var1, int var2) {
        Chunk var3 = this.provideChunk(var1, var2);
        if (!var3.func_21167_h()) {
            var3.onChunkUnload();
        }
        if(((WorldClient)this.worldObj).downloadThisWorld && !var3.neverSave && var3.isFilled){
			saveChunk(var3);
			try
			{
				((WorldClient)worldObj).downloadChunkLoader.saveExtraChunkData(worldObj, var3);
			}
			catch(IOException ioexception)
			{
				ioexception.printStackTrace();
			}
		}
        this.chunkMapping.remove(new ChunkCoordIntPair(var1, var2));
        this.field_889_c.remove(var3);
    }

    public Chunk func_538_d(int var1, int var2) {
        ChunkCoordIntPair var3 = new ChunkCoordIntPair(var1, var2);
        byte[] var4 = new byte['\u8000'];
        Chunk var5 = new Chunk(this.worldObj, var4, var1, var2);
        Arrays.fill(var5.skylightMap.data, (byte)-1);
        this.chunkMapping.put(var3, var5);
        var5.isChunkLoaded = true;
        if(((WorldClient)worldObj).downloadThisWorld)
        {
            var5.importOldChunkTileEntities();
        }
        return var5;
    }

    public Chunk provideChunk(int var1, int var2) {
        ChunkCoordIntPair var3 = new ChunkCoordIntPair(var1, var2);
        Chunk var4 = (Chunk)this.chunkMapping.get(var3);
        return var4 == null ? this.blankChunk : var4;
    }

    public boolean saveChunks(boolean var1, IProgressUpdate var2) {
    	if(!((WorldClient)worldObj).downloadThisWorld)
		{
			return true;
		}
		Iterator iterator = chunkMapping.keySet().iterator();
		do
		{
			if(!iterator.hasNext())
			{
				break;
			}
			Object obj = iterator.next();
			Chunk chunk = (Chunk)chunkMapping.get(obj);
			if(var1 && chunk != null && !chunk.neverSave && chunk.isFilled)
			{
				try
				{
					((WorldClient)worldObj).downloadChunkLoader.saveExtraChunkData(worldObj, chunk);
				}
				catch(IOException ioexception)
				{
					ioexception.printStackTrace();
				}
			}
			if(chunk != null && !chunk.neverSave && chunk.isFilled)
			{
				saveChunk(chunk);
			}
		} while(true);
		if(var1)
		{
			((WorldClient)worldObj).downloadChunkLoader.saveExtraData();
		}
		return true;
    }

    public boolean func_532_a() {
        return false;
    }

    public boolean func_536_b() {
        return false;
    }

    public void populate(IChunkProvider var1, int var2, int var3) {
    }

    public String makeString() {
        return "MultiplayerChunkCache: " + this.chunkMapping.size();
    }

    public void importOldTileEntities() {
		Iterator iterator = chunkMapping.keySet().iterator();
        do
        {
            if(!iterator.hasNext())
            {
                break;
            }
            Object obj = iterator.next();
            Chunk chunk = (Chunk)chunkMapping.get(obj);
            if(chunk != null && chunk.isFilled)
            {
                chunk.importOldChunkTileEntities();
            }
        } while(true);
	}
}
