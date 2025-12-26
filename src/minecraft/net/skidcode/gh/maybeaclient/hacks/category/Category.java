package net.skidcode.gh.maybeaclient.hacks.category;

import java.util.ArrayList;

import net.skidcode.gh.maybeaclient.gui.click.Tab;
import net.skidcode.gh.maybeaclient.hacks.Hack;

public class Category {
	
	public static final Category MOVEMENT = new Category("Movement");
	public static final Category RENDER = new Category("Render");
	public static final Category COMBAT = new Category("Combat");
	public static final Category MISC = new Category("Misc");
	public static final Category UI = new Category("UI");
	
	public String name;
	public ArrayList<Hack> hacks = new ArrayList<Hack>();
	public Tab tab;
	
	Category(String name){
		this.name = name;
	}
}
