package net.skidcode.gh.maybeaclient.hacks;

import org.lwjgl.input.Keyboard;

import net.minecraft.src.Block;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.IInventory;
import net.minecraft.src.InventoryBasic;
import net.minecraft.src.InventoryLargeChest;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Packet100OpenWindow;
import net.minecraft.src.Packet101CloseWindow;
import net.minecraft.src.Packet104WindowItems;
import net.minecraft.src.Packet15Place;
import net.minecraft.src.TileEntity;
import net.minecraft.src.TileEntityChest;
import net.skidcode.gh.maybeaclient.events.Event;
import net.skidcode.gh.maybeaclient.events.EventListener;
import net.skidcode.gh.maybeaclient.events.EventRegistry;
import net.skidcode.gh.maybeaclient.events.impl.EventPacketReceive;
import net.skidcode.gh.maybeaclient.hacks.category.Category;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingFloat;

public class ChestContentHack extends Hack implements EventListener{
	public static ChestContentHack instance;
	public static boolean realOpened = false;
	public SettingFloat refreshData = new SettingFloat(this, "RefreshDataS", 1, 0.5f, 10, 0.5f);
	public ChestContentHack() {
		super("ChestContent", "Shows content of chest in specific tab", Keyboard.KEY_NONE, Category.UI);
		instance = this;
		
		this.addSetting(this.refreshData);
		
		EventRegistry.registerListener(EventPacketReceive.class, this);
	}
	public boolean waitingGui = false;
	@Override
	public void handleEvent(Event event) {
		if(event instanceof EventPacketReceive) {
			if(waitingGui && ((EventPacketReceive) event).packet instanceof Packet104WindowItems) {
				Packet104WindowItems pk = (Packet104WindowItems) ((EventPacketReceive) event).packet;
				ItemStack[] chest = new ItemStack[pk.itemStack.length - 36];
				System.arraycopy(pk.itemStack, 0, chest, 0, chest.length);
				this.setData(requestedX, requestedY, requestedZ, chest);
				event.cancelled = true;
			}else if(!realOpened && ((EventPacketReceive) event).packet instanceof Packet100OpenWindow) {
				Packet100OpenWindow pk = (Packet100OpenWindow) ((EventPacketReceive) event).packet;
				if(pk.inventoryType == 0) { //chest
					event.cancelled = true;
					mc.getSendQueue().addToSendQueue(new Packet101CloseWindow(pk.windowId));
				}
			}
		}
	}
	
	@Override
	public void onDisable() {
		items = null;
		fakeOpenY = -1;
		requestedY = -1;
		locked = 0;
		waitingGui = false;
	}
	
	public int requestedX, requestedY, requestedZ;
	public void requestChestContent(int x, int y, int z) {
		if(!mc.isMultiplayerWorld()) {
			IInventory var6 = (TileEntityChest)mc.theWorld.getBlockTileEntity(x, y, z);
			if (mc.theWorld.getBlockId(x - 1, y, z) == Block.chest.blockID) {
				var6 = new InventoryLargeChest("Large chest", (TileEntityChest)mc.theWorld.getBlockTileEntity(x - 1, y, z), var6);
			}

			if (mc.theWorld.getBlockId(x + 1, y, z) == Block.chest.blockID) {
				var6 = new InventoryLargeChest("Large chest", (IInventory)var6, (TileEntityChest)mc.theWorld.getBlockTileEntity(x + 1, y, z));
			}

			if (mc.theWorld.getBlockId(x, y, z - 1) == Block.chest.blockID) {
				var6 = new InventoryLargeChest("Large chest", (TileEntityChest)mc.theWorld.getBlockTileEntity(x, y, z - 1), var6);
			}

			if (mc.theWorld.getBlockId(x, y, z + 1) == Block.chest.blockID) {
				var6 = new InventoryLargeChest("Large chest", (IInventory)var6, (TileEntityChest)mc.theWorld.getBlockTileEntity(x, y, z + 1));
			}
			ItemStack[] chest = new ItemStack[var6.getSizeInventory()];
			for(int i = 0; i < chest.length; ++i) chest[i] = var6.getStackInSlot(i);
			this.setData(x, y, z, chest);
			
			return;
		}
		if(locked != 0 || mc.currentScreen != null) return;
		if((this.requestedX != x || this.requestedY != y || this.requestedZ != z)) {
			mc.getSendQueue().addToSendQueue(new Packet15Place(x, y, z, mc.objectMouseOver.sideHit, mc.thePlayer.inventory.getCurrentItem()));
			waitingGui = true;
		}
		this.requestedX = x;
		this.requestedY = y;
		this.requestedZ = z;
		
	}
	
	public void setData(int x, int y, int z, ItemStack[] data) {
		this.fakeOpenX = x;
		this.fakeOpenY = y;
		this.fakeOpenZ = z;
		this.requestedY = -1;
		this.items = data;
		this.waitingGui = false;
		this.lastData = System.currentTimeMillis();
	}
	
	public boolean hasData(int x, int y, int z) {
		return this.fakeOpenX == x && this.fakeOpenY == y && this.fakeOpenZ == z && this.items != null;
	}
	public ItemStack[] getChestContents(int x, int y, int z) {
		if(this.hasData(x, y, z)) {
			long refresh = (long) (this.refreshData.getValue()*1000);
			if((System.currentTimeMillis() - this.lastData) > refresh) this.requestChestContent(x, y, z);
			return this.items;
		}
		this.requestChestContent(x, y, z);
		return null;
	}
	public ItemStack[] items;
	public int fakeOpenX, fakeOpenY = -1, fakeOpenZ;
	public long lastData = 0;
	public int locked = 0;
	
}
