package net.skidcode.gh.maybeaclient.utils;

public class TunnelPos extends BlockPos{
	public final int xwidth;
	public final int zwidth;
	public TunnelPos(int x, int y, int z, int xwidth, int zwidth) {
		super(x, y, z);
		this.xwidth = xwidth;
		this.zwidth = zwidth;
	}
}
