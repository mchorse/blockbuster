package mchorse.blockbuster.client.render;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;

public class RenderCustomGlobal extends RenderGlobal
{
    public RenderCustomGlobal(Minecraft mcIn)
    {
        super(mcIn);
    }

    @Override
    public void renderSky(float partialTicks, int pass)
    {
        GlStateManager.clearColor(0, 1, 0, 1);
        GlStateManager.clear(GL11.GL_COLOR_BUFFER_BIT);
        GL11.glDisable(GL11.GL_FOG);
    }

    @Override
    public void renderClouds(float partialTicks, int pass, double p_180447_3_, double p_180447_5_, double p_180447_7_)
    {}
}