package net.minecraft.src;

import net.skidcode.gh.maybeaclient.Client;
import net.skidcode.gh.maybeaclient.hacks.AFKDisconnectHack;
import net.skidcode.gh.maybeaclient.hacks.AutoReconnectHack;
import net.skidcode.gh.maybeaclient.hacks.CombatLogHack;
import net.skidcode.gh.maybeaclient.hacks.NoFallHack;
import net.skidcode.gh.maybeaclient.hacks.PlayerlistHack;

public class GuiConnectFailed extends GuiScreen {
    private String errorMessage;
    private String errorDetail;

    public GuiConnectFailed(String var1, String var2, Object... var3) {
        StringTranslate var4 = StringTranslate.getInstance();
        this.errorMessage = var4.translateKey(var1);
        if (var3 != null) {
            this.errorDetail = var4.translateKeyFormat(var2, var3);
        } else {
            this.errorDetail = var4.translateKey(var2);
        }

    }

    public void updateScreen() {
    }

    protected void keyTyped(char var1, int var2) {
    }
    public GuiButton reconnectButton;
    public void initGui() {
        StringTranslate var1 = StringTranslate.getInstance();
        this.controlList.clear();
        this.controlList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 120 + 12, var1.translateKey("gui.toMenu")));
        this.controlList.add(this.reconnectButton = new GuiButton(1, this.width / 2 - 100, this.height / 4 + 120 + 12 + 20, "Reconnect"));
    }

    protected void actionPerformed(GuiButton var1) {
        if (var1.id == 0) {
            this.mc.displayGuiScreen(new GuiMainMenu());
        }
        if(var1.id == this.reconnectButton.id) {
        	String ipport = this.mc.gameSettings.realLastServer.replaceAll("_", ":");
            String[] var3 = ipport.split(":");
            this.mc.displayGuiScreen(new GuiConnecting(this.mc, var3[0], var3.length > 1 ? GuiMultiplayer.getPort(var3[1], 25565) : 25565));
        }
    }

    public boolean renderDPS = false;
    public boolean canAutoReconnect = true;
    public String additionalToRender = "";
    public boolean beganReconnect = false;
    public long timeMS = 0;
    public long counter = 0;
    public void drawScreen(int var1, int var2, float var3) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, this.errorMessage, this.width / 2, this.height / 2 - 50, 16777215);
        this.drawCenteredString(this.fontRenderer, this.errorDetail, this.width / 2, this.height / 2 - 10, 16777215);
        
        if(this.renderDPS || NoFallHack.instance.sentDisconnectPacket) {
        	additionalToRender = "(NoFall sent disconnect packet)";
        	
        	if(NoFallHack.instance.sentDisconnectPacket && NoFallHack.instance.instantReconnect.getValue()) {
        		this.actionPerformed(this.reconnectButton);
        	}
        	NoFallHack.instance.sentDisconnectPacket = false;
        	this.renderDPS = true;
        	NoFallHack.instance.cancelMovement = false;
        	NoFallHack.instance.hasdPosY = false;
        }
        PlayerlistHack.detectedPlayers.clear();
        
        if(Client.disconnectCausedBy instanceof AFKDisconnectHack) {
			if(AFKDisconnectHack.instance.preventReconnect.getValue()) canAutoReconnect = false;
			additionalToRender = "(Caused by AFKDisconnect)";
		}
        if(Client.disconnectCausedBy instanceof CombatLogHack) {
        	if(CombatLogHack.instance.preventReconnect.getValue()) canAutoReconnect = false;
        	additionalToRender = "(Caused by CombatLog)";
        }
        this.drawCenteredString(this.fontRenderer, additionalToRender, this.width / 2, this.height / 2 - 10 + 10, 16777215);
        Client.disconnectCausedBy = null;
        
        if(AutoReconnectHack.instance.status && this.reconnectButton != null && canAutoReconnect) {
        	if(!beganReconnect) {
        		
        		beganReconnect = true;
        		timeMS = System.currentTimeMillis();
        		counter = AutoReconnectHack.instance.delaySeconds.value*1000;
        		this.reconnectButton.displayString = "Reconnect (" +  AutoReconnectHack.instance.delaySeconds.value + ")";
        	}else {
        		long time = System.currentTimeMillis();
        		this.counter -= (time - this.timeMS);
        		this.timeMS = time;
        		this.reconnectButton.displayString = "Reconnect (" +  (int)Math.ceil((double)counter/1000) + ")";
        		
        		if(this.counter <= 0) {
        			this.actionPerformed(this.reconnectButton);
        		}
        	}
        }
        
        super.drawScreen(var1, var2, var3);
    }
}
