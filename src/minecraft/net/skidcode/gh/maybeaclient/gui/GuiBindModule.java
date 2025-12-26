package net.skidcode.gh.maybeaclient.gui;

import org.lwjgl.input.Keyboard;

import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiScreen;
import net.skidcode.gh.maybeaclient.Client;
import net.skidcode.gh.maybeaclient.hacks.Hack;
import net.skidcode.gh.maybeaclient.utils.ChatColor;

public class GuiBindModule extends GuiScreen{
	
	public GuiScreen parent;
	public Hack hack;
	
	public int currentKey = 0;
	public boolean pressed = false;
	
	public GuiBindModule(GuiScreen parent, Hack hack) {
		this.parent = parent;
		this.hack = hack;
	}
	
	@Override
	public void mouseClicked(int var1, int var2, int var3) {
		super.mouseClicked(var1, var2, var3);
		if(this.selectedButton == null) {
			currentKey = Client.getKeycodeForMouseButton(var3);
			this.pressed = true;
		}
	}
	
	@Override
	public void keyTyped(char var1, int var2) {
		currentKey = var2 == Keyboard.KEY_ESCAPE ? 0 : var2;
		this.pressed = true;
	}
	
	@Override
	public void actionPerformed(GuiButton button) {
		switch(button.id) {
			case 1: //done
				if(this.pressed) this.hack.bind(this.currentKey);
				mc.displayGuiScreen(this.parent);
				break;
			case 2: //cancel
				mc.displayGuiScreen(this.parent);
				break;
		}
	}
	
	public void initGui() {
		int midX = this.width / 2;
		int midY = this.height / 2;
		this.controlList.add(new GuiButton(1, midX - 48, midY + 16, 48, 20, "Done"));
		this.controlList.add(new GuiButton(2, midX, midY + 16, 48, 20, "Cancel"));
	}
	public void drawScreen(int var1, int var2, float var3) {
		this.drawDefaultBackground();
		int midX = this.width / 2;
		int midY = this.height / 2;
		
		String s = ChatColor.LIGHTCYAN+"Module: "+ChatColor.GOLD+hack.name;
		this.fontRenderer.drawString(s, midX - this.fontRenderer.getStringWidth(s) / 2, midY - 24, 0xdeadbeef);
		s = ChatColor.LIGHTCYAN+"Current bind: "+ChatColor.GOLD+hack.keybinding.valueToString();
		this.fontRenderer.drawString(s, midX - this.fontRenderer.getStringWidth(s) / 2, midY - 12, 0xdeadbeef);
		s = ChatColor.LIGHTCYAN+"New bind: "+ChatColor.GOLD+(this.pressed ? Client.getKeyName(this.currentKey) : "Press Any Key(ESC = None)");
		this.fontRenderer.drawString(s, midX - this.fontRenderer.getStringWidth(s) / 2, midY - 0, 0xdeadbeef);
		
		super.drawScreen(var1, var2, var3);
	}
}
