package mchorse.blockbuster.client.textures;

import java.util.HashMap;
import java.util.Map;

import at.dhyan.open_imaging.GifDecoder.GifImage;
import mchorse.blockbuster.client.RenderingHandler;
import mchorse.blockbuster.utils.mclib.GifFolder;
import mchorse.mclib.utils.ReflectionUtils;
import mchorse.mclib.utils.resources.MultiResourceLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * GIF process thread
 * 
 * This bad boy is responsible for creating an animated GIF texture
 */
@SideOnly(Side.CLIENT)
public class GifProcessThread implements Runnable
{
    public static final Map<ResourceLocation, GifProcessThread> THREADS = new HashMap<ResourceLocation, GifProcessThread>();

    public ResourceLocation texture;
    public GifFolder gifFile;

    public GifProcessThread(ResourceLocation texture, GifFolder gif)
    {
        this.texture = texture;
        this.gifFile = gif;
    }

    @Override
    public void run()
    {
        if (this.texture instanceof MultiResourceLocation)
        {
            return;
        }

        try
        {
            Minecraft mc = Minecraft.getMinecraft();

            GifImage image = this.gifFile.gif;
            int[] delays = new int[image.getFrameCount()];
            
            for (int i = 0; i < delays.length; i++)
            {
                delays[i] = image.getDelay(i);
            }
            
            GifTexture texture = new GifTexture(this.texture, delays);
            Map<ResourceLocation, ITextureObject> map = ReflectionUtils.getTextures(mc.renderEngine);
            ITextureObject old = map.remove(this.texture);

            if (old != null)
            {
                if (old instanceof AbstractTexture)
                {
                    ((AbstractTexture) old).deleteGlTexture();
                }
            }

            map.put(this.texture, texture);

            texture.calculateDuration();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void create(ResourceLocation location, GifFolder gif)
    {
        GifProcessThread thread = new GifProcessThread(location, gif);

        THREADS.put(location, thread);
        thread.run();
        THREADS.remove(location);
    }
}