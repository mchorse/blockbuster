package mchorse.blockbuster.client;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

import javax.imageio.ImageIO;

import mchorse.blockbuster.commands.model.SubCommandModelClear;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.util.ResourceLocation;

public class URLDownloadThread implements Runnable
{
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
            InputStream input = new URL(this.url.toString()).openStream();
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