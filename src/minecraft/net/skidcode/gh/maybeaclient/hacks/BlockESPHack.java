package net.skidcode.gh.maybeaclient.hacks;

import java.util.ArrayList;
import java.util.HashSet;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraft.src.Block;
import net.minecraft.src.GuiIngame;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.RenderManager;
import net.minecraft.src.Tessellator;
import net.skidcode.gh.maybeaclient.Client;
import net.skidcode.gh.maybeaclient.events.Event;
import net.skidcode.gh.maybeaclient.events.EventListener;
import net.skidcode.gh.maybeaclient.events.EventRegistry;
import net.skidcode.gh.maybeaclient.events.impl.EventWorldRenderPreFog;
import net.skidcode.gh.maybeaclient.gui.click.ClickGUI;
import net.skidcode.gh.maybeaclient.gui.click.SettingsTab;
import net.skidcode.gh.maybeaclient.gui.click.Tab;
import net.skidcode.gh.maybeaclient.gui.click.element.Element;
import net.skidcode.gh.maybeaclient.gui.click.element.VerticalContainer;
import net.skidcode.gh.maybeaclient.hacks.ClickGUIHack.Theme;
import net.skidcode.gh.maybeaclient.hacks.category.Category;
import net.skidcode.gh.maybeaclient.hacks.settings.Setting;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingBlockChooser;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingBoolean;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingColor;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingFloat;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingsProvider;
import net.skidcode.gh.maybeaclient.utils.BlockPos;

public class BlockESPHack extends Hack implements EventListener{
	public static class SettingsTabWithParentProvider extends SettingsTab{
		public static final Tab fakeMinimized = new Tab("") {};
		static {
			fakeMinimized.minimized.setValue(true);
			fakeMinimized.shown = false;
		}
		public SettingsTabWithParentProvider(Element parent, SettingsProvider sp) {
			super(parent, sp);
		}
		
		@Override
		public int getInitialYPos() {
			return this.parent.startY;
		}
	}
	public static class ESPBlockChooser extends SettingBlockChooser{
		public static ESPBlockChooser instanz;
		public SettingsProvider[] blockSettings;
		public SettingColor[] color;
		public SettingBoolean[] usesDefault;
		@Override
		public void writeToNBT(NBTTagCompound output) {
			super.writeToNBT(output);
			NBTTagCompound tag = output.getCompoundTag(this.name);
			byte blockColors[] = new byte[this.color.length*3];
			byte usesDefaults[] = new byte[this.usesDefault.length];
			for(int i = 0; i < this.color.length; ++i) {
				blockColors[i*3+0] = (byte) (this.color[i].red & 0xff);
				blockColors[i*3+1] = (byte) (this.color[i].green & 0xff);
				blockColors[i*3+2] = (byte) (this.color[i].blue & 0xff);
				
				usesDefaults[i] = (byte) (this.usesDefault[i].value ? 1 : 0);
				
			}
			tag.setByteArray("BlockColors", blockColors);
			tag.setByteArray("BlockUsesDefaults", usesDefaults);
		}

		@Override
		public void readFromNBT(NBTTagCompound input) {
			super.readFromNBT(input);
			NBTTagCompound tag = input.getCompoundTag(this.name);
			byte[] colors = tag.getByteArray("BlockColors");
			byte[] usesDefs = tag.getByteArray("BlockUsesDefaults");
			
			for(int i = 0; i < this.color.length; ++i) {
				if(colors.length > 0) {
					int red = colors[i*3+0] & 0xff;
					int green = colors[i*3+1] & 0xff;
					int blue = colors[i*3+2] & 0xff;
					
					this.color[i].setValue(red, green, blue);
				}
				if(usesDefs.length > 0) {
					this.usesDefault[i].setValue(usesDefs[i] != 0 ? true : false);
				}
			}
		}
		
		public ESPBlockChooser(Hack hack, String name, int... ids) {
			super(hack, name, ids);
			instanz = this;
			
			this.color = new SettingColor[this.blocks.length];
			this.usesDefault = new SettingBoolean[this.blocks.length];
			this.blockSettings = new SettingsProvider[this.blocks.length];
			
			for(int i = 0; i < this.color.length; ++i) {
				this.blockSettings[i] = new SettingsProvider() {
					public ArrayList<Setting> settings = new ArrayList<Setting>();
					
					@Override
					public ArrayList<Setting> getSettings() {
						return this.settings;
					}

					@Override
					public void incrHiddens(int i) {
						//XXX do nothing?
					}

					@Override
					public VerticalContainer getSettingContainer() {
						//TODO aaaaaa
						return null;
					}
				};
				final int uwu = i;
				this.color[i] = new SettingColor(null, "Color", 255, 0, 0);
				this.usesDefault[i] = new SettingBoolean(null, "Use Default Color", true) {
					@Override
					public void setValue(boolean b) {
						super.setValue(b);
						ESPBlockChooser.instanz.color[uwu].hidden = this.value;
					}
				};
				this.blockSettings[i].getSettings().add(new Setting(null, "") {
					@Override
					public String valueToString() {
						return null;
					}

					@Override
					public void reset() {

					}
					
					@Override
					public void renderElement(Element tab, int xStart, int yStart, int xEnd, int yEnd) {
						int yn = yStart + (this.getSettingHeight(tab) - 16)/2;
						GL11.glPushMatrix();
						GL11.glDisable(GL11.GL_LIGHTING);
						GL11.glEnable(GL11.GL_TEXTURE_2D);
						GuiIngame.itemRenderer.renderItemIntoGUI(Client.mc.fontRenderer, Client.mc.renderEngine, new ItemStack(uwu, 1, 0), xStart, yn);
						GL11.glDisable(GL11.GL_DEPTH_TEST);
						GL11.glDisable(GL11.GL_TEXTURE_2D);
						GL11.glDisable(GL11.GL_LIGHTING);
						GL11.glPopMatrix();	
					}
					
					@Override
					public void renderText(Element tab, int x, int y, int xEnd, int yEnd) {
						int yy = this.getSettingHeight(tab) / 2 - 8 / 2; //XXX text height
						String s = "ID: "+uwu;
						Client.mc.fontRenderer.drawString(s, x + 16 + 4, y + yy, 0xffffff);
					}
					
					@Override
					public boolean validateValue(String value) {
						return false;
					}
					
					@Override
					public int getSettingHeight(Element tab) {
						return super.getSettingHeight(tab)*2;
					}
					
					@Override
					public void writeToNBT(NBTTagCompound output) {}

					@Override
					public void readFromNBT(NBTTagCompound input) {}
					
				});
				this.blockSettings[i].getSettings().add(this.usesDefault[i]);
				this.blockSettings[i].getSettings().add(this.color[i]);
			}
		}
		
		@Override
		public void blockChanged(int id) {
			mc.entityRenderer.updateRenderer();
	        mc.theWorld.markBlocksDirty((int)mc.thePlayer.posX - 256, 0, (int)mc.thePlayer.posZ - 256, (int)mc.thePlayer.posX + 256, 127, (int)mc.thePlayer.posZ + 256);
		}
		@Override
		public void renderElement(Element tab, int xStart, int yStart, int xEnd, int yEnd) {
			super.renderElement(tab, xStart, yStart, xEnd, yEnd);
		}
		
		@Override
		public void onPressedInside(Element tab, int xMin, int yMin, int xMax, int yMax, int mouseX, int mouseY, int mouseClick) {
			if(mouseClick != 1) {
				super.onPressedInside(tab, xMin, yMin, xMax, yMax, mouseX, mouseY, mouseClick);
				return;
			}
			if(ClickGUIHack.theme() == Theme.HEPHAESTUS) {
				xMin += 5;
				xMax -= 5;
			}
			int ySpace = ClickGUIHack.theme().yspacing;
			if(mouseY >= yMin && mouseY < (yMin+ySpace) && mouseX > xMin && mouseX < xMax) {
				if(!this.minPressd) {
					this.minimized = !this.minimized;
					this.minPressd = true;
				}
				return;
			}
			xMin += 2;
			yMin += 2 + ySpace;
			
			int col = (mouseX-xMin) / 18;
			int row = (mouseY-yMin) / 18;
			int offX = (mouseX-xMin) % 18;
			
			int id = 1;
			int drawn = 0;
			int yOff = 0;
			if(xoff == -1 && yoff == -1 && offX < 14) {
				while(id < 256) {
					Block b = Block.blocksList[id];
					if(b != null) {
						if(yOff == row && col == drawn) {
							boolean m = false;
							lbl: {
								if(this.settingTab != null && !(m = ((SettingsTab)this.settingTab).settingProvider != this.blockSettings[id])) {
									ClickGUI.removeTab(this.settingTab);
									this.settingTab = null;
									break lbl;
								}
								if(m) {
									ClickGUI.removeTab(this.settingTab);
								}
								//int hid = yMin - tab.startY; //TODO vscrolloffset - tab.getVScrollOffset();
								System.out.println(tab);
								this.settingTab = new SettingsTabWithParentProvider(tab, this.blockSettings[id]);
								ClickGUI.addTab(0, this.settingTab);
							}
							
							xoff = drawn;
							yoff = row;
							break;
						}
						++drawn;
					}
					if(drawn >= getMaxColoumns(xMin, xMax)) {
						drawn = 0;
						++yOff;
					}
					++id;
				}
			}
		}
	}
	
	public static HashSet<BlockPos> blocksToRender = new HashSet<>();
	public static ArrayList<BlockPos> removed = new ArrayList<>();
	public static BlockESPHack instance;
	
	public SettingColor color = new SettingColor(this, "Color", 255, 0, 0);
	public SettingFloat width = new SettingFloat(this, "Line Width", 1f, 1f, 5, 0.1f);
	
	public ESPBlockChooser blocks = new ESPBlockChooser(this, "Blocks");
	
	public BlockESPHack() {
		super("BlockESP", "Outlines blocks", Keyboard.KEY_NONE, Category.RENDER);
		instance = this;
		this.addSetting(this.blocks);
		this.addSetting(this.color);
		this.addSetting(this.width);
		EventRegistry.registerListener(EventWorldRenderPreFog.class, this);
	}
	
	@Override
	public void handleEvent(Event event) {
		if(event instanceof EventWorldRenderPreFog) {
			Tessellator tess = Tessellator.instance;
			
			GL11.glPushMatrix();
			GL11.glBlendFunc(770, 771);
			
			GL11.glLineWidth(this.width.value);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glDepthMask(false);
			
			for(BlockPos pos : BlockESPHack.blocksToRender) {
				int id = mc.theWorld.getBlockId(pos.x, pos.y, pos.z);
				if (blocks.blocks[id]) {
					
					if(blocks.usesDefault[id].getValue()) {
						GL11.glColor3f(this.color.red / 255f, this.color.green / 255f, this.color.blue / 255f);
					}else {
						SettingColor col = this.blocks.color[id];
						GL11.glColor3f(col.red / 255f, col.green / 255f, col.blue / 255f);
					}
					
					double renderX = pos.x - RenderManager.renderPosX;
					double renderY = pos.y - RenderManager.renderPosY;
					double renderZ = pos.z - RenderManager.renderPosZ;
					
			        tess.startDrawing(3);
			        tess.addVertex(renderX, renderY, renderZ);
			        tess.addVertex(renderX+1, renderY, renderZ);
			        tess.addVertex(renderX+1, renderY, renderZ+1);
			        tess.addVertex(renderX, renderY, renderZ+1);
			        tess.addVertex(renderX, renderY, renderZ);
			        
			        tess.addVertex(renderX, renderY+1, renderZ);
			        tess.addVertex(renderX+1, renderY+1, renderZ);
			        tess.addVertex(renderX+1, renderY+1, renderZ+1);
			        tess.addVertex(renderX, renderY+1, renderZ+1);
			        tess.addVertex(renderX, renderY+1, renderZ);
			        tess.draw();
			        
			        tess.startDrawing(1);
			        tess.addVertex(renderX, renderY, renderZ);
			        tess.addVertex(renderX, renderY+1, renderZ);
			        tess.addVertex(renderX+1, renderY, renderZ);
			        tess.addVertex(renderX+1, renderY+1, renderZ);
			        tess.addVertex(renderX+1, renderY, renderZ+1);
			        tess.addVertex(renderX+1, renderY+1, renderZ+1);
			        tess.addVertex(renderX, renderY, renderZ+1);
			        tess.addVertex(renderX, renderY+1, renderZ+1);
			        tess.draw();
				} else {
					removed.add(pos);
				}
			}
			
			int i = removed.size();
			while (--i >= 0) {
				BlockPos pos = removed.remove(i);
				blocksToRender.remove(pos);
			}
			
			GL11.glDepthMask(true);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			GL11.glPopMatrix();
		}
	}
}
