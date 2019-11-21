package mchorse.blockbuster.client.textures;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import javax.imageio.ImageIO;

import mchorse.mclib.utils.ReflectionUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.util.ResourceLocation;

/**
 * URL download thread
 * 
 * This bad boy downloads a picture from internet and puts it into the 
 * texture manager's.
 */
public class URLDownloadThread implements Runnable
{
    /**
     * Look, MA! I'm Google Chrome on OS X!!! xD 
     */
    public static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.110 Safari/537.36";

    private ResourceLocation url;

    public URLDownloadThread(ResourceLocation url)
    {
        this.url = url;
    }

    public static InputStream downloadImage(final ResourceLocation url) throws IOException
    {
        URLConnection con = new URL(url.toString()).openConnection();
        con.setRequestProperty("User-Agent", USER_AGENT);

        InputStream stream = con.getInputStream();
        String type = con.getHeaderField("Content-Type");

        if (type != null && !type.startsWith("image/"))
        {
            return null;
        }

        return stream;
    }

    public static void addToManager(ResourceLocation url, InputStream is) throws IOException
    {
        BufferedImage image = ImageIO.read(is);

        SimpleTexture texture = new SimpleTexture(url);
        TextureUtil.uploadTextureImageAllocate(texture.getGlTextureId(), image, false, false);

        TextureManager manager = Minecraft.getMinecraft().renderEngine;
        Map<ResourceLocation, ITextureObject> map = ReflectionUtils.getTextures(manager);

        map.put(url, texture);
    }

    @Override
    public void run()
    {
        Minecraft.getMinecraft().addScheduledTask(() ->
        {
            try
            {
                InputStream stream = downloadImage(this.url);

                if (stream != null)
                {
                    addToManager(this.url, stream);
                }
            }
            catch (IOException e)
            {}
        });
    }
}