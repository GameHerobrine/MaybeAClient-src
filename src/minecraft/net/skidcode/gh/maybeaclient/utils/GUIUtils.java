package net.skidcode.gh.maybeaclient.utils;

import java.awt.Cursor;

import org.lwjgl.input.Mouse;

import net.minecraft.client.Minecraft;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.MinecraftAppletImpl;
import net.minecraft.src.MinecraftImpl;
import net.minecraft.src.ScaledResolution;
import net.skidcode.gh.maybeaclient.Client;

public class GUIUtils {
	public static void setCursor(int cursor) {
		try {
			Cursor cur = Cursor.getPredefinedCursor(cursor);
			if(Client.mc instanceof MinecraftAppletImpl) {
				MinecraftAppletImpl applt = (MinecraftAppletImpl) Client.mc;
				applt.mainFrame.setCursor(cur);
			}else if(Client.mc instanceof MinecraftImpl) {
				MinecraftImpl applt = (MinecraftImpl) Client.mc;
				applt.mcFrame.setCursor(cur);
			}else {
				System.out.println("Not applet and not awt frame???");
			}
		}catch(IllegalArgumentException e) {
			System.out.println("Failed to change cursor!");
			e.printStackTrace();
		}
	}
	
	public static void setGUIScale(GuiScreen screen, int scale) {
		Minecraft mc = Client.mc;
		mc.gameSettings.guiScale = scale;
		ScaledResolution sc = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
		screen.setWorldAndResolution(mc, sc.getScaledWidth(), sc.getScaledHeight());
		mc.entityRenderer.setupScaledResolution();
	}
	
	public static boolean isInsideRect(int mx, int my, int startX, int startY, int endX, int endY) {
		return mx >= startX && mx <= endX && my >= startY && my <= endY;
	}
}
