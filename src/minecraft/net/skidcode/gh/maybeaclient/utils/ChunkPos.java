package net.skidcode.gh.maybeaclient.utils;

public class ChunkPos {
	public final int x;
	public final int z;
	public final int hash;
	
	public ChunkPos(int x, int z) {
		this.x = x;
		this.z = z;
		this.hash = hash(x, z);
	}
	
	private int hash(int a, int b) {
		return (a + b + 1) * (a + b) / 2 + b;
	}
	
	@Override
	public boolean equals(Object o) {
		ChunkPos p = (ChunkPos) o;
		return p.x == this.x && p.z == this.z;
	}
	
	public int hashCode() {
		return this.hash;
	}
}
