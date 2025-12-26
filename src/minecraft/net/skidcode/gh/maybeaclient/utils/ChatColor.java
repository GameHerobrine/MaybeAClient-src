package net.skidcode.gh.maybeaclient.utils;

public enum ChatColor {
	BLACK("0"), 
	BLUE("1"), 
	GREEN("2"), 
	DARKCYAN("3"), 
	RED("4"), 
	PURPLE("5"), 
	GOLD("6"), 
	LIGHTGRAY("7"), 
	DARKGRAY("8"),
	CYAN("9"), 
	LIGHTGREEN("a"), 
	LIGHTCYAN("b"), 
	LIGHTRED("c"), 
	MAGENTA("d"), 
	YELLOW("e"), 
	WHITE("f"),
	EXP_RESET("\01"),
	THREEBYTECOL("\02");
	
	public static String custom(int rgb) {
		return custom((rgb & 0xff0000) >> 16, (rgb & 0xff00) >> 8, rgb & 0xff);
	}
	public static String custom(int r, int g, int b) {
		return THREEBYTECOL+""+(char)(r&0xff)+(char)(g&0xff)+(char)(b&0xff);
	}
	
	public final String color;
	public static final String SYM = "§";
	ChatColor(String s) {
		this.color = s;
	}
	public String toString() {
		return "§"+this.color;
	}
	
}
