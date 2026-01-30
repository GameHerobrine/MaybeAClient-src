package net.skidcode.gh.maybeaclient.hacks.settings;

import java.util.HashMap;

import net.minecraft.src.NBTTagCompound;
import net.skidcode.gh.maybeaclient.Client;
import net.skidcode.gh.maybeaclient.gui.click.Tab;
import net.skidcode.gh.maybeaclient.gui.click.element.Element;
import net.skidcode.gh.maybeaclient.hacks.ClickGUIHack;
import net.skidcode.gh.maybeaclient.hacks.Hack;
import net.skidcode.gh.maybeaclient.hacks.NoRenderHack;
import net.skidcode.gh.maybeaclient.hacks.ClickGUIHack.Theme;
import net.skidcode.gh.maybeaclient.utils.ChatColor;

public class SettingChooser extends Setting{
	
	public HashMap<String, Boolean> value = new HashMap<>();
	public String[] choices;
	public boolean[] initial;
	public boolean minimized = false;
	
	public SettingChooser(SettingsProvider hack, String name, String[] choices, boolean[] initial) {
		super(hack, name);
		if(choices.length != initial.length) throw new RuntimeException("Lengths of choices and initial are different!");
		
		this.initial = initial;
		this.choices = choices;
		this.setValue(initial);
	}
	
	public void setValue(boolean[] values) {
		for(int i = 0; i < this.choices.length; ++i) {
			this.setValue(this.choices[i], values[i]);
		}
	}
	
	public boolean getValue(String key) {
		return this.value.get(key.toLowerCase());
	}
	
	public void setValue(String name, boolean value) {
		this.value.put(name.toLowerCase(), value);
	}
	@Override
	public String valueToString() {
		String s = "";
		for(int i = 0; i < this.choices.length; ++i) {
			String m = this.choices[i];
			
			s += this.getValue(m) ? ChatColor.LIGHTGREEN : ChatColor.LIGHTRED;
			s += m;
			s += ChatColor.WHITE;
			s += ";";
		}
		return s.substring(0, s.length()-1);
	}
	public String valueToStringConsole() {
		String s = ChatColor.WHITE+"";
		for(int i = 0; i < this.choices.length; ++i) {
			String m = this.choices[i];
			
			s += this.getValue(m) ? ChatColor.LIGHTGREEN : ChatColor.LIGHTRED;
			s += m;
			s += ChatColor.WHITE;
			s += ";";
		}
		
		return s.substring(0, s.length()-1);
	}
	@Override
	public void reset() {
		this.setValue(this.initial);
	}

	@Override
	public boolean validateValue(String value) {
		
		String[] splitted = value.split(";");
		
		for(String s : splitted) {
			if(!this.value.containsKey(s.toLowerCase())) {
				return false;
			}
		}
		
		return true;
	}

	@Override
	public void writeToNBT(NBTTagCompound output) {
		NBTTagCompound val = new NBTTagCompound();
		for(int i = 0; i < this.choices.length; ++i) {
			val.setBoolean(this.choices[i], this.value.get(this.choices[i].toLowerCase()));
		}
		val.setBoolean("Minimized", this.minimized);
		output.setCompoundTag(this.name, val);
	}

	@Override
	public void readFromNBT(NBTTagCompound input) {
		NBTTagCompound val = input.getCompoundTag(this.name);
		
		if(!val.tagMap.isEmpty()) {
			this.minimized = val.getBoolean("Minimized");
			for(int i = 0; i < this.choices.length; ++i) {
				this.setValue(this.choices[i], val.getBoolean(this.choices[i]));
			}
		}
	}
	@Override
	public int getSettingWidth() {
		int wid = super.getSettingWidth();
		
		for(int i = 0; i < this.choices.length; ++i) {
			int wid2 = Client.mc.fontRenderer.getStringWidth(this.choices[i]) + 4;
			if(wid2 > wid) wid = wid2;
		}
		
		return wid;
	}
	
	@Override
	public void renderElement(Element tab, int xStart, int yStart, int xEnd, int yEnd) {
		
		int ySpace = ClickGUIHack.theme().yspacing;
		int yReduce = ClickGUIHack.theme().settingYreduce;
		if(ClickGUIHack.theme() == Theme.UWARE) {
			Tab.renderFrameBackGround(xStart, yStart, xEnd, yEnd, 0x16/255f, 0x16/255f, 0x16/255f, 0xaa/255f);
		}
		if(this.minimized) return;
		if(ClickGUIHack.theme() == Theme.IRIDIUM) return;
		if(ClickGUIHack.theme() == Theme.NODUS) {
			Tab.renderFrameBackGround(xStart, yStart, xEnd, yStart + ySpace, 0, 0, 0, 0x80/255f);
		}else if(ClickGUIHack.theme() == Theme.HEPHAESTUS){
			
		}else if(ClickGUIHack.theme() != Theme.UWARE){
			Tab.renderFrameBackGround(xStart, yStart, xEnd, yStart + ySpace-yReduce, ClickGUIHack.r(), ClickGUIHack.g(), ClickGUIHack.b(), 1f);
		}
		
		yStart += ySpace;
		
		for(int i = 0; i < this.choices.length; ++i) {
			boolean bb = this.getValue(this.choices[i]);
			if(ClickGUIHack.theme() == Theme.HEPHAESTUS) {
				float r, g, b, a = 128f/255f;
				g = b = r = 21/255f;
				if(bb) {
					r = ClickGUIHack.r();
					g = ClickGUIHack.g();
					b = ClickGUIHack.b();
					a = 255/255f;
				}
				Tab.renderFrameBackGround(xEnd - Theme.HEPH_OPT_XADD - 7, yStart+3, xEnd - Theme.HEPH_OPT_XADD + 1, yStart + ySpace - 3, r, g, b, a);
				Tab.renderFrameOutlines((double)xEnd - Theme.HEPH_OPT_XADD - 7, (double)yStart+3, (double)xEnd - Theme.HEPH_OPT_XADD + 1, (double)yStart + ySpace - 3);
				
			}else if(bb) {
				if(ClickGUIHack.theme() == Theme.NODUS) {
					Tab.renderFrameBackGround(xStart, yStart, xEnd, yStart + ySpace, 0, 0, 0, 0x80/255f);
				}else if(ClickGUIHack.theme() == Theme.UWARE) {
					Tab.renderFrameBackGround(xStart, yStart, xEnd, yStart + ySpace-yReduce, 0x16/255f, 0x16/255f, 0x16/255f, 0xaa/255f);
				}else {
					Tab.renderFrameBackGround(xStart, yStart, xEnd, yStart + ySpace-yReduce, ClickGUIHack.r(), ClickGUIHack.g(), ClickGUIHack.b(), 1f);
				}
			}
			yStart += ySpace;
		}
	}
	public int lastPressed = -1;
	@Override
	public void onPressedInside(Element tab, int xMin, int yMin, int xMax, int yMax, int mouseX, int mouseY, int mouseClick) {
		if(this.lastPressed != -1) return;
		int ySpace = ClickGUIHack.theme().yspacing;
		int diff = mouseY - yMin;
		int md = diff / ySpace;
		if(md > 0) {
			if(ClickGUIHack.theme() == Theme.HEPHAESTUS) {
				int d = diff % ySpace;
				System.out.println(d);
				if(mouseX < (xMax - Theme.HEPH_OPT_XADD - 7) || mouseX > (xMax - Theme.HEPH_OPT_XADD + 1) || d < 3 || d > (ySpace-3)) {
					this.lastPressed = md;
					return;
				}
			}
			md -= 1;
			this.setValue(this.choices[md], !this.getValue(this.choices[md]));
		}else if(md == 0){
			this.minimized = !this.minimized;
		}else {
			return;
		}
		
		this.lastPressed = md;
	}
	
	@Override
	public void onDeselect(Element tab, int xMin, int yMin, int xMax, int yMax, int mouseX, int mouseY, int mouseClick) {
		this.lastPressed = -1;
	}
	@Override
	public void renderText(Element tab, int x, int y, int xEnd, int yEnd) {
		int ySpace = ClickGUIHack.theme().yspacing;
		int txtColor = 0xffffff;
		if(ClickGUIHack.theme() == Theme.NODUS) {
			txtColor = ClickGUIHack.instance.themeColor.rgb();
			if(this.mouseHovering) {
				if(hmouseY >= y && hmouseY <= (y+ySpace) && hmouseX >= x && hmouseX <= xEnd) {
					txtColor = ClickGUIHack.instance.secColor.rgb();
					this.mouseHovering = false;
				}
			}
		}
		
		if(ClickGUIHack.theme() == Theme.IRIDIUM) {
			if(this == NoRenderHack.instance.particles) {
				txtColor = this.minimized ? Theme.IRIDIUM_DISABLED_COLOR : Theme.IRIDIUM_ENABLED_COLOR;
			}else {
				txtColor = Theme.IRIDIUM_ENABLED_COLOR;
			}
			
			Client.mc.fontRenderer.drawStringWithShadow(this.name, x + 2, y + ClickGUIHack.theme().yaddtocenterText, txtColor);
		}else if(ClickGUIHack.theme() == Theme.HEPHAESTUS) {
			if(this == NoRenderHack.instance.particles) {
				txtColor = this.minimized ? Theme.HEPH_DISABLED_COLOR : 0xffffff;
			}
			Client.mc.fontRenderer.drawStringWithShadow(this.name, x + Theme.HEPH_OPT_XADD, y + ClickGUIHack.theme().yaddtocenterText, txtColor);
			String s = this.minimized ? "+" : "-";
			Client.mc.fontRenderer.drawStringWithShadow(s, xEnd - Theme.HEPH_OPT_XADD - Client.mc.fontRenderer.getStringWidth(s) + 1, y + ClickGUIHack.theme().yaddtocenterText, txtColor);
		}else {
			Client.mc.fontRenderer.drawString(this.name, x + 2, y + ClickGUIHack.theme().yaddtocenterText, txtColor);
		}
		
		if(this.minimized) return;
		
		int rx = x + 2 + 4;
		int ry = y + ySpace;
		
		for(int i = 0; i < this.choices.length; ++i){
			txtColor = 0xffffff;
			if(ClickGUIHack.theme() == Theme.NODUS) {
				txtColor = ClickGUIHack.instance.themeColor.rgb();
				if(this.mouseHovering) {
					if(hmouseY >= (ry+i*ySpace) && hmouseY <= (ry+i*ySpace+ySpace) && hmouseX >= rx && hmouseX <= xEnd) {
						txtColor = ClickGUIHack.instance.secColor.rgb();
						this.mouseHovering = false;
					}
				}
			}
			if(ClickGUIHack.theme() == Theme.IRIDIUM) {
				int color = txtColor = !this.getValue(this.choices[i]) ? Theme.IRIDIUM_DISABLED_COLOR : Theme.IRIDIUM_ENABLED_COLOR;
				
				Client.mc.fontRenderer.drawStringWithShadow(this.choices[i], rx + 2, ry + i*ySpace + ClickGUIHack.theme().yaddtocenterText, color);
			}else if(ClickGUIHack.theme() == Theme.HEPHAESTUS) {
				int color = 0xffffff;
				//if(!this.getValue(this.choices[i])) color = Theme.HEPH_DISABLED_COLOR;
				
				Client.mc.fontRenderer.drawStringWithShadow(this.choices[i], rx + Theme.HEPH_OPT_XADD + 2, ry + i*ySpace + ClickGUIHack.theme().yaddtocenterText, color);
			}else {
				Client.mc.fontRenderer.drawString(this.choices[i], rx + 2, ry + i*ySpace + ClickGUIHack.theme().yaddtocenterText, txtColor);
			}
		}
		
	}
	
	public int getSettingHeight() {
		if(this.minimized) return ClickGUIHack.theme().yspacing;
		return ClickGUIHack.theme().yspacing*this.choices.length + ClickGUIHack.theme().yspacing;
	}
}
