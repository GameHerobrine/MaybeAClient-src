package net.skidcode.gh.maybeaclient.gui.click;

import net.skidcode.gh.maybeaclient.gui.click.element.Element;
import net.skidcode.gh.maybeaclient.gui.click.element.ScrollBarElement;
import net.skidcode.gh.maybeaclient.gui.click.element.VerticalContainer;

public abstract class ScrollableTab extends ElementTab{
	public ScrollBarElement scrollbar;
	public VerticalContainer container;
	
	public ScrollableTab(String name) {
		super(name);
		this.container = new VerticalContainer();
		this.scrollbar = new ScrollBarElement(this.container);
		super.addElement(this.scrollbar);
	}
	
	public void clearElements() {
		this.container.elements.clear();
	}
	public void addElement(Element e) {
		this.container.elements.add(e);
	}
}