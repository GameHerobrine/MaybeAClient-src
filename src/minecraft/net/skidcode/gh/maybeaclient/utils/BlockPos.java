package net.skidcode.gh.maybeaclient.utils;

import java.util.Comparator;

public class BlockPos {
	public final int x;
	public final int y;
	public final int z;
	public final int hash;
	
	public BlockPos(int x, int y, int z) {
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
		BlockPos p = (BlockPos) o;
		return p.x == this.x && p.y == this.y && p.z == this.z;
	}
	
	public int hashCode() {
		return this.hash;
	}
	
	public static class Sorter implements Comparator<BlockPos>{
		public double xc, yc, zc;
		public Sorter(double xc, double yc, double zc) {
			this.xc = xc;
			this.yc = yc;
			this.zc = zc;
		}
		@Override
		public int compare(BlockPos a, BlockPos b) {
			double xd = this.xc - a.x;
			double yd = this.yc - a.y;
			double zd = this.zc - a.z;
			double d2a = xd*xd + yd*yd + zd*zd;
			xd = this.xc - b.x;
			yd = this.yc - b.y;
			zd = this.zc - b.z;
			double d2b = xd*xd + yd*yd + zd*zd;
			return Double.compare(d2a, d2b);
		}
		
	}
}
