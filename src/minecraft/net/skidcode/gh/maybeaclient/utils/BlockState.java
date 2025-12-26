package net.skidcode.gh.maybeaclient.utils;

public class BlockState{
	public final int x;
	public final int y;
	public final int z;
	public final int id;
	public final int hash;
	public BlockState(int x, int y, int z, int id) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.id = id;
		this.hash = hash(hash(x, hash(y, z)), id);
	}
	
	private static int hash(int a, int b) {
		return (a + b + 1) * (a + b) / 2 + b;
	}
	
	@Override
	public boolean equals(Object o) {
		BlockState bs = (BlockState) o;
		return this.x == bs.x && this.y == bs.y && this.z == bs.z && this.id == bs.id;
	}

}
