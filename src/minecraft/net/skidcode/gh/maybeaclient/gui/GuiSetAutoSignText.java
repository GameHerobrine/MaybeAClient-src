package net.skidcode.gh.maybeaclient.gui;

import org.lwjgl.input.Keyboard;

import net.minecraft.src.Block;
import net.minecraft.src.GuiEditSign;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.Packet130UpdateSign;
import net.minecraft.src.TileEntitySign;
import net.skidcode.gh.maybeaclient.Client;
import net.skidcode.gh.maybeaclient.hacks.AutoSignHack;

public class GuiSetAutoSignText extends GuiEditSign{
	public GuiScreen parent;
	public GuiSetAutoSignText(GuiScreen prev) {
		super(new TileEntitySign() {
			public Block getBlockType() {
				return Block.signWall;
			}
			
			public int getBlockMetadata() {
				return 0;
			}
		});
		this.entitySign.signText[0] = AutoSignHack.instance.text[0];
		this.entitySign.signText[1] = AutoSignHack.instance.text[1];
		this.entitySign.signText[2] = AutoSignHack.instance.text[2];
		this.entitySign.signText[3] = AutoSignHack.instance.text[3];
		
		this.screenTitle = "Edit AutoSign Text:";
		this.parent = prev;
	}
	
	public boolean closed = false;
	
	@Override
	public void displayScreen() {
		Client.mc.displayGuiScreen(this.parent);
	}
	
	@Override
	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
		AutoSignHack.instance.text[0] = this.entitySign.signText[0];
		AutoSignHack.instance.text[1] = this.entitySign.signText[1];
		AutoSignHack.instance.text[2] = this.entitySign.signText[2];
		AutoSignHack.instance.text[3] = this.entitySign.signText[3];
		Client.saveModules();
	}

}
