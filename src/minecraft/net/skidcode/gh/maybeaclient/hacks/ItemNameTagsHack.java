package net.skidcode.gh.maybeaclient.hacks;

import org.lwjgl.input.Keyboard;

import net.minecraft.src.EntityItem;
import net.minecraft.src.StringTranslate;
import net.skidcode.gh.maybeaclient.hacks.category.Category;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingBoolean;

public class ItemNameTagsHack extends Hack{
	public static ItemNameTagsHack instance;
	public static SettingBoolean enableCountDisplay;
	public ItemNameTagsHack() {
		super("ItemNameTags", "Show item nametags", Keyboard.KEY_NONE, Category.RENDER);
		instance = this;
		this.addSetting(enableCountDisplay = new SettingBoolean(this, "Display count", true));
	}
	public static String getName(EntityItem entity) {
		String s = StringTranslate.getInstance().translateNamedKey(entity.item.func_20109_f()).trim();
		if(enableCountDisplay.value) return entity.item.stackSize+" "+s;
		return s;
	}
}
