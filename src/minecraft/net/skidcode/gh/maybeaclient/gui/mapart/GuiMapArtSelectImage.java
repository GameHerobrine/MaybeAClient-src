package net.skidcode.gh.maybeaclient.gui.mapart;

import java.net.URI;
import org.lwjgl.Sys;
import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.GuiSmallButton;
import net.skidcode.gh.maybeaclient.Client;

public class GuiMapArtSelectImage extends GuiScreen{
	public GuiScreen parent;
	public GuiMapArtSelectImage(GuiScreen prev) {
		super();
		this.parent = prev;
	}
	
	public GuiButton doneButton, openFolder;
	public GuiImageSlot slot;
	@Override
	public void initGui() {
		this.controlList.add(doneButton = new GuiSmallButton(0, this.width / 2 + 4, this.height - 48, "Done"));
		this.controlList.add(openFolder = new GuiSmallButton(1, this.width / 2 - 154, this.height - 48, "Open maparts folder"));
		this.slot = new GuiImageSlot(this);
		this.slot.registerScrollButtons(this.controlList, 2, 3);
		
	}
	
	@Override
	public void updateScreen() {
		Client.getMaparts();
	}
	
	@Override
	public void actionPerformed(GuiButton var1) {
		if(var1 == this.doneButton) {
			Client.mc.displayGuiScreen(this.parent);
		}
		
		if(var1 == this.openFolder) {
			boolean success = false;

			try {
				Class c = Class.forName("java.awt.Desktop");
				Object m = c.getMethod("getDesktop", new Class[0]).invoke((Object) null, new Object[0]);
				c.getMethod("browse", new Class[] {
					URI.class
				}).invoke(m, new Object[] {
					Client.mapartsDirectory.toURI()
				});
			} catch (Throwable e) {
				e.printStackTrace();
				success = true;
			}

			if (success) {
				System.out.println("Opening via Sys class!");
				Sys.openURL("file://" + Client.mapartsDirectory.getAbsolutePath());
			}
		}
	}
	
	
	@Override
	public void drawScreen(int mX, int mY, float rendTicks) {
		this.slot.drawScreen(mX, mY, rendTicks);
		this.drawCenteredString(this.fontRenderer, "Select image", this.width / 2, 12, 16777215);
		
		super.drawScreen(mX, mY, rendTicks);
	}
}
