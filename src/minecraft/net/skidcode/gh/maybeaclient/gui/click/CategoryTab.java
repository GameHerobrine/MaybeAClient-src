package net.skidcode.gh.maybeaclient.gui.click;

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
			for(Hack hack : this.category.hacks) {
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
