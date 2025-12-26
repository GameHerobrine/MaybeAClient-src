package net.minecraft.src;

import net.minecraft.client.Minecraft;
import net.skidcode.gh.maybeaclient.Client;
import net.skidcode.gh.maybeaclient.hacks.EntityESPHack;
import net.skidcode.gh.maybeaclient.hacks.NameTagsHack;
import net.skidcode.gh.maybeaclient.hacks.settings.SettingColor;
import net.skidcode.gh.maybeaclient.utils.RenderUtils;

import org.lwjgl.opengl.GL11;

public class RenderLiving extends Render {
    protected ModelBase mainModel;
    protected ModelBase renderPassModel;

    public RenderLiving(ModelBase var1, float var2) {
        this.mainModel = var1;
        this.shadowSize = var2;
    }

    public void setRenderPassModel(ModelBase var1) {
        this.renderPassModel = var1;
    }

    public void doRenderLiving(EntityLiving entity, double var2, double var4, double var6, float var8, float var9) {
        GL11.glPushMatrix();
        GL11.glDisable(2884 /*GL_CULL_FACE*/);
        this.mainModel.onGround = this.func_167_c(entity, var9);
        this.mainModel.isRiding = entity.isRiding();
        if (this.renderPassModel != null) {
            this.renderPassModel.isRiding = this.mainModel.isRiding;
        }

        try {
            float var10 = entity.prevRenderYawOffset + (entity.renderYawOffset - entity.prevRenderYawOffset) * var9;
            float var11 = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * var9;
            float var12 = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * var9;
            this.func_22012_b(entity, var2, var4, var6);
            float var13 = this.func_170_d(entity, var9);
            this.func_21004_a(entity, var13, var10, var9);
            float var14 = 0.0625F;
            GL11.glEnable(32826 /*GL_RESCALE_NORMAL_EXT*/);
            GL11.glScalef(-1.0F, -1.0F, 1.0F);
            this.preRenderCallback(entity, var9);
            GL11.glTranslatef(0.0F, -24.0F * var14 - 0.0078125F, 0.0F);
            float var15 = entity.field_705_Q + (entity.field_704_R - entity.field_705_Q) * var9;
            float var16 = entity.field_703_S - entity.field_704_R * (1.0F - var9);
            if (var15 > 1.0F) {
                var15 = 1.0F;
            }

            this.loadDownloadableImageTexture(entity.skinUrl, entity.getEntityTexture());
            GL11.glEnable(3008 /*GL_ALPHA_TEST*/);
            this.mainModel.func_25103_a(entity, var16, var15, var9);
            this.mainModel.render(var16, var15, var13, var11 - var10, var12, var14);

            for(int var17 = 0; var17 < 4; ++var17) {
                if (this.shouldRenderPass(entity, var17, var9)) {
                    this.renderPassModel.render(var16, var15, var13, var11 - var10, var12, var14);
                    GL11.glDisable(3042 /*GL_BLEND*/);
                    GL11.glEnable(3008 /*GL_ALPHA_TEST*/);
                }
            }

            if (EntityESPHack.instance.status && EntityESPHack.instance.shouldRender(entity)) {
				float r, g, b;
				SettingColor color = EntityESPHack.instance.getRenderColor(entity);
				if(color == null) {
					System.out.println("Tried rendering esp for "+entity+" that has no color!");
					r = 0;
					g = 0;
					b = 0;
				}else {
					r = color.red;
					g = color.green;
					b = color.blue;
				}
				
				if(EntityESPHack.instance.getRenderingMode(entity).equalsIgnoreCase("Fill")) {
					GL11.glPushMatrix();
					GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
					GL11.glDisable(GL11.GL_TEXTURE_2D);
					GL11.glDisable(GL11.GL_LIGHTING);
					GL11.glDisable(GL11.GL_DEPTH_TEST);
					GL11.glDisable(GL11.GL_ALPHA_TEST);
					GL11.glEnable(GL11.GL_BLEND);
					GL11.glDisable(GL11.GL_FOG);
					GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_CONSTANT_ALPHA);
					GL11.glColor3f((float)r/255, (float)g/255, (float)b/255);
					this.mainModel.render(var16, var15, var13, var11 - var10, var12, var14);
					GL11.glPopAttrib();
					GL11.glPopMatrix();
				}else if(EntityESPHack.instance.getRenderingMode(entity).equalsIgnoreCase("Outline") || EntityESPHack.instance.getRenderingMode(entity).equalsIgnoreCase("Glow")){
					boolean glow = EntityESPHack.instance.getRenderingMode(entity).equalsIgnoreCase("Glow");
					if(entity instanceof EntityPlayer){
						((ModelBiped)this.mainModel).bipedHeadwear.showModel = false;
					}
					
					double dist = Math.sqrt(
						(var2)*(var2) + 
						(var4)*(var4) + 
						(var6)*(var6)
					);
					
					float lineWidthMin = 3;
					float lineWidthMax = !glow ? 3 : 15;
					
					lineWidthMax = lineWidthMin + (float) (1 - Math.min(dist/32d, 1))*12;
					if(lineWidthMax == lineWidthMin) lineWidthMax += 0.01;
					if(!glow) lineWidthMin = lineWidthMax = 3;

					GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
					GL11.glDisable(GL11.GL_FOG);
					GL11.glDisable(GL11.GL_ALPHA_TEST);
					GL11.glDisable(GL11.GL_TEXTURE_2D);
					GL11.glDisable(GL11.GL_LIGHTING);
					GL11.glEnable(GL11.GL_BLEND);
					GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
					GL11.glLineWidth(lineWidthMax);
					GL11.glEnable(GL11.GL_LINE_SMOOTH);
					GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
					
					GL11.glEnable(GL11.GL_STENCIL_TEST);
					GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
					GL11.glClearStencil(0xF);
					GL11.glStencilFunc(GL11.GL_NEVER, 1, 0xF);
					GL11.glStencilOp(GL11.GL_REPLACE, GL11.GL_REPLACE, GL11.GL_REPLACE);
					GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
					mainModel.render(var16, var15, var13, var11 - var10, var12, var14);
					
					GL11.glStencilFunc(GL11.GL_NEVER, 0, 0xF);
					GL11.glStencilOp(GL11.GL_REPLACE, GL11.GL_REPLACE, GL11.GL_REPLACE);
					GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
					mainModel.render(var16, var15, var13, var11 - var10, var12, var14);
					
					GL11.glStencilFunc(GL11.GL_EQUAL, 1, 0xF);
					GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
					GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
					
					GL11.glColor4f(r/255, g/255, b/255, 1);
					GL11.glDepthMask(false);
					GL11.glDisable(GL11.GL_DEPTH_TEST);
					//GL13.glMultiTexCoord2f(GL13.GL_TEXTURE1, 240.0F, 240.0F);
					if(glow) {
						float c = 1;
						for(float i = lineWidthMin; i <= lineWidthMax; i+=(lineWidthMax-lineWidthMin)/4) {
							GL11.glLineWidth(i);
							GL11.glColor4f(r/255, g/255, b/255, c);
							mainModel.render(var16, var15, var13, var11 - var10, var12, var14);
							c /= 2;
						}
					}else{
						mainModel.render(var16, var15, var13, var11 - var10, var12, var14);
					}
					
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
					if(entity instanceof EntityPlayer){
						((ModelBiped)this.mainModel).bipedHeadwear.showModel = true;
					}
				}
			}
            
            
            this.renderEquippedItems(entity, var9);
            float var25 = entity.getEntityBrightness(var9);
            int var18 = this.getColorMultiplier(entity, var25, var9);
            if ((var18 >> 24 & 255) > 0 || entity.hurtTime > 0 || entity.deathTime > 0) {
                GL11.glDisable(3553 /*GL_TEXTURE_2D*/);
                GL11.glDisable(3008 /*GL_ALPHA_TEST*/);
                GL11.glEnable(3042 /*GL_BLEND*/);
                GL11.glBlendFunc(770, 771);
                GL11.glDepthFunc(514);
                if (entity.hurtTime > 0 || entity.deathTime > 0) {
                    GL11.glColor4f(var25, 0.0F, 0.0F, 0.4F);
                    this.mainModel.render(var16, var15, var13, var11 - var10, var12, var14);

                    for(int var19 = 0; var19 < 4; ++var19) {
                        if (this.func_27005_b(entity, var19, var9)) {
                            GL11.glColor4f(var25, 0.0F, 0.0F, 0.4F);
                            this.renderPassModel.render(var16, var15, var13, var11 - var10, var12, var14);
                        }
                    }
                }

                if ((var18 >> 24 & 255) > 0) {
                    float var26 = (float)(var18 >> 16 & 255) / 255.0F;
                    float var20 = (float)(var18 >> 8 & 255) / 255.0F;
                    float var21 = (float)(var18 & 255) / 255.0F;
                    float var22 = (float)(var18 >> 24 & 255) / 255.0F;
                    GL11.glColor4f(var26, var20, var21, var22);
                    this.mainModel.render(var16, var15, var13, var11 - var10, var12, var14);

                    for(int var23 = 0; var23 < 4; ++var23) {
                        if (this.func_27005_b(entity, var23, var9)) {
                            GL11.glColor4f(var26, var20, var21, var22);
                            this.renderPassModel.render(var16, var15, var13, var11 - var10, var12, var14);
                        }
                    }
                }

                GL11.glDepthFunc(515);
                GL11.glDisable(3042 /*GL_BLEND*/);
                GL11.glEnable(3008 /*GL_ALPHA_TEST*/);
                GL11.glEnable(3553 /*GL_TEXTURE_2D*/);
            }

            GL11.glDisable(32826 /*GL_RESCALE_NORMAL_EXT*/);
        } catch (Exception var24) {
            var24.printStackTrace();
        }

        GL11.glEnable(2884 /*GL_CULL_FACE*/);
        GL11.glPopMatrix();
        this.passSpecialRender(entity, var2, var4, var6);
    }

    protected void func_22012_b(EntityLiving var1, double var2, double var4, double var6) {
        GL11.glTranslatef((float)var2, (float)var4, (float)var6);
    }

    protected void func_21004_a(EntityLiving var1, float var2, float var3, float var4) {
        GL11.glRotatef(180.0F - var3, 0.0F, 1.0F, 0.0F);
        if (var1.deathTime > 0) {
            float var5 = ((float)var1.deathTime + var4 - 1.0F) / 20.0F * 1.6F;
            var5 = MathHelper.sqrt_float(var5);
            if (var5 > 1.0F) {
                var5 = 1.0F;
            }

            GL11.glRotatef(var5 * this.func_172_a(var1), 0.0F, 0.0F, 1.0F);
        }

    }

    protected float func_167_c(EntityLiving var1, float var2) {
        return var1.getSwingProgress(var2);
    }

    protected float func_170_d(EntityLiving var1, float var2) {
        return (float)var1.ticksExisted + var2;
    }

    protected void renderEquippedItems(EntityLiving var1, float var2) {
    }

    protected boolean func_27005_b(EntityLiving var1, int var2, float var3) {
        return this.shouldRenderPass(var1, var2, var3);
    }

    protected boolean shouldRenderPass(EntityLiving var1, int var2, float var3) {
        return false;
    }

    protected float func_172_a(EntityLiving var1) {
        return 90.0F;
    }

    protected int getColorMultiplier(EntityLiving var1, float var2, float var3) {
        return 0;
    }

    protected void preRenderCallback(EntityLiving var1, float var2) {
    }

    protected void passSpecialRender(EntityLiving entity, double var2, double var4, double var6) {
    	if (EntityESPHack.instance.status && EntityESPHack.instance.getRenderingMode(entity).equalsIgnoreCase("Box") && EntityESPHack.instance.shouldRender(entity)) {
			
    		float r, g, b;
			SettingColor color = EntityESPHack.instance.getRenderColor(entity);
			if(color == null) {
				System.out.println("Tried rendering esp for "+entity+" that has no color!");
				r = 0;
				g = 0;
				b = 0;
			}else {
				r = color.red;
				g = color.green;
				b = color.blue;
			}
			
			GL11.glEnable(3042 /* GL_BLEND */);
			GL11.glBlendFunc(770, 771);
			GL11.glLineWidth(1F);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glDepthMask(false);
			GL11.glEnable(GL11.GL_LINE_SMOOTH);
			GL11.glBlendFunc(770, 771);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(2929 /* GL_DEPTH_TEST */);
			GL11.glDepthMask(false);
			GL11.glEnable(GL11.GL_LINE_SMOOTH);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glColor3f((float)r, (float)g, (float)b);
			RenderUtils.drawOutlinedBB(
				AxisAlignedBB.getBoundingBox(var2 - entity.width / 2, var4, var6 - entity.width / 2, var2 + entity.width / 2, var4 + entity.height, var6 + entity.width / 2)
			);
			GL11.glColor4f(0.0F, 230F, 255F, 0.3F);
			GL11.glDepthMask(true);
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glEnable(3553 /* GL_TEXTURE_2D */);
			GL11.glEnable(2929 /* GL_DEPTH_TEST */);
			
		}
    	
    	if (Minecraft.isDebugInfoEnabled()) {
            this.renderLivingLabel(entity, Integer.toString(entity.entityId), var2, var4, var6, 64);
        }

    }

    // $FF: synthetic method
    // $FF: bridge method
    public void doRender(Entity var1, double var2, double var4, double var6, float var8, float var9) {
        this.doRenderLiving((EntityLiving)var1, var2, var4, var6, var8, var9);
    }
}
