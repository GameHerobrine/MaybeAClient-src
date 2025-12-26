package net.skidcode.gh.maybeaclient;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.src.ScaledResolution;
import net.minecraft.src.Tessellator;
import net.skidcode.gh.maybeaclient.gui.click.ArrayListTab;
import net.skidcode.gh.maybeaclient.gui.click.ClickGUI;
import net.skidcode.gh.maybeaclient.gui.click.Tab;
import net.skidcode.gh.maybeaclient.hacks.ClickGUIHack;

public class IngameHook {
	public static Minecraft mc;
	public static void handleIngame(ScaledResolution res) {
		if(!(mc.currentScreen instanceof ClickGUI)) {
			int prev = mc.gameSettings.guiScale;
			int newScale = ClickGUIHack.instance.getScale();
			if(prev != newScale) {
				mc.gameSettings.guiScale = newScale;
				mc.entityRenderer.setupScaledResolution();
			}
			for(int i = ClickGUI.tabs.size()-1; i >= 0; --i ) {
				ClickGUI.tabs.get(i).preRender();
			}
			
			for(int i = ClickGUI.tabs.size()-1; i >= 0; --i ) {
				ClickGUI.tabs.get(i).renderIngame();
			}
			if(prev != newScale) {
				mc.gameSettings.guiScale = prev;
				mc.entityRenderer.setupScaledResolution();
			}
		}
		
		/*GL11.glBindTexture(GL11.GL_TEXTURE_2D, mc.renderEngine.getTexture("/gui/items.png"));
		Tessellator.instance.startDrawingQuads();
		Tessellator.instance.addVertexWithUV(0, 256, 0, 0, 1);
		Tessellator.instance.addVertexWithUV(256, 256, 0, 1, 1);
		Tessellator.instance.addVertexWithUV(256, 0, 0, 1, 0);
		Tessellator.instance.addVertexWithUV(0, 0, 0, 0, 0);
		Tessellator.instance.draw();
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, mc.renderEngine.getTexture("/terrain.png"));
		Tessellator.instance.startDrawingQuads();
		Tessellator.instance.addVertexWithUV(256, 256, 0, 0, 1);
		Tessellator.instance.addVertexWithUV(256+256, 256, 0, 1, 1);
		Tessellator.instance.addVertexWithUV(256+256, 0, 0, 1, 0);
		Tessellator.instance.addVertexWithUV(256, 0, 0, 0, 0);
		Tessellator.instance.draw();*/
	}
}
