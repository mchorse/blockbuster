package mchorse.blockbuster.client;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import javax.imageio.ImageIO;

import mchorse.blockbuster.commands.model.SubCommandModelClear;
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
    public static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.110 Safari/537.36";

    private ResourceLocation url;

    public URLDownloadThread(ResourceLocation url)
    {
        this.url = url;
    }

    @Override
    public void run()
    {
        try
        {
            URLConnection con = new URL(this.url.toString()).openConnection();
            con.setRequestProperty("User-Agent", USER_AGENT);

            InputStream input = con.getInputStream();
            BufferedImage image = ImageIO.read(input);

            Minecraft.getMinecraft().addScheduledTask(() ->
            {
                SimpleTexture texture = new SimpleTexture(this.url);
                TextureUtil.uploadTextureImageAllocate(texture.getGlTextureId(), image, false, false);

                TextureManager manager = Minecraft.getMinecraft().renderEngine;
                Map<ResourceLocation, ITextureObject> map = SubCommandModelClear.getTextures(manager);

                map.put(this.url, texture);
            });
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}