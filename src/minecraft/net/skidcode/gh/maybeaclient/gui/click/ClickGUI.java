package net.skidcode.gh.maybeaclient.gui.click;

import java.util.ArrayList;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import net.minecraft.src.GuiScreen;
import net.skidcode.gh.maybeaclient.Client;
import net.skidcode.gh.maybeaclient.hacks.AFKDisconnectHack;
import net.skidcode.gh.maybeaclient.hacks.ClickGUIHack;
import net.skidcode.gh.maybeaclient.hacks.Hack;
import net.skidcode.gh.maybeaclient.hacks.ClickGUIHack.Theme;
import net.skidcode.gh.maybeaclient.hacks.category.Category;
import net.skidcode.gh.maybeaclient.utils.GUIUtils;
import net.skidcode.gh.maybeaclient.utils.InputHandler;

public class ClickGUI extends GuiScreen{

	public GuiScreen parent;
	public static boolean initialized = false;
	public static ArrayList<Tab> tabs = new ArrayList<>();
	public static float mouseX, mouseY, mouseClicked;
	
	public Tab selectedTab = null;
	public Tab hoveringOver = null;
	
	public ClickGUI(GuiScreen parent) {
		this.parent = parent;
		descHack = null;
	}
	
	@Override
	public void keyTyped(char var1, int var2) {
		if (var2 == Keyboard.KEY_ESCAPE) {
			this.mc.displayGuiScreen((GuiScreen)null);
			this.mc.setIngameFocus();
			ClickGUIHack.instance.toggle();
		}

	}
	
	@Override
	public void handleMouseInput() {

	}
	public static InputHandler inputHandler = null;
	public static boolean hasInputHandler = false;
	public static boolean setInputHandler(InputHandler hd) {
		if(inputHandler != null && hasInputHandler) inputHandler.onInputFocusStop();
		inputHandler = hd;
		return true;
	}
	
	@Override
	public void handleKeyboardInput() {
		if(inputHandler != null && hasInputHandler) {
			char keyname = Keyboard.getEventCharacter();
			int keycode = Keyboard.getEventKey();
			inputHandler.onKeyPress(keycode);
		}else {
			super.handleKeyboardInput();
		}
    }
	
	@Override
	public void handleInput() {
		while (Keyboard.next()) {
			AFKDisconnectHack.stopAFKing();
			this.handleKeyboardInput();
		}
	}
	
	public boolean canSelectNewTab = true;
	@Override
	public void mouseClicked(int x, int y, int click) {
		if(inputHandler != null && hasInputHandler) {
			inputHandler.onKeyPress(Client.getKeycodeForMouseButton(click));
			return;
		}
		if(!canSelectNewTab) return;
		
		for(Tab tab : tabs) {
			if(!tab.shown) continue;
			if(tab.isPointInside(x, y)) {
				this.selectedTab = tab;
				canSelectNewTab = false;
				tab.onClick(x, y, click);
				tabs.remove(this.selectedTab);
				tabs.add(0, this.selectedTab);
				break;
			}
		}
	}
	private void mouseWheelMoved(int x, int y, int wheel) {
		for(Tab tab : tabs) {
			if(!tab.shown) continue;
			if(tab.isPointInside(x, y)) {
				this.selectedTab = tab;
				tab.wheelMoved(wheel, x, y);
				tabs.remove(this.selectedTab);
				tabs.add(0, this.selectedTab);
				break;
			}
		}
	}
	
	@Override
	public void mouseMovedOrUp(int x, int y, int click) {
		if(inputHandler != null && hasInputHandler) {
			inputHandler.onKeyRelease(Client.getKeycodeForMouseButton(click));
			return;
		}
		
		if(click != -1) {
			canSelectNewTab = true;
		}
		
		if(this.selectedTab != null) {
			this.selectedTab.mouseMovedSelected(x, y);
			if(click != -1) {
				this.selectedTab.onDeselect(x, y);
				this.selectedTab = null;
				canSelectNewTab = true;
			}
		}else {
			Tab tb = null;
			for(Tab tab : tabs) {
				if(!tab.shown) continue;
				if(tab.isPointInside(x, y)) {
					tb = tab;
					//this.hoveringOver = tab;
					//this.hoveringOver.mouseHovered(x, y, click);
					break;
				}
			}
			if(this.hoveringOver != null && tb != this.hoveringOver) {
				this.hoveringOver.stopHovering();
				this.hoveringOver = null;
			}

			if(tb != null){
				this.hoveringOver = tb;
				this.hoveringOver.hoveringOver(x, y);
			}
		}
	}
	
	public static void registerTabs() {
		Tab t;
		tabs.add(new ArrayListTab());
		tabs.add(new ClientNameTab());
		tabs.add(new ClientInfoTab());
		tabs.add(new KeybindingsTab());
		tabs.add(new RadarTab());
		tabs.add(new PlayerViewTab());
		tabs.add(new InventoryTab());
		tabs.add(new ChestContentTab());
		tabs.add(new PlayerlistTab());
		tabs.add(new LastSeenSpotsTab());
		tabs.add(new SlimeRadarTab());
		tabs.add(new ImageViewerTab());
		
		//tabs.add(TabManagerTab.instance);
		
		Category.MOVEMENT.setTabOptions(160, 10, true);
		Category.RENDER.setTabOptions(160, 24, true);
		Category.COMBAT.setTabOptions(160, 24+14, true);
		Category.MISC.setTabOptions(160, 24+14+14, true);
		Category.UI.setTabOptions(160, 24+14+14+14, true);
		for(Category c : Category.categories) tabs.add(c.tab);
		TabManagerTab.instance.addAll(tabs);
		
		initialized = true;
	}
	
	public static int descX, descY;
	public static Hack descHack;
	public static void showDescription(int x, int y, Hack h) {
		descX = x;
		descY = y;
		descHack = h;
	}
	
	public static ArrayList<Tab> toAdd = new ArrayList<Tab>();
	public static ArrayList<Tab> toRemove = new ArrayList<Tab>();
	
	public static void addTab(int i, Tab tab) {
		toAdd.add(i, tab);
		tab.shown = true;
	}
	public static void removeTab(Tab tab) {
		toRemove.add(tab);
		tab.shown = false;
	}
	public static int prevGUIScale = -1;
	public static int newGUIScale = -1;
	
	@Override
	public void drawScreen(int var1, int var2, float var3) {
		hasInputHandler = ClickGUI.inputHandler != null;
		
		descHack = null;
		for(int i = toAdd.size(); --i >= 0;) {
			Tab t = toAdd.remove(i);
			tabs.add(0, t);
		}
		for(int i = toRemove.size(); --i >= 0;) {
			Tab t = toRemove.remove(i);
			tabs.remove(t);
		}
		
		prevGUIScale = mc.gameSettings.guiScale;
		newGUIScale = ClickGUIHack.instance.getScale();
		if(prevGUIScale != newGUIScale) GUIUtils.setGUIScale(this, newGUIScale);
		
		
		while(Mouse.next()) {
			AFKDisconnectHack.stopAFKing();
			if(Mouse.getEventButton() != -1) break; 
		};
		AFKDisconnectHack.startAFKing();
		
		
		for(int i = tabs.size()-1; i >= 0; --i ) {
			Tab t = tabs.get(i);
			t.recalculatePosition(null, 0, 0);
		}
		
		for(int i = tabs.size()-1; i >= 0; --i ) { //inverse render them
			Tab t = tabs.get(i);
			if(!t.shown) continue;
			
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glDisable(GL11.GL_LINE_SMOOTH);
			t.renderBottom();
		}
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		
		
		int x = (int) ((Mouse.getEventX() * this.width) / this.mc.displayWidth);
		int y = (int) (this.height - ((Mouse.getEventY() * this.height) / this.mc.displayHeight) - 1);
		if(hasInputHandler && this.selectedTab != null) {
			this.selectedTab.onDeselect(x, y);
			this.selectedTab = null;
			canSelectNewTab = true;
		}
		
		int wheel = Mouse.getDWheel();
		
		if(wheel != 0) {
			this.mouseWheelMoved(x, y, wheel);
		}
		
		if (Mouse.getEventButtonState()) {
			this.mouseClicked(x, y, Mouse.getEventButton());
		} else {
			this.mouseMovedOrUp(x, y, Mouse.getEventButton());
		}
		
		if(descHack != null) {
			int xMin = descX;
			int yMin = descY;
			int xMax = xMin + 6*24;
			Theme theme = ClickGUIHack.theme();
			
			int txtColor = 0xffffff;
			if(theme == Theme.NODUS) {
				txtColor = ClickGUIHack.instance.themeColor.rgb();
			}
			Client.debug = true;
			int[] width_y = Client.mc.fontRenderer.getSplittedStringWidthAndHeight_h(descHack.description, xMax - xMin - 4, 12);
			Client.debug = false;
			xMax = xMin + width_y[0] + 2;
			int yMax = yMin + width_y[1];
			Tab.renderFrame(null, xMin, yMin, xMax, yMax);
			Client.mc.fontRenderer.drawSplittedString_h(descHack.description, xMin + 2, yMin + 2, txtColor, xMax - xMin - 2, 12);
			
		}
		
		super.drawScreen(var1, var2, var3);
		if(prevGUIScale != newGUIScale) {
			GUIUtils.setGUIScale(this, prevGUIScale);
		}
		
	}
	@Override
	public void onGuiClosed() {
		if(prevGUIScale != newGUIScale) {
			GUIUtils.setGUIScale(this, prevGUIScale);
		}
	}
}
