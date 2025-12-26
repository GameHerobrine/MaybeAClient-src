package net.skidcode.gh.maybeaclient.hacks;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraft.src.Block;
import net.minecraft.src.ItemBlock;
import net.minecraft.src.ItemStack;
import net.minecraft.src.MathHelper;
import net.minecraft.src.NBTTagByte;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.Packet14BlockDig;
import net.minecraft.src.Packet16BlockItemSwitch;
import net.minecraft.src.PlayerControllerMP;
import net.minecraft.src.RenderManager;
import net.minecraft.src.Tessellator;
import net.skidcode.gh.maybeaclient.Client;
import net.skidcode.gh.maybeaclient.events.Event;
import net.skidcode.gh.maybeaclient.events.EventListener;
import net.skidcode.gh.maybeaclient.events.EventRegistry;
import net.skidcode.gh.maybeaclient.events.impl.EventPlayerUpdatePost;
import net.skidcode.gh.maybeaclient.events.impl.EventWorldRenderPreFog;
import net.skidcode.gh.maybeaclient.hacks.category.Category;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingBlockChooser;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingBoolean;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingInteger;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingMode;
import net.skidcode.gh.maybeaclient.utils.BlockPos;
import net.skidcode.gh.maybeaclient.utils.ChatColor;
import net.skidcode.gh.maybeaclient.utils.Direction;
import net.skidcode.gh.maybeaclient.utils.PlayerUtils;
import net.skidcode.gh.maybeaclient.utils.RenderUtils;
import net.skidcode.gh.maybeaclient.utils.PlayerUtils.LookStatus;

public class AutoTunnelHack extends Hack implements EventListener{
	
	public SettingMode direction = new SettingMode(this, "Direction", "Camera", "X+", "X-", "Z+", "Z-");
	public SettingMode breakMode;
	public SettingInteger maxMultiBlocks = new SettingInteger(this, "MaxMultiBlocks", 4, 1, 20);
	public SettingBoolean swing = new SettingBoolean(this, "Swing", false);
	
	public SettingMode autoWalk;
	public SettingInteger scanForDanger = new SettingInteger(this, "Required walkable blocks", 1, 1, 4);
	
	public SettingBoolean placeBlocksBelow = new SettingBoolean(this, "Place blocks below", false);
	public SettingBoolean backFill = new SettingBoolean(this, "BackFill", false);
	public SettingBoolean enableBlockFilter;
	public SettingBlockChooser filter = new SettingBlockChooser(this, "Backfill block filter");
	public SettingInteger placeDelay = new SettingInteger(this, "PlaceDelay", 1, 0, 20);
	public SettingInteger backfillReach = new SettingInteger(this, "BackfillReach", 1, 1, 4);
	public SettingBoolean clientSidePlace = new SettingBoolean(this, "ClientSidePlace", false);
	
	public SettingInteger tunnelHeight = new SettingInteger(this, "Tunnel Height", 2, 1, 4);
	public SettingInteger tunnelWidthA = new SettingInteger(this, "Tunnel Width Left", 0, 0, 5);
	public SettingInteger tunnelWidthD = new SettingInteger(this, "Tunnel Width Right", 0, 0, 5);
	public SettingInteger tunnelLength = new SettingInteger(this, "Tunnel Length", 2, 1, 4);
	public SettingInteger delayBetweenBlocks = new SettingInteger(this, "Delay between blocks", 0, 0, 20);
	
	public SettingMode rotationMode = new SettingMode(this, "Rotation mode", "ServerSideOnly", "Off");
	
	
	public static AutoTunnelHack instance;
	public int breakTimer = 0;
	public int selectedBlockX = 0;
	public int selectedBlockY = 0;
	public int selectedBlockZ = 0;
	public int placeTimer = 0;
	public boolean isSelected = false;
	public static ArrayList<BlockPos> currentlyDestroying = new ArrayList<>();
	
	public AutoTunnelHack() {
		super("AutoTunnel", "Automatically destroy blocks in front of the player", Keyboard.KEY_NONE, Category.MISC);
		AutoTunnelHack.instance = this;
		
		
		this.breakMode = new SettingMode(this, "BreakMode", "Legal", "InstantSingle", "InstantMulti") {
			public void setValue(String value) {
				super.setValue(value);
				AutoTunnelHack.instance.maxMultiBlocks.hidden = !value.equalsIgnoreCase("InstantMulti");
				delayBetweenBlocks.hidden = !value.equalsIgnoreCase("Legal");
			}
		};
		this.addSetting(this.delayBetweenBlocks);
		
		this.addSetting(this.direction);
		this.addSetting(this.autoWalk = new SettingMode(this, "DirectionAutowalk", "Off", "Simple", "CheckCanWalk") {
			@Override
			public void setValue(String s) {
				super.setValue(s);
				scanForDanger.hidden = !s.equalsIgnoreCase("CheckCanWalk");
			}
			@Override
			public void readFromNBT(NBTTagCompound input) {
				if(input.hasKey(this.name)) {
					if(input.tagMap.get(this.name) instanceof NBTTagByte) {
						boolean b = input.getBoolean(this.name);
						this.setValue(b ? "Simple" : "Off");
						return;
					}
				}
				
				super.readFromNBT(input);
			}
			
		});
		this.addSetting(this.scanForDanger);
		
		this.addSetting(this.breakMode);
		this.addSetting(this.maxMultiBlocks);
		this.addSetting(this.swing);
		this.addSetting(this.backFill);
		this.addSetting(this.placeBlocksBelow);
		this.enableBlockFilter = new SettingBoolean(this, "Enable place block filter", false) {
			public void setValue(boolean d) {
				super.setValue(d);
				((AutoTunnelHack)this.hack).filter.hidden = !this.value;
			}
		};

		this.addSetting(this.enableBlockFilter);
		this.addSetting(this.filter);
		this.addSetting(this.placeDelay);
		this.addSetting(this.backfillReach);
		this.addSetting(this.clientSidePlace);
		
		this.addSetting(this.tunnelHeight);
		this.addSetting(this.tunnelWidthA);
		this.addSetting(this.tunnelWidthD);
		this.addSetting(this.tunnelLength);
		
		this.addSetting(this.rotationMode);
		
		EventRegistry.registerListener(EventPlayerUpdatePost.class, this);
		EventRegistry.registerListener(EventWorldRenderPreFog.class, this);
	}
	
	@Override
	public String getPrefix() {
		return this.getDirection().toString();
	}
	@Override
	public void onDisable() {
		mc.playerController.field_1064_b = false;
		this.isSelected = false;
		this.placeTimer = 0;
	}
	
	public boolean trySettingXYZ(int x, int y, int z) {
		int id = mc.theWorld.getBlockId(x, y, z);
		
		if(id != 0) {
			float hardness = Block.blocksList[id].blockHardness;
			if(hardness >= 0) { //block is destructable
				//notch moment
				if(id != Block.waterMoving.blockID && id != Block.waterStill.blockID && id != Block.lavaMoving.blockID && id != Block.lavaStill.blockID) {
					this.isSelected = true;
					if(x != this.selectedBlockX || y != this.selectedBlockY || z != this.selectedBlockZ) {
						if(this.breakMode.currentMode.equalsIgnoreCase("Legal")) {
							this.breakTimer = this.delayBetweenBlocks.getValue();
						}
					}
					this.selectedBlockX = x;
					this.selectedBlockY = y;
					this.selectedBlockZ = z;
					
					if(this.breakMode.currentMode.equalsIgnoreCase("InstantMulti")) {
						this.isSelected = false;
						if(currentlyDestroying.size() >= this.maxMultiBlocks.value) {
							return true;
						}else {
							currentlyDestroying.add(new BlockPos(x, y, z));
							if(this.swing.value) mc.thePlayer.swingItem();
							PlayerUtils.destroyBlockInstant(x, y, z, this.getDirection().hitSide);
							return false;
						}
					}
				}else {
					this.isSelected = false;
				}
			}
			
		}else {
			this.isSelected = false;
		}
		
		return this.isSelected;
	}
	
	public Direction getDirection() {
		if(this.direction.currentMode.equalsIgnoreCase("X+")) return Direction.XPOS;
		if(this.direction.currentMode.equalsIgnoreCase("X-")) return Direction.XNEG;
		if(this.direction.currentMode.equalsIgnoreCase("Z+")) return Direction.ZPOS;
		if(this.direction.currentMode.equalsIgnoreCase("Z-")) return Direction.ZNEG;
		if(this.direction.currentMode.equalsIgnoreCase("Camera")) return PlayerUtils.getDirection();
		return Direction.NULL;
	}
	
	public int getPossiblePlaceSide(int xx, int yy, int zz) {
		
		ItemStack item = mc.thePlayer.getCurrentEquippedItem();
		if(item == null || !(item.getItem() instanceof ItemBlock)) return 6; 
		
		for(int i = 0; i < 6; ++i) {
			int x = xx;
			int y = yy;
			int z = zz;
			
			if(i == 0) ++y;
			if(i == 1) --y;
			if(i == 2) ++z;
			if(i == 3) --z;
			if(i == 4) ++x;
			if(i == 5) --x;
			int placeon = mc.theWorld.getBlockId(x, y, z);
			if(placeon == 0) continue;
			
			Block b = Block.blocksList[placeon];
			if(b.blockMaterial.getIsSolid()) {
				return i;
			}
		}
		
		return 6;
	}
	public int findItemToPlace() {
		for(int i = 0; i < 9; ++i) {
			ItemStack stack = mc.thePlayer.inventory.mainInventory[i];
			
			if(stack != null && stack.getItem() instanceof ItemBlock) {
				if(stack.stackSize == 0) {
					mc.thePlayer.inventory.mainInventory[i] = null;
					continue;
				}
				ItemBlock bl = (ItemBlock) stack.getItem();
				if(!this.enableBlockFilter.value) {
					if(Block.blocksList[bl.blockID].blockMaterial.getIsSolid()) return i;
					continue;
				}else if(this.filter.blocks[bl.blockID]) {
					return i;
				}
			}
		}
		return -1;
	}
	
	public boolean isLiquid(int id) {
		return id == Block.waterMoving.blockID || id == Block.waterStill.blockID || id == Block.lavaMoving.blockID || id == Block.lavaStill.blockID;
	}
	
	public boolean isBlockWalkable(int id) {
		Block b = Block.blocksList[id];
		if(b == null || this.isLiquid(id)) return false;
		return b.blockMaterial.getIsSolid() && b.renderAsNormalBlock();
	}
	
	public boolean tryPlacingXYZ(int x, int y, int z) {
		if(this.placeTimer > 0) return true;
		if(this.isBlockWalkable(mc.theWorld.getBlockId(x, y, z))) {
			return false;
		}
		
		int it = this.findItemToPlace();
		int prev = -1;
		if(it != -1) {
			prev = mc.thePlayer.inventory.currentItem;
			mc.thePlayer.inventory.currentItem = it;
			if(mc.isMultiplayerWorld()) {
				((PlayerControllerMP)mc.playerController).syncCurrentPlayItem();
			}
		}
		
		
		
		int face = this.getPossiblePlaceSide(x, y, z);
		if(face != 6) {
			//if(this.rotationMode.currentMode.equalsIgnoreCase("ServerSideOnly")) PlayerUtils.lookAt(x, y, z, LookStatus.PACKET);
			
			if(face == 0) ++y;
			if(face == 1) --y;
			if(face == 2) ++z;
			if(face == 3) --z;
			if(face == 4) ++x;
			if(face == 5) --x;
			
			if(this.clientSidePlace.getValue()) PlayerUtils.placeBlockUnsafe(x, y, z, face);
			else PlayerUtils.placeBlock(x, y, z, face);
			placeTimer = this.placeDelay.getValue();
		}
		
		if(it != -1) {
			mc.thePlayer.inventory.currentItem = prev;
			if(mc.isMultiplayerWorld()) {
				((PlayerControllerMP)mc.playerController).syncCurrentPlayItem();
			}
		}
		return true;
	}
	
	public boolean isAutoWalking = false;
	
	@Override
	public void handleEvent(Event event) {
		if(event instanceof EventWorldRenderPreFog) {
			
			double rendX = RenderManager.renderPosX;
			double rendY = RenderManager.renderPosY;
			double rendZ = RenderManager.renderPosZ;
			GL11.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			if(this.isSelected) {
				RenderUtils.drawOutlinedBlockBB(this.selectedBlockX - rendX, this.selectedBlockY - rendY, this.selectedBlockZ - rendZ);
			}
			
			for(BlockPos pos : currentlyDestroying){
				RenderUtils.drawOutlinedBlockBB(pos.x - rendX, pos.y - rendY, pos.z - rendZ);
			}
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			
		}else if(event instanceof EventPlayerUpdatePost) {
			currentlyDestroying.clear();
			Direction dir = this.getDirection();
			int x = MathHelper.floor_double(mc.thePlayer.posX);
			int y = MathHelper.floor_double(mc.thePlayer.posY)-1;
			int z = MathHelper.floor_double(mc.thePlayer.posZ);
			
			int tla = this.tunnelWidthA.getValue();
			int tld = this.tunnelWidthD.getValue();
			int tlh = this.tunnelHeight.getValue();
			int tll = this.tunnelLength.getValue();
			
			if(this.placeTimer > 0) --this.placeTimer;
			if(this.backFill.getValue()) {
				boolean destroyLocked = this.placeTimer > 0;
				boolean playerinside = false;
				tunnelLengthLoop: for(int i = -1; i < 0; ++i) {
					int xb = x + dir.offX*i;
					int zb = z + dir.offZ*i;
					
					if(dir.x()) zb -= tla;
					else xb -= tla;
					
					for(int j = 0; j <= tla+tld; ++j) {
						int yb = y + dir.offY*i;
						for(int k = 0; k < tlh; ++k) {
							if(destroyLocked |= this.tryPlacingXYZ(xb, yb, zb)) {
								
								playerinside = PlayerUtils.collidesWithBlockIfPlaced(xb, yb, zb);
								break tunnelLengthLoop;
							}
							++yb;
						}
						if(dir.x()) ++zb;
						else ++xb;
					}
				}
				// && this.breakMode.currentMode.equalsIgnoreCase("Legal")
				if(destroyLocked) {
					this.isAutoWalking = playerinside;
					return;
				}
				
			}
			
			
			
			if(this.autoWalk.currentMode.equalsIgnoreCase("CheckCanWalk")) {
				boolean cancelled = false;
				tunnelLengthLoop: for(int i = 0; i <= this.scanForDanger.getValue(); ++i) {
					int xb = x + dir.offX*i;
					int zb = z + dir.offZ*i;
					
					if(dir.x()) zb -= tla;
					else xb -= tla;
					
					for(int j = 0; j <= tla+tld; ++j) {
						int yb = (y - 1);
						int id = Client.mc.theWorld.getBlockId(xb, yb, zb);
						if(!this.isBlockWalkable(id)) {
							cancelled = true;
							break tunnelLengthLoop;
						}
						
						for(int k = 0; k < tlh; ++k) {
							++yb;
							id = Client.mc.theWorld.getBlockId(xb, yb, zb);
							if(this.isBlockWalkable(id)) {
								cancelled = true;
								break tunnelLengthLoop;
							}
						}
						
						if(dir.x()) ++zb;
						else ++xb;
					}
				}
				this.isAutoWalking = !cancelled;
			}else if(!this.autoWalk.currentMode.equalsIgnoreCase("Off")){
				isAutoWalking = true;
			}

			//this.placeBlocksBelow.getValue()
			
			tunnelLengthLoop: for(int i = 0; i <= tll; ++i) {
				int xb = x + dir.offX*i;
				int zb = z + dir.offZ*i;
				
				if(dir.x()) zb -= tla;
				else xb -= tla;
				
				for(int j = 0; j <= tla+tld; ++j) {
					int yb = (y + dir.offY*i) + tlh;
					for(int k = 0; k <= tlh; ++k) {
						--yb;
						if(k == tlh) {
							if(this.placeBlocksBelow.getValue() && this.tryPlacingXYZ(xb, yb, zb)) break tunnelLengthLoop;
						}else {
							if(this.trySettingXYZ(xb, yb, zb)) break tunnelLengthLoop;
						}
						
						
					}
					if(dir.x()) ++zb;
					else ++xb;
				}
			}
			
			
			
			if(this.isSelected) {
				if(this.rotationMode.currentMode.equalsIgnoreCase("ServerSideOnly")) PlayerUtils.lookAt(this.selectedBlockX, this.selectedBlockY, this.selectedBlockZ, LookStatus.PACKET);
				
				
				if(this.swing.value) mc.thePlayer.swingItem();
				if(this.breakMode.currentMode.equalsIgnoreCase("InstantSingle")) {
		            PlayerUtils.destroyBlockInstant(this.selectedBlockX, this.selectedBlockY, this.selectedBlockZ, dir.hitSide);
				}else {
					if(this.breakTimer > 0) {
						--this.breakTimer;
						return;
					}
					PlayerUtils.destroyBlock(this.selectedBlockX, this.selectedBlockY, this.selectedBlockZ, dir.hitSide);
				}
			}
		}
	}

	public static boolean autoWalking() {
		return AutoTunnelHack.instance.status && !AutoTunnelHack.instance.autoWalk.currentMode.equalsIgnoreCase("Off") && AutoTunnelHack.instance.isAutoWalking;
	}

}
