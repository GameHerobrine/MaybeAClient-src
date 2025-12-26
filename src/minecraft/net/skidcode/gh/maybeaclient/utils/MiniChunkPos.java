package net.skidcode.gh.maybeaclient.utils;

import java.util.Comparator;

import net.minecraft.src.RenderManager;

public class MiniChunkPos{
	public final int x;
	public final int y;
	public final int z;
	public final int hash;
	
	public MiniChunkPos(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.hash = hash(x, hash(y, z));
	}
	
	private int hash(int a, int b) {
		return (a + b + 1) * (a + b) / 2 + b;
	}
	
	@Override
	public boolean equals(Object o) {
		MiniChunkPos p = (MiniChunkPos) o;
		return p.x == this.x && p.y == this.y && p.z == this.z;
	}
	
	public int hashCode() {
		return this.hash;
	}
}
