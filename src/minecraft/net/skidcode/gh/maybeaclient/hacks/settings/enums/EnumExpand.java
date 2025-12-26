package net.skidcode.gh.maybeaclient.hacks.settings.enums;

public enum EnumExpand{
	TOP("Top"),
	BOTTOM("Bottom");
	
	public final String value;
	EnumExpand(String s) {
		this.value = s;
	}
	
	@Override
	public String toString() {
		return this.value;
	}
}
