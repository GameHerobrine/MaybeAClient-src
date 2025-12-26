package net.skidcode.gh.maybeaclient.gui.altman;

import org.lwjgl.input.Keyboard;

import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.GuiSmallButton;
import net.minecraft.src.GuiTextField;
import net.skidcode.gh.maybeaclient.Client;

public class GuiEditAccount extends GuiScreen{
	public AccountInfo info;
	public GuiScreen parent;
	public GuiTextField username;
	public GuiButton done;
	public GuiEditAccount(GuiScreen parent, AccountInfo info) {
		this.parent = parent;
		this.info = info;
	}
	
	public GuiEditAccount(GuiScreen parent) {
		this(parent, null);
	}

	public void initGui() {
		super.initGui();
		this.controlList.clear();
		Keyboard.enableRepeatEvents(true);
		this.username = new GuiTextField(this, this.fontRenderer, this.width / 2 - 100, this.height / 4 + 40, 200, 20, this.info == null ? "" : this.info.name);
        this.username.setMaxStringLength(16);
        this.username.isFocused = true;
        this.controlList.add(this.done = new GuiSmallButton(1, this.width / 2 - 50 + 52, this.height/4 - 10 + 50 + 20 + 14 + 24, 100, 20, "Done"));
        this.done.enabled = this.username.getText().length() > 0;
        this.controlList.add(new GuiSmallButton(2, this.width / 2 - 50 - 52, this.height/4 - 10 + 50 + 20 + 14 + 24, 100, 20, "Cancel"));
	}
	@Override
	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
	}
	protected void keyTyped(char var1, int var2) {
        this.username.textboxKeyTyped(var1, var2);
        if (var1 == '\r') {
            this.actionPerformed(this.done);
        }
        this.done.enabled = this.username.getText().length() > 0;
    }
	protected void actionPerformed(GuiButton b) {
        if (b.enabled) {
            if (b.id == 2) {
                this.mc.displayGuiScreen(this.parent);
            }else if(b.id == 1) {
            	if(this.info == null) {
            		this.info = new AccountInfo(this.username.getText());
            		GuiAccManager.addAccount(this.info);
            	}else {
            		GuiAccManager.changeUsername(this.info, this.username.getText());
            	}
            	Client.writeCurrentAccounts();
            	this.mc.displayGuiScreen(this.parent);
            }
        }
	}
    protected void mouseClicked(int var1, int var2, int var3) {
        super.mouseClicked(var1, var2, var3);
        this.username.mouseClicked(var1, var2, var3);
    }
    
	public void drawScreen(int mX, int mY, float rendTicks) {
		this.drawDefaultBackground();
		this.drawCenteredString(this.fontRenderer, "Edit account", this.width / 2, 12, 16777215);
		
		
		AccountInfo ind = GuiAccManager.usernames.get(this.username.getText());
		if(ind != null && (this.info == null || ind != GuiAccManager.usernames.get(this.info.name))) {
			this.done.enabled = false;
			Client.mc.fontRenderer.drawString("Username", this.username.xPos, this.username.yPos - 10, 0xFF5555);
			this.username.drawRedTextBox();
		}else {
			Client.mc.fontRenderer.drawString("Username", this.username.xPos, this.username.yPos - 10, 0xffffff);
			this.username.drawTextBox();
		}
		
		
		super.drawScreen(mX, mY, rendTicks);
	}
}
