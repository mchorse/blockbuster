package mchorse.blockbuster.client.textures;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;

import mchorse.mclib.utils.resources.RLUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GifTexture extends AbstractTexture
{
    public static int globalTick = 0;
    public static int entityTick = -1;

    public static boolean tried = false;
    public static Field fieldMultiTex = null;

    public ResourceLocation base;
    public ResourceLocation[] frames;
    public int[] delays;

    public int duration;

    public static void bindTexture(ResourceLocation location, int ticks, float partialTicks)
    {
        TextureManager textures = Minecraft.getMinecraft().renderEngine;

        if (location.getResourcePath().endsWith("gif"))
        {
            ITextureObject object = textures.getTexture(location);

            if (object instanceof GifTexture)
            {
                GifTexture texture = (GifTexture) object;

                location = texture.getFrame(ticks, partialTicks);
            }
        }

        textures.bindTexture(location);
    }

    public static void updateTick()
    {
        globalTick += 1;
    }

    public GifTexture(ResourceLocation texture, int[] delays, ResourceLocation[] frames)
    {
        this.base = texture;
        this.delays = Arrays.copyOf(delays, delays.length);
        this.frames = frames;
    }

    public void calculateDuration()
    {
        this.duration = 0;

        for (int delay : this.delays)
        {
            this.duration += delay;
        }
    }

    @Override
    public void loadTexture(IResourceManager resourceManager) throws IOException
    {}

    @Override
    public int getGlTextureId()
    {
        Minecraft mc = Minecraft.getMinecraft();
        TextureManager textures = mc.renderEngine;
        ResourceLocation rl = this.getFrame(entityTick > -1 ? entityTick : globalTick, mc.getRenderPartialTicks());

        textures.bindTexture(rl);

        ITextureObject texture = textures.getTexture(rl);

        this.updateMultiTex(texture);

        return texture.getGlTextureId();
    }

    @Override
    public void deleteGlTexture()
    {}

    public ResourceLocation getFrame(int ticks, float partialTicks)
    {
        int tick = (int) ((ticks + partialTicks) * 5 % this.duration);

        int duration = 0;
        int index = 0;

        for (int delay : this.delays)
        {
            duration += delay;

            if (tick < duration)
            {
                break;
            }

            index++;
        }

        return this.frames[index];
    }

    private void updateMultiTex(ITextureObject texture)
    {
        if (!tried)
        {
            try
            {
                fieldMultiTex = AbstractTexture.class.getField("multiTex");
            }
            catch (NoSuchFieldException | SecurityException e)
            {
                fieldMultiTex = null;
            }

            tried = true;
        }

        if (texture instanceof AbstractTexture && fieldMultiTex != null)
        {
            try
            {
                Object obj = fieldMultiTex.get(texture);

                fieldMultiTex.set(this, obj);
            }
            catch (IllegalArgumentException | IllegalAccessException e)
            {}
        }
    }
}
