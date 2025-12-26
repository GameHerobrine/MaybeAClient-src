package net.skidcode.gh.maybeaclient.utils;

public enum Direction {
	ZPOS(0, 0, 1, 2, 0),
	XNEG(-1, 0, 0, 5, 90),
	ZNEG(0, 0, -1, 3, 180),
	XPOS(1, 0, 0, 4, 270),
	NULL(0, 0, 0, 0, 0);
	
	public int offX, offY, offZ, hitSide;
	public float yaw;
	
	Direction(int x, int y, int z, int hitside, float yaw) {
		this.offX = x;
		this.offY = y;
		this.offZ = z;
		this.hitSide = hitside;
		this.yaw = yaw;
	}
	
	@Override
	public String toString() {
		switch(this) {
			case ZPOS: return "Z+";
			case XNEG: return "X-";
			case ZNEG: return "Z-";
			case XPOS: return "X+";
			default: return "NULL";
		}
	}

	public boolean x() {
		return this == XNEG || this == XPOS;
	}
	
	public boolean z() {
		return this == ZNEG || this == ZPOS;
	}
}
