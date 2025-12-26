package net.skidcode.gh.maybeaclient.hacks.settings;

import java.util.ArrayList;

import net.minecraft.src.NBTTagCompound;
import net.skidcode.gh.maybeaclient.Client;
import net.skidcode.gh.maybeaclient.gui.click.SettingsTab;
import net.skidcode.gh.maybeaclient.gui.click.Tab;
import net.skidcode.gh.maybeaclient.hacks.ClickGUIHack;
import net.skidcode.gh.maybeaclient.hacks.Hack;
import net.skidcode.gh.maybeaclient.hacks.ClickGUIHack.Theme;

public abstract class Setting implements SettingsProvider{
	public String name;
	public String noWhitespacesName;
	public boolean hidden = false;
	public Hack hack;
	public ArrayList<Setting> settings = new ArrayList<>();
	public Tab settingTab = null;
	
	public Setting(Hack hack, String name) {
		this.name = name;
		this.noWhitespacesName = name.replace(" ", "");
		this.hack = hack;
	}
	public ArrayList<Setting> getSettings(){
		return this.settings;
	}
	public abstract String valueToString();
	public abstract void reset();
	public abstract boolean validateValue(String value);
	public String valueToStringConsole() {
		return this.valueToString();
	}
	public void setValue_(String value) {
		if(this instanceof SettingDouble) {
			((SettingDouble)this).setValue(Double.parseDouble(value));
		}
		else if(this instanceof SettingMode) {
			((SettingMode)this).setValue(value);
		}
		else if(this instanceof SettingFloat) {
			((SettingFloat)this).setValue(Float.parseFloat(value));
		}
		else if(this instanceof SettingLong) {
			((SettingLong)this).setValue(Long.parseLong(value));
		}
		else if(this instanceof SettingBoolean) {
			((SettingBoolean)this).setValue(Boolean.parseBoolean(value));
		}
		else if(this instanceof SettingChooser) {
			
			SettingChooser sc = (SettingChooser)this; 
			
			String[] splitted = value.split(";");
			for(String s : sc.choices) {
				sc.setValue(s, false);
			}
			for(String s : splitted) {
				sc.setValue(s, true);
			}
		}
		else if(this instanceof SettingColor) {
			String[] splitted = value.split(";");
			int r = Integer.parseInt(splitted[0]);
			int g = Integer.parseInt(splitted[1]);
			int b = Integer.parseInt(splitted[2]);
			((SettingColor)this).setValue(r, g, b);
		}else if(this instanceof SettingKeybind) {
			//TODO better way
			((SettingKeybind)this).setValue(Integer.parseInt(value));
		}
		else if(this instanceof SettingBlockChooser) {
			int i = Integer.parseInt(value); //TODO better setter
			((SettingBlockChooser) this).blocks[i] = !((SettingBlockChooser) this).blocks[i];
			((SettingBlockChooser) this).blockChanged(i);
		}
		else if(this instanceof SettingIgnoreList) {
			((SettingIgnoreList) this).setValue(value);
		}
		else if(this instanceof SettingInteger) {
			int i = Integer.parseInt(value);
			((SettingInteger) this).setValue(i);
		}else if(this instanceof SettingTextBox) {
			((SettingTextBox) this).setValue(value);
		}
		else {
			throw new RuntimeException("Tried setting value "+value+" for "+this);
		}
	}
	
	public void hide() {
		this.hidden = true;
		this.hack.hiddens += 1;
	}
	
	public void show() {
		this.hidden = false;
		this.hack.hiddens -= 1;
	}
	
	public void renderText(Tab tab, int x, int y, int xEnd, int yEnd) {
		this.renderText(x, y);
	}
	public void renderText(int x, int y) {
		int txtColor = 0xffffff;
		if(ClickGUIHack.theme() == Theme.HEPHAESTUS) {
			Client.mc.fontRenderer.drawStringWithShadow(this.name, x + Theme.HEPH_OPT_XADD, y + ClickGUIHack.theme().yaddtocenterText, 0xffffff);
			this.mouseHovering = false;
			return;
		}
		if(ClickGUIHack.theme() == Theme.NODUS) {
			txtColor = ClickGUIHack.instance.themeColor.rgb();
			if(this.mouseHovering) {
				txtColor = ClickGUIHack.instance.secColor.rgb();
			}
		}
		
		Client.mc.fontRenderer.drawString(this.name, x + 2, y + ClickGUIHack.theme().yaddtocenterText, txtColor);
		this.mouseHovering = false;
	}
	
	public void onPressedInside(Tab tab, int xMin, int yMin, int xMax, int yMax, int mouseX, int mouseY, int mouseClick) {
		
	}
	public void onDeselect(Tab tab, int xMin, int yMin, int xMax, int yMax, int mouseX, int mouseY, int mouseClick) {

	}
	public void renderElement(Tab tab, int xStart, int yStart, int xEnd, int yEnd) {
		
	}
	public void onMouseMoved(int xMin, int yMin, int xMax, int yMax, int mouseX, int mouseY, int mouseClick) {
		
	}
	public int getSettingWidth() {
		return Client.mc.fontRenderer.getStringWidth(this.name) + 10 + (ClickGUIHack.theme() == Theme.HEPHAESTUS ? Theme.HEPH_OPT_XADD*2 : 0);
	}
	
	public int getSettingHeight() {
		return ClickGUIHack.theme().yspacing + (ClickGUIHack.theme() == Theme.HEPHAESTUS ? 0 : 0);
	}
	
	
	public abstract void writeToNBT(NBTTagCompound output);
	public abstract void readFromNBT(NBTTagCompound input);

	public int getSettingHeight(Tab tab) {
		return this.getSettingHeight();
	}

	public boolean mouseHovering = false;
	public int hmouseX, hmouseY;
	public void mouseHovered(int x, int y, int click) {
		this.hmouseX = x;
		this.hmouseY = y;
		this.mouseHovering = true;
	}

	
}
