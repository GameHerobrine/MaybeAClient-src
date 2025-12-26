package net.minecraft.src;

import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import net.skidcode.gh.maybeaclient.Client;
import net.skidcode.gh.maybeaclient.gui.GuiCharSelector;
import net.skidcode.gh.maybeaclient.utils.ChatColor;
import net.skidcode.gh.maybeaclient.utils.GUIUtils;

public class GuiChat extends GuiScreen {
    protected String message = "";
    private int updateCounter = 0;
    private static final String field_20082_i;
    public static ArrayList<String> history = new ArrayList<>();
    public int lastHistoryIndex = 0;
    public GuiChat() {
    	super();
    	this.lastHistoryIndex = history.size();
    }
    public void initGui() {
    	this.controlList.clear();
        Keyboard.enableRepeatEvents(true);
        this.controlList.add(new GuiCharSelector(10, 0, 0));
    }

    protected void actionPerformed(GuiButton var1) {
    	if(var1 instanceof GuiCharSelector) {
    		GuiCharSelector sel = (GuiCharSelector) var1;
    		this.append(sel.selectedChar);
    	}
    }
    
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    public void updateScreen() {
        ++this.updateCounter;
    }
    public int cursorPosition = 0;
    public int selectStart = -1;
    public int selectEnd = -1;
    public void removeSelected() {
    	this.message = this.message.substring(0, this.selectStart)+this.message.substring(this.selectEnd, this.message.length());
		this.cursorPosition = this.selectStart;
		this.selectStart = this.selectEnd = -1;
    }
    protected void keyTyped(char var1, int code) {
    	if(Client.BETTER_CHAT_CONTROLS) {
    		if(code == Keyboard.KEY_LEFT) {
    			if(this.selectStart != -1 && this.selectEnd != -1) {
    				this.cursorPosition = 0;
    				this.selectStart = this.selectEnd = -1;
    			}else if(this.cursorPosition > 0) --this.cursorPosition;
    		}
    		if(code == Keyboard.KEY_RIGHT) {
    			if(this.selectStart != -1 && this.selectEnd != -1) {
    				this.cursorPosition = this.message.length();
    				this.selectStart = this.selectEnd = -1;
    			}else if(this.cursorPosition < this.message.length()) ++this.cursorPosition;
    		}
    		if(code == Keyboard.KEY_HOME) {
    			this.cursorPosition = 0;
    			this.selectEnd = this.selectStart = -1;
    		}
    		if(code == Keyboard.KEY_END) {
    			this.cursorPosition = this.message.length();
    			this.selectEnd = this.selectStart = -1;
    		}
    	}
    	if(code == Keyboard.KEY_UP) {
    		if(lastHistoryIndex > 0 && lastHistoryIndex <= history.size()) {
    			this.message = history.get(--lastHistoryIndex);
    			this.cursorPosition = this.message.length();
    			this.selectEnd = this.selectStart = -1;
    		}
    	} else if(code == Keyboard.KEY_DOWN) {
    		if((lastHistoryIndex+1) >= history.size()) {
    			this.message = "";
    			this.cursorPosition = 0;
    			if(++lastHistoryIndex > history.size()) --lastHistoryIndex;
    			this.selectEnd = this.selectStart = -1;
    		}else if(lastHistoryIndex >= 0 && lastHistoryIndex < history.size()) {
    			this.message = history.get(++lastHistoryIndex);
    			this.cursorPosition = this.message.length();
    			this.selectEnd = this.selectStart = -1;
    		}
    	} else if (code == 1) {
            this.mc.displayGuiScreen((GuiScreen)null);
        } else if (code == 28) {
            String var3 = this.message.trim();
            if (var3.length() > 0) {
                String var4 = this.message.trim();
                history.add(var4);
                while(history.size() > 100) history.remove(0);
                if (!this.mc.lineIsCommand(var4)) {
                    this.mc.thePlayer.sendChatMessage(var4);
                }
            }
            if(mc.currentScreen instanceof GuiChat) {
            	if(!(mc.currentScreen instanceof GuiSleepMP)) {
            		this.mc.displayGuiScreen((GuiScreen)null);
            	}else {
            		this.message = "";
            		this.cursorPosition = 0;
        			this.selectEnd = this.selectStart = -1;
            	}
            }
        } else {
        	if(Client.BETTER_CHAT_CONTROLS) {
        		if (code == Keyboard.KEY_BACK) {
        			if(this.selectStart != -1 && this.selectEnd != -1) {
        				this.removeSelected();
        			}else if(this.message.length() > 0 && this.cursorPosition > 0){
        				this.message = new StringBuilder(this.message).deleteCharAt(this.cursorPosition-1).toString();
            			--this.cursorPosition;
        			}
                }
        		
        		if(code == Keyboard.KEY_DELETE && this.message.length() > 0 && this.cursorPosition < this.message.length()) {
        			if(this.selectStart != -1 && this.selectEnd != -1) {
        				this.removeSelected();
        			}else {
        				this.message = new StringBuilder(this.message).deleteCharAt(this.cursorPosition).toString();
        			}
        		}
        		//if(code == Keyboard.)
        		if((Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL))){
        			
	        		if(code == Keyboard.KEY_V) {
	        			try {
	        				if(this.selectStart != -1 && this.selectEnd != -1) {
	        					this.removeSelected();
	            			}
							String stuff = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null).getTransferData(DataFlavor.stringFlavor);
							for(int i = 0; i < stuff.length() && this.message.length() < 100; ++i) {
								char toCopy = stuff.charAt(i);
								if (field_20082_i.indexOf(toCopy) >= 0) {
									this.message = new StringBuilder(this.message).insert(this.cursorPosition, toCopy).toString();
				                	++this.cursorPosition;
								}
							}
						} catch (HeadlessException | UnsupportedFlavorException | IOException e) {
							//e.printStackTrace();
						}
	        		}
	        		if(code == Keyboard.KEY_C && this.selectStart != -1 && this.selectEnd != -1) {
	        			try {
	        				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(this.message.substring(this.selectStart, this.selectEnd)), null);
	        			}catch(IllegalStateException e) {
	        				
	        			}
	        		}
	        		if(code == Keyboard.KEY_X && this.selectStart != -1 && this.selectEnd != -1) {
	        			try {
	        				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(this.message.substring(this.selectStart, this.selectEnd)), null);
	        				this.removeSelected();
	        			}catch(IllegalStateException e) {
	        				
	        			}
	        		}
	        		if(code == Keyboard.KEY_A && this.message.length() > 0) {
	        			this.selectEnd = this.message.length();
	        			this.selectStart = 0;
	        		}
        		}
                this.append(var1);
        	}else {
        		if (code == Keyboard.KEY_BACK && this.message.length() > 0) {
                    this.message = this.message.substring(0, this.message.length() - 1);
                }

                if (field_20082_i.indexOf(var1) >= 0 && this.message.length() < 100) {
                    this.message = this.message + var1;
                }
        	}
        }
    }
    public void append(char c) {
    	if (field_20082_i.indexOf(c) >= 0 && this.message.length() < 100) {
        	if(this.selectStart != -1 && this.selectEnd != -1) {
        		this.removeSelected();
        	}
        	this.message = new StringBuilder(this.message).insert(this.cursorPosition, c).toString();
        	++this.cursorPosition;
        }
    }
    public void drawScreen(int var1, int var2, float var3) {
        this.drawRect(2, this.height - 14, this.width - 2, this.height - 2, Integer.MIN_VALUE);
        if(Client.BETTER_CHAT_CONTROLS) {
        	StringBuilder sb = new StringBuilder(this.message);
        	String insert = this.selectEnd == -1 && this.selectStart == -1 && this.updateCounter / 6 % 2 == 0 ? "|" : "";
        	sb.insert(this.cursorPosition, insert);
        	
        	
        	if(this.selectEnd != -1 && this.selectStart != -1) {
        		int yStart = this.height - 12;
        		int yEnd = this.height - 4;
        		int xStart = 4 + this.fontRenderer.getStringWidth("> "+this.message.substring(0, this.selectStart));
        		int xEnd = xStart + this.fontRenderer.getStringWidth(this.message.substring(this.selectStart, this.selectEnd));
        		this.drawRect(xStart, yStart, xEnd, yEnd, 0xffffffff);
        		sb.insert(this.selectStart, ChatColor.CYAN);
        		sb.insert(this.selectEnd+2, ChatColor.EXP_RESET);
        	}
        	String rendMsg = sb.toString();
        	this.drawString(this.fontRenderer, "> " + rendMsg, 4, this.height - 12, 14737632);
        	
        }else {
        	this.drawString(this.fontRenderer, "> " + this.message + (this.updateCounter / 6 % 2 == 0 ? "_" : ""), 4, this.height - 12, 14737632);
        }
        super.drawScreen(var1, var2, var3);
    }
    public boolean mouseClicked = false;
    public void mouseMovedOrUp(int x, int y, int button) {
    	if(this.mouseClicked) {
    		if(button == 0) {
    			this.mouseClicked = false;
    		}else {
    			StringBuilder sb = new StringBuilder(this.message);
            	String insert = this.selectEnd == -1 && this.selectStart == -1 && this.updateCounter / 6 % 2 == 0 ? "|" : "";
            	sb.insert(this.cursorPosition, insert);
            	String rendMsg = sb.toString();
    			int xmin = 2;
    			int begWid = this.fontRenderer.getStringWidth("> ");
            	if(x <= begWid+xmin) {
                	this.selectStart = Math.min(0, this.cursorPosition);
                	this.selectEnd = Math.max(0, this.cursorPosition);
            		return;
            	}
            	char[] c = rendMsg.toCharArray();
            	int xx = begWid;
            	int index = 0;
            	for(int i = 0; i < c.length; ++i) {
            		int w = this.fontRenderer.getCharWidth(c[i]);
            		if(i == this.cursorPosition && (this.selectEnd == -1 && this.selectStart == -1 && this.updateCounter / 6 % 2 == 0)) {
            			xx += w;
            			continue;
            		}
            		if(x > xx+w) {
            			++index;
            		}else {
            			break;
            		}
            		xx += w;
            	}
            	this.selectStart = Math.min(index, this.cursorPosition);
            	this.selectEnd = Math.max(index, this.cursorPosition);
    		}
    	}
    }
    protected void mouseClicked(int x, int y, int button) {
        if (button == 0) {
            if(Client.BETTER_CHAT_CONTROLS) {
            	
            	if(GUIUtils.isInsideRect(x, y, 2, this.height - 14, this.width - 2, this.height - 2)) {
            		StringBuilder sb = new StringBuilder(this.message);
                	String insert = this.selectEnd == -1 && this.selectStart == -1 && this.updateCounter / 6 % 2 == 0 ? "|" : "";
                	sb.insert(this.cursorPosition, insert);
                	String rendMsg = sb.toString();
                	int xmin = 2;
                	int xmax = this.width - 2;
                	int begWid = this.fontRenderer.getStringWidth("> ");
                	if(x <= begWid+xmin) {
                		this.cursorPosition = 0;
                		this.mouseClicked = true;
                		this.selectEnd = this.selectStart = -1;
                		return;
                	}
                	char[] c = rendMsg.toCharArray();
                	int xx = begWid;
                	int index = 0;
                	for(int i = 0; i < c.length; ++i) {
                		int w = this.fontRenderer.getCharWidth(c[i]);
                		if(i == this.cursorPosition && (this.selectEnd == -1 && this.selectStart == -1 && this.updateCounter / 6 % 2 == 0)) {
                			xx += w;
                			continue;
                		}
                		if(x > xx+w) {
                			++index;
                		}else {
                			break;
                		}
                		xx += w;
                	}
                	this.mouseClicked = true;
                	this.cursorPosition = index;
            		this.selectEnd = this.selectStart = -1;
            	}
            }
        }
        super.mouseClicked(x, y, button);
    }

    static {
        field_20082_i = ChatAllowedCharacters.allowedCharacters;
    }
}
