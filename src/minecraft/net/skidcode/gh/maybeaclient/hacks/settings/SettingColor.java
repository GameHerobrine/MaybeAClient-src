package net.skidcode.gh.maybeaclient.hacks.settings;

import java.nio.ByteBuffer;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.minecraft.src.NBTBase;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.ScaledResolution;
import net.minecraft.src.Tessellator;
import net.skidcode.gh.maybeaclient.Client;
import net.skidcode.gh.maybeaclient.gui.click.Tab;
import net.skidcode.gh.maybeaclient.gui.click.element.Element;
import net.skidcode.gh.maybeaclient.hacks.ClickGUIHack;
import net.skidcode.gh.maybeaclient.hacks.Hack;
import net.skidcode.gh.maybeaclient.hacks.ClickGUIHack.Theme;
import net.skidcode.gh.maybeaclient.utils.GUIUtils;
import net.skidcode.gh.maybeaclient.utils.RenderUtils;

public class SettingColor extends Setting{
	
	public static final int sliders = 1;
	public static final int picker = 2;
	
	public int guiMode = 1;
	public int initialR, initialG, initialB;
	public int red, green, blue;
	
	public ByteBuffer colPick = ByteBuffer.allocateDirect(16);
	
	public boolean minimized = true;
	
	public SettingColor(Hack hack, String name, int initialR, int initialG, int initialB) {
		super(hack, name);
		this.setValue(initialR, initialG, initialB);
		this.initialG = initialG;
		this.initialR = initialR;
		this.initialB = initialB;
	}
	
	public void setValue(int r, int g, int b) {
		this.red = r;
		this.green = g;
		this.blue = b;
	}
	public int rgb() {
		return 0xff000000 | ((red & 0xff) << 16) | ((green & 0xff) << 8) | (blue & 0xff);
	}
	@Override
	public String valueToString() {
		return ""+this.red+";"+this.green+";"+this.blue;
	}

	public boolean validateValue(String value) {
		String[] splitted = value.split(";");
		if(splitted.length != 3) return false;
		
		for(String s : splitted) {
			try{
				Integer.parseInt(s);
			}catch(NumberFormatException e) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void reset() {
		this.setValue(initialR, initialG, initialB);
	}

	@Override
	public void writeToNBT(NBTTagCompound output) {
		
		NBTTagCompound tg = new NBTTagCompound();
		tg.setByteArray("Color", new byte[] {(byte)(this.red & 0xff), (byte)(this.green & 0xff), (byte)(this.blue & 0xff)});
		tg.setInteger("GUIMode", this.guiMode);
		tg.setBoolean("Minimized", this.minimized);
		output.setCompoundTag(this.name, tg);
	}
	
	public String getModeName() {
		if(this.guiMode == 1) return "Slider";
		return "Picker";
	}
	
	@Override
	public void readFromNBT(NBTTagCompound input) {
		if(input.hasKey(this.name)) {
			
			if(Client.convertingVersion == 1) {
				System.out.println("SettingColor: converting.(Provider: "+this.hack+")");
				byte[] bytes = input.getByteArray(this.name);
				this.setValue(bytes[0] & 0xff, bytes[1] & 0xff, bytes[2] & 0xff);
			}else {
				NBTTagCompound tg = input.getCompoundTag(this.name);
				byte[] bytes = tg.getByteArray("Color");
				this.setValue(bytes[0] & 0xff, bytes[1] & 0xff, bytes[2] & 0xff);
				
				int guiMode = tg.getInteger("GUIMode");
				this.guiMode = guiMode;
				
				if(tg.hasKey("Minimized")) {
					boolean minimized = tg.getBoolean("Minimized");
					this.minimized = minimized;
				}
				
			}
		}
	}
	
	@Override
	public void renderText(Element tab, int x, int y, int xEnd, int yEnd) {
		int ySpace = ClickGUIHack.theme().yspacing;
		int txtCenter = ClickGUIHack.theme().yaddtocenterText;
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
		

		if(ClickGUIHack.theme() == Theme.IRIDIUM) {
			txtColor = this.minimized ? Theme.IRIDIUM_DISABLED_COLOR : Theme.IRIDIUM_ENABLED_COLOR;
			Client.mc.fontRenderer.drawStringWithShadow(this.name, x + 2, y + txtCenter, txtColor);
		}else if(ClickGUIHack.theme() == Theme.HEPHAESTUS) {
			Client.mc.fontRenderer.drawStringWithShadow(this.name, x + Theme.HEPH_OPT_XADD, y + txtCenter, txtColor);
			String e = this.minimized ? "+" : "-";
			Client.mc.fontRenderer.drawStringWithShadow(e, xEnd - Theme.HEPH_OPT_XADD - Client.mc.fontRenderer.getStringWidth(e) + 1, y + txtCenter, txtColor);
		}else {
			Client.mc.fontRenderer.drawString(this.name, x + 2, y + txtCenter, txtColor);
		}
		
		if(this.minimized) return;
		
		txtColor = 0xffffff;
		if(ClickGUIHack.theme() == Theme.NODUS) {
			txtColor = ClickGUIHack.instance.themeColor.rgb();
			if(this.mouseHovering) {
				if(hmouseY >= (y+ySpace) && hmouseY <= (y+ySpace*2) && hmouseX >= x && hmouseX <= xEnd) {
					txtColor = ClickGUIHack.instance.secColor.rgb();
					this.mouseHovering = false;
				}
			}
		}
		
		
		if(ClickGUIHack.theme() == Theme.HEPHAESTUS) {
			Client.mc.fontRenderer.drawStringWithShadow("Mode", x + Theme.HEPH_OPT_XADD + 2, y + txtCenter + ySpace*1, txtColor);
			Client.mc.fontRenderer.drawStringWithShadow(this.getModeName(), xEnd - Theme.HEPH_OPT_XADD + 1 - Client.mc.fontRenderer.getStringWidth(this.getModeName()), y + txtCenter + ySpace*1, txtColor);
		}else {
			if(ClickGUIHack.theme() == Theme.IRIDIUM) {
				txtColor = Theme.IRIDIUM_ENABLED_COLOR;
				Client.mc.fontRenderer.drawStringWithShadow("Mode - "+this.getModeName(), x + 2 + 4, y + txtCenter + ySpace*1, txtColor);
			}else {
				Client.mc.fontRenderer.drawString("Mode - "+this.getModeName(), x + 2 + 4, y + txtCenter + ySpace*1, txtColor);
			}
		}
		
		
		if(this.guiMode == SettingColor.sliders) {
			int txtColor2 = 0xffffff, txtColor3 = txtColor = 0xffffff;
			ae:
			if(ClickGUIHack.theme() == Theme.NODUS) {
				txtColor = txtColor2 = txtColor3 = ClickGUIHack.instance.themeColor.rgb();
				if(this.mouseHovering) {
					this.mouseHovering = false;
					if(hmouseY >= (y+ySpace*2) && hmouseY <= (y+ySpace*3) && hmouseX >= x && hmouseX <= xEnd) {
						txtColor = ClickGUIHack.instance.secColor.rgb();
						break ae;
					}
					if(hmouseY >= (y+ySpace*3) && hmouseY <= (y+ySpace*4) && hmouseX >= x && hmouseX <= xEnd) {
						txtColor2 = ClickGUIHack.instance.secColor.rgb();
						break ae;
					}
					if(hmouseY >= (y+ySpace*4) && hmouseY <= (y+ySpace*5) && hmouseX >= x && hmouseX <= xEnd) {
						txtColor3 = ClickGUIHack.instance.secColor.rgb();
						break ae;
					}
				}
			}
			if(ClickGUIHack.theme() == Theme.IRIDIUM) {
				Client.mc.fontRenderer.drawStringWithShadow("Red - "+this.red, x + 2 + 4, y + txtCenter + ySpace*2, Theme.IRIDIUM_ENABLED_COLOR);
				Client.mc.fontRenderer.drawStringWithShadow("Green - "+this.green, x + 2 + 4, y + txtCenter + ySpace*3, Theme.IRIDIUM_ENABLED_COLOR);
				Client.mc.fontRenderer.drawStringWithShadow("Blue - "+this.blue, x + 2 + 4, y + txtCenter + ySpace*4, Theme.IRIDIUM_ENABLED_COLOR);
			}else if(ClickGUIHack.theme() == Theme.HEPHAESTUS) {
				Client.mc.fontRenderer.drawStringWithShadow("Red", x + Theme.HEPH_OPT_XADD + 2, y + txtCenter + ySpace*2, txtColor);
				Client.mc.fontRenderer.drawStringWithShadow("Green", x + Theme.HEPH_OPT_XADD + 2, y + txtCenter + ySpace*3, txtColor2);
				Client.mc.fontRenderer.drawStringWithShadow("Blue", x + Theme.HEPH_OPT_XADD + 2, y + txtCenter + ySpace*4, txtColor3);
				Client.mc.fontRenderer.drawStringWithShadow(""+this.red, xEnd - Client.mc.fontRenderer.getStringWidth(""+this.red) - Theme.HEPH_OPT_XADD + 1, y + txtCenter + ySpace*2, txtColor);
				Client.mc.fontRenderer.drawStringWithShadow(""+this.green, xEnd - Client.mc.fontRenderer.getStringWidth(""+this.green) - Theme.HEPH_OPT_XADD + 1, y + txtCenter + ySpace*3, txtColor2);
				Client.mc.fontRenderer.drawStringWithShadow(""+this.blue, xEnd - Client.mc.fontRenderer.getStringWidth(""+this.blue) - Theme.HEPH_OPT_XADD + 1, y + txtCenter + ySpace*4, txtColor3);
			}else {
				Client.mc.fontRenderer.drawString("Red - "+this.red, x + 2 + 4, y + txtCenter + ySpace*2, txtColor);
				Client.mc.fontRenderer.drawString("Green - "+this.green, x + 2 + 4, y + txtCenter + ySpace*3, txtColor2);
				Client.mc.fontRenderer.drawString("Blue - "+this.blue, x + 2 + 4, y + txtCenter + ySpace*4, txtColor3);
			}
		}
	}
	public void onDeselect(Element tab, int xMin, int yMin, int xMax, int yMax, int mouseX, int mouseY, int mouseClick) {
		this.pressedOn = -1;
	}
	
	@Override
	public void onMouseMoved(int xMin, int yMin, int xMax, int yMax, int mouseX, int mouseY, int mouseClick) {
		if(ClickGUIHack.theme() == Theme.HEPHAESTUS) {
			xMin += 5 + 2;
			xMax -= 5;
		}
		if(ClickGUIHack.theme() == Theme.UWARE) {
			xMin += 2 + 2;
			xMax -= 2;
		}
		
		int ySpace = ClickGUIHack.theme().yspacing;
		int sizeX = xMax - xMin;
		int mouseOff = mouseX - xMin;
		float step = 255f/sizeX;
		int val = (int)((float)Math.round(mouseOff*step*100)/100);
		if(val < 0) val = 0;
		if(val > 255) val = 255;
		
		if(this.guiMode == SettingColor.picker && this.pressedOn > 1) {
			if(mouseX < xMin || mouseX > xMax) return;
			if(mouseY < yMin+ySpace+ySpace || mouseY > yMax) return;
			
			int mx = Mouse.getX();
			int my = Mouse.getY();
			GL11.glReadPixels(mx, my, 1, 1, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, this.colPick);
			this.red = this.colPick.get(0) & 0xff;
			this.green = this.colPick.get(1) & 0xff;
			this.blue = this.colPick.get(2) & 0xff;
			return;
		}
		
		switch(this.pressedOn) {
			case 2:
				this.red = val;
				break;
			case 3:
				this.green = val;
				break;
			case 4:
				this.blue = val;
				break;
		}
	}
	
	int pressedOn = -1;
	@Override
	public void onPressedInside(Element tab, int xMin, int yMin, int xMax, int yMax, int mouseX, int mouseY, int mouseClick) {
		if(ClickGUIHack.theme() == Theme.HEPHAESTUS) {
			xMin += 5 + 2;
			xMax -= 5;
		}
		if(ClickGUIHack.theme() == Theme.UWARE) {
			xMin += 2 + 2;
			xMax -= 2;
		}
		int ySpace = ClickGUIHack.theme().yspacing;
		int mos = (mouseY - yMin) / ySpace;
		if(mos > 0) {
			int oldPressedOn = this.pressedOn;
			this.pressedOn = mos;
			int sizeX = xMax - xMin;
			int mouseOff = mouseX - xMin;
			float step = 255f/sizeX;
			int val = (int)((float)Math.round(mouseOff*step*100)/100);
			
			if(val < 0) val = 0;
			if(val > 255) val = 255;
			if(this.guiMode == SettingColor.picker && mos > 1) {
				if(mouseX < xMin || mouseX > xMax) return;
				if(mouseY < yMin+ySpace || mouseY > yMax) return;
				int mx = Mouse.getX();
				int my = Mouse.getY();
				GL11.glReadPixels(mx, my, 1, 1, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, this.colPick);
				this.red = this.colPick.get(0) & 0xff;
				this.green = this.colPick.get(1) & 0xff;
				this.blue = this.colPick.get(2) & 0xff;
				this.colPick.clear();
				return;
			}
			switch(mos) {
				case 1:
					if(oldPressedOn == -1) {
						++this.guiMode;
						if(this.guiMode > SettingColor.picker) this.guiMode = 1;
					}
					break;
				case 2:
					this.red = val;
					break;
				case 3:
					this.green = val;
					break;
				case 4:
					this.blue = val;
					break;
			}
		}else if(mos == 0){
			if(this.pressedOn == -1) {
				this.minimized = !this.minimized;
				this.pressedOn = 0;
			}
		}
	}
	
	public void renderElement(Element tab, int xStart, int yStart, int xEnd, int yEnd) {
		Theme theme = ClickGUIHack.theme();
		if(theme == Theme.HEPHAESTUS) {
			xStart += 5 + 2;
			xEnd -= 5;
		}
		
		int ySpace = ClickGUIHack.theme().yspacing;
		int ySpace2 = ySpace - ClickGUIHack.theme().settingYreduce;
		float bgcolr = ClickGUIHack.r();
		float bgcolg = ClickGUIHack.g();
		float bgcolb = ClickGUIHack.b();
		float bgcola = ClickGUIHack.theme() == Theme.IRIDIUM ? 0xaa/255f : 1f;
		if(theme == Theme.NODUS) {
			bgcolr = bgcolg = bgcolb = 0;
			bgcola = 0x80/255f;
		}
		if(theme == Theme.UWARE) {
			bgcolr = bgcolg = bgcolb = 0x16/255f;
			bgcola = 0xaa/255f;
		}
		
		if(!this.minimized && theme != Theme.HEPHAESTUS && theme != Theme.IRIDIUM || (this.minimized && theme == Theme.UWARE)) Tab.renderFrameBackGround(xStart, yStart, xEnd, yStart + ySpace2, bgcolr, bgcolg, bgcolb, bgcola);
		
		int xmi = xEnd - ySpace2;
		int xma = xEnd;
		int ymi = yStart;
		int yma = yStart + ySpace2;
		if(theme == Theme.HEPHAESTUS) {
			Tab.renderFrameBackGround(xEnd - Theme.HEPH_OPT_XADD - Theme.HEPH_OPT_XADD - 4, yStart+3, xEnd - Theme.HEPH_OPT_XADD - 3, yStart + ySpace2 - 3, this.red / 255f, this.green / 255f, this.blue / 255f, 1);
			Tab.renderFrameOutlines((double)xEnd - Theme.HEPH_OPT_XADD - Theme.HEPH_OPT_XADD - 4, (double)yStart+3, (double)xEnd - Theme.HEPH_OPT_XADD - 3, (double)yStart + ySpace2 - 3);
		}else if(theme != Theme.IRIDIUM){
			Tab.renderFrameBackGround(xmi, ymi, xma, yma, this.red / 255f, this.green / 255f, this.blue / 255f, 1f);
		}
		
		if(this.minimized) return;
		
		yStart += ySpace;
		
		int diff1 = theme == Theme.UWARE ? (xEnd-2) - (xStart+2+2) : xEnd - xStart;
		float step = 255f/diff1;
		
		if(theme != Theme.HEPHAESTUS && theme != Theme.IRIDIUM) Tab.renderFrameBackGround(xStart, yStart, xEnd, yStart + ySpace2, bgcolr, bgcolg, bgcolb, bgcola);
		yStart += ySpace;

		rendersliders:
		if(this.guiMode == SettingColor.sliders) {
			int value = this.red;
			if(value > 255) value = 255;
			if(value < 0) value = 0;
			int diff3 = (int) ((float)value/step);
			if(ClickGUIHack.theme() == Theme.HEPHAESTUS) {
				int sliderYbegin = (yStart + ySpace2) - Theme.HEPH_SLIDER_HEIGHT;
				int sliderYend = yStart + ySpace2;
				Tab.renderFrameBackGround(xStart, sliderYbegin, xEnd, sliderYend, 100/255f, 100/255f, 100/255f, 1);
				Tab.renderFrameBackGround(xStart, sliderYbegin, xStart+diff3, sliderYend, bgcolr, bgcolg, bgcolb, bgcola);
			}else if(ClickGUIHack.theme() == Theme.UWARE){
				Tab.renderFrameBackGround(xStart, yStart, xEnd, yStart + ySpace2, bgcolr, bgcolg, bgcolb, bgcola);
				int sliderYbegin = (yStart + ySpace2) - Theme.UWARE_SLIDER_HEIGHT;
				int sliderYend = yStart + ySpace2;
				RenderUtils.glColor(Theme.UWARE_OPT_D_COLOR);
				Tab.renderRoundedFrameBackGround(xStart+2+2, sliderYbegin, xEnd-2, sliderYend, 1);
				RenderUtils.glColor(Theme.UWARE_OPT_E_COLOR);
				Tab.renderRoundedFrameBackGround(xStart+2+2, sliderYbegin, xStart+2+2+diff3, sliderYend, 1);
			}else {
				Tab.renderFrameBackGround(xStart, yStart, xStart + diff3, yStart + ySpace2, bgcolr, bgcolg, bgcolb, bgcola);
			}
			yStart += ySpace;
			
			value = this.green;
			if(value > 255) value = 255;
			if(value < 0) value = 0;
			diff3 = (int) ((float)value/step);
			if(ClickGUIHack.theme() == Theme.HEPHAESTUS) {
				int sliderYbegin = (yStart + ySpace2) - Theme.HEPH_SLIDER_HEIGHT;
				int sliderYend = yStart + ySpace2;
				Tab.renderFrameBackGround(xStart, sliderYbegin, xEnd, sliderYend, 100/255f, 100/255f, 100/255f, 1);
				Tab.renderFrameBackGround(xStart, sliderYbegin, xStart+diff3, sliderYend, bgcolr, bgcolg, bgcolb, bgcola);
			}else if(ClickGUIHack.theme() == Theme.UWARE){
				Tab.renderFrameBackGround(xStart, yStart, xEnd, yStart + ySpace2, bgcolr, bgcolg, bgcolb, bgcola);
				int sliderYbegin = (yStart + ySpace2) - Theme.UWARE_SLIDER_HEIGHT;
				int sliderYend = yStart + ySpace2;
				RenderUtils.glColor(Theme.UWARE_OPT_D_COLOR);
				Tab.renderRoundedFrameBackGround(xStart+2+2, sliderYbegin, xEnd-2, sliderYend, 1);
				RenderUtils.glColor(Theme.UWARE_OPT_E_COLOR);
				Tab.renderRoundedFrameBackGround(xStart+2+2, sliderYbegin, xStart+2+2+diff3, sliderYend, 1);
			}else {
				Tab.renderFrameBackGround(xStart, yStart, xStart + diff3, yStart + ySpace2, bgcolr, bgcolg, bgcolb, bgcola);
			}
			yStart += ySpace;
			
			value = this.blue;
			if(value > 255) value = 255;
			if(value < 0) value = 0;
			diff3 = (int) ((float)value/step);
			if(ClickGUIHack.theme() == Theme.HEPHAESTUS) {
				int sliderYbegin = (yStart + ySpace2) - Theme.HEPH_SLIDER_HEIGHT;
				int sliderYend = yStart + ySpace2;
				Tab.renderFrameBackGround(xStart, sliderYbegin, xEnd, sliderYend, 100/255f, 100/255f, 100/255f, 1);
				Tab.renderFrameBackGround(xStart, sliderYbegin, xStart+diff3, sliderYend, bgcolr, bgcolg, bgcolb, bgcola);
			}else if(ClickGUIHack.theme() == Theme.UWARE){
				Tab.renderFrameBackGround(xStart, yStart, xEnd, yStart + ySpace2, bgcolr, bgcolg, bgcolb, bgcola);
				int sliderYbegin = (yStart + ySpace2) - Theme.UWARE_SLIDER_HEIGHT;
				int sliderYend = yStart + ySpace2;
				RenderUtils.glColor(Theme.UWARE_OPT_D_COLOR);
				Tab.renderRoundedFrameBackGround(xStart+2+2, sliderYbegin, xEnd-2, sliderYend, 1);
				RenderUtils.glColor(Theme.UWARE_OPT_E_COLOR);
				Tab.renderRoundedFrameBackGround(xStart+2+2, sliderYbegin, xStart+2+2+diff3, sliderYend, 1);
			}else {
				Tab.renderFrameBackGround(xStart, yStart, xStart + diff3, yStart + ySpace2, bgcolr, bgcolg, bgcolb, bgcola);
			}
		}else {
        	Tessellator tes = Tessellator.instance;
        	int yHalfEnd = yStart + ((yEnd - yStart) / 2);
			GL11.glPushMatrix();
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			GL11.glColor4f(0, 0, 0, 1);
        	GL11.glBegin(GL11.GL_QUADS);
        	GL11.glVertex2d(xStart, yStart);
        	GL11.glVertex2d(xStart, yHalfEnd);
        	GL11.glVertex2d(xEnd, yHalfEnd);
        	GL11.glVertex2d(xEnd, yStart);
        	GL11.glEnd();
        	GL11.glColor4f(1, 1, 1, 1);
        	GL11.glBegin(GL11.GL_QUADS);
        	GL11.glVertex2d(xStart, yHalfEnd);
        	GL11.glVertex2d(xStart, yEnd);
        	GL11.glVertex2d(xEnd, yEnd);
        	GL11.glVertex2d(xEnd, yHalfEnd);
        	GL11.glEnd();
        	int prv = GL11.glGetInteger(GL11.GL_SHADE_MODEL);
			GL11.glShadeModel(GL11.GL_SMOOTH);
        	GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        	GL11.glEnable(GL11.GL_BLEND);
        	GL11.glBegin(GL11.GL_QUADS);
        	for(int i = 0; i < 6; ++i) {
        		float hue = i*60;
        		float saturation = 100;
        		float value = 100;
        		float huenxt = (i+1)*60;
        		
        		float factor = (xEnd - xStart) / 6f;
        		
        		float r = 0, g = 0, b = 0;
        		float rn = 0, gn = 0, bn = 0;
        		
        		float h = hue / 360;
        		float hn = huenxt / 360;
        		float s = saturation / 100;
        		float v = value / 100;
        		
        		int d = (int)(h * 6);
        		float f = h * 6 - d;
        		float p = v * (1 - s);
        		float q = v * (1 - f * s);
        		float t = v * (1 - (1 - f) * s);
        		
        		switch (d % 6) {
        			case 0: r = v; g = t; b = p; break;
        			case 1: r = q; g = v; b = p; break;
        			case 2: r = p; g = v; b = t; break;
        			case 3: r = p; g = q; b = v; break;
        			case 4: r = t; g = p; b = v; break;
        			case 5: r = v; g = p; b = q; break;
        		}
        		
        		d = (int)(hn * 6);
        		f = h * 6 - d;
        		p = v * (1 - s);
        		q = v * (1 - f * s);
        		t = v * (1 - (1 - f) * s);
        		
        		switch (d % 6) {
        			case 0: rn = v; gn = t; bn = p; break;
        			case 1: rn = q; gn = v; bn = p; break;
        			case 2: rn = p; gn = v; bn = t; break;
        			case 3: rn = p; gn = q; bn = v; break;
        			case 4: rn = t; gn = p; bn = v; break;
        			case 5: rn = v; gn = p; bn = q; break;
        		}
        		
        		GL11.glColor4f(r, g, b, 0);
        		GL11.glVertex2d(xStart + i*factor, yEnd);
        		GL11.glColor4f(rn, gn, bn, 0);
        		GL11.glVertex2d(xStart + (i+1)*factor, yEnd);
        		GL11.glColor4f(rn, gn, bn, 1);
        		GL11.glVertex2d(xStart + (i+1)*factor, yHalfEnd);
        		GL11.glColor4f(r, g, b, 1);
        		GL11.glVertex2d(xStart + i*factor, yHalfEnd);
        		
        		GL11.glColor4f(r, g, b, 1);
            	GL11.glVertex2d(xStart + i*factor, yHalfEnd);
            	GL11.glColor4f(rn, gn, bn, 1);
            	GL11.glVertex2d(xStart + (i+1)*factor, yHalfEnd);
            	GL11.glColor4f(rn, gn, bn, 0);
            	GL11.glVertex2d(xStart + (i+1)*factor, yStart);
            	GL11.glColor4f(r, g, b, 0);
            	GL11.glVertex2d(xStart + i*factor, yStart);
        	}

        	
        	GL11.glEnd();
        	GL11.glDisable(GL11.GL_BLEND);
        	GL11.glShadeModel(prv);
        	GL11.glPopAttrib();
        	GL11.glPopMatrix();
		}
	}
	
	@Override
	public int getSettingWidth() {
		int w1 = super.getSettingWidth();
		if(ClickGUIHack.theme() == Theme.HEPHAESTUS) w1 += Theme.HEPH_OPT_XADD;
		
		if(w1 < 90) w1 = 100;
		return w1;
	}
	public int getSettingHeight() {
		if(this.minimized) return ClickGUIHack.theme().yspacing;
		return 5*ClickGUIHack.theme().yspacing;
	}
}
