package mchorse.blockbuster.client.textures;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import at.dhyan.open_imaging.GifDecoder;
import at.dhyan.open_imaging.GifDecoder.GifImage;
import mchorse.blockbuster.client.RenderingHandler;
import mchorse.mclib.utils.ReflectionUtils;
import mchorse.mclib.utils.resources.MultiResourceLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.resources.IResourceManager;
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

    public GifProcessThread(ResourceLocation texture)
    {
        this.texture = texture;
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
            IResourceManager manager = mc.getResourceManager();
            InputStream stream = manager.getResource(this.texture).getInputStream();

            GifImage image = GifDecoder.read(stream);
            GifTexture texture = new GifTexture(this.texture);

            texture.width = image.getWidth();
            texture.height = image.getHeight();
            int frames = image.getFrameCount();

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
            RenderingHandler.registerGif(this.texture, texture);

            for (int i = 0; i < frames; i++)
            {
                BufferedImage buffer = image.getFrame(i);
                int delay = image.getDelay(i);

                texture.add(delay, MipmapTexture.bytesFromBuffer(buffer));
            }

            texture.calculateDuration();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void create(ResourceLocation location)
    {
        GifProcessThread thread = new GifProcessThread(location);

        THREADS.put(location, thread);
        thread.run();
        THREADS.remove(location);
    }
}