package net.minecraft.src;

import java.util.Random;
import org.lwjgl.opengl.GL11;

import net.skidcode.gh.maybeaclient.Client;
import net.skidcode.gh.maybeaclient.hacks.EntityESPHack;
import net.skidcode.gh.maybeaclient.hacks.ItemNameTagsHack;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingColor;
import net.skidcode.gh.maybeaclient.utils.RenderUtils;

public class RenderItem extends Render {
    private RenderBlocks renderBlocks = new RenderBlocks();
    private Random random = new Random();
    public boolean field_27004_a = true;

    public RenderItem() {
        this.shadowSize = 0.15F;
        this.field_194_c = 0.75F;
    }

    public void doRenderItem(EntityItem entity, double var2, double var4, double var6, float var8, float var9) {
        this.random.setSeed(187L);
        
        if(ItemNameTagsHack.instance.status) {
			this.renderLivingLabel(entity, ItemNameTagsHack.getName(entity), var2, var4 - 1.3d, var6, 64);
		}
        
        ItemStack itemstack = entity.item;
        GL11.glPushMatrix();
        float var11 = MathHelper.sin(((float)entity.age + var9) / 10.0F + entity.field_804_d) * 0.1F + 0.1F;
        float var12 = (((float)entity.age + var9) / 20.0F + entity.field_804_d) * 57.295776F;
        byte var13 = 1;
        if (entity.item.stackSize > 1) {
            var13 = 2;
        }

        if (entity.item.stackSize > 5) {
            var13 = 3;
        }

        if (entity.item.stackSize > 20) {
            var13 = 4;
        }

        if(EntityESPHack.instance.shouldRender(entity) && EntityESPHack.instance.getRenderingMode(entity).equalsIgnoreCase("Box")) {
			//GL11.glTranslatef(-(float)var2, -(float)(var4 + var11), -(float)var6);
			int r, g, b;
			SettingColor color = EntityESPHack.instance.getRenderColor(entity);
			if(color == null) {
				System.out.println("Tried rendering esp for "+entity+" that has no color!");
				r = g = b = 0;
			}else {
				r = color.red;
				g = color.green;
				b = color.blue;
			}

			
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(770, 771);
			GL11.glLineWidth(1F);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glDepthMask(false);
			GL11.glEnable(GL11.GL_LINE_SMOOTH);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glColor3f((float)r / 255f, (float)g / 255f, (float)b / 255f);
			RenderUtils.drawOutlinedBB(
				AxisAlignedBB.getBoundingBox(var2 - entity.width / 2, var4, var6 - entity.width / 2, var2 + entity.width / 2, var4 + entity.height, var6 + entity.width / 2)
			);
			GL11.glDepthMask(true);
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glColor4f(1, 1, 1, 1);
			GL11.glEnable(GL11.GL_DEPTH_TEST);
		}
        
        GL11.glTranslatef((float)var2, (float)var4 + var11, (float)var6);
        GL11.glEnable(32826 /*GL_RESCALE_NORMAL_EXT*/);
        float var16;
        float var17;
        float var18;
        if (itemstack.itemID < 256 && RenderBlocks.renderItemIn3d(Block.blocksList[itemstack.itemID].getRenderType())) {
            GL11.glRotatef(var12, 0.0F, 1.0F, 0.0F);
            this.loadTexture("/terrain.png");
            float var27 = 0.25F;
            if (!Block.blocksList[itemstack.itemID].renderAsNormalBlock() && itemstack.itemID != Block.stairSingle.blockID) {
                var27 = 0.5F;
            }

            GL11.glScalef(var27, var27, var27);
            if(EntityESPHack.instance.shouldRender(entity) && EntityESPHack.instance.getRenderingMode(entity).equalsIgnoreCase("Fill")) {
            	int r, g, b;
				r = EntityESPHack.instance.itemColor.red;
				g = EntityESPHack.instance.itemColor.green;
				b = EntityESPHack.instance.itemColor.blue;
				GL11.glEnable(GL11.GL_STENCIL_TEST);
				for(int var28 = 0; var28 < var13; ++var28) {
					GL11.glPushMatrix();
					GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
					GL11.glClearStencil(0xF);
					GL11.glStencilFunc(GL11.GL_NEVER, 1, 0xF);
					GL11.glStencilOp(GL11.GL_REPLACE, GL11.GL_REPLACE, GL11.GL_REPLACE);
					
					if (var28 > 0) {
						var16 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.2F / var27;
						var17 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.2F / var27;
						var18 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.2F / var27;
						GL11.glTranslatef(var16, var17, var18);
					}
					this.renderBlocks.renderBlockOnInventory(Block.blocksList[itemstack.itemID], itemstack.getItemDamage());
					
					GL11.glStencilFunc(GL11.GL_EQUAL, 1, 0xF);
					GL11.glStencilOp(GL11.GL_REPLACE, GL11.GL_REPLACE, GL11.GL_KEEP);
					
					GL11.glPushMatrix();
					GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
					GL11.glDisable(GL11.GL_TEXTURE_2D);
					GL11.glDisable(GL11.GL_LIGHTING);
					GL11.glDisable(GL11.GL_DEPTH_TEST);
					GL11.glDisable(GL11.GL_ALPHA_TEST);
					GL11.glEnable(GL11.GL_BLEND);
					GL11.glDisable(GL11.GL_FOG);
					GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_CONSTANT_ALPHA);
					GL11.glColor4f((float)r/255, (float)g/255, (float)b/255, 1);
					
					this.renderBlocks.renderBlockOnInventory(Block.blocksList[itemstack.itemID], itemstack.getItemDamage());
						
					GL11.glPopAttrib();
					GL11.glPopMatrix();
					GL11.glPopMatrix();
				}
				GL11.glDisable(GL11.GL_STENCIL_TEST);
            }else if(EntityESPHack.instance.shouldRender(entity) && EntityESPHack.instance.getRenderingMode(entity).equalsIgnoreCase("Outline")) {
            	float r, g, b;
				r = (float)EntityESPHack.instance.itemColor.red / 255;
				g = (float)EntityESPHack.instance.itemColor.green / 255;
				b = (float)EntityESPHack.instance.itemColor.blue / 255;
				for(int var28 = 0; var28 < var13; ++var28) {
					GL11.glPushMatrix();
					GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
					GL11.glClearStencil(0xF);
					GL11.glStencilFunc(GL11.GL_NEVER, 1, 0xF);
					GL11.glStencilOp(GL11.GL_REPLACE, GL11.GL_REPLACE, GL11.GL_REPLACE);
					
					if (var28 > 0) {
						var16 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.2F / var27;
						var17 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.2F / var27;
						var18 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.2F / var27;
						GL11.glTranslatef(var16, var17, var18);
					}
					
					/*GL11.glDisable(GL11.GL_DEPTH_TEST);
					GL11.glDisable(GL11.GL_CULL_FACE);
					GL11.glColor4f(1, 0, 0, 1);
					System.out.println(Client.fb_itemOutlineESP);
		        	EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, Client.fb_itemOutlineESP);
		        	
		        	GL11.glBegin(GL11.GL_QUADS);
		        	GL11.glVertex3f(0, 0, 0);
		        	GL11.glVertex3f(0, 0, 1);
		        	GL11.glVertex3f(0, 1, 1);
		        	GL11.glVertex3f(0, 1, 0);
		        	GL11.glEnd();
		        	
		        	GL11.glEnable(GL11.GL_CULL_FACE);
		        	GL11.glEnable(GL11.GL_DEPTH_TEST);*/
					
					GL11.glLineWidth(2f);
					GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
					this.renderBlocks.renderBlockOnInventory(Block.blocksList[itemstack.itemID], itemstack.getItemDamage());
					
					
    		        GL11.glDisable(GL11.GL_ALPHA_TEST);
    		        GL11.glDisable(GL11.GL_TEXTURE_2D);
    		        GL11.glDisable(GL11.GL_LIGHTING);
    		        GL11.glEnable(GL11.GL_BLEND);
    		        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    		        GL11.glEnable(GL11.GL_LINE_SMOOTH);
    		        GL11.glEnable(GL11.GL_STENCIL_TEST);
    		        GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
    		        GL11.glClearStencil(0xF);
    		        GL11.glStencilFunc(GL11.GL_NEVER, 1, 0xF);
    		        GL11.glStencilOp(GL11.GL_REPLACE, GL11.GL_REPLACE, GL11.GL_REPLACE);
    		        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
    		        //renderBlocks.renderBlockOnInventory(Block.blocksList[itemstack.itemID], itemstack.getItemDamage(), entityitem.getEntityBrightness(f1));
    		        this.renderBlocks.renderBlockOnInventory(Block.blocksList[itemstack.itemID], itemstack.getItemDamage());
    		        GL11.glStencilFunc(GL11.GL_NEVER, 0, 0xF);
    		        GL11.glStencilOp(GL11.GL_REPLACE, GL11.GL_REPLACE, GL11.GL_REPLACE);
    		        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
    		        //renderBlocks.renderBlockOnInventory(Block.blocksList[itemstack.itemID], itemstack.getItemDamage(), entityitem.getEntityBrightness(f1));
    		        this.renderBlocks.renderBlockOnInventory(Block.blocksList[itemstack.itemID], itemstack.getItemDamage());
    		        GL11.glStencilFunc(GL11.GL_EQUAL, 1, 0xF);
    		        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
    		        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
    		       
    		        GL11.glDepthMask(false);
    		        GL11.glDisable(GL11.GL_DEPTH_TEST);

		        	GL11.glColor4f(r, g, b, 1);
		        	this.renderBlocks.renderBlockOnInventory(Block.blocksList[itemstack.itemID], itemstack.getItemDamage());
    		        GL11.glEnable(GL11.GL_DEPTH_TEST);
    		        GL11.glDepthMask(true);
    		        GL11.glDisable(GL11.GL_STENCIL_TEST);
    		        GL11.glDisable(GL11.GL_LINE_SMOOTH);
    		        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_DONT_CARE);
					
    		        GL11.glEnable(GL11.GL_BLEND);
    		        GL11.glEnable(GL11.GL_LIGHTING);
    		        GL11.glEnable(GL11.GL_TEXTURE_2D);
    		        GL11.glEnable(GL11.GL_ALPHA_TEST);
    		        GL11.glPopAttrib();
					GL11.glPopMatrix();
					
					
				}
				GL11.glDisable(GL11.GL_STENCIL_TEST);
            }else {
	            for(int var28 = 0; var28 < var13; ++var28) {
	                GL11.glPushMatrix();
	                if (var28 > 0) {
	                    var16 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.2F / var27;
	                    var17 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.2F / var27;
	                    var18 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.2F / var27;
	                    GL11.glTranslatef(var16, var17, var18);
	                }
	
	                this.renderBlocks.renderBlockOnInventory(Block.blocksList[itemstack.itemID], itemstack.getItemDamage());
	                GL11.glPopMatrix();
	            }
            }
        } else {
            GL11.glScalef(0.5F, 0.5F, 0.5F);
            int itemIconIndex = itemstack.getIconIndex();
            if (itemstack.itemID < 256) {
                this.loadTexture("/terrain.png");
            } else {
                this.loadTexture("/gui/items.png");
            }

            Tessellator tess = Tessellator.instance;
            var16 = (float)(itemIconIndex % 16 * 16 + 0) / 256.0F;
            var17 = (float)(itemIconIndex % 16 * 16 + 16) / 256.0F;
            var18 = (float)(itemIconIndex / 16 * 16 + 0) / 256.0F;
            float var19 = (float)(itemIconIndex / 16 * 16 + 16) / 256.0F;
            float var20 = 1.0F;
            float var21 = 0.5F;
            float var22 = 0.25F;
            if(EntityESPHack.instance.shouldRender(entity) && EntityESPHack.instance.getRenderingMode(entity).equalsIgnoreCase("Fill")) {
				int r, g, b;
				r = EntityESPHack.instance.itemColor.red;
				g = EntityESPHack.instance.itemColor.green;
				b = EntityESPHack.instance.itemColor.blue;
				GL11.glEnable(GL11.GL_STENCIL_TEST);
				for(int var23 = 0; var23 < var13; ++var23) {
					GL11.glPushMatrix();
					GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
					GL11.glClearStencil(0xF);
					GL11.glStencilFunc(GL11.GL_NEVER, 1, 0xF);
					GL11.glStencilOp(GL11.GL_REPLACE, GL11.GL_REPLACE, GL11.GL_REPLACE);
					if (var23 > 0) {
						float var24 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.3F;
						float var25 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.3F;
						float var26 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.3F;
						GL11.glTranslatef(var24, var25, var26);
					}
	
					GL11.glRotatef(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
					tess.startDrawingQuads();
					tess.setNormal(0.0F, 1.0F, 0.0F);
					tess.addVertexWithUV((double)(0.0F - var21), (double)(0.0F - var22), 0.0D, (double)var16, (double)var19);
					tess.addVertexWithUV((double)(var20 - var21), (double)(0.0F - var22), 0.0D, (double)var17, (double)var19);
					tess.addVertexWithUV((double)(var20 - var21), (double)(1.0F - var22), 0.0D, (double)var17, (double)var18);
					tess.addVertexWithUV((double)(0.0F - var21), (double)(1.0F - var22), 0.0D, (double)var16, (double)var18);
					tess.draw();
						
					GL11.glStencilFunc(GL11.GL_EQUAL, 1, 0xF);
					GL11.glStencilOp(GL11.GL_REPLACE, GL11.GL_REPLACE, GL11.GL_KEEP);
					
					GL11.glPushMatrix();
					GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
					GL11.glDisable(GL11.GL_TEXTURE_2D);
					GL11.glDisable(GL11.GL_LIGHTING);
					GL11.glDisable(GL11.GL_DEPTH_TEST);
					GL11.glDisable(GL11.GL_ALPHA_TEST);
					GL11.glEnable(GL11.GL_BLEND);
					GL11.glDisable(GL11.GL_FOG);
					GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_CONSTANT_ALPHA);
					GL11.glColor4f((float)r/255, (float)g/255, (float)b/255, 1);
						
					tess.startDrawingQuads();
					tess.setNormal(0.0F, 1.0F, 0.0F);
					tess.addVertexWithUV((double)(0.0F - var21), (double)(0.0F - var22), 0.0D, (double)var16, (double)var19);
					tess.addVertexWithUV((double)(var20 - var21), (double)(0.0F - var22), 0.0D, (double)var17, (double)var19);
					tess.addVertexWithUV((double)(var20 - var21), (double)(1.0F - var22), 0.0D, (double)var17, (double)var18);
					tess.addVertexWithUV((double)(0.0F - var21), (double)(1.0F - var22), 0.0D, (double)var16, (double)var18);
					tess.draw();
						
					GL11.glPopAttrib();
					GL11.glPopMatrix();
					GL11.glPopMatrix();
				}
				GL11.glDisable(GL11.GL_STENCIL_TEST);
			}else if(EntityESPHack.instance.shouldRender(entity) && EntityESPHack.instance.getRenderingMode(entity).equalsIgnoreCase("Outline")){
				for(int var23 = 0; var23 < var13; ++var23) {
					GL11.glPushMatrix();
					
					if (itemstack.itemID < 256) this.loadTexture("/terrain.png");
					else this.loadTexture("/gui/items.png");
					
					if (var23 > 0) {
						float var24 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.3F;
						float var25 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.3F;
						float var26 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.3F;
						GL11.glTranslatef(var24, var25, var26);
					}

					GL11.glRotatef(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
					
					tess.startDrawingQuads();
					tess.setNormal(0.0F, 1.0F, 0.0F);
					tess.addVertexWithUV(-0.5, -0.25, 0.0D, (double)var16, (double)var19);
					tess.addVertexWithUV(0.5d, -0.25, 0.0D, (double)var17, (double)var19);
					tess.addVertexWithUV(0.5d, 0.75, 0.0D, (double)var17, (double)var18);
					tess.addVertexWithUV(-0.5d, 0.75, 0.0D, (double)var16, (double)var18);
					tess.draw();
					
					
					GL11.glDisable(GL11.GL_DEPTH_TEST);
					GL11.glDisable(GL11.GL_TEXTURE_2D);
					GL11.glDisable(GL11.GL_LIGHTING);
					GL11.glColor4f(EntityESPHack.instance.itemColor.red / 255f, EntityESPHack.instance.itemColor.green / 255f, EntityESPHack.instance.itemColor.blue / 255f, 1);
					GL11.glLineWidth(1);
					
					int tres = 16;
					//ArrayList<DrawCall> calls;
					int listToCall = -1;
					if (itemstack.itemID < 256) {
						tres = Client.terrainTexSize;
						listToCall = Client.blockDisplayLists + itemIconIndex;
						//calls = Client.drawCallsTerrain[itemIconIndex];
					}
					else {
						tres = Client.itemsTexSize;
						listToCall = Client.itemDisplayLists + itemIconIndex;
						//calls = Client.drawCallsItems[itemIconIndex];
					}
					if(listToCall == -1) {
						System.out.println("Calls is null! Item id: "+ itemstack.itemID);
					}else {
						double px = 1d/(double)tres;
						GL11.glBegin(GL11.GL_LINES);
						GL11.glCallList(listToCall);
						GL11.glEnd();
					}
					
					
					GL11.glEnable(GL11.GL_LIGHTING);
					GL11.glEnable(GL11.GL_DEPTH_TEST);
					GL11.glEnable(GL11.GL_TEXTURE_2D);
					GL11.glColor4f(1, 1, 1, 1);
					GL11.glPopMatrix();
				}
			}else {
	            for(int var23 = 0; var23 < var13; ++var23) {
	                GL11.glPushMatrix();
	                if (var23 > 0) {
	                    float var24 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.3F;
	                    float var25 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.3F;
	                    float var26 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.3F;
	                    GL11.glTranslatef(var24, var25, var26);
	                }
	
	                GL11.glRotatef(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
	                tess.startDrawingQuads();
	                tess.setNormal(0.0F, 1.0F, 0.0F);
	                tess.addVertexWithUV((double)(0.0F - var21), (double)(0.0F - var22), 0.0D, (double)var16, (double)var19);
	                tess.addVertexWithUV((double)(var20 - var21), (double)(0.0F - var22), 0.0D, (double)var17, (double)var19);
	                tess.addVertexWithUV((double)(var20 - var21), (double)(1.0F - var22), 0.0D, (double)var17, (double)var18);
	                tess.addVertexWithUV((double)(0.0F - var21), (double)(1.0F - var22), 0.0D, (double)var16, (double)var18);
	                tess.draw();
	                GL11.glPopMatrix();
	            }
			}
        }

        GL11.glDisable(32826 /*GL_RESCALE_NORMAL_EXT*/);
        GL11.glPopMatrix();
    }

    public void drawItemIntoGui(FontRenderer var1, RenderEngine var2, int id, int var4, int var5, int x, int y) {
        if (id < 256 && RenderBlocks.renderItemIn3d(Block.blocksList[id].getRenderType())) {
            var2.bindTexture(var2.getTexture("/terrain.png"));
            Block var12 = Block.blocksList[id];
            GL11.glPushMatrix();
            GL11.glTranslatef((float)(x - 2), (float)(y + 3), -3.0F);
            GL11.glScalef(10.0F, 10.0F, 10.0F);
            GL11.glTranslatef(1.0F, 0.5F, 8.0F);
            GL11.glScalef(1.0F, 1.0F, -1.0F);
            GL11.glRotatef(210.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
            if (this.field_27004_a) {
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            }

            GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
            this.renderBlocks.renderBlockOnInventory(var12, var4);
            GL11.glPopMatrix();
        } else if (var5 >= 0) {
            GL11.glDisable(2896 /*GL_LIGHTING*/);
            if (id < 256) {
                var2.bindTexture(var2.getTexture("/terrain.png"));
            } else {
                var2.bindTexture(var2.getTexture("/gui/items.png"));
            }

            int var8 = Item.itemsList[id].getColorFromDamage(var4);
            float var9 = (float)(var8 >> 16 & 255) / 255.0F;
            float var10 = (float)(var8 >> 8 & 255) / 255.0F;
            float var11 = (float)(var8 & 255) / 255.0F;
            if (this.field_27004_a) {
                GL11.glColor4f(var9, var10, var11, 1.0F);
            }

            this.renderTexturedQuad(x, y, var5 % 16 * 16, var5 / 16 * 16, 16, 16);
            GL11.glEnable(2896 /*GL_LIGHTING*/);
        }

        GL11.glEnable(2884 /*GL_CULL_FACE*/);
    }
    
    public void renderItemForNametag(FontRenderer fr, RenderEngine eng, ItemStack stack, int x, int y) {
        if (stack != null) {
        	if (stack.itemID < 256 && RenderBlocks.renderItemIn3d(Block.blocksList[stack.itemID].getRenderType())) {
                eng.bindTexture(eng.getTexture("/terrain.png"));
                Block var12 = Block.blocksList[stack.itemID];
                GL11.glPushMatrix();
                GL11.glTranslatef((float)(x - 2), (float)(y + 3), 0);
                GL11.glScalef(10.0F, 10.0F, 10.0F);
                GL11.glTranslatef(1.0F, 0.5F, 0.0F);
                GL11.glRotatef(210.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                if (this.field_27004_a) {
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                }

                GL11.glScalef(1.0F, 1.0F, 1.0F);
                this.renderBlocks.renderBlockOnInventory(var12, stack.getItemDamage());
                GL11.glPopMatrix();
            } else if (stack.getIconIndex() >= 0) {
                GL11.glDisable(2896 /*GL_LIGHTING*/);
                if (stack.itemID < 256) {
                	eng.bindTexture(eng.getTexture("/terrain.png"));
                } else {
                	eng.bindTexture(eng.getTexture("/gui/items.png"));
                }

                int var8 = Item.itemsList[stack.itemID].getColorFromDamage(stack.getItemDamage());
                float var9 = (float)(var8 >> 16 & 255) / 255.0F;
                float var10 = (float)(var8 >> 8 & 255) / 255.0F;
                float var11 = (float)(var8 & 255) / 255.0F;
                if (this.field_27004_a) {
                    GL11.glColor4f(var9, var10, var11, 1.0F);
                }

                this.renderTexturedQuad(x, y, stack.getIconIndex() % 16 * 16, stack.getIconIndex() / 16 * 16, 16, 16);
                GL11.glEnable(2896 /*GL_LIGHTING*/);
            }

            GL11.glEnable(2884 /*GL_CULL_FACE*/);
        }
    }
    public void renderItemOverlayForNametags(FontRenderer var1, RenderEngine var2, ItemStack var3, int var4, int var5) {
    	if (var3 != null) {
            if (var3.stackSize > 1) {
                String var6 = "" + var3.stackSize;
                GL11.glDisable(2896 /*GL_LIGHTING*/);
                var1.drawStringWithShadow(var6, var4 + 19 - 2 - var1.getStringWidth(var6), var5 + 6 + 3, 16777215);
                GL11.glEnable(2896 /*GL_LIGHTING*/);
            }

            if (var3.isItemDamaged()) {
                int var11 = (int)Math.round(13.0D - (double)var3.getItemDamageForDisplay() * 13.0D / (double)var3.getMaxDamage());
                int var7 = (int)Math.round(255.0D - (double)var3.getItemDamageForDisplay() * 255.0D / (double)var3.getMaxDamage());
                GL11.glDisable(2896 /*GL_LIGHTING*/);
                GL11.glDisable(3553 /*GL_TEXTURE_2D*/);
                Tessellator var8 = Tessellator.instance;
                int var9 = 255 - var7 << 16 | var7 << 8;
                int var10 = (255 - var7) / 4 << 16 | 16128;
                this.renderQuad(var8, var4 + 2, var5 + 13, 13, 2, 0);
                this.renderQuad(var8, var4 + 2, var5 + 13, 12, 1, var10);
                this.renderQuad(var8, var4 + 2, var5 + 13, var11, 1, var9);
                GL11.glEnable(3553 /*GL_TEXTURE_2D*/);
                GL11.glEnable(2896 /*GL_LIGHTING*/);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            }

        }
    }

    public void renderItemIntoGUI(FontRenderer var1, RenderEngine var2, ItemStack var3, int var4, int var5) {
        if (var3 != null) {
            this.drawItemIntoGui(var1, var2, var3.itemID, var3.getItemDamage(), var3.getIconIndex(), var4, var5);
        }
    }

    public void renderItemOverlayIntoGUI(FontRenderer var1, RenderEngine var2, ItemStack var3, int var4, int var5) {
        if (var3 != null) {
            if (var3.stackSize > 1 || (Client.FORCE_STACK_DRAW && var3.stackSize != 1)) {
                String var6 = "" + var3.stackSize;
                GL11.glDisable(2896 /*GL_LIGHTING*/);
                GL11.glDisable(2929 /*GL_DEPTH_TEST*/);
                var1.drawStringWithShadow(var6, var4 + 19 - 2 - var1.getStringWidth(var6), var5 + 6 + 3, 16777215);
                GL11.glEnable(2896 /*GL_LIGHTING*/);
                GL11.glEnable(2929 /*GL_DEPTH_TEST*/);
            }

            if (var3.isItemDamaged()) {
                int var11 = (int)Math.round(13.0D - (double)var3.getItemDamageForDisplay() * 13.0D / (double)var3.getMaxDamage());
                int var7 = (int)Math.round(255.0D - (double)var3.getItemDamageForDisplay() * 255.0D / (double)var3.getMaxDamage());
                GL11.glDisable(2896 /*GL_LIGHTING*/);
                GL11.glDisable(2929 /*GL_DEPTH_TEST*/);
                GL11.glDisable(3553 /*GL_TEXTURE_2D*/);
                Tessellator var8 = Tessellator.instance;
                int var9 = 255 - var7 << 16 | var7 << 8;
                int var10 = (255 - var7) / 4 << 16 | 16128;
                this.renderQuad(var8, var4 + 2, var5 + 13, 13, 2, 0);
                this.renderQuad(var8, var4 + 2, var5 + 13, 12, 1, var10);
                this.renderQuad(var8, var4 + 2, var5 + 13, var11, 1, var9);
                GL11.glEnable(3553 /*GL_TEXTURE_2D*/);
                GL11.glEnable(2896 /*GL_LIGHTING*/);
                GL11.glEnable(2929 /*GL_DEPTH_TEST*/);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            }

        }
    }

    private void renderQuad(Tessellator var1, int var2, int var3, int var4, int var5, int var6) {
        var1.startDrawingQuads();
        var1.setColorOpaque_I(var6);
        var1.addVertex((double)(var2 + 0), (double)(var3 + 0), 0.0D);
        var1.addVertex((double)(var2 + 0), (double)(var3 + var5), 0.0D);
        var1.addVertex((double)(var2 + var4), (double)(var3 + var5), 0.0D);
        var1.addVertex((double)(var2 + var4), (double)(var3 + 0), 0.0D);
        var1.draw();
    }

    public void renderTexturedQuad(int var1, int var2, int var3, int var4, int var5, int var6) {
        float var7 = 0.0F;
        float var8 = 0.00390625F;
        float var9 = 0.00390625F;
        Tessellator var10 = Tessellator.instance;
        var10.startDrawingQuads();
        var10.addVertexWithUV((double)(var1 + 0), (double)(var2 + var6), (double)var7, (double)((float)(var3 + 0) * var8), (double)((float)(var4 + var6) * var9));
        var10.addVertexWithUV((double)(var1 + var5), (double)(var2 + var6), (double)var7, (double)((float)(var3 + var5) * var8), (double)((float)(var4 + var6) * var9));
        var10.addVertexWithUV((double)(var1 + var5), (double)(var2 + 0), (double)var7, (double)((float)(var3 + var5) * var8), (double)((float)(var4 + 0) * var9));
        var10.addVertexWithUV((double)(var1 + 0), (double)(var2 + 0), (double)var7, (double)((float)(var3 + 0) * var8), (double)((float)(var4 + 0) * var9));
        var10.draw();
    }

    // $FF: synthetic method
    // $FF: bridge method
    public void doRender(Entity var1, double var2, double var4, double var6, float var8, float var9) {
        this.doRenderItem((EntityItem)var1, var2, var4, var6, var8, var9);
    }
}
