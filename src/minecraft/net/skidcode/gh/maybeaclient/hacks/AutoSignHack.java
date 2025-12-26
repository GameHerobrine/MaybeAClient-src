package net.skidcode.gh.maybeaclient.hacks;

import org.lwjgl.input.Keyboard;

import lunatrius.schematica.GuiSchematicLoad;
import net.minecraft.src.GuiEditSign;
import net.minecraft.src.NBTBase;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NBTTagList;
import net.minecraft.src.NBTTagString;
import net.skidcode.gh.maybeaclient.gui.GuiSetAutoSignText;
import net.skidcode.gh.maybeaclient.gui.click.Tab;
import net.skidcode.gh.maybeaclient.gui.click.element.Element;
import net.skidcode.gh.maybeaclient.hacks.category.Category;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingBoolean;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingButton;

public class AutoSignHack extends Hack{
	public static final int MAXSIGNTEXTLENGTH = 15;
	public static AutoSignHack instance;
	public String[] text = new String[] {"", "", "", ""};
	public SettingButton openLoad = new SettingButton(this, "Edit Default Sign Text") {
		@Override
		public void setValue_(String value) {
			String[] as = value.split(" ");
			int i = Integer.parseInt(as[0]);
			String txt = "";
			for(int j = 1; j < as.length; ++j) txt = as[j]+" ";
			txt = txt.trim();
			text[i-1] = txt;
		}
		
		@Override
		public boolean validateValue(String value) {
			try {
				String[] as = value.split(" ");
				int i = Integer.parseInt(as[0]);
				if(i < 1 || i > 4) return false;
				String txt = "";
				for(int j = 1; j < as.length; ++j) txt = as[j]+" ";
				txt = txt.trim();
				if(txt.length() > MAXSIGNTEXTLENGTH) return false;
				return true;
			}catch(NumberFormatException e) {
				return false;
			}
		}
		
		@Override
		public void writeToNBT(NBTTagCompound output) {
			NBTTagList lst = new NBTTagList(); 
			lst.setTag(new NBTTagString(text[0]));
			lst.setTag(new NBTTagString(text[1]));
			lst.setTag(new NBTTagString(text[2]));
			lst.setTag(new NBTTagString(text[3]));
			output.setTag(this.name, lst);
		}
		
		@Override
		public void readFromNBT(NBTTagCompound input) {
			if(input.hasKey(this.name)) {
				NBTTagList list = input.getTagList(this.name);
				if(list.tagType == NBTBase.STRING && list.tagCount() >= 4) {
					text[0] = ((NBTTagString)list.tagAt(0)).stringValue;
					text[1] = ((NBTTagString)list.tagAt(1)).stringValue;
					text[2] = ((NBTTagString)list.tagAt(2)).stringValue;
					text[3] = ((NBTTagString)list.tagAt(3)).stringValue;
				}else {
					System.out.println("Cant set edit sign text: Tag Type: "+list.getType()+"(need "+NBTBase.STRING+") Tag count: "+list.tagCount()+"(need 4+)");
				}
			}
		}
	};
	
	public void onPressed(SettingButton b) {
		super.onPressed(b);
		if(b == openLoad) {
			mc.displayGuiScreen(new GuiSetAutoSignText(mc.currentScreen));
		}
	}
	
	public AutoSignHack() {
		super("AutoSign", "Automatically fills signs", Keyboard.KEY_NONE, Category.MISC);
		instance = this;
		
		this.addSetting(this.openLoad);
	}

}
