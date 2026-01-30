package net.skidcode.gh.maybeaclient.hacks.settings;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraft.src.ChatAllowedCharacters;
import net.minecraft.src.NBTTagCompound;
import net.skidcode.gh.maybeaclient.Client;
import net.skidcode.gh.maybeaclient.gui.click.ClickGUI;
import net.skidcode.gh.maybeaclient.gui.click.Tab;
import net.skidcode.gh.maybeaclient.gui.click.element.Element;
import net.skidcode.gh.maybeaclient.hacks.ClickGUIHack;
import net.skidcode.gh.maybeaclient.hacks.ClickGUIHack.Theme;
import net.skidcode.gh.maybeaclient.utils.InputHandler;

public class SettingTextBox extends Setting implements InputHandler{

	public String initial, value;
	public int maxTextboxWidth = -1;
	public boolean isEditing = false;
	public SettingTextBox(SettingsProvider hack, String name, String value) {
		super(hack, name);
		this.initial = value;
		this.setValue(value);
	}
	
	public SettingTextBox(SettingsProvider hack, String name, String value, int maxTBWidth) {
		super(hack, name);
		this.initial = value;
		this.setValue(value);
		this.maxTextboxWidth = maxTBWidth;
	}

	@Override
	public String valueToString() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	@Override
	public void reset() {
		this.setValue(this.initial);
	}

	@Override
	public boolean validateValue(String value) {
		return true;
	}

	@Override
	public void writeToNBT(NBTTagCompound output) {
		output.setString(this.name, this.value);
	}

	@Override
	public void readFromNBT(NBTTagCompound input) {
		if(input.hasKey(this.name)) this.setValue(input.getString(this.name));
	}
	
	public long time = 0;
	public String add = "";
	@Override
	public void renderText(Element tab, int x, int y, int xEnd, int yEnd) {
		long time = System.currentTimeMillis();
		if(time - this.time > 500) {
			this.time = time;
			add = add.equals("|") ? "" : "|";
		}
		
		int txtColor = 0xffffff;
		if(ClickGUIHack.theme() == Theme.NODUS) {
			txtColor = ClickGUIHack.instance.themeColor.rgb();
			if(this.mouseHovering) {
				txtColor = ClickGUIHack.instance.secColor.rgb();
			}
		}
		this.mouseHovering = false;
		
		if(!this.isEditing) add = "";
		String s = this.name + ": ";
		int maxy = (y + this.splittedHeight);
		int miny = y;
		
		if(ClickGUIHack.theme() == Theme.HEPHAESTUS){
			Client.mc.fontRenderer.drawStringWithShadow(s, x + Theme.HEPH_OPT_XADD, y + (maxy - miny) / 2 - 5, txtColor);
			Client.mc.fontRenderer.drawSplittedStringWithShadow(this.value+add, xEnd - Theme.HEPH_OPT_XADD - this.maxSplitWidth + 2, y + ClickGUIHack.theme().yaddtocenterText, this.isEditing ? 0xafafaf : 0xffffff, this.maxSplitWidth, 12);
		}else {
			Client.mc.fontRenderer.drawString(s, x + 2, y + (maxy - miny) / 2 - 5, txtColor);
			Client.mc.fontRenderer.drawSplittedString(this.value+add, x + 2 + Client.mc.fontRenderer.getStringWidth(s), y + ClickGUIHack.theme().yaddtocenterText, this.isEditing ? 0xafafaf : 0xffffff, this.maxSplitWidth, 12);
		}
		
	}
	@Override
	public void onPressedInside(Element tab, int xMin, int yMin, int xMax, int yMax, int mouseX, int mouseY, int mouseClick) {
		int tbxmin = xMin + Client.mc.fontRenderer.getStringWidth(this.name + ": ");
		int tbymin = yMin;
		int tbxmax = xMax;
		int tbymax = yMax;
		
		if(mouseX >= tbxmin && mouseX <= tbxmax && mouseY >= tbymin && mouseY <= tbymax) {
			this.isEditing = ClickGUI.setInputHandler(this);
		}
	}
	
	@Override
	public void renderElement(Element tab, int xStart, int yStart, int xEnd, int yEnd) {
		int w = Client.mc.fontRenderer.getStringWidth(this.name + ": ");
		int tbxs = xStart + w;
		if(ClickGUIHack.theme() == Theme.IRIDIUM) {
			Tab.renderFrameBackGround(tbxs, yStart, xEnd, yEnd, 0, 0, 0, 0.5f);
			Tab.renderFrameOutlines(tbxs, yStart, xEnd, yEnd);
			return;
		}
		if(ClickGUIHack.theme() == Theme.HEPHAESTUS) {
			tbxs = xEnd - this.maxSplitWidth - Theme.HEPH_OPT_XADD + 1;
			Tab.renderFrameOutlines(tbxs, yStart, xEnd - Theme.HEPH_OPT_XADD + 3, yEnd);
			
			return;
		}
		
		boolean stenciltesting = GL11.glGetBoolean(GL11.GL_STENCIL_TEST);
		GL11.glPushAttrib(GL11.GL_STENCIL_BUFFER_BIT);
		GL11.glEnable(GL11.GL_STENCIL_TEST);
		
		if(stenciltesting) {
			GL11.glStencilOp(GL11.GL_ZERO, GL11.GL_ZERO, GL11.GL_INCR); 
			GL11.glStencilFunc(GL11.GL_EQUAL, Client.STENCIL_REF_ELDRAW, 0xFF);
		}else {
			GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
			GL11.glStencilOp(GL11.GL_ZERO, GL11.GL_ZERO, GL11.GL_REPLACE); 
			GL11.glStencilFunc(GL11.GL_ALWAYS, Client.STENCIL_REF_TBDRAW, 0xFF);
		}
		GL11.glStencilMask(0xFF);
		if(ClickGUIHack.theme() == Theme.NODUS) {}
		else Tab.renderFrameOutlines(tbxs, yStart, xEnd, yEnd);
		GL11.glColorMask(false, false, false, false);
		Tab.renderFrameBackGround(tbxs, yStart, xEnd, yEnd, ClickGUIHack.r(), ClickGUIHack.g(), ClickGUIHack.b(), 1f);
		GL11.glColorMask(true, true, true, true);
		if(stenciltesting) {
			GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
			GL11.glStencilFunc(GL11.GL_EQUAL, Client.STENCIL_REF_ELDRAW, 0xFF);
		}else {
			GL11.glStencilOp(GL11.GL_ZERO, GL11.GL_ZERO, GL11.GL_ZERO);
			GL11.glStencilFunc(GL11.GL_NOTEQUAL, Client.STENCIL_REF_TBDRAW, 0xFF);
		}
		
		if(ClickGUIHack.theme() == Theme.NODUS) {
			Tab.renderFrameBackGround(xStart, yStart, xEnd, yEnd, 0, 0, 0, 0x80/255f);
		}else if(ClickGUIHack.theme() == Theme.HEPHAESTUS){
			
		}else if(ClickGUIHack.theme() == Theme.UWARE) {
			Tab.renderFrameBackGround(xStart, yStart, xEnd, yEnd, 0x16/255f, 0x16/255f, 0x16/255f, 0xaa/255f);
		}else{
			Tab.renderFrameBackGround(xStart, yStart, xEnd, yEnd, ClickGUIHack.r(), ClickGUIHack.g(), ClickGUIHack.b(), 1f);
		}
		if(stenciltesting) {
			GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_DECR); 
			GL11.glStencilFunc(GL11.GL_EQUAL, Client.STENCIL_REF_TBDRAW, 0xFF);
			GL11.glColorMask(false, false, false, false);
			Tab.renderFrameBackGround(xStart, yStart, xEnd, yEnd, ClickGUIHack.r(), ClickGUIHack.g(), ClickGUIHack.b(), 1f);
			GL11.glColorMask(true, true, true, true);
		}
		GL11.glDisable(GL11.GL_STENCIL_TEST);
		GL11.glPopAttrib();
	}
	@Override
	public int getSettingWidth() {
		if(this.maxTextboxWidth >= 0) {
			int hw = this.maxTextboxWidth;
			int w = Client.mc.fontRenderer.getStringWidth(this.name + ":" + "|") + hw + (ClickGUIHack.theme() == Theme.HEPHAESTUS ? Theme.HEPH_OPT_XADD*3 + 2 : 0);
			
			return w;
		}
		return Client.mc.fontRenderer.getStringWidth(this.name + ":" + this.value + "|") + 10 + (ClickGUIHack.theme() == Theme.HEPHAESTUS ? Theme.HEPH_OPT_XADD*3 : 0);
	}

	@Override
	public void onKeyPress(int keycode) {
		boolean b = Keyboard.getEventKeyState();
		if(!b) return;
		if(keycode == Keyboard.KEY_ESCAPE) {
			ClickGUI.setInputHandler(null);
			return;
		}
		if(keycode == Keyboard.KEY_BACK) {
			if(this.value.length() > 0) this.setValue(this.value.substring(0, this.value.length()-1));
			return;
		}
		char c = Keyboard.getEventCharacter();
		
		if(ChatAllowedCharacters.allowedCharacters.indexOf(c) >= 0) this.setValue(this.value + c);
	}

	@Override
	public void onInputFocusStop() {
		this.isEditing = false;
		Client.saveModules();
	}
	@Override
	public void onKeyRelease(int keycode) {}
	public int maxSplitWidth = this.maxTextboxWidth;
	public int splittedHeight = 12;
	@Override
	public int getSettingHeight(Element tab) {
		//return 24;
		if(this.maxTextboxWidth < 0) return super.getSettingHeight(tab);
		int a = tab.getCachedWidth() - Client.mc.fontRenderer.getStringWidth(this.name + ":" + "|");
		int b = this.maxTextboxWidth;
		if(ClickGUIHack.theme() == Theme.HEPHAESTUS) {
			a -= 3*Theme.HEPH_OPT_XADD;
			a += 2;
		}
		if(b > a) {
			a = b;
		}
		this.maxSplitWidth = a-4-ClickGUIHack.theme().settingBorder;
		
		int[] sz = Client.mc.fontRenderer.getSplittedStringWidthAndHeight(this.value+"|", this.maxSplitWidth, 12);
		this.splittedHeight = sz[1];
		return sz[1];
	}

	
}
