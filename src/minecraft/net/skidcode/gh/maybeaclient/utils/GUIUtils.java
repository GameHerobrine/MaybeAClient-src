package net.skidcode.gh.maybeaclient.utils;

import java.awt.Cursor;
import java.util.LinkedList;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

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
	public static boolean isInsideRoundedRect(int mx, int my, int startX, int startY, int endX, int endY, int roundness) {
		boolean yy = my >= startY && my <= endY-roundness;
		boolean xx = mx >= startX && mx <= endX-roundness;
		int xmin = startX - roundness;
		int xmax = endX;
		int ymin = startY - roundness;
		int ymax = endY;

		System.out.println(mx+" "+my+" "+startX+" "+startY+" "+endX+" "+endY);
		if(mx >= xmin && mx <= xmax && yy) return true;
		if(my >= ymin && my <= ymax && xx) return true;
		//TODO check tiny corners too?
		return false;
	}
	
	public static boolean isInsideRect(int mx, int my, int startX, int startY, int endX, int endY) {
		
		return mx >= startX && mx <= endX && my >= startY && my <= endY;
	}
	
    public static void enableScissorTest() {
    	GL11.glEnable(GL11.GL_SCISSOR_TEST);
    }
    public static void disableScissorTest() {
    	GL11.glDisable(GL11.GL_SCISSOR_TEST);
    	if(scissorState != 0) {
    		new Exception("Scissor state != 0: something forgot to call scissorEnd? force clearing states. State: "+scissorState).printStackTrace();
    		scissorStates.clear();
    		scissorState = 0;
    	}
    }
	
	private static int scissorState = 0;
    public static LinkedList<ScissorState> scissorStates = new LinkedList<ScissorState>(); 
	public static void scissorStart(double startX, double startY, double endX, double endY) {
		ScaledResolution scaledresolution = new ScaledResolution(Client.mc.gameSettings, Client.mc.displayWidth, Client.mc.displayHeight);
		int screenStartX = scaledresolution.screenX(startX);
		int screenEndX = scaledresolution.screenX(endX);
		int screenStartY = scaledresolution.screenY(startY);
		int screenEndY = scaledresolution.screenY(endY);
		if(screenEndY > Client.mc.displayHeight) screenEndY = Client.mc.displayHeight;
		if(screenStartY > Client.mc.displayHeight) screenStartY = Client.mc.displayHeight;
		if(screenEndY < 0) screenEndY = 0;
		if(screenStartY < 0) screenStartY = 0;
		
		if(screenStartX < 0) screenStartX = 0;
		if(screenEndX < 0) screenEndX = 0;
		
		int xs = screenStartX;
		int ys = Client.mc.displayHeight - screenEndY;
		int wid = screenEndX - screenStartX;
		int hei = screenEndY - screenStartY;
		
		if(wid < 0) wid = 0;
		if(hei < 0) hei = 0;
		
		++scissorState;
		GL11.glScissor(xs, ys, wid, hei);
		scissorStates.add(new ScissorState(xs, ys, wid, hei));
	}
    
	public static void scissorEnd() {
		if(scissorState == 0) return;
		ScissorState fb = scissorStates.removeLast();
		--scissorState;
		GL11.glScissor(fb.x, fb.y, fb.w, fb.h);
	}
}
