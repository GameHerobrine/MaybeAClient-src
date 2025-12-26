package net.skidcode.gh.maybeaclient.hacks.settings.enums;

public enum EnumStaticPos {
	BOTTOM_RIGHT("Bottom Right"),
	BOTTOM_LEFT("Bottom Left"),
	TOP_RIGHT("Top Right"),
	TOP_LEFT("Top Left"), 
	DISABLED("Disabled");
	
	public final String value;
	EnumStaticPos(String s) {
		this.value = s;
	}
	
	@Override
	public String toString() {
		return this.value;
	}
}
