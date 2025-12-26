package net.skidcode.gh.maybeaclient.gui.altman;

import org.lwjgl.input.Keyboard;

import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.GuiSmallButton;
import net.minecraft.src.GuiTextField;
import net.skidcode.gh.maybeaclient.Client;
import net.skidcode.gh.maybeaclient.gui.altman.PasswordInfo.MatchMode;
import net.skidcode.gh.maybeaclient.utils.ChatColor;

public class GuiEditPassword extends GuiScreen{
	
	public GuiScreen parent;
	public AccountInfo info;
	public PasswordInfo passInfo;
	
	public GuiTextField serverIPField;
	public GuiTextField password;
	public GuiTextField prompt, command;
	
	public GuiButton done, showPassword;
	public GuiButton promtMatchMode;
	public boolean needsAdding = false;
	public boolean showingPasswd = false;
	public MatchMode orig;
	
	public GuiEditPassword(GuiScreen parent, AccountInfo info, PasswordInfo pinf) {
		this.parent = parent;
		this.info = info;
		this.passInfo = pinf;
		
		if(pinf == null) {
			needsAdding = true;
			this.passInfo = new PasswordInfo("", "");
		}else {
			this.orig = pinf.mode;
		}
		
	}
	@Override
	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
	}
	protected void actionPerformed(GuiButton b) {
		if (b.enabled) {
			switch(b.id) {
				case 1:
					this.info.removePassword(this.passInfo);
					this.passInfo.loginCommand = this.command.getText();
					this.passInfo.loginPrompt = this.prompt.getText();
					this.passInfo.password = this.password.getText();
					this.passInfo.serverIP = this.serverIPField.getText();
					this.info.addPassword(this.passInfo);
					Client.writeCurrentAccounts();
					this.mc.displayGuiScreen(this.parent);
					break;
				case 2:
					this.passInfo.mode = this.orig;
					this.mc.displayGuiScreen(this.parent);
					break;
				case 3:
					this.showingPasswd = !showingPasswd;
					if(this.showingPasswd) this.showPassword.displayString = "Show";
					else this.showPassword.displayString = "Hide";
					break;
				case 4:
					int n = this.passInfo.mode.ordinal() + 1;
					if(n >= MatchMode.values().length) {
						n = 0;
					}
					this.passInfo.mode = MatchMode.values()[n];
			}
		}
	}
	protected void keyTyped(char var1, int var2) {
		this.serverIPField.textboxKeyTyped(var1, var2);
		this.password.textboxKeyTyped(var1, var2);
		this.prompt.textboxKeyTyped(var1, var2);
		this.command.textboxKeyTyped(var1, var2);
		
		if (var1 == '\r') {
			this.actionPerformed(this.done);
		}
		this.checkButtons();
	}
	protected void mouseClicked(int var1, int var2, int var3) {
		super.mouseClicked(var1, var2, var3);
		this.serverIPField.mouseClicked(var1, var2, var3);
		this.password.mouseClicked(var1, var2, var3);
		this.prompt.mouseClicked(var1, var2, var3);
		this.command.mouseClicked(var1, var2, var3);
	}
	
	public void initGui() {
		super.initGui();
		this.controlList.clear();
		Keyboard.enableRepeatEvents(true);
		this.serverIPField = new GuiTextField(this, this.fontRenderer, this.width / 2 - 100, this.height / 2 - 72, 200, 20, "");
		this.serverIPField.setMaxStringLength(32);
		this.serverIPField.isFocused = true;
		
		
		this.password = new GuiTextField(this, this.fontRenderer, this.width / 2 - 100, this.height / 2 - 72 + 36, 200, 20, "");
		this.prompt = new GuiTextField(this, this.fontRenderer, this.width / 2 - 100, this.height / 2 - 72 + 36*2, 200, 20, "");
		this.command = new GuiTextField(this, this.fontRenderer, this.width / 2 - 100, this.height / 2 - 72 + 36*3, 200, 20, "");
		
		if(this.passInfo != null) {
			this.serverIPField.setText(this.passInfo.serverIP);
			this.password.setText(this.passInfo.password);
			this.prompt.setText(this.passInfo.loginPrompt);
			this.command.setText(this.passInfo.loginCommand);
		}
		
		
		this.done = new GuiSmallButton(1, this.width / 2 - 50 + 52, this.height/2 + 72, 100, 20, "Done");
		this.controlList.add(new GuiSmallButton(2, this.width / 2 - 50 - 52, this.height/2 + 72, 100, 20, "Cancel"));
		this.controlList.add(this.done);
		this.controlList.add(this.showPassword = new GuiSmallButton(3, this.width / 2 + 102, this.height/2 - 72 + 36, 30, 20, "Hide"));
		this.controlList.add(this.promtMatchMode = new GuiSmallButton(4, this.width / 2 + 102, this.height/2 - 72 + 36*2, 75, 20, ""));
		this.checkButtons();
	}
	public void drawScreen(int mX, int mY, float rendTicks) {
		this.drawDefaultBackground();
		this.drawCenteredString(this.fontRenderer, "Edit password", this.width / 2, 12, 16777215);
		
		this.promtMatchMode.displayString = this.passInfo.mode.name;
		
		
		Client.mc.fontRenderer.drawString("Password", this.password.xPos, this.password.yPos - 10, 0xffffff);
		Client.mc.fontRenderer.drawString("Prompt ("+this.prompt.getText().replace("&", ChatColor.SYM)+ChatColor.WHITE+")", this.prompt.xPos, this.prompt.yPos - 10, 0xffffff);
		Client.mc.fontRenderer.drawString("Command", this.command.xPos, this.command.yPos - 10, 0xffffff);
		
		String prevPass = this.password.getText();
		if(!this.showingPasswd) {
			String s = "";
			for(int i = 0; i < this.password.getText().length(); ++i) {
				s += "*";
			}
			this.password.setText(s);
		}
		if(this.info.passwords.get(this.serverIPField.getText()) != null && (this.needsAdding || this.info.passwords.get(this.serverIPField.getText()) != this.passInfo)) {
			Client.mc.fontRenderer.drawString("Server IP", this.serverIPField.xPos, this.serverIPField.yPos - 10, 0xffFF5555);
			this.serverIPField.drawRedTextBox();
			this.done.enabled = false;
		}
		else {
			Client.mc.fontRenderer.drawString("Server IP", this.serverIPField.xPos, this.serverIPField.yPos - 10, 0xffffff);
			this.serverIPField.drawTextBox();
			this.checkButtons();
		}
		this.password.drawTextBox();
		this.prompt.drawTextBox();
		this.command.drawTextBox();
		
		if(!this.showingPasswd) this.password.setText(prevPass);
		
		super.drawScreen(mX, mY, rendTicks);
	}
	public void checkButtons() {
		this.done.enabled = this.serverIPField.getText().length() > 0 && this.password.getText().length() > 0 && this.prompt.getText().length() > 0 && this.command.getText().length() > 0;
	}
}
