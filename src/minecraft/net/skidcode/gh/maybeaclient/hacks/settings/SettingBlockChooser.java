package net.skidcode.gh.maybeaclient.hacks.settings;

import org.lwjgl.opengl.GL11;

import net.minecraft.src.Block;
import net.minecraft.src.GuiIngame;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.skidcode.gh.maybeaclient.Client;
import net.skidcode.gh.maybeaclient.gui.click.Tab;
import net.skidcode.gh.maybeaclient.gui.click.element.Element;
import net.skidcode.gh.maybeaclient.hacks.ClickGUIHack;
import net.skidcode.gh.maybeaclient.hacks.Hack;
import net.skidcode.gh.maybeaclient.hacks.ClickGUIHack.Theme;

public class SettingBlockChooser extends Setting{
    public static boolean rendering = false;
	public boolean blocks[];
	public int ids[];
	//public int width, height;
	
	public boolean minimized = false;
	public int blockCount = 0;
	
	public int getMaxColoumns(int xStart, int xEnd) {
		if(ClickGUIHack.theme().verticalSettings) {
			int a = (xEnd - xStart)/18;
			if(a < 7) return 7;
			return a;
		}
		return 13;
	}
	
	public SettingBlockChooser(Hack hack, String name, int... ids) {
		super(hack, name);
		blocks = new boolean[Block.blocksList.length];
		for(int id : ids) blocks[id] = true;
		this.ids = ids;
		
		int id = 1;
		int drawn = 0;
		int hei = 0;
		int wid = 0;
		while(id < 256) if(Block.blocksList[id++] != null) ++blockCount;
	}
	
	
	
	public void blockChanged(int id) {
		
	}
	
	@Override
	public String valueToString() {
		String s = "";
		for(int i = 0; i < blocks.length; ++i) {
			if(blocks[i]) s += i+", ";
		}
		
		return s.substring(0, s.length()-2);
	}

	@Override
	public void reset() {
		for(int i = 0; i < blocks.length; ++i) blocks[i] = false;
		for(int id : this.ids) blocks[id] = true;
	}

	@Override
	public boolean validateValue(String value) {
		try{
			Integer.parseInt(value);
			return true;
		}catch(NumberFormatException e) {
		}
		return false;
	}
	
	@Override
	public void renderElement(Element tab, int xStart, int yStart, int xEnd, int yEnd) {
		if(this.minimized) return;
		
		int ySpace = ClickGUIHack.theme().yspacing;
		int yReduce = ClickGUIHack.theme().settingYreduce;
		
		if(ClickGUIHack.theme() == Theme.NODUS) {
			Tab.renderFrameBackGround(xStart, yStart, xEnd, yStart + ySpace, 0, 0, 0, 0x80/255f);
		}else if(ClickGUIHack.theme() != Theme.HEPHAESTUS) {
			Tab.renderFrameBackGround(xStart, yStart, xEnd, yStart + ySpace-yReduce, ClickGUIHack.r(), ClickGUIHack.g(), ClickGUIHack.b(), 1f);
		}
		if(ClickGUIHack.theme() == Theme.HEPHAESTUS) {
			xStart += 5;
			xEnd -= 5;
		}
		
		yStart += ySpace;
		GL11.glPushMatrix();
		int id = 1;
		int drawn = 0;
		int yOff = 0;
		while(id < 256) {
			Block b = Block.blocksList[id];
			if(b != null) {
				GL11.glDisable(2896 /*GL_LIGHTING*/);
				
				if(this.blocks[id]) {
					int xb = xStart + drawn*18;
					int yb = yStart + yOff;
					if(ClickGUIHack.theme() == Theme.NODUS) Tab.renderFrameBackGround(xb, yb, xb+16, yb+16, 0, 0, 0, 0x80/255f);
					else Tab.renderFrameBackGround(xb, yb, xb+16, yb+16, ClickGUIHack.r(), ClickGUIHack.g(), ClickGUIHack.b(), 1f);
					
				}
				++drawn;
			}
			if(drawn >= getMaxColoumns(xStart, xEnd)) {
				drawn = 0;
				yOff += 18;
			}
			++id;
		}
		GL11.glPopMatrix();
	}
	public boolean minPressd = false;
	public int xoff = -1;
	public int yoff = -1;
	@Override
	public void onDeselect(Element tab, int xMin, int yMin, int xMax, int yMax, int mouseX, int mouseY, int mouseClick) {
		xoff = yoff = -1;
		this.minPressd = false;
	}
	@Override
	public void onPressedInside(Element tab, int xMin, int yMin, int xMax, int yMax, int mouseX, int mouseY, int mouseClick) {
		int ySpace = ClickGUIHack.theme().yspacing;
		if(mouseY >= yMin && mouseY < (yMin+ySpace) && mouseX > xMin && mouseX < xMax) {
			if(!this.minPressd) {
				this.minimized = !this.minimized;
				this.minPressd = true;
			}
			return;
		}
		if(ClickGUIHack.theme() == Theme.HEPHAESTUS) {
			xMin += 5;
			xMax -= 5;
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
						this.blocks[id] = !this.blocks[id];
						this.blockChanged(id);
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
	@Override
	public void renderText(Element tab, int x, int y, int xEnd, int yEnd) {
		int ySpace = ClickGUIHack.theme().yspacing;
		int txtColor = 0xffffff;
		if(ClickGUIHack.theme() == Theme.NODUS) {
			txtColor = ClickGUIHack.instance.themeColor.rgb();
			if(this.mouseHovering) {
				if(hmouseY >= y && hmouseY <= (y+ySpace) && hmouseX >= x && hmouseX <= xEnd) {
					txtColor = ClickGUIHack.instance.secColor.rgb();
					this.mouseHovering = false;
				}
			}
		}
		if(ClickGUIHack.theme() == Theme.HEPHAESTUS) {
			Client.mc.fontRenderer.drawStringWithShadow(this.name, x + Theme.HEPH_OPT_XADD, y + ClickGUIHack.theme().yaddtocenterText, 0xffffff);
			String e = this.minimized ? "+" : "-";
			Client.mc.fontRenderer.drawStringWithShadow(e, xEnd - Client.mc.fontRenderer.getStringWidth(e) + 1 - Theme.HEPH_OPT_XADD, y + ClickGUIHack.theme().yaddtocenterText, 0xffffff);
		}else {
			Client.mc.fontRenderer.drawString(this.name, x + 2, y + ClickGUIHack.theme().yaddtocenterText, txtColor);
		}
		
		if(this.minimized) return;
		
		if(ClickGUIHack.theme() == Theme.HEPHAESTUS) {
			x += 5;
			xEnd -= 5;
		}

        rendering = true;
		y += ySpace;
		GL11.glPushMatrix();
		int id = 1;
		int drawn = 0;
		int yOff = 0;
		while(id < 256) {
			Block b = Block.blocksList[id];
			if(b != null) {
				GL11.glDisable(GL11.GL_LIGHTING);
				GL11.glDisable(GL11.GL_DEPTH_TEST);
				GuiIngame.itemRenderer.renderItemIntoGUI(Client.mc.fontRenderer, Client.mc.renderEngine, new ItemStack(b), x + drawn*18, y + yOff);
				++drawn;
			}
			if(drawn >= getMaxColoumns(x, xEnd)) {
				drawn = 0;
				yOff += 18;
			}
			++id;
		}
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glPopMatrix();
        rendering = false;
	}
	
	@Override
	public void writeToNBT(NBTTagCompound output) {
		byte[] bs = new byte[this.blocks.length];
		for(int i = 0; i < bs.length; ++i) bs[i] = (byte) (this.blocks[i] ? 1 : 0);
		NBTTagCompound tg = new NBTTagCompound();
		tg.setByteArray("Blocks", bs);
		tg.setBoolean("Minimized", this.minimized);
		output.setCompoundTag(this.name, tg);
	}

	@Override
	public void readFromNBT(NBTTagCompound input) {
		NBTTagCompound tg = input.getCompoundTag(this.name);
		byte[] bts = tg.getByteArray("Blocks");
		for(int i = 0; i < bts.length; ++i) {
			this.blocks[i] = bts[i] != 0;
		}
		
		this.minimized = tg.getBoolean("Minimized");
	}
	
	public int getSettingWidth() {
		return getMaxColoumns(0, 0) * 18 + (ClickGUIHack.theme() == Theme.HEPHAESTUS ? Theme.HEPH_OPT_XADD : 0);
	}
	
	@Override
	public int getSettingHeight(Element tab) {
		if(this.minimized) return ClickGUIHack.theme().yspacing;
		if(ClickGUIHack.theme().verticalSettings) {
			return ((int)Math.ceil((double)this.blockCount / getMaxColoumns(tab.startX, tab.endX)))*18 + ClickGUIHack.theme().yspacing;
		}
		return ((int)Math.ceil((double)this.blockCount / getMaxColoumns(0, 0)))*18 + ClickGUIHack.theme().yspacing;
	}
}
