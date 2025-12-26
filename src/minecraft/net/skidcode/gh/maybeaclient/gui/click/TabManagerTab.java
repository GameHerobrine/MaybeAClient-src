package net.skidcode.gh.maybeaclient.gui.click;

import java.util.ArrayList;

import net.skidcode.gh.maybeaclient.gui.click.element.ToggleButtonElement;
import net.skidcode.gh.maybeaclient.gui.click.element.ToggleButtonElement.ToggleButtonActionListener;
import net.skidcode.gh.maybeaclient.hacks.category.Category;

public class TabManagerTab extends ScrollableTab{
	public static TabManagerTab instance = new TabManagerTab();
	public ArrayList<Tab> tabs = new ArrayList<>();
	//public ToggleButtonElement addTabButton;
	public TabManagerTab() {
		super("Tab Manager");
		showninmanager = false;
		this.xDefPos = this.startX = 160;
		this.yDefPos = this.startY = 24 + 14*7;
		/*this.addTabButton = new ToggleButtonElement(new ToggleButtonActionListener() {
			@Override
			public String getDisplayString(boolean v) {
				return "Add new tab";
			}

			@Override
			public boolean getValue() {
				return false;
			}

			@Override
			public void onPressed(int mx, int my, int click) {
				//TODO add new tab
				Category c = Category.create();
				Tab t = new CategoryTab(c);
				ClickGUI.addTab(0, t);
				instance.add(t);
			}
		});*/
	}
	
	public void addAll(ArrayList<Tab> tabs) {
		this.tabs.addAll(tabs);
	}
	public void remove(Tab tab) {
		this.tabs.remove(tab);
	}
	public void add(Tab tab) {
		this.tabs.add(tab);
	}
	
	
	@Override
	public void renderIngame() {}
	
	@Override
	public void preRender() {
		
		this.clearElements();
		for(Tab tab : this.tabs) {
			if(tab.showninmanager) {
				this.addElement(tab.tabmanagerentry);
			}
		}
		
		//this.addElement(this.addTabButton);
		
		super.preRender();
	}
}
