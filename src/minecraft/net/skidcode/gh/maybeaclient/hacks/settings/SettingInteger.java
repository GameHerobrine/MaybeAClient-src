package net.skidcode.gh.maybeaclient.hacks.settings;

import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NBTTagInt;
import net.skidcode.gh.maybeaclient.Client;
import net.skidcode.gh.maybeaclient.gui.click.Tab;
import net.skidcode.gh.maybeaclient.gui.click.element.Element;
import net.skidcode.gh.maybeaclient.hacks.ClickGUIHack;
import net.skidcode.gh.maybeaclient.hacks.Hack;
import net.skidcode.gh.maybeaclient.hacks.ClickGUIHack.Theme;

public class SettingInteger extends Setting{
	public int minGUI, maxGUI;
	public int value, initialValue;
	
	public boolean fixedStep = false;
	public int step = 0;
	
	public SettingInteger(Hack hack, String name, int initialValue, int minGUI, int maxGUI) {
		super(hack, name);
		this.setValue(initialValue);
		this.initialValue = initialValue;
		this.minGUI = minGUI;
		this.maxGUI = maxGUI;
	}
	
	public SettingInteger(Hack hack, String name, int initialValue, int minGUI, int maxGUI, int step) {
		this(hack, name, initialValue, minGUI, maxGUI);
		this.fixedStep = true;
		this.step = step;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public void setValue(int d) {
		this.value = d;
	}
	
	@Override
	public String valueToString() {
		return ""+this.value;
	}

	public boolean validateValue(String value) {
		try {
			Integer.parseInt(value);
			return true;
		}catch(NumberFormatException e) {
			return false;
		}
	}

	@Override
	public void reset() {
		this.setValue(this.initialValue);
	}

	@Override
	public void writeToNBT(NBTTagCompound output) {
		output.setInteger(this.name, this.value);
	}
	
	@Override
	public void renderText(Element tab, int x, int y, int xEnd, int yEnd) {
		int txtColor = 0xffffff;
		if(ClickGUIHack.theme() == Theme.NODUS) {
			txtColor = ClickGUIHack.instance.themeColor.rgb();
			if(this.mouseHovering) {
				txtColor = ClickGUIHack.instance.secColor.rgb();
			}
		}
		if(ClickGUIHack.theme() == Theme.HEPHAESTUS) {
			Client.mc.fontRenderer.drawStringWithShadow(this.name, x + Theme.HEPH_OPT_XADD, y + ClickGUIHack.theme().yaddtocenterText, txtColor);
			Client.mc.fontRenderer.drawStringWithShadow(""+this.getValue(), xEnd - Theme.HEPH_OPT_XADD + 1 - Client.mc.fontRenderer.getStringWidth(""+this.getValue()), y + ClickGUIHack.theme().yaddtocenterText, txtColor);
		}else {
			Client.mc.fontRenderer.drawString(this.name + " - " + this.getValue(), x + 2, y + ClickGUIHack.theme().yaddtocenterText, txtColor);
		}
		
		this.mouseHovering = false;
	}
	
	public int getSettingWidth() {
		int w1 = Client.mc.fontRenderer.getStringWidth(this.name+" - "+this.getValue()) + 5;
		if(this.fixedStep) {
			int w2 = (this.maxGUI - this.minGUI)/this.step;
			if(w1 > w2) return w1;
			return (int)Math.floor(w2);
		}else {
			return w1;
		}
	}
	
	@Override
	public void onPressedInside(Element tab, int xMin, int yMin, int xMax, int yMax, int mouseX, int mouseY, int mouseClick) {
		if(ClickGUIHack.theme() == Theme.HEPHAESTUS) {
			xMin += 5;
			xMax -= 5;
		}
		int sizeX = xMax - xMin;
		int mouseOff = mouseX - xMin;
		double step = (double)(this.maxGUI - this.minGUI)/sizeX;
		
		int value = (int)Math.round(this.minGUI*100 + (double)mouseOff*step*100)/100;
		if(this.fixedStep) {
			int valueI = value;
			int stepI = this.step;
			int mod = valueI % stepI;
			
			value = valueI-mod;
		}
		selected = true;
		this.setValue(value);
	}
	public void renderElement(Element tab, int xStart, int yStart, int xEnd, int yEnd) {
		if(ClickGUIHack.theme() == Theme.HEPHAESTUS) {
			xStart += 5;
			xEnd -= 5;
		}
		double diff1 = xEnd - xStart;
		double diff2 = (this.maxGUI - this.minGUI)/diff1;
		
		int val = this.value;
		if(val > this.maxGUI) val = this.maxGUI;
		if(val < this.minGUI) val = this.minGUI;
		
		int diff3 = (int) Math.round(val/diff2 - this.minGUI/diff2);
		
		if(ClickGUIHack.theme() == Theme.HEPHAESTUS) {
			int sliderYbegin = yEnd - Theme.HEPH_SLIDER_HEIGHT;
			int sliderYend = yEnd;
			Tab.renderFrameBackGround(xStart, sliderYbegin, xEnd, sliderYend, 100/255f, 100/255f, 100/255f, 1);
			Tab.renderFrameBackGround(xStart, sliderYbegin, xStart+diff3, sliderYend, ClickGUIHack.r(), ClickGUIHack.g(), ClickGUIHack.b(), 1);
		}else if(ClickGUIHack.theme() == Theme.NODUS) {
			Tab.renderFrameBackGround(xStart, yStart, xStart + diff3, yEnd, 0, 0, 0, 0x80/255f);
		}else {
			Tab.renderFrameBackGround(xStart, yStart, xStart + diff3, yEnd, ClickGUIHack.r(), ClickGUIHack.g(), ClickGUIHack.b(), ClickGUIHack.theme() == Theme.IRIDIUM ? 0xaa/255f : 1f);
		}
	}
	public void onMouseMoved(int xMin, int yMin, int xMax, int yMax, int mouseX, int mouseY, int mouseClick) {
		if(!selected) return;
		if(ClickGUIHack.theme() == Theme.HEPHAESTUS) {
			xMin += 5;
			xMax -= 5;
		}
		double sizeX = xMax - xMin;
		int mouseOff = mouseX - xMin;
		double step = (this.maxGUI - this.minGUI)/sizeX;
		int value = (int)Math.round(this.minGUI*100 + mouseOff*step*100)/100;
		
		if(this.fixedStep) {
			int valueI = value;
			int stepI = this.step;
			int mod = valueI % stepI;
			value = valueI-mod;
		}
		
		if(value > this.maxGUI) value = this.maxGUI;
		if(value < this.minGUI) value = this.minGUI;
		this.setValue(value);
	}
	
	public boolean selected = false;
	@Override
	public void onDeselect(Element tab, int xMin, int yMin, int xMax, int yMax, int mouseX, int mouseY, int mouseClick) {
		this.selected = false;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound input) {
		if(input.hasKey(this.name) && input.tagMap.get(this.name) instanceof NBTTagInt) this.setValue(input.getInteger(this.name));
	}
}
