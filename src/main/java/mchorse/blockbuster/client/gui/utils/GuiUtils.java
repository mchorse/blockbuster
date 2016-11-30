package mchorse.blockbuster.client.gui.utils;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;

/**
 * GUI utilities
 */
public class GuiUtils
{
    /**
     * Draw an entity on the screen.
     *
     * Taken <s>stolen</s> from minecraft's class GuiInventory. I wonder what's
     * the license of minecraft's decompiled code?
     */
    public static void drawEntityOnScreen(int posX, int posY, int scale, int mouseX, int mouseY, EntityLivingBase ent)
    {
        GL11.glEnable(2903);
        GL11.glPushMatrix();
        GL11.glTranslatef(posX, posY, 100.0F);
        GL11.glScalef((-scale), scale, scale);
        GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);

        float f = ent.renderYawOffset;
        float f1 = ent.rotationYaw;
        float f2 = ent.rotationPitch;
        float f3 = ent.prevRotationYawHead;
        float f4 = ent.rotationYawHead;

        ent.renderYawOffset = (float) Math.atan(mouseX / 40.0F) * 20.0F;
        ent.rotationYaw = (float) Math.atan(mouseX / 40.0F) * 40.0F;
        ent.rotationPitch = -((float) Math.atan(mouseY / 40.0F)) * 20.0F;
        ent.rotationYawHead = ent.rotationYaw;
        ent.prevRotationYawHead = ent.rotationYaw;

        GL11.glTranslatef(0.0F, 0.0F, 0.0F);

        RenderManager manager = RenderManager.instance;
        manager.playerViewX = 180.0F;
        manager.renderEntityWithPosYaw(ent, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F);

        ent.renderYawOffset = f;
        ent.rotationYaw = f1;
        ent.rotationPitch = f2;
        ent.prevRotationYawHead = f3;
        ent.rotationYawHead = f4;

        GL11.glPopMatrix();
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(32826);
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }
}