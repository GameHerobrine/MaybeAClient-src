package net.skidcode.gh.maybeaclient.gui.server;

import org.lwjgl.input.Keyboard;

import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiConnecting;
import net.minecraft.src.GuiMultiplayer;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.GuiSmallButton;
import net.minecraft.src.GuiTextField;
import net.skidcode.gh.maybeaclient.Client;

public class GuiEditServer extends GuiScreen{
	public ServerInfo info;
	public GuiScreen parent;
	public GuiTextField serverIp, serverName;
	public GuiButton done;
	public GuiEditServer(GuiScreen parent, ServerInfo info) {
		this.parent = parent;
		this.info = info;
	}
	
	public GuiEditServer(GuiScreen parent) {
		this(parent, null);
	}

	public void initGui() {
		super.initGui();
		Keyboard.enableRepeatEvents(true);
		this.controlList.clear();
		
		this.serverName = new GuiTextField(this, this.fontRenderer, this.width / 2 - 100, this.height / 4 + 40, 200, 20, this.info == null ? "" : this.info.name);
        this.serverName.setMaxStringLength(32);
		
		this.serverIp = new GuiTextField(this, this.fontRenderer, this.width / 2 - 100, this.height / 4 + 74, 200, 20, this.info == null ? "" : (this.info.ip+":"+this.info.port));
        this.serverName.isFocused = true;
        this.serverIp.setMaxStringLength(32);
        
        this.controlList.add(this.done = new GuiSmallButton(1, this.width / 2 - 50 + 52, this.height/4 - 10 + 50 + 20 + 14 + 24, 100, 20, "Done"));
        this.done.enabled = this.serverIp.getText().length() > 0;
        this.controlList.add(new GuiSmallButton(2, this.width / 2 - 50 - 52, this.height/4 - 10 + 50 + 20 + 14 + 24, 100, 20, "Cancel"));
	}
	@Override
	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
	}
	protected void keyTyped(char var1, int var2) {
        this.serverIp.textboxKeyTyped(var1, var2);
        this.serverName.textboxKeyTyped(var1, var2);
        if (var1 == '\r') {
        	if(this.serverIp.getText().equals("")){
        		this.serverIp.isFocused = true;
        		this.serverName.isFocused = false;
        	}else this.actionPerformed(this.done);
        }
        this.done.enabled = this.serverIp.getText().length() > 0;
    }
	protected void actionPerformed(GuiButton b) {
        if (b.enabled) {
            if (b.id == 2) {
                this.mc.displayGuiScreen(this.parent);
            }else if(b.id == 1) {
            	if(this.info == null) {
                    String[] ipport = this.serverIp.getText().split(":");
            		this.info = new ServerInfo(this.serverName.getText().length() <= 0 ? "Unnamed Server" : this.serverName.getText(), ipport[0], ipport.length > 1 ? GuiMultiplayer.getPort(ipport[1], 25565) : 25565);
            		GuiServerSelector.servers.add(this.info);
            	}else {
            		String[] ipport = this.serverIp.getText().split(":");
            		this.info.name = this.serverName.getText().length() <= 0 ? "Unnamed Server" : this.serverName.getText();
            		this.info.ip = ipport[0];
            		this.info.port = ipport.length > 1 ? GuiMultiplayer.getPort(ipport[1], 25565) : 25565;
            	}
            	Client.writeCurrentServers();
            	this.mc.displayGuiScreen(this.parent);
            }
        }
	}
    protected void mouseClicked(int var1, int var2, int var3) {
        super.mouseClicked(var1, var2, var3);
        this.serverIp.mouseClicked(var1, var2, var3);
        this.serverName.mouseClicked(var1, var2, var3);
    }
    
	public void drawScreen(int mX, int mY, float rendTicks) {
		this.drawDefaultBackground();
		this.drawCenteredString(this.fontRenderer, "Edit server", this.width / 2, 12, 16777215);
		Client.mc.fontRenderer.drawString("Server Name", this.serverName.xPos, this.serverName.yPos - 10, 0xffffff);
		Client.mc.fontRenderer.drawString("Server IP", this.serverIp.xPos, this.serverIp.yPos - 10, 0xffffff);
		this.serverIp.drawTextBox();
		this.serverName.drawTextBox();
		
		super.drawScreen(mX, mY, rendTicks);
	}
}
