package net.skidcode.gh.maybeaclient.gui.click;

import org.lwjgl.opengl.GL11;

import net.minecraft.src.ChatAllowedCharacters;
import net.skidcode.gh.maybeaclient.Client;
import net.skidcode.gh.maybeaclient.hacks.ClickGUIHack;
import net.skidcode.gh.maybeaclient.hacks.ClickGUIHack.Theme;

public class CharacterSelectorTab extends Tab{
	public static CharacterSelectorTab instance = new CharacterSelectorTab("Character Selector", 200, 100);	
	public static final int ALLOWEDLEN = ChatAllowedCharacters.allowedCharacters.length();
	public CharacterSelectorTab(String name, int width, int height) {
		super(name, width, height);
	}
	public static final int MAXATROW = 16;
	@Override
	public void preRender() {
		int ySpace = ClickGUIHack.theme().yspacing;
		int prevSpace = ClickGUIHack.theme().titlebasediff;
		int txtCenter = ClickGUIHack.theme().yaddtocenterText;
		final int SIZE = ySpace;
		
		this.width = SIZE * MAXATROW;
		int n = (int) Math.ceil((double)ALLOWEDLEN/MAXATROW) + 1;
		this.height = n * SIZE + txtCenter;
	}
	public int hoveringOver = -1;
	@Override
	public void mouseHovered(int x, int y, int click) {
		super.mouseHovered(x, y, click);
		
		int ySpace = ClickGUIHack.theme().yspacing;
		int prevSpace = ClickGUIHack.theme().titlebasediff;
		int txtCenter = ClickGUIHack.theme().yaddtocenterText;
		
		int baseOff = ySpace + prevSpace;
		final int SIZE = ySpace;
		int bodyX = this.xPos;
		int bodyY = this.yPos + baseOff;
		
		int xoff = x - bodyX;
		int yoff = y - bodyY;
		if(xoff >= 0 && yoff >= 0) {
			int xx = xoff / SIZE;
			int yy = yoff / SIZE;
			hoveringOver = xx + yy*MAXATROW;
		}else {
			hoveringOver = -1;
		}
		
	}
	
	
	public static String lastCharTyped = "";
	
	@Override
	public boolean onSelect(int click, int x, int y) {
		boolean ret = super.onSelect(click, x, y);
		if(ret) return ret;
		
		int ySpace = ClickGUIHack.theme().yspacing;
		int prevSpace = ClickGUIHack.theme().titlebasediff;
		int txtCenter = ClickGUIHack.theme().yaddtocenterText;
		
		int baseOff = ySpace + prevSpace;
		final int SIZE = ySpace;
		int bodyX = this.xPos;
		int bodyY = this.yPos + baseOff;
		
		int xoff = x - bodyX;
		int yoff = y - bodyY;
		if(xoff >= 0 && yoff >= 0) {
			int xx = xoff / SIZE;
			int yy = yoff / SIZE;
			int c = xx + yy*MAXATROW;
			if(c < ALLOWEDLEN) {
				lastCharTyped = ChatAllowedCharacters.allowedCharacters.charAt(c)+"";
				return true;
			}
		}
		
		return false;
	}
	public void stopHovering() {
		super.stopHovering();
		hoveringOver = -1;
	}
	
	@Override
	public void render() {		
		super.render();
		int ySpace = ClickGUIHack.theme().yspacing;
		int prevSpace = ClickGUIHack.theme().titlebasediff;
		int txtCenter = ClickGUIHack.theme().yaddtocenterText;
		Theme theme = ClickGUIHack.theme();
		int baseOff = ySpace + prevSpace;
		final int SIZE = ySpace;
		
		
		int bodyX = this.xPos;
		int bodyY = this.yPos + baseOff;
		
		this.renderFrame(this.xPos, bodyY, this.xPos + this.width, this.yPos + this.height);
		
		int x = 0;
		int y = 0;
		for(int i = 0; i < ALLOWEDLEN; ++i) {
			if(i != 0 && i%MAXATROW == 0) {
				y += SIZE;
				x = 0;
			}
			int color = 0xffffff;
			if(theme == Theme.NODUS) color = ClickGUIHack.instance.themeColor.rgb();
			if(this.hoveringOver == i) {
				
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glDisable(GL11.GL_TEXTURE_2D);
				GL11.glBlendFunc(770, 771);
				
				if(theme != Theme.NODUS) this.renderFrameBackGround(bodyX + x, bodyY + y, bodyX + x+SIZE, bodyY + y+SIZE, ClickGUIHack.r(), ClickGUIHack.g(), ClickGUIHack.b(), 1);
				if(theme == Theme.NODUS) color = ClickGUIHack.instance.secColor.rgb();
				
				GL11.glEnable(GL11.GL_TEXTURE_2D);
				GL11.glDisable(GL11.GL_BLEND);
				
			}
			
			String s = ""+ChatAllowedCharacters.allowedCharacters.charAt(i);
			int c = (SIZE - Client.mc.fontRenderer.getStringWidth(s))/2;
			int lx = x + c;
			this.drawString(s, bodyX + lx, bodyY + y + txtCenter, color);
			x += SIZE;
		}
		
		if(theme == Theme.CLIFF) {
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glBlendFunc(770, 771);
			this.renderFrameOutlines(this.xPos, bodyY, this.xPos + this.width, this.yPos + this.height);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_BLEND);
		}
		
	}
}
