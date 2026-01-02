package net.skidcode.gh.maybeaclient.hacks;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map.Entry;

import org.lwjgl.input.Keyboard;

import lunatrius.schematica.GuiSchematicControl;
import lunatrius.schematica.GuiSchematicLoad;
import lunatrius.schematica.GuiSchematicSave;
import lunatrius.schematica.GuiSchematicaStats;
import lunatrius.schematica.SchematicWorld;
import lunatrius.schematica.Settings;
import net.minecraft.src.Block;
import net.minecraft.src.ItemBlock;
import net.minecraft.src.ItemStack;
import net.minecraft.src.MathHelper;
import net.skidcode.gh.maybeaclient.Client;
import net.skidcode.gh.maybeaclient.events.Event;
import net.skidcode.gh.maybeaclient.events.EventListener;
import net.skidcode.gh.maybeaclient.events.EventRegistry;
import net.skidcode.gh.maybeaclient.events.impl.EventPlayerUpdatePost;
import net.skidcode.gh.maybeaclient.hacks.category.Category;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingBoolean;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingButton;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingInteger;
import net.skidcode.gh.maybeaclient.utils.PlayerUtils;

public class SchematicaHack extends Hack implements EventListener{
	public SettingBoolean enableAlpha;
	public SettingInteger alpha;
	public SettingBoolean highlight;
	
	//public SettingInteger renderRangeX = new SettingInteger(this, "Render Range X", 20, 5, 50);
	//public SettingInteger renderRangeY = new SettingInteger(this, "Render Range Y", 20, 5, 50);
	//public SettingInteger renderRangeZ = new SettingInteger(this, "Render Range Z", 20, 5, 50);
	//public SettingFloat blockDelta = new SettingFloat(this, "Highlight block delta", 0.005f, 0, 0.5f);
	
	public SettingButton openLoad = new SettingButton(this, "Load");
	public SettingButton openSave = new SettingButton(this, "Save");
	public SettingButton openControl = new SettingButton(this, "Control");
	public SettingButton openStats = new SettingButton(this, "Stats");
	
	public SettingBoolean autoPlacer;
	public SettingBoolean placeOnlyBelow = new SettingBoolean(this, "PlaceOnlyBelow", false);
	
	public SettingInteger placeDelay = new SettingInteger(this, "Delay", 5, 0, 10);
	public SettingInteger placeRadius = new SettingInteger(this, "Radius", 2, 1, 6);
	public final lunatrius.schematica.Render render = new lunatrius.schematica.Render(this);
	
	public static SchematicaHack instance;
	
	public SchematicaHack() {
		super("Schematica", "Port of Schematica", Keyboard.KEY_NONE, Category.MISC);
		instance = this;
		
		this.enableAlpha = new SettingBoolean(this, "Enable Transparency", false) {
			@Override
			public void setValue(boolean b) {
				super.setValue(b);
				Settings.instance().requestFullUpdate();
			}
		};
		this.alpha = new SettingInteger(this, "Transparency", 0, 0, 255) {
			@Override
			public void setValue(int i) {
				int prev = this.value;
				super.setValue(i);
				Settings.instance().requestFullUpdate();
			}
		};
		this.highlight = new SettingBoolean(this, "Highlight", true) {
			@Override
			public void setValue(boolean b) {
				boolean prev = this.value;
				super.setValue(b);
				Settings.instance().requestFullUpdate();
			}
		};
		
		this.addSetting(this.autoPlacer = new SettingBoolean(this, "AutoBlockPlace", false) {
			public void setValue(boolean d) {
				super.setValue(d);
				SchematicaHack.instance.placeOnlyBelow.hidden = SchematicaHack.instance.placeDelay.hidden = SchematicaHack.instance.placeRadius.hidden = !d;
			}
		});
		this.addSetting(this.placeDelay);
		this.addSetting(this.placeRadius);
		this.addSetting(this.placeOnlyBelow);
		
		this.addSetting(this.enableAlpha);
		this.addSetting(this.alpha);
		this.addSetting(this.highlight);
		
		//this.addSetting(this.renderRangeX);
		//this.addSetting(this.renderRangeY);
		//this.addSetting(this.renderRangeZ);
		
		this.addSetting(this.openLoad);
		this.addSetting(this.openStats);
		this.openStats.hidden = true;
		this.addSetting(this.openSave);
		this.addSetting(this.openControl);
		
        Settings.schematicDirectory.mkdirs();
        Settings.textureDirectory.mkdirs();
		//this.addSetting(this.blockDelta);
        
        EventRegistry.registerListener(EventPlayerUpdatePost.class, this);
	}
	

	@Override
	public void onPressed(SettingButton b) {
		if(b == openSave) mc.displayGuiScreen(new GuiSchematicSave(mc.currentScreen));
		else if(b == openControl) mc.displayGuiScreen(new GuiSchematicControl(mc.currentScreen));
		else if(b == openStats) mc.displayGuiScreen(new GuiSchematicaStats(mc.currentScreen));
		else if(b == openLoad) mc.displayGuiScreen(new GuiSchematicLoad(mc.currentScreen));
		super.onPressed(b);
	}
	
	public int ticks = 0;
	@Override
	public void handleEvent(Event event) {
		if(event instanceof EventPlayerUpdatePost) {
			if(!this.autoPlacer.getValue()) return;
			
			Settings inst = Settings.instance();
			SchematicWorld schem = inst.schematic;
			if(schem != null) {
				if(this.ticks++ < this.placeDelay.getValue()) {
					return;
				}
				this.ticks = 0;
				int minX = 0, maxX = 0, minY = 0, maxY = 0, minZ = 0, maxZ = 0;
				maxX = inst.schematic.width();
				maxY = inst.schematic.height();
				maxZ = inst.schematic.length();

				if (inst.renderingLayer >= 0) {
					minY = inst.renderingLayer;
					maxY = inst.renderingLayer + 1;
				}
				
				int radius = this.placeRadius.getValue();
				int ppX = (int) mc.thePlayer.posX;
				int ppY = (int) mc.thePlayer.posY;
				int ppZ = (int) mc.thePlayer.posZ;
				
				HashMap<Integer, Entry<ItemStack, Integer>> hm = new HashMap<Integer, Entry<ItemStack, Integer>>();
				for(int i = 0; i < 9; ++i) {
					ItemStack s = mc.thePlayer.inventory.mainInventory[i];
					if(s != null) {
						int idm = s.itemID << 8 | s.getItemDamage();
						hm.put(idm, new AbstractMap.SimpleEntry<ItemStack, Integer>(s, i));
					}
				}

				int minXr = (int) (ppX - radius);
				int maxXr = (int) (ppX + radius);
				int minZr = (int) (ppZ - radius);
				int maxZr = (int) (ppZ + radius);
				int minYr = (int) (ppY - radius);
				int maxYr = (int) (ppY + radius);
				for(int x = minX; x < maxX; ++x) {
					for(int z = minZ; z < maxZ; ++z) {
						for(int y = minY; y < maxY; ++y) {
							if(y > 127) continue;
							System.out.println(y+" "+mc.thePlayer.boundingBox.minY);
							int rX = 0, rY = 0, rZ = 0;
							try {
								rX = x + inst.offset.x;
								rY = y + inst.offset.y;
								rZ = z + inst.offset.z;

								if(SchematicaHack.instance.placeOnlyBelow.getValue() && rY >= MathHelper.floor_double(mc.thePlayer.boundingBox.minY)) {
									continue;
								}
								if(rX <= maxXr && rX >= minXr && rY <= maxYr && rY >= minYr && rZ <= maxZr && rZ >= minZr) {
									int id = schem.getBlockId(x, y, z); //schem.blocks[x][y][z];
									int worldID = mc.theWorld.getBlockId(rX, rY, rZ);
									int meta = schem.getBlockMetadata(x, y, z); //schem.metadata[x][y][z];
									Entry<ItemStack, Integer> ent = hm.get(id << 8 | meta);
									if((id != 0 && (worldID == 0/*XXX modern versions: Replaceable || Block.blocksList[worldID].blockMaterial.getIsGroundCover()*/)) && id != worldID && ent != null) {
										if(id == Block.gravel.blockID || id == Block.sand.blockID) {
											if(mc.theWorld.getBlockId(rX, rY-1, rZ) == 0) continue;
										}
										ItemStack is = ent.getKey();
										int slot = ent.getValue();
										int saved = mc.thePlayer.inventory.currentItem;
										mc.thePlayer.inventory.currentItem = slot;
										this.placeBlock(rX, rY, rZ);
										mc.thePlayer.inventory.currentItem = saved;
									}
								}
							}catch(java.lang.ArrayIndexOutOfBoundsException e) {e.printStackTrace();}
						}
					}
				}
				
				
			}
		}
	}
	
	public boolean canPlaceBlock(int x, int y, int z) {
		int id = mc.theWorld.getBlockId(x, y, z);
		return id == 0 || id == 10 || id == 11 || id == 8 || id == 9;
	}
	
	public int getPossiblePlaceSide(int xx, int yy, int zz) {
		
		ItemStack item = mc.thePlayer.getCurrentEquippedItem();
		if(item == null || !(item.getItem() instanceof ItemBlock)) return -1; 
		
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
			if(Client.isActiveable[placeon]) continue;
			if(b.blockMaterial.getIsSolid()) {
				return i;
			}
		}
		
		return -1;
	}
	

	public void placeBlock(int x, int y, int z) {
		int face = this.getPossiblePlaceSide(x, y, z);
		if(face != -1) {
			if(face == 0) ++y;
			if(face == 1) --y;
			if(face == 2) ++z;
			if(face == 3) --z;
			if(face == 4) ++x;
			if(face == 5) --x;
			
			//PlayerUtils.placeBlockUnsafe(x, y, z, face);
			PlayerUtils.placeBlock(x, y, z, face);
			//if(this.clientSidePlace.getValue()) 
			//else PlayerUtils.placeBlock(x, y, z, face);
		}
	}
	
	@Override
	public void onEnable() {
		this.ticks = 0;
	}
}
