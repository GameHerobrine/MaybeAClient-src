package net.skidcode.gh.maybeaclient.gui.mapart;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.imageio.ImageIO;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import lunatrius.schematica.BlockStat;
import lunatrius.schematica.GuiSchematicaStats;
import lunatrius.schematica.SchematicWorld;
import lunatrius.schematica.Settings;
import lunatrius.schematica.GuiSchematicaStats.GuiBlockRequired;
import net.minecraft.client.Minecraft;
import net.minecraft.src.Block;
import net.minecraft.src.CompressedStreamTools;
import net.minecraft.src.FontRenderer;
import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiIngame;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.GuiTextField;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.Tessellator;
import net.minecraft.src.TileEntity;
import net.skidcode.gh.maybeaclient.Client;
import net.skidcode.gh.maybeaclient.gui.GuiSliderCustom;
import net.skidcode.gh.maybeaclient.utils.ChatColor;

public class GuiMapArtCreator extends GuiScreen{
	public static HashMap<Integer, Block> color2block = new HashMap<>();
	
	public static enum NotConnectedLayers{
		OFF("No"),
		RESETTO0("Reset to 0"),
		SWAPMAXMIN("Swap min/max"),
		OFFSETBY1("Offset by 1");
		
		final String name;
		NotConnectedLayers(String s){
			this.name = s;
		}
	}
	
	public static enum ColorType{
		FLAT,
		UP,
		DOWN
	}
	
	public GuiScreen prev;
	public GuiMapArtCreator(GuiScreen prev) {
		super();
		this.prev = prev;
	}
	
	public String savedMessage = "";
	public ArrayList<GuiButton> blockSelector = new ArrayList<>();
	public GuiButton ditherButton, save, palette, selectImage, exit, scaffoldBlock, stats;
	public GuiButton ladderingButton;
	public GuiButton similarityButton;
	public GuiButton allowNotConnectedLayers;
	public GuiTextField tfFilename;
	public GuiSliderCustom layersSlider;
	
	public String filename = "";
	
	@Override
	protected void mouseClicked(int par1, int par2, int par3) {
		this.tfFilename.mouseClicked(par1, par2, par3);
		super.mouseClicked(par1, par2, par3);
	}

	@Override
	protected void keyTyped(char par1, int par2) {
		this.tfFilename.textboxKeyTyped(par1, par2);
		this.filename = this.tfFilename.getText();
		
		if (par2 == Keyboard.KEY_ESCAPE) {
	    	this.mc.displayGuiScreen(this.prev);
	    }
	}

	@Override
	public void updateScreen() {
		this.tfFilename.updateCursorCounter();
		super.updateScreen();
	}
	
	public static int scaffoldBlockId = Block.cobblestone.blockID;
	
	public void initGui() {
		int midX = this.width / 2;
		int midY = this.height / 2;
		

		this.controlList.add(save = new GuiButton(2, this.width - 80 - 2, this.height - 20 - 1, 80, 20, "Save"));
		this.controlList.add(selectImage = new GuiButton(8, this.width - 80 - 2, this.height - 40 - 1, 80, 20, "Select Image"));
		this.controlList.add(exit = new GuiButton(9, this.width - 80 - 2, this.height - 60 - 1, 80, 20, "Exit"));
		this.controlList.add(scaffoldBlock = new GuiButton(10, this.width - 80 - 4 - 105 +1, this.height - 40 - 1, 105, 20, "Scaffold Block:") {
			@Override
		    public void drawButton(Minecraft var1, int var2, int var3) {
		        if (this.enabled2) {
		            FontRenderer var4 = var1.fontRenderer;
		            GL11.glBindTexture(3553 /*GL_TEXTURE_2D*/, var1.renderEngine.getTexture("/gui/gui.png"));
		            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		            boolean var5 = var2 >= this.xPosition && var3 >= this.yPosition && var2 < this.xPosition + this.width && var3 < this.yPosition + this.height;
		            int var6 = this.getHoverState(var5);
		            this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 46 + var6 * 20, this.width / 2, this.height);
		            this.drawTexturedModalRect(this.xPosition + this.width / 2, this.yPosition, 200 - this.width / 2, 46 + var6 * 20, this.width / 2, this.height);
		            this.mouseDragged(var1, var2, var3);
		            if (!this.enabled) {
		                this.drawString(var4, this.displayString, this.xPosition+4, this.yPosition + (this.height - 8) / 2, -6250336);
		            } else if (var5) {
		                this.drawString(var4, this.displayString, this.xPosition+4, this.yPosition + (this.height - 8) / 2, 16777120);
		            } else {
		                this.drawString(var4, this.displayString, this.xPosition+4, this.yPosition + (this.height - 8) / 2, 14737632);
		            }
		            
		            GuiIngame.itemRenderer.drawItemIntoGui(Client.mc.fontRenderer, Client.mc.renderEngine, scaffoldBlockId, 0, 1, this.xPosition+this.width-20, this.yPosition + 2);

		        }
		    }
		});
		this.controlList.add(stats = new GuiButton(11, this.width - 80 - 4 - 105 +1, this.height - 60 - 1, 105, 20, "Schematic Stats"));
		
		this.controlList.add(ditherButton = new GuiButton(1, 2, this.height - 120, 80, 20, ""));
		this.controlList.add(similarityButton = new GuiButton(4, 2, this.height - 40, 160, 20, ""));
		this.controlList.add(palette = new GuiButton(3, 2, this.height - 80, 80, 20, "Select blocks"));
		this.controlList.add(ladderingButton = new GuiButton(5, 2, this.height - 100, 80, 20, ""));
		this.controlList.add(allowNotConnectedLayers = new GuiButton(6, 2, this.height - 20, 200, 20, ""));
		this.controlList.add(layersSlider = new GuiSliderCustom(7, 2, this.height-60, "Max layers", maxYLayers/64f) {
			public String getText() {
				return String.format("%s: %d", this.base, maxYLayers*2);
			}
			public void onValueChanged() {
				maxYLayers = (int) (this.sliderValue*64);
				updateNames();
			}
		});
		
		this.tfFilename = new GuiTextField(this, this.fontRenderer, this.width - 80 - 4 - 100 - 2, this.height - 20, 100, 18, "");
		this.tfFilename.setMaxStringLength(20);
		this.tfFilename.setText(this.filename);
		
		this.updateNames();
	}
	
	public void save(int width, int length, int depth) {
		try {
			NBTTagCompound tagCompound = new NBTTagCompound();
			int[][][] blocks = new int[width][depth][length];
			int[][][] metadata = new int[width][depth][length];
			List<TileEntity> tileEntities = new ArrayList<TileEntity>();
			
			for (int x = 0; x < width; x++) {
				for (int z = 0; z < length; z++) {
					int color = imageData[z*width + (x)];
					ColorType colt = this.imageColorTypes[z*width + (x)];
					int layer = this.imageLayers[z*width+(x)];
					layer += maxYLayers;
					//System.out.println(layer);
					blocks[x][layer][z] = scaffoldBlockId;
					blocks[x][layer+1][z] = color2block.get(Client.color2default.get(color)).blockID; //color2block.get(color).blockID; //Client.color2block.get(color).get(0).blockID;
				}
			}
			SchematicWorld.putToNBT(tagCompound, (short)width, (short)length, (short)depth, blocks, metadata, tileEntities);
			String path = (new File(Settings.schematicDirectory, this.tfFilename.getText() + ".schematic")).getAbsolutePath();
			OutputStream stream = new FileOutputStream(path);
			CompressedStreamTools.writeGzippedCompoundToOutputStream(tagCompound, stream);
			savedMessage = ChatColor.LIGHTGREEN+"Saved successfully.";
		}catch(Exception e) {
			e.printStackTrace();
			savedMessage = ChatColor.LIGHTRED+"Saving failed. Check console for more info.";
		}
	}
	
	public void actionPerformed(GuiButton gb) {
		if(gb == this.stats) {
			mc.displayGuiScreen(new GuiSchematicaStats(this) {
				public void initGui() {
					Settings.instance().emptyStats();
					for(int color : imageData) {
						Settings.instance().incrementStat(color2block.get(Client.color2default.get(color)).blockID, 0, BlockStat.PLACE);
						Settings.instance().incrementStat(scaffoldBlockId, 0, BlockStat.PLACE);
					}
					Settings.instance().stats_initialized = true;
					
					this.slot = new GuiBlockRequired(this);
					this.controlList.add(new GuiButton(1, this.width / 2 - 200 / 2, this.height - 25, "Back"));
				}
			});
		}
		
		if(gb == this.exit) {
			mc.displayGuiScreen(this.prev);
		}
		if(gb == this.ditherButton) {
			dithering = !dithering;
			this.updateNames();
		}
		
		if(gb == this.scaffoldBlock) {
			mc.displayGuiScreen(new GuiSelectScaffoldBlock(this));
		}
		
		if(gb == this.similarityButton) {
			scaledSimilarity = !scaledSimilarity;
			this.updateNames();
		}
		if(gb == this.ladderingButton) {
			laddering = !laddering;
			this.updateNames();
		}
		if(gb == this.palette) {
			mc.displayGuiScreen(new GuiMapArtSelectBlocks(this));
		}
		if(gb == save) {
			this.save(128, 128, 2+maxYLayers*2);
		}
		if(gb == this.selectImage) {
			mc.displayGuiScreen(new GuiMapArtSelectImage(this));
		}
		
		if(gb == this.allowNotConnectedLayers) {
			
			int cur = notConnectedLayers.ordinal();
			if(++cur >= NotConnectedLayers.values().length) cur = 0;
			notConnectedLayers = NotConnectedLayers.values()[cur];
			this.updateNames();
		}
	}
	
	public void updateNames() {
		this.ditherButton.displayString = "Dithering: "+(dithering ? "ON" : "OFF");
		this.similarityButton.displayString = "Color Similarity: " + (scaledSimilarity ? "WeightedRGB" : "UnweightedRGB");
		this.ladderingButton.displayString = "Laddering: "+(laddering ? "ON" : "OFF");
		this.allowNotConnectedLayers.displayString = "Not Connected layers: "+(notConnectedLayers.name);
		this.allowNotConnectedLayers.enabled = laddering;
		this.layersSlider.enabled = laddering;
		this.generateImage();
		
		this.save.enabled = selectedImage != null && this.g_errors.size() == 0;
		this.stats.enabled = selectedImage != null && this.g_errors.size() == 0;
		savedMessage = "";
	}
	
	public static boolean dithering = true;
	public static boolean scaledSimilarity = true; 
	public static boolean laddering = false;
	public static NotConnectedLayers notConnectedLayers = NotConnectedLayers.OFF;
	public static int maxYLayers = 16;
	
	public double similarity(int color, int rgb) {
		int rd = (((color >> 16) & 0xff) - ((rgb >> 16) & 0xff));
		int gd = (((color >> 8) & 0xff) - ((rgb >> 8) & 0xff));
		int bd = ((color & 0xff) - (rgb & 0xff));
		if(scaledSimilarity) return 0.3*rd*rd + 0.59*gd*gd + 0.11*bd*bd;
		else return rd*rd + gd*gd + bd*bd;
	}
	
	public int defcol(int color) {
		int r = (color >> 16 & 255) * 220 / 255;
        int g = (color >> 8 & 255) * 220 / 255;
        int b = (color & 255) * 220 / 255;
        int c = (r << 16) | (g << 8) | b;
        return c;
	}
	
	public int upcol(int color) {
		return color;
	}
	
	public int downcol(int color) {
		int r = (color >> 16 & 255) * 180 / 255;
		int g = (color >> 8 & 255) * 180 / 255;
		int b = (color & 255) * 180 / 255;
		int c = (r << 16) | (g << 8) | b;
        return c;
	}
	
	public double closest_similarity;
	public int closest(int rgb) {
		closest_similarity = Double.POSITIVE_INFINITY;
		int col = 0;
		double ss;
		int lmod = 0;
		
		//for(int color : Client.possibleColors) {
		for(int color : GuiMapArtCreator.color2block.keySet()) {
			//default tone: no laddering
			int c;
			if(!this.canChangeColType) {
				if(this.colType == ColorType.FLAT) c = defcol(color);
				else if(this.colType == ColorType.UP) c = upcol(color);
				else c = downcol(color);
			}else {
				c = defcol(color);
			}
			
			ss = similarity(c, rgb);
			if(ss < closest_similarity) {
				closest_similarity = ss;
				col = c;
				lmod = 0;
			}
			if(!this.canChangeColType) continue;
			if(laddering) {
				//up
				if(this.curYLayer < maxYLayers) {
					c = upcol(color);
					ss = similarity(c, rgb);
					if(ss < closest_similarity) {
						closest_similarity = ss;
						col = c;
						lmod = 1;
					}
				}

				
				//down
				if(this.curYLayer > -maxYLayers) {
					c = downcol(color);
					ss = similarity(c, rgb);
					if(ss < closest_similarity) {
						closest_similarity = ss;
						col = c;
						lmod = -1;
						
					}
				}
			}
		}
		this.curYLayer += lmod;
		return col;
	}
	
	public int g_image = -1;
	public ArrayList<String> g_errors;
	public void generateImage() {
		if(g_image != -1) {
			mc.renderEngine.deleteTexture(g_image);
			g_image = -1;
		}
		
		
		g_errors = new ArrayList<>();
		if(GuiMapArtCreator.color2block.size() <= 0) g_errors.add("No blocks selected!");
		if(selectedImage == null) g_errors.add("No image selected!");
		if(g_errors.size() > 0) return;
		
		BufferedImage buf = new BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB);
		int wid = 128;
		int hei = 128;
		int[] data = new int[wid*hei];
		imageData = new int[wid*hei];
		imageColorTypes = new ColorType[wid*hei];
		imageLayers = new int[wid*hei];
		selectedImage.getRGB(0, 0, wid, hei, data, 0, wid);
		ai = 0;
		for(int x = 0; x < wid; ++x) {
			
			
			int[] prefferedTable = new int[hei];
			for(int y = 0; y < hei; ++y) {
				canChangeColType = true;
				curYLayer = 0;
				int rgb = data[y*wid + x];
				prefferedTable[y] = closest(rgb);
			}
			
			colType = ColorType.UP; //first time it is forced to go up
			canChangeColType = false;
			curYLayer = 0;
			for(int y = 0; y < hei; ++y) { //z
				++ai;
				int rgb = data[y*wid + x];
				int closest = closest(rgb);
				if(notConnectedLayers != NotConnectedLayers.OFF && y > 0 && y < hei && prefferedTable[y] != closest) {
					
					int savCurLay = this.curYLayer;
					//notConnectedLayers
					if(this.curYLayer >= maxYLayers || this.curYLayer <= -maxYLayers) {
						if(notConnectedLayers == NotConnectedLayers.RESETTO0) {
							if(this.curYLayer >= maxYLayers) {
								this.colType = ColorType.DOWN;
							}
							else if(this.curYLayer <= -maxYLayers) {
								this.colType = ColorType.UP;
							}
							this.curYLayer = 0;
						} else if(notConnectedLayers == NotConnectedLayers.OFFSETBY1) {
							if(this.curYLayer >= maxYLayers) {
								--this.curYLayer;
								this.colType = ColorType.DOWN;
							}
							else if(this.curYLayer <= -maxYLayers) {
								++this.curYLayer;
								this.colType = ColorType.UP;
							}
						}else if(notConnectedLayers == NotConnectedLayers.SWAPMAXMIN) {
							if(this.curYLayer >= maxYLayers) {
								this.curYLayer = -maxYLayers;
								this.colType = ColorType.DOWN;
							}
							else if(this.curYLayer <= -maxYLayers) {
								this.curYLayer = maxYLayers;
								this.colType = ColorType.UP;
							}
						}
						this.canChangeColType = false;
						int savl = this.curYLayer;
						int closest2 = closest(rgb);
						this.colType = ColorType.FLAT;
						this.canChangeColType = true;
						int closest3 = closest(data[y*wid + x]);
						
						if(closest3 != prefferedTable[y]) {
							this.curYLayer = savCurLay;
						}else {
							closest = closest2;
							this.curYLayer = savl;
						}
					}
					//closest = prefferedTable[y];
				}
				int col = closest; //prefferedTable[y]; //closest(rgb);
				//if(prefferedTable[y] != col) {
				//	
				//}
				imageData[y*wid + (x)] = col;
				imageColorTypes[y*wid + (x)] = colType;
				imageLayers[y*wid + (x)] = curYLayer;
				if(minLayer > curYLayer) minLayer = curYLayer;
				if(maxLayer < curYLayer) maxLayer = curYLayer;
				
				buf.setRGB(x, y, col | 0xff000000);
				canChangeColType = true;
				this.colType = ColorType.FLAT;
				
				if(dithering) {
					int qer = (((rgb >> 16) & 0xff) - ((col >> 16) & 0xff));
					int qeg = (((rgb >> 8) & 0xff) - ((col >> 8) & 0xff));
					int qeb = ((rgb & 0xff) - (col & 0xff));
					
					if(x < wid-1) {
						double tbl = 7.0/16.0;
						int rgb2 = data[y*wid + (x+1)];
						int r = (rgb2 >> 16) & 0xff;
						int g = (rgb2 >> 8) & 0xff;
						int b = (rgb2) & 0xff;
						
						r = clmp((r + (int)(qer*tbl)));
						g = clmp((g + (int)(qeg*tbl)));
						b = clmp((b + (int)(qeb*tbl)));
						
						data[y*wid + (x+1)] = (r << 16) | (g << 8) | b | 0xff000000;
					}
					
					if(x > 0 && y < hei-1) {
						double tbl = 3.0/16.0;
						int rgb2 = data[(y+1)*wid + (x-1)];
						int r = (rgb2 >> 16) & 0xff;
						int g = (rgb2 >> 8) & 0xff;
						int b = (rgb2) & 0xff;
						
						r = clmp((r + (int)(qer*tbl)));
						g = clmp((g + (int)(qeg*tbl)));
						b = clmp((b + (int)(qeb*tbl)));
						data[(y+1)*wid + (x-1)] = (r << 16) | (g << 8) | b | 0xff000000;
					}
					
					if(y < hei-1) {
						double tbl = 5.0/16.0;
						int rgb2 = data[(y+1)*wid + (x)];
						int r = (rgb2 >> 16) & 0xff;
						int g = (rgb2 >> 8) & 0xff;
						int b = (rgb2) & 0xff;
						
						r = clmp((r + (int)(qer*tbl)));
						g = clmp((g + (int)(qeg*tbl)));
						b = clmp((b + (int)(qeb*tbl)));
						
						data[(y+1)*wid + (x)] = (r << 16) | (g << 8) | b | 0xff000000;
					}
					
					if(x < wid-1 && y < hei-1) {
						double tbl = 1.0/16.0;
						int rgb2 = data[(y+1)*wid + (x+1)];
						int r = (rgb2 >> 16) & 0xff;
						int g = (rgb2 >> 8) & 0xff;
						int b = (rgb2) & 0xff;
						
						r = clmp((r + (int)(qer*tbl)));
						g = clmp((g + (int)(qeg*tbl)));
						b = clmp((b + (int)(qeb*tbl)));
						
						data[(y+1)*wid + (x+1)] = (r << 16) | (g << 8) | b | 0xff000000;
					}
				}
			}
		}
		
		g_image = mc.renderEngine.allocateAndSetupTexture(buf);
	}
	
	public int clmp(int col) {
		if(col < 0) return 0;
		if(col > 255) return 255;
		return col;
	}
	
	int ai = 0;
	public int curYLayer = 0;
	public ColorType colType;
	public boolean canChangeColType = false;
	public int[] imageData;
	public int[] imageLayers;
	public int minLayer = Integer.MAX_VALUE, maxLayer = Integer.MIN_VALUE;
	public ColorType[] imageColorTypes;
	
	public static BufferedImage selectedImage;
	public static File selectedImageF;
	
	public void drawScreen(int var1, int var2, float var3) {
		this.drawDefaultBackground();
		int midX = this.width / 2;
		int midY = this.height / 2;
		
		String s = "MapArt Creator";
		this.fontRenderer.drawString(s, midX - this.fontRenderer.getStringWidth(s) / 2, 12, 0xffffff);
		s = savedMessage;
		this.fontRenderer.drawString(s, midX - this.fontRenderer.getStringWidth(s) / 2, 24, 0xffffff);
		GL11.glColor4f(1, 1, 1, 1);
		if(g_errors.size() > 0) {
			int yy = 24;
			for(String ss : g_errors) {
				this.fontRenderer.drawString(ChatColor.LIGHTRED+ss, midX - this.fontRenderer.getStringWidth(ss) / 2, yy, 0xffffff);
				yy += 12;
			}
		}else {
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, g_image);
			Tessellator.instance.startDrawingQuads();
			Tessellator.instance.addVertexWithUV(midX-64, midY-64, 0, 0, 0);
			Tessellator.instance.addVertexWithUV(midX-64, midY+64, 0, 0, 1);
			Tessellator.instance.addVertexWithUV(midX+64, midY+64, 0, 1, 1);
			Tessellator.instance.addVertexWithUV(midX+64, midY-64, 0, 1, 0);
			Tessellator.instance.draw();
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.mc.renderEngine.getTexture("/gui/background.png"));
		}
		
		this.tfFilename.drawTextBox();
		super.drawScreen(var1, var2, var3);
	}
}
