package net.skidcode.gh.maybeaclient.hacks.category;

import java.util.ArrayList;

import net.skidcode.gh.maybeaclient.Client;
import net.skidcode.gh.maybeaclient.gui.click.CategoryTab;
import net.skidcode.gh.maybeaclient.gui.click.Tab;
import net.skidcode.gh.maybeaclient.hacks.Hack;

public class Category {
	public static ArrayList<Category> categories = new ArrayList<>();
	
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
		categories.add(this);
		this.tab = new CategoryTab(this);
	}
	
	public static Category create() {
		return new Category("New Category");
	}

	public boolean[] generateContainsLookup() {
		boolean[] b = new boolean[Client.hacksByName.size()];
		int i = 0;
		for(Hack h : Client.hacksByName.values()) {
			b[i] = this.hacks.contains(h);
		}
			
		return b;
	}

	public void setTabOptions(int x, int y, boolean b) {
		this.tab.startX = this.tab.xDefPos = x;
		this.tab.startY = this.tab.yDefPos = y;
		this.tab.minimized.setValue(b);
	}

	public void addContentListener(ContentListener listener) {
		
		
	}
}
