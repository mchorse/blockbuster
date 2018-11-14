package mchorse.blockbuster.client;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import mchorse.blockbuster.api.ModelPack;
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
        String path = location.getResourcePath();
        String[] splits = path.split("/");

        if (splits.length == 2 && !path.matches("\\.[\\w\\d_]+$"))
        {
            return new FileInputStream(this.pack.skins.get(splits[0]).get(splits[1]));
        }

        File fileFile = this.lastFile;

        /* In case this pack was used without checking for resource 
         * in other method first, find the file */
        if (fileFile != null)
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

        return null;
    }

    /**
     * Check if resource is available in the model pack. This method 
     * also supports old mapping method for model skins
     */
    @Override
    public boolean resourceExists(ResourceLocation location)
    {
        String path = location.getResourcePath();
        String[] splits = path.split("/");

        if (splits.length == 2 && !path.matches("\\.[\\w\\d_]+$"))
        {
            Map<String, File> skins = this.pack.skins.get(splits[0]);

            return skins != null && skins.containsKey(splits[1]);
        }

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
     * Seems like this method is used only once resource packs are getting
     * reloaded. So no need for a public static field. That's also very strange
     * that return time is a set, instead of a simple string. Several domains
     * for one pack, but why?
     */
    @Override
    public Set<String> getResourceDomains()
    {
        return ImmutableSet.<String>of("blockbuster.actors", "b.a");
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
     * I don't think that my actor resources pack should have an icon. However
     * that icon would look really badass/sexy.
     */
    @Override
    public BufferedImage getPackImage() throws IOException
    {
        return null;
    }
}