package net.skidcode.gh.maybeaclient.hacks.settings;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import net.minecraft.src.NBTTagCompound;
import net.skidcode.gh.maybeaclient.Client;
import net.skidcode.gh.maybeaclient.gui.click.Tab;
import net.skidcode.gh.maybeaclient.gui.click.element.Element;
import net.skidcode.gh.maybeaclient.hacks.ClickGUIHack;
import net.skidcode.gh.maybeaclient.hacks.Hack;
import net.skidcode.gh.maybeaclient.hacks.ClickGUIHack.Theme;
import net.skidcode.gh.maybeaclient.utils.RenderUtils;

public strictfp class SettingFloat extends Setting{
	
	public float value, initialValue;
	public float minGUI, maxGUI;
	
	public int additionalWidth = 0;
	
	public boolean fixedStep = false;
	public float step = 0;
	
	public SettingFloat(Hack hack, String name, float initialValue, float minGUI, float maxGUI) {
		super(hack, name);
		this.setValue(initialValue);
		this.initialValue = initialValue;
		this.minGUI = minGUI;
		this.maxGUI = maxGUI;
	}
	public SettingFloat(Hack hack, String name, float initialValue, float minGUI, float maxGUI, float step) {
		this(hack, name, initialValue, minGUI, maxGUI);
		this.fixedStep = true;
		this.step = step;
	}
	public float getValue() {
		return this.value;
	}
	
	public void setValue(float d) {
		this.value = d;
	}
	
	@Override
	public String valueToString() {
		return ""+this.value;
	}

	public boolean validateValue(String value) {
		try {
			Float.parseFloat(value);
			return true;
		}catch(NumberFormatException e) {
			return false;
		}
	}
	@Override
	public void onPressedInside(Element tab, int xMin, int yMin, int xMax, int yMax, int mouseX, int mouseY, int mouseClick) {
		if(ClickGUIHack.theme() == Theme.HEPHAESTUS) {
			xMin += 5;
			xMax -= 5;
		}
		if(ClickGUIHack.theme() == Theme.UWARE) {
			xMin += 2;
			xMax -= 2;
		}
		int sizeX = xMax - xMin;
		int mouseOff = mouseX - xMin;
		float step = (this.maxGUI - this.minGUI)/sizeX;
		float value = (float)Math.round(this.minGUI*100 + mouseOff*step*100)/100;
		if(this.fixedStep) {
			int valueI = Math.round(value*100);
			int stepI = Math.round(this.step*100);
			int mod = valueI % stepI;
			value = (float)(valueI-mod)/100;
		}
		if(value > this.maxGUI) value = this.maxGUI;
		if(value < this.minGUI) value = this.minGUI;
		
		int v = (int) (value*100);
		float f = (float)((double)v/100d);
		this.setValue(value);
		selected = true;
	}
	
	public boolean selected = false;
	
	public void renderElement(Element tab, int xStart, int yStart, int xEnd, int yEnd) {
		if(ClickGUIHack.theme() == Theme.HEPHAESTUS) {
			xStart += 5;
			xEnd -= 5;
		}
		if(ClickGUIHack.theme() == Theme.UWARE) {
			Tab.renderFrameBackGround(xStart, yStart, xEnd, yEnd, 0x16/255f, 0x16/255f, 0x16/255f, 0xaa/255f);
			xStart += 2;
			xEnd -= 2;
		}
		int diff1 = xEnd - xStart;
		float step = (this.maxGUI - this.minGUI)/diff1;
		
		float val = this.value;
		if(val > this.maxGUI) val = this.maxGUI;
		if(val < this.minGUI) val = this.minGUI;
		
		int diff3 = (int) Math.round(val/step - this.minGUI/step);
		if(ClickGUIHack.theme() == Theme.UWARE) {
			int sliderYbegin = yEnd - Theme.UWARE_SLIDER_HEIGHT;
			int sliderYend = yEnd;
			RenderUtils.glColor(Theme.UWARE_OPT_D_COLOR);
			Tab.renderRoundedFrameBackGround(xStart, sliderYbegin, xEnd, sliderYend, 1);
			RenderUtils.glColor(Theme.UWARE_OPT_E_COLOR);
			Tab.renderRoundedFrameBackGround(xStart, sliderYbegin, xStart+diff3, sliderYend, 1);
			return;
		}else if(ClickGUIHack.theme() == Theme.HEPHAESTUS) {
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
	
	@Override
	public void onDeselect(Element tab, int xMin, int yMin, int xMax, int yMax, int mouseX, int mouseY, int mouseClick) {
		selected = false;
	}
	
	@Override
	public void onMouseMoved(int xMin, int yMin, int xMax, int yMax, int mouseX, int mouseY, int mouseClick) {
		if(!selected) return;
		if(ClickGUIHack.theme() == Theme.HEPHAESTUS) {
			xMin += 5;
			xMax -= 5;
		}
		if(ClickGUIHack.theme() == Theme.UWARE) {
			xMin += 2;
			xMax -= 2;
		}
		int sizeX = xMax - xMin;
		int mouseOff = mouseX - xMin;
		float step = (this.maxGUI - this.minGUI)/sizeX;
		float value = (float)Math.round(this.minGUI*100 + mouseOff*step*100)/100;
		
		if(this.fixedStep) {
			int valueI = Math.round(value*100);
			int stepI = Math.round(this.step*100);
			int mod = valueI % stepI;
			value = (float)(valueI-mod)/100;
		}
		
		if(value > this.maxGUI) value = this.maxGUI;
		if(value < this.minGUI) value = this.minGUI;
		this.setValue(value);
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
		String value = String.format("%.2f", this.getValue());
		if(ClickGUIHack.theme() == Theme.HEPHAESTUS) {
			Client.mc.fontRenderer.drawStringWithShadow(this.name, x + Theme.HEPH_OPT_XADD, y + ClickGUIHack.theme().yaddtocenterText, txtColor);
			Client.mc.fontRenderer.drawStringWithShadow(value, xEnd - Theme.HEPH_OPT_XADD + 1 - Client.mc.fontRenderer.getStringWidth(value), y + ClickGUIHack.theme().yaddtocenterText, txtColor);
		}else {
			Client.mc.fontRenderer.drawString(this.name + " - " + value, x + 2, y + ClickGUIHack.theme().yaddtocenterText, txtColor);
		}
		this.mouseHovering = false;
	}
	
	public int getSettingWidth() {
		int w1 = Client.mc.fontRenderer.getStringWidth(this.name+" - "+String.format("%.2f", this.getValue())) + 5;
		if(this.fixedStep) {
			float w2 = (this.maxGUI - this.minGUI)/this.step;
			if(w1 > w2) return w1;
			return (int)Math.floor(w2);
		}else {
			return w1;
		}
	}
	
	@Override
	public void reset() {
		this.setValue(this.initialValue);
	}

	@Override
	public void writeToNBT(NBTTagCompound output) {
		output.setFloat(this.name, this.value);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound input) {
		if(input.hasKey(this.name)) this.setValue(input.getFloat(this.name));
	}
}
