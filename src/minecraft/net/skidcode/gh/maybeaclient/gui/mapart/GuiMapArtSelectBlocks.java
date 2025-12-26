package net.skidcode.gh.maybeaclient.gui.mapart;

import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.Tessellator;
import net.skidcode.gh.maybeaclient.Client;
import net.skidcode.gh.maybeaclient.utils.ChatColor;

public class GuiMapArtSelectBlocks extends GuiScreen{
	public GuiScreen parent;
	public GuiMapArtSelectBlocks(GuiScreen prev) {
		super();
		this.parent = prev;
	}
	
	public GuiButton doneButton;
	public GuiColorSlot slot;
	public void initGui() {
		this.controlList.add(doneButton = new GuiButton(0, this.width/2 - 40, this.height - 22, 80, 20, "Done"));
		this.slot = new GuiColorSlot(this);
		this.slot.registerScrollButtons(this.controlList, 1, 2);
		
	}
	
	public void actionPerformed(GuiButton var1) {
		if(var1 == this.doneButton) {
			Client.mc.displayGuiScreen(this.parent);
		}
	}
	
	public void drawScreen(int mX, int mY, float rendTicks) {
		this.slot.drawScreen(mX, mY, rendTicks);
		this.drawCenteredString(this.fontRenderer, "Select blocks", this.width / 2, 12, 16777215);
		
		super.drawScreen(mX, mY, rendTicks);
	}
}
