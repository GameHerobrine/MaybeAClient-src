package net.skidcode.gh.maybeaclient.hacks.settings;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import net.minecraft.src.NBTTagCompound;
import net.skidcode.gh.maybeaclient.Client;
import net.skidcode.gh.maybeaclient.gui.click.ClickGUI;
import net.skidcode.gh.maybeaclient.gui.click.Tab;
import net.skidcode.gh.maybeaclient.gui.click.element.Element;
import net.skidcode.gh.maybeaclient.hacks.ClickGUIHack;
import net.skidcode.gh.maybeaclient.hacks.Hack;
import net.skidcode.gh.maybeaclient.hacks.ClickGUIHack.Theme;
import net.skidcode.gh.maybeaclient.utils.ChatColor;
import net.skidcode.gh.maybeaclient.utils.InputHandler;

public class SettingKeybind extends Setting implements InputHandler{
	
	public int value, initialValue;
	boolean activated = false;
	
	public SettingKeybind(Hack hack, String name, int initialValue) {
		super(hack, name);
		this.setValue(initialValue);
		this.initialValue = initialValue;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public void setValue(int d) {
		this.value = d;
	}
	
	@Override
	public String valueToString() {
		return Client.getKeyName(this.value);
	}

	public boolean validateValue(String value) {
		try {
			Integer.parseInt(value); //TODO better value parsing
			return true;
		}catch(NumberFormatException e) {
			return false;
		}
	}

	@Override
	public void reset() {
		this.setValue(this.initialValue);
	}
	
	public int getSettingWidth() {
		return Client.mc.fontRenderer.getStringWidth(this.name + ": "+ (listening ? ChatColor.LIGHTGRAY+"Listening..." : this.valueToString())) + 10;
	}
	
	@Override
	public void onDeselect(Element tab, int xMin, int yMin, int xMax, int yMax, int mouseX, int mouseY, int mouseClick) {
		if(selected) this.listening = ClickGUI.setInputHandler(this);
		selected = false;
	}
	public boolean selected = false;
	@Override
	public void onPressedInside(Element tab, int xMin, int yMin, int xMax, int yMax, int mouseX, int mouseY, int mouseClick) {
		selected = true;
	}
	
	public boolean canRelease = false;
	@Override
	public void onKeyPress(int keycode) {
		if(keycode == Keyboard.KEY_ESCAPE || Keyboard.KEY_NONE == keycode) {
			this.value = 0;
			canRelease = true;
		}else {
			this.value = keycode;
			canRelease = true;
		}
	}
	
	@Override
	public void onInputFocusStop() {
		this.listening = false;
		canRelease = false;
	}


	@Override
	public void onKeyRelease(int keycode) {
		if(canRelease) {
			ClickGUI.setInputHandler(null);
			Client.saveModules();
		}
		
	}
	
	@Override
	public void renderElement(Element tab, int xStart, int yStart, int xEnd, int yEnd) {
		Theme theme = ClickGUIHack.theme();
		if(theme == Theme.IRIDIUM) return;
		
		if(theme == Theme.NODUS) {
			Tab.renderFrameBackGround(xStart, yStart, xEnd, yEnd, 0, 0, 0, 0x80/255f);
		}else if(theme == Theme.HEPHAESTUS){
			
		}else if(theme == Theme.UWARE) {
			Tab.renderFrameBackGround(xStart, yStart, xEnd, yEnd, 0, 0, 0, Theme.UWARE_SETTING_OVERLAY_A);
		}else{
			Tab.renderFrameBackGround(xStart, yStart, xEnd, yEnd, ClickGUIHack.r(), ClickGUIHack.g(), ClickGUIHack.b(), 1f);
		}
	}
	
	@Override
	public void writeToNBT(NBTTagCompound output) {
		output.setInteger(this.name, this.value);
	}
	
	boolean listening = false;
	
	@Override
	public void renderText(Element tab, int x, int y, int xEnd, int yEnd) {
		Theme theme = ClickGUIHack.theme();
		int txtColor = 0xffffff;
		if(theme == Theme.NODUS) {
			txtColor = ClickGUIHack.instance.themeColor.rgb();
			if(this.mouseHovering) {
				txtColor = ClickGUIHack.instance.secColor.rgb();
			}
		}
		this.mouseHovering = false;
		if(theme == Theme.IRIDIUM) {
			Client.mc.fontRenderer.drawStringWithShadow(this.name + ": "+ (listening ? ChatColor.LIGHTGRAY+"Listening..." : this.valueToString()), x + 2, y + ClickGUIHack.theme().yaddtocenterText, Theme.IRIDIUM_ENABLED_COLOR);
			return;
		}
		if(theme == Theme.HEPHAESTUS){
			if(listening) {
				Client.mc.fontRenderer.drawStringWithShadow("Listening...", x+Theme.HEPH_OPT_XADD, y+ClickGUIHack.theme().yaddtocenterText, 0xffffff);
			}else {
				String s = this.valueToString();
				Client.mc.fontRenderer.drawStringWithShadow(this.name + ": ", x+Theme.HEPH_OPT_XADD, y+ClickGUIHack.theme().yaddtocenterText, 0xffffff);
				Client.mc.fontRenderer.drawStringWithShadow(s, xEnd-Theme.HEPH_OPT_XADD-Client.mc.fontRenderer.getStringWidth(s), y+ClickGUIHack.theme().yaddtocenterText, 0xffffff);
			}
		}else if(theme == Theme.UWARE){
			String s = this.name + ": "+ (listening ? ChatColor.LIGHTGRAY+"Listening..." : this.valueToString());
			int xstart = x + ((xEnd-x) - Client.mc.fontRenderer.getStringWidth(s))/2;
			txtColor = Theme.UWARE_ENABLED_COLOR;
			Client.mc.fontRenderer.drawString(s, xstart + 2, y + ClickGUIHack.theme().yaddtocenterText, txtColor);
		}else {
			Client.mc.fontRenderer.drawString(this.name + ": "+ (listening ? ChatColor.LIGHTGRAY+"Listening..." : this.valueToString()), x + 2, y + ClickGUIHack.theme().yaddtocenterText, txtColor);
		}
	}
	
	@Override
	public void readFromNBT(NBTTagCompound input) {
		if(input.hasKey(this.name)) this.setValue(input.getInteger(this.name));
	}
}
