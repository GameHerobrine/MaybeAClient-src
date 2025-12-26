package net.skidcode.gh.maybeaclient.hacks.settings;

import java.util.ArrayList;
import java.util.HashSet;

import net.minecraft.src.NBTBase;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NBTTagList;
import net.minecraft.src.NBTTagString;
import net.skidcode.gh.maybeaclient.Client;
import net.skidcode.gh.maybeaclient.gui.click.Tab;
import net.skidcode.gh.maybeaclient.gui.click.element.Element;
import net.skidcode.gh.maybeaclient.hacks.ClickGUIHack;
import net.skidcode.gh.maybeaclient.hacks.Hack;
import net.skidcode.gh.maybeaclient.hacks.ClickGUIHack.Theme;

public class SettingIgnoreList extends Setting{
	public boolean enabled = true;
	public HashSet<String> names = new HashSet<>();
	
	public SettingIgnoreList(Hack hack, String name) {
		super(hack, name);
	}
	
	public boolean contains(String name) {
		name = name.toLowerCase();
		return this.names.contains(name);
	}
	
	public void setValue(String name) {
		name = name.toLowerCase();
		if(this.names.contains(name)) {
			this.names.remove(name);
		}else {
			this.names.add(name);
		}
	}
	
	@Override
	public String valueToString() {
		String nms = String.join(", ", names);
		return nms;
	}

	@Override
	public void reset() {
		this.names.clear();
		this.enabled = true;
	}

	@Override
	public boolean validateValue(String value) {
		return true;
	}

	@Override
	public void writeToNBT(NBTTagCompound output) {
		NBTTagCompound comp = new NBTTagCompound();
		NBTTagList list = new NBTTagList();
		comp.setBoolean("Enabled", this.enabled);
		for(String s : this.names) {
			NBTTagString st = new NBTTagString();
			st.stringValue = s;
			list.setTag(st);
		}
		comp.setTag("Names", list);
		output.setTag(this.name, comp);
	}

	@Override
	public void readFromNBT(NBTTagCompound input) {
		if(input.tagMap.containsKey(this.name)) {
			NBTTagCompound tag = input.getCompoundTag(this.name);
			
			this.enabled = tag.getBoolean("Enabled");
			NBTTagList list = tag.getTagList("Names");
			if(list != null) {
				for(int i = 0; i < list.tagCount(); ++i) {
					NBTBase nb = list.tagAt(i);
					if(nb instanceof NBTTagString) {
						NBTTagString str = (NBTTagString) nb;
						this.names.add(str.stringValue);
					}else {
						System.out.println("[MaybeAClient] Ignore list contains invalid tag types! "+nb.getType());
					}
				}
			}
		}
	}
	@Override
	public void renderText(int x, int y) {
		if(ClickGUIHack.theme() == Theme.IRIDIUM) {
			Client.mc.fontRenderer.drawStringWithShadow(this.name, x+2, y + ClickGUIHack.theme().yaddtocenterText, this.enabled ? Theme.IRIDIUM_ENABLED_COLOR : Theme.IRIDIUM_DISABLED_COLOR);
			this.mouseHovering = false;
			return;
		}
		if(ClickGUIHack.theme() == Theme.HEPHAESTUS) {
			Client.mc.fontRenderer.drawStringWithShadow(this.name, x + Theme.HEPH_OPT_XADD, y + ClickGUIHack.theme().yaddtocenterText, this.enabled ? 0xffffff : Theme.HEPH_DISABLED_COLOR);
			this.mouseHovering = false;
			return;
		}
		super.renderText(x, y);
	}
	@Override
	public void renderElement(Element tab, int xStart, int yStart, int xEnd, int yEnd) {
		if(this.enabled) {
			if(ClickGUIHack.theme() == Theme.HEPHAESTUS) return;
			if(ClickGUIHack.theme() == Theme.IRIDIUM) return;
			if(ClickGUIHack.theme() == Theme.NODUS) {
				Tab.renderFrameBackGround(xStart, yStart, xEnd, yEnd, 0, 0, 0, 0x80/255f);
			}else {
				Tab.renderFrameBackGround(xStart, yStart, xEnd, yEnd, ClickGUIHack.r(), ClickGUIHack.g(), ClickGUIHack.b(), 1f);
			}
		}
	}
	
	@Override
	public void onPressedInside(Element tab, int xMin, int yMin, int xMax, int yMax, int mouseX, int mouseY, int mouseClick) {
		this.enabled = !this.enabled;
	}
}
