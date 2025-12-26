package net.skidcode.gh.maybeaclient.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.src.ChatAllowedCharacters;
import net.minecraft.src.GuiButton;
import net.skidcode.gh.maybeaclient.hacks.CharSelectorHack;

public class GuiCharSelector extends GuiButton{
	public static final GuiButton buttons[];
	public static final int SIZE = 12;
	public static final int MAXATROW = 16;
	
	static {
		buttons = new GuiButton[ChatAllowedCharacters.allowedCharacters.length()];
		int x = 0;
		int y = 0;
		for(int i = 0; i < buttons.length; ++i) {
			if(i != 0 && i%MAXATROW == 0) {
				y += SIZE;
				x = 0;
			}
			buttons[i] = new GuiButton(i, x, y, SIZE, SIZE, ""+ChatAllowedCharacters.allowedCharacters.charAt(i));
			x += SIZE;
		}
	}
	public GuiCharSelector(int id, int x, int y) {
		super(id, x, y, 256, 200, "");
	}
	
	public void drawButton(Minecraft var1, int var2, int var3) {
		if(!CharSelectorHack.instance.status) return;
		for(GuiButton but : buttons) {
			but.xPosition += this.xPosition;
			but.yPosition += this.yPosition;
			
			but.drawButton(var1, var2, var3);
			
			but.xPosition -= this.xPosition;
			but.yPosition -= this.yPosition;
		}
	}
	
	public char selectedChar = ' ';
    public boolean mousePressed(Minecraft var1, int var2, int var3) {
    	if(!CharSelectorHack.instance.status) return false;
    	
    	for(GuiButton but : buttons) {
			but.xPosition += this.xPosition;
			but.yPosition += this.yPosition;
			boolean p = but.mousePressed(var1, var2, var3);
			but.xPosition -= this.xPosition;
			but.yPosition -= this.yPosition;
			if(p) {
				selectedChar = but.displayString.charAt(0);
				return true;
			}
		}
    	
        return false;
    }
}
