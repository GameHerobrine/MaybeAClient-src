package net.skidcode.gh.maybeaclient.hacks;

import org.lwjgl.input.Keyboard;
import net.minecraft.src.MathHelper;
import net.skidcode.gh.maybeaclient.events.Event;
import net.skidcode.gh.maybeaclient.events.EventListener;
import net.skidcode.gh.maybeaclient.events.EventRegistry;
import net.skidcode.gh.maybeaclient.events.impl.EventWorldRenderPreFog;
import net.skidcode.gh.maybeaclient.hacks.category.Category;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingBoolean;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingColor;
import net.skidcode.gh.maybeaclient.utils.RenderUtils;

public class ChunkBordersHack extends Hack implements EventListener{

	public SettingBoolean renderThroughBlocks = new SettingBoolean(this, "Render Through Blocks", false);
	public SettingColor color = new SettingColor(this, "Render Color", 255, 0, 0);
	public ChunkBordersHack() {
		super("ChunkBorders", "Shows chunk borders", Keyboard.KEY_NONE, Category.RENDER);
		EventRegistry.registerListener(EventWorldRenderPreFog.class, this);
		this.addSetting(this.renderThroughBlocks);
		this.addSetting(this.color);
	}
	
	@Override
	public void handleEvent(Event event) {
		if(event instanceof EventWorldRenderPreFog) {
			int pcx = (MathHelper.floor_double(mc.thePlayer.posX / 16));
			int pcz = (MathHelper.floor_double(mc.thePlayer.posZ / 16));
			RenderUtils.renderChunk(pcx, pcz, (float)this.color.red/255, (float)this.color.green/255, (float)this.color.blue/255, !this.renderThroughBlocks.value);
		}
	}
}
