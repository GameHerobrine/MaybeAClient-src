package net.skidcode.gh.maybeaclient.hacks.settings;

import net.minecraft.src.NBTTagCompound;
import net.skidcode.gh.maybeaclient.Client;
import net.skidcode.gh.maybeaclient.gui.click.Tab;
import net.skidcode.gh.maybeaclient.hacks.ClickGUIHack;
import net.skidcode.gh.maybeaclient.hacks.Hack;
import net.skidcode.gh.maybeaclient.hacks.ClickGUIHack.Theme;

public class SettingLong extends Setting{
	public long minGUI, maxGUI;
	public long value, initialValue;
	public long step = 0;
	public boolean fixedStep = false;
	public SettingLong(Hack hack, String name, long initialValue, long minGUI, long maxGUI) {
		super(hack, name);
		this.setValue(initialValue);
		this.initialValue = initialValue;
		this.minGUI = minGUI;
		this.maxGUI = maxGUI;
	}
	public SettingLong(Hack hack, String name, long initialValue, long minGUI, long maxGUI, long step) {
		this(hack, name, initialValue, minGUI, maxGUI);
		this.step = step;
		this.fixedStep = true;
	}
	public long getValue() {
		return this.value;
	}
	
	public void setValue(long d) {
		this.value = d;
	}
	
	@Override
	public String valueToString() {
		return ""+this.value;
	}

	public boolean validateValue(String value) {
		try {
			Long.parseLong(value);
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
		output.setLong(this.name, this.value);
	}
	@Override
	public void renderText(Tab tab, int x, int y, int xEnd, int yEnd) {
		
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
			int w2 = (int) ((this.maxGUI - this.minGUI)/this.step);
			if(w1 > w2) return w1;
			return (int)Math.floor(w2);
		}else {
			return w1;
		}
	}
	@Override
	public void onPressedInside(Tab tab, int xMin, int yMin, int xMax, int yMax, int mouseX, int mouseY, int mouseClick) {
		if(ClickGUIHack.theme() == Theme.HEPHAESTUS) {
			xMin += 5;
			xMax -= 5;
		}
		int sizeX = xMax - xMin;
		int mouseOff = mouseX - xMin;
		double step = ((double)this.maxGUI - this.minGUI)/sizeX;
		long value = (long)Math.round((double)this.minGUI*100 + mouseOff*step*100)/100;
		if(this.fixedStep) {
			long valueI = value;
			long stepI = this.step;
			long mod = valueI % stepI;
			
			value = valueI-mod;
		}
		
		this.setValue(value);
	}
	public void renderElement(Tab tab, int xStart, int yStart, int xEnd, int yEnd) {
		if(ClickGUIHack.theme() == Theme.HEPHAESTUS) {
			xStart += 5;
			xEnd -= 5;
		}
		
		int diff1 = xEnd - xStart;
		double diff2 = ((double)this.maxGUI - this.minGUI)/diff1;
		
		long val = this.value;
		if(val > this.maxGUI) val = this.maxGUI;
		if(val < this.minGUI) val = this.minGUI;
		
		int diff3 = (int) (val/diff2 - this.minGUI/diff2);
		if(ClickGUIHack.theme() == Theme.HEPHAESTUS) {
			int sliderYbegin = yEnd - Theme.HEPH_SLIDER_HEIGHT;
			int sliderYend = yEnd;
			tab.renderFrameBackGround(xStart, sliderYbegin, xEnd, sliderYend, 100/255f, 100/255f, 100/255f, 1);
			tab.renderFrameBackGround(xStart, sliderYbegin, xStart+diff3, sliderYend, ClickGUIHack.r(), ClickGUIHack.g(), ClickGUIHack.b(), 1);
			
		}else if(ClickGUIHack.theme() == Theme.NODUS) {
			tab.renderFrameBackGround(xStart, yStart, xStart + diff3, yEnd, 0, 0, 0, 0x80/255f);
		}else {
			tab.renderFrameBackGround(xStart, yStart, xStart + diff3, yEnd, ClickGUIHack.r(), ClickGUIHack.g(), ClickGUIHack.b(), 1f);
		}
		
		
	}
	public void onMouseMoved(int xMin, int yMin, int xMax, int yMax, int mouseX, int mouseY, int mouseClick) {
		if(ClickGUIHack.theme() == Theme.HEPHAESTUS) {
			xMin += 5;
			xMax -= 5;
		}
		int sizeX = xMax - xMin;
		int mouseOff = mouseX - xMin;
		double step = ((double)this.maxGUI - this.minGUI)/sizeX;
		
		long value = (long)Math.round(this.minGUI*100 + mouseOff*step*100)/100;
		if(value > this.maxGUI) value = this.maxGUI;
		if(value < this.minGUI) value = this.minGUI;
		if(this.fixedStep) {
			long valueI = value;
			long stepI = this.step;
			long mod = valueI % stepI;
			value = valueI-mod;
		}
		this.setValue(value);
	}
	
	public void onDeselect(Tab tab, int mouseX, int mouseY, int mouseClick) {
		
	}
	
	@Override
	public void readFromNBT(NBTTagCompound input) {
		if(input.hasKey(this.name)) this.setValue(input.getLong(this.name));
	}
}
