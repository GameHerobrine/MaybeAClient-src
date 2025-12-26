package net.skidcode.gh.maybeaclient.hacks.settings.enums;

public enum EnumAlign {
	LEFT("Left"),
	RIGHT("Right");
	
	public final String value;
	EnumAlign(String s) {
		this.value = s;
	}
	
	@Override
	public String toString() {
		return this.value;
	}
}
