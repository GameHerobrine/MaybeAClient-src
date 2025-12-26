package net.skidcode.gh.maybeaclient.hacks;

import java.util.List;

import org.lwjgl.input.Keyboard;

import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.Block;
import net.minecraft.src.BlockFluid;
import net.minecraft.src.MathHelper;
import net.minecraft.src.Packet10Flying;
import net.skidcode.gh.maybeaclient.Client;
import net.skidcode.gh.maybeaclient.events.Event;
import net.skidcode.gh.maybeaclient.events.EventListener;
import net.skidcode.gh.maybeaclient.events.EventRegistry;
import net.skidcode.gh.maybeaclient.events.impl.EventPacketSend;
import net.skidcode.gh.maybeaclient.events.impl.EventPlayerUpdatePre;
import net.skidcode.gh.maybeaclient.hacks.category.Category;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingBoolean;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingInteger;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingMode;
import net.skidcode.gh.maybeaclient.utils.BlockPos;
import net.skidcode.gh.maybeaclient.utils.ChatColor;

public class NoFallHack extends Hack implements EventListener{
	public static NoFallHack instance;
	public SettingMode mode;
	
	public SettingInteger disconnectIfRemainingHpWillBe = new SettingInteger(this, "DisconnectHealth", 10, 1, 20);
	public SettingBoolean instantReconnect = new SettingBoolean(this, "InstantReconnect", false);
	public SettingBoolean checkWater = new SettingBoolean(this, "CheckWater", true);
	
	public NoFallHack() {
		super("NoFall", "Removes fall damage", Keyboard.KEY_NONE, Category.MISC);
		instance = this;
		this.addSetting(this.mode = new SettingMode(this, "Mode", "Normal", "Test", "Disconnect") {
			public void setValue(String value) {
				super.setValue(value);
				
				NoFallHack.instance.disconnectIfRemainingHpWillBe.hidden = !this.currentMode.equalsIgnoreCase("Disconnect");
				NoFallHack.instance.instantReconnect.hidden = !this.currentMode.equalsIgnoreCase("Disconnect");
				NoFallHack.instance.checkWater.hidden = !this.currentMode.equalsIgnoreCase("Disconnect");
			}
		});
		this.addSetting(this.disconnectIfRemainingHpWillBe);
		this.addSetting(this.instantReconnect);
		this.addSetting(this.checkWater);
		EventRegistry.registerListener(EventPacketSend.class, this);
		EventRegistry.registerListener(EventPlayerUpdatePre.class, this);
	}
	
	@Override
	public String getPrefix() {
		return this.mode.currentMode;
	}
	
	public boolean hasdPosY = false;
	public boolean cancelMovement = false;
	public boolean sentDisconnectPacket = false;
	
	public double dPosY = 0, dBBMinY = 0, dBBMaxY = 0;
	@Override
	public void handleEvent(Event event) {
		if(event instanceof EventPacketSend) {
			EventPacketSend ev = (EventPacketSend) event;
			if(mode.currentMode.equalsIgnoreCase("Normal")) {
				if(ev.packet instanceof Packet10Flying) {
					((Packet10Flying)ev.packet).onGround = true;
				}
			}else if(mode.currentMode.equalsIgnoreCase("Test")){
				if(ev.packet instanceof Packet10Flying) {
					((Packet10Flying)ev.packet).onGround = false;
				}
			}else if(mode.currentMode.equalsIgnoreCase("Disconnect")) {
				if(this.cancelMovement) event.cancelled = true;
			}
		}else if(event instanceof EventPlayerUpdatePre) {
			if(mode.currentMode.equalsIgnoreCase("Disconnect")) {
				if(!mc.thePlayer.onGround) {
					if(!this.hasdPosY) {
						this.hasdPosY = true;
						this.dPosY = mc.thePlayer.posY;
					}else if(mc.thePlayer.posY > this.dPosY) {
						this.dPosY = mc.thePlayer.posY;
					}
				}else {
					this.hasdPosY = false;
				}
				if(this.hasdPosY) {
					double motY = -mc.thePlayer.posY;
					AxisAlignedBB bb = mc.thePlayer.boundingBox.copy();
					bb.minY = 0;
					bb.maxY = mc.thePlayer.posY; //.addCoord(0, motY, 0);
					//BlockFluid.forceNullBB = true;
					List bbs = mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, bb);
	
		            for(int var20 = 0; var20 < bbs.size(); ++var20) {
		                motY = ((AxisAlignedBB)bbs.get(var20)).calculateYOffset(mc.thePlayer.boundingBox, motY);
		            }
		            //BlockFluid.forceNullBB = false;
					double fallDistance = -motY + (this.dPosY - mc.thePlayer.posY);
					
					int dmg = (int) Math.ceil((double)(fallDistance - 3.0F));
					int playerHealth = mc.thePlayer.health - dmg;
					if(this.checkWater.getValue()) {
						int x = MathHelper.floor_double(mc.thePlayer.posX);
						int z = MathHelper.floor_double(mc.thePlayer.posZ);
						int y = MathHelper.floor_double(mc.thePlayer.posY + motY);
						int id1 = mc.theWorld.getBlockId(x, y, z);
						int id2 = mc.theWorld.getBlockId(x, y-1, z);
						
						if((id1 == Block.waterStill.blockID || id1 == Block.waterMoving.blockID) && (id2 == Block.waterStill.blockID || id2 == Block.waterMoving.blockID)) {
							dmg = 0;
						}
					}
					
					if(dmg > 0 && playerHealth <= this.disconnectIfRemainingHpWillBe.value) {
						
						
						if(motY > -3) {
							Client.forceDisconnect(this);
							cancelMovement = true;
							sentDisconnectPacket = true;
						}
					}
				}
				return;
			}
			if(!mc.isMultiplayerWorld()) mc.thePlayer.fallDistance = 0.0f;
			if(mode.currentMode.equalsIgnoreCase("Test") && mc.isMultiplayerWorld() && mc.thePlayer.fallDistance > 2.0f){
				mc.getSendQueue().addToSendQueue(new Packet10Flying(false));
			}
		}
	}
}
