package net.skidcode.gh.maybeaclient.gui.click;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import net.skidcode.gh.maybeaclient.Client;
import net.skidcode.gh.maybeaclient.hacks.ClickGUIHack;
import net.skidcode.gh.maybeaclient.hacks.Hack;
import net.skidcode.gh.maybeaclient.hacks.ClickGUIHack.Theme;
import net.skidcode.gh.maybeaclient.hacks.category.Category;
import net.skidcode.gh.maybeaclient.hacks.category.ContentListener;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingChooser;

public class CategoryTab extends ScrollableTab implements ContentListener {
	public Category category;
	
	public CategoryTab(Category category) {
		super(category.name);
		this.category = category;
		this.category.tab = this;
		/*String[] names = new String[Client.hacksByName.size()];
		boolean[] stats = new boolean[names.length];
		int i = 0;
		for(Hack h : Client.hacksByName.values()) {
			names[i] = h.name;
			stats[i] = category.hacks.contains(h);
			++i;
		}
		
		this.settings.add(new SettingChooser(this, "Modules", names, stats) {
			@Override
			public void setValue(String name, boolean value) {
				super.setValue(name, value);
				System.out.println(name);
				if(this.getValue(name)) category.hacks.add(Client.findHack(name));
				else category.hacks.remove(Client.findHack(name));
			}
		});*/
		
		this.category.addContentListener(this);
	}
	
	public boolean regenerateElements = true;
	@Override
	public void onContentChanged() {
		this.regenerateElements = true;
	}
	
	public CategoryTab(Category category, int x, int y) {
		this(category);
		this.startX = x;
		this.startY = y;
		this.xDefPos = x;
		this.yDefPos = y;
	}
	
	@Override
	public int getTitleWidth() {
		int w = super.getTitleWidth();
		if(ClickGUIHack.theme() == Theme.NODUS) w += Client.mc.fontRenderer.getStringWidth(" ("+this.category.hacks.size()+")");
		return w;
	}
	
	@Override
	public void preRender() {
		if(this.regenerateElements) {
			this.clearElements();
			ArrayList<Hack> hacks = new ArrayList<Hack>(this.category.hacks); //freehij will haet me <3
			if(ClickGUIHack.instance.sortModules.getValue() == ClickGUIHack.SortDirection.A_Z) {
				Collections.sort(hacks, new Comparator<Hack>() {
					@Override
					public int compare(Hack o1, Hack o2) {
						return o1.name.compareTo(o2.name);
					}
				});
			}
			if(ClickGUIHack.instance.sortModules.getValue() == ClickGUIHack.SortDirection.Z_A) {
				Collections.sort(hacks, new Comparator<Hack>() {
					@Override
					public int compare(Hack o1, Hack o2) {
						return o2.name.compareTo(o1.name);
					}
				});
			}
			
			for(Hack hack : hacks) {
				this.addElement(hack.categorybutton);
			}
			
			this.regenerateElements = false;
		}
		
		super.preRender();
	}
	
	@Override
	public void renderIngame() {}
	
	@Override
	public void renderNameAt(int x, int y) {
		super.renderNameAt(x, y);
		if(ClickGUIHack.theme() == Theme.NODUS) {
			x = x + Client.mc.fontRenderer.getStringWidth(this.getTabName());
			Client.mc.fontRenderer.drawString(" ("+this.category.hacks.size()+")", x + ClickGUIHack.theme().headerXAdd, y + ClickGUIHack.theme().yaddtocenterText, ClickGUIHack.instance.themeColor.rgb());
		}
	}
}
