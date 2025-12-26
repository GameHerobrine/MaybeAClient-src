package net.skidcode.gh.maybeaclient.utils;

import java.util.Random;

import net.minecraft.src.Block;
import net.minecraft.src.ItemBlock;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Material;
import net.skidcode.gh.maybeaclient.Client;

public class WorldUtils {
	public static boolean isWalkable(int x, int y, int z) {
		return isWalkable(Client.mc.theWorld.getBlockId(x, y, z));
	}
	
	public static boolean isPassable(int x, int y, int z) {
		return isPassable(Client.mc.theWorld.getBlockId(x, y, z));
	}
	public static boolean isPassable(int id) {
		if(id == 0) return true;
		
		Block b = Block.blocksList[id];
		if(b.blockMaterial.getIsLiquid()) return false;
		if(b.blockMaterial == Material.plants || b.blockMaterial == Material.snow || b.blockMaterial == Material.circuits) return true;
		
		return false;
	}
	

	public static boolean isSlimeChunk(int chunkX, int chunkZ) {
		Random rng = new Random(Client.mc.theWorld.getRandomSeed() + (long)(chunkX * chunkX * 4987142) + (long)(chunkX * 5947611) + (long)(chunkZ * chunkZ) * 4392871L + (long)(chunkZ * 389711) ^ 987234911L);
		return rng.nextInt(10) == 0;
	}
	
	public static int resX, resY, resZ; 
	public static void toBlockPos(int x, int y, int z, int side) {
		resX = x;
		resY = y;
		resZ = z;
		
		if(side == 0) resY = y-1;
		if(side == 1) resY = y+1;
		if(side == 2) resZ = z-1;
		if(side == 3) resZ = z+1;
		if(side == 4) resX = x-1;
		if(side == 5) resX = x+1;
	}
	
	public static boolean isWalkable(int id) {
		if(id == 0) return false;
		Block b = Block.blocksList[id];
		if(b.blockMaterial.getIsLiquid()) return false;
		if(b.blockMaterial.getIsSolid()) return true;
		return false;
	}
}
