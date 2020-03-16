package mchorse.blockbuster.client;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.api.ModelPack;
import mchorse.blockbuster.client.textures.GifProcessThread;
import mchorse.blockbuster.client.textures.URLDownloadThread;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.MetadataSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Actors pack
 *
 * This class allows players to customize their actors with custom models and
 * skins.
 *
 * This class is used both on server and on client. I just realized that you
 * can't use IResourcePack on server...
 */
@SideOnly(Side.CLIENT)
public class ActorsPack implements IResourcePack
{
    public ModelPack pack = new ModelPack();

    /**
     * Cached last file from {@link #resourceExists(ResourceLocation)} 
     * method
     */
    private File lastFile;

    /* IResourcePack implementation */

    /**
     * Read a resource from model pack
     */
    @Override
    public InputStream getInputStream(ResourceLocation location) throws IOException
    {
        String domain = location.getResourceDomain();
        String path = location.getResourcePath();

        if (path.toLowerCase().endsWith(".gif"))
        {
            this.handleGif(location);
        }

        if ((domain.equals("http") || domain.equals("https")) && path.startsWith("//") && !path.endsWith(".mcmeta"))
        {
            return this.hanldeURLSkins(location);
        }

        File fileFile = this.lastFile;

        /* In case this pack was used without checking for resource 
         * in other method first, find the file */
        if (fileFile == null)
        {
            for (File file : this.pack.folders)
            {
                File packFile = new File(file, path);

                if (packFile.exists())
                {
                    fileFile = packFile;
                    break;
                }
            }
        }

        if (fileFile != null)
        {
            this.lastFile = null;

            return new FileInputStream(fileFile);
        }

        throw new FileNotFoundException(location.toString());
    }

    /**
     * Handle creation of GIF texture 
     */
    private void handleGif(ResourceLocation location)
    {
        if (GifProcessThread.THREADS.containsKey(location))
        {
            return;
        }

        new Thread(() ->
        {
            Minecraft.getMinecraft().addScheduledTask(() -> GifProcessThread.create(location));
        }).start();
    }

    /**
     * Handle URL skins 
     */
    private InputStream hanldeURLSkins(ResourceLocation location)
    {
        try
        {
            if (Blockbuster.proxy.config.url_skins_sync_download)
            {
                InputStream stream = URLDownloadThread.downloadImage(location);

                if (stream == null)
                {
                    throw new IOException("Couldn't download image...");
                }

                return stream;
            }
            else
            {
                new Thread(new URLDownloadThread(location)).start();
            }
        }
        catch (IOException e)
        {}

        /* Make it a black pixel in case it fails */
        return ActorsPack.class.getResourceAsStream("/assets/blockbuster/textures/blocks/black.png");
    }

    /**
     * Check if resource is available in the model pack. This method 
     * also supports old mapping method for model skins
     */
    @Override
    public boolean resourceExists(ResourceLocation location)
    {
        /* Handle skin URLs */
        String domain = location.getResourceDomain();
        String path = location.getResourcePath();

        /* Only actual HTTP URL can go here, also ignore mcmeta data */
        if ((domain.equals("http") || domain.equals("https")) && path.startsWith("//") && !path.endsWith(".mcmeta"))
        {
            return true;
        }

        /* Handle models path */
        for (File file : this.pack.folders)
        {
            this.lastFile = new File(file, path);

            if (this.lastFile.exists())
            {
                return true;
            }
        }

        this.lastFile = null;

        return false;
    }

    /**
     * Get resource domains
     *
     * Having multiple domains seems like a nice idea for aliases. 
     * I'm totally using it for URL skins
     */
    @Override
    public Set<String> getResourceDomains()
    {
        return ImmutableSet.<String>of("b.a", "http", "https");
    }

    @Override
    public String getPackName()
    {
        return "Blockbuster's Actor Pack";
    }

    /**
     * Get pack metadata
     *
     * This method is returns null, because it isn't an actual resource pack, but
     * just a way to pass resources into minecraft core.
     *
     * Either Jabelar or TheGreyGhost mentioned that returning null in this
     * method, disable listing of this resource pack in the resource pack menu.
     * Seems legit to me.
     */
    @Override
    public <T extends IMetadataSection> T getPackMetadata(MetadataSerializer metadataSerializer, String metadataSectionName) throws IOException
    {
        return null;
    }

    /**
     * I don't think that my actor resources pack should have an icon. 
     * However that icon would look really badass/sexy.
     */
    @Override
    public BufferedImage getPackImage() throws IOException
    {
        return null;
    }
}