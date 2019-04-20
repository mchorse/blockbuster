package mchorse.blockbuster.client.textures;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.ITickableTextureObject;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;

public class GifTexture extends AbstractTexture implements ITickableTextureObject
{
    public List<GifElement> elements = new ArrayList<GifElement>();
    public int lastTick;
    public int timer;
    public int index;
    public int duration;

    public int width;
    public int height;

    public void add(int delay, ByteBuffer buffer)
    {
        this.elements.add(new GifElement(delay, this.width, this.height, buffer));
    }

    public void calculateDuration()
    {
        this.duration = 0;

        for (GifElement element : this.elements)
        {
            this.duration += element.delay;
        }
    }

    @Override
    public void loadTexture(IResourceManager resourceManager) throws IOException
    {}

    @Override
    public void tick()
    {
        Minecraft mc = Minecraft.getMinecraft();

        /* No point to cause NPE xD */
        if (mc.thePlayer == null)
        {
            return;
        }

        int ticks = mc.thePlayer.ticksExisted;
        float partial = mc.getRenderPartialTicks();
        float diff = ticks - this.lastTick + partial;

        this.timer = (int) (diff * 5);

        if (this.timer >= this.duration)
        {
            this.timer = 0;
            this.lastTick = ticks;
        }

        int duration = 0;
        int index = 0;

        for (GifElement element : this.elements)
        {
            duration += element.delay;

            if (this.index != index && this.timer < duration)
            {
                this.index = index == 0 ? 0 : index - 1;

                break;
            }

            index++;
        }
    }

    @Override
    public int getGlTextureId()
    {
        if (this.index < 0 || this.index >= this.elements.size())
        {
            return -1;
        }

        return this.elements.get(this.index).id;
    }

    @Override
    public void deleteGlTexture()
    {
        for (GifElement element : this.elements)
        {
            GlStateManager.deleteTexture(element.id);
            element.id = -1;
        }
    }

    public static class GifElement
    {
        public int delay;
        public int id = -1;

        public GifElement(int delay, int w, int h, ByteBuffer buffer)
        {
            this.delay = delay;
            this.id = TextureUtil.glGenTextures();

            GlStateManager.bindTexture(this.id);
            GlStateManager.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
            GlStateManager.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, w, h, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
        }
    }
}