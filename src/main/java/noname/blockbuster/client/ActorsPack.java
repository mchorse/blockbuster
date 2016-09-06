package noname.blockbuster.client;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.MetadataSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Actors pack
 *
 * This class allows players to customize their actors (by providing a pack
 * that is responsible for loading skins from config/blockbuster/skins and
 * world's save skins folder with resource domain of blockbuster.actors)
 */
@SideOnly(Side.CLIENT)
public class ActorsPack implements IResourcePack
{
    /**
     * Default resource domains, this property is responsible for setting
     * domain in the ResourceLocation's first argument.
     */
    public static final Set<String> defaultResourceDomains = ImmutableSet.<String> of("blockbuster.actors");

    /**
     * Cached models
     */
    protected Map<String, File> models = new HashMap<String, File>();

    /**
     * Cached skins
     */
    protected Map<String, Map<String, File>> skins = new HashMap<String, Map<String, File>>();

    /**
     * Config folder where skins are located on the client side
     */
    protected File modelFolder;

    /**
     * Actor pack initialization
     */
    public ActorsPack(String path)
    {
        this.modelFolder = new File(path);

        if (!this.modelFolder.exists())
        {
            this.modelFolder.mkdirs();
        }

        this.reload();
    }

    /**
     * Get available skins
     */
    public List<String> getSkins(String model)
    {
        Set<String> keys = this.skins.containsKey(model) ? this.skins.get(model).keySet() : Collections.<String> emptySet();

        return new ArrayList<String>(keys);
    }

    /**
     * Get available skins
     */
    public Map<String, Map<String, File>> getAllSkins()
    {
        return this.skins;
    }

    /**
     * Get available models
     */
    public List<String> getModels()
    {
        return new ArrayList<String>(this.models.keySet());
    }

    /**
     * Reload skins
     *
     * Damn, that won't be fun to reload the game every time you want to put
     * another skin in the skins folder, so why not just reload it every time
     * the GUI is showed? It's easy to implement and requires no extra code.
     *
     * This method reloads skins from config/blockbuster/skins.
     */
    public void reload()
    {
        this.reloadModels();
        this.reloadSkins();
    }

    /**
     * Reload models
     *
     * Simply caches file instances in the map for retrieval in actor GUI
     */
    protected void reloadModels()
    {
        this.models.clear();

        for (File file : this.modelFolder.listFiles())
        {
            File model = new File(file.getAbsolutePath() + "/model.json");

            if (file.isDirectory() && model.isFile())
            {
                this.models.put(file.getName(), model);
            }
        }
    }

    /**
     * Reload skins from model folders
     *
     * The algorithm of this method takes the same code from method that above
     * (reloadModels) and scans all skins in the "skins" folder in model's
     * folder.
     */
    protected void reloadSkins()
    {
        this.skins.clear();

        for (File file : this.modelFolder.listFiles())
        {
            File skins = new File(file.getAbsolutePath() + "/skins/");

            if (file.isDirectory())
            {
                Map<String, File> map = new HashMap<String, File>();

                skins.mkdirs();

                for (File skin : skins.listFiles())
                {
                    int suffix = skin.getName().indexOf(".png");

                    map.put(skin.getName().substring(0, suffix), skin);
                }

                this.skins.put(file.getName(), map);
            }
        }
    }

    /* IResourcePack implementation */

    /**
     * Read a JSON model or skin for an actor
     */
    @Override
    public InputStream getInputStream(ResourceLocation location) throws IOException
    {
        String[] path = location.getResourcePath().split("/");

        if (path.length == 1)
        {
            return new FileInputStream(this.models.get(path[0]));
        }
        else if (path.length == 2)
        {
            return new FileInputStream(this.skins.get(path[0]).get(path[1]));
        }

        return null;
    }

    /**
     * Check if model or skin (texture mapped on the model) is existing
     * in the actor's pack
     */
    @Override
    public boolean resourceExists(ResourceLocation location)
    {
        String[] path = location.getResourcePath().split("/");

        if (path.length == 1)
        {
            return this.models.containsKey(path[0]);
        }
        else if (path.length == 2)
        {
            Map<String, File> skins = this.skins.get(path[0]);

            return skins != null && skins.containsKey(path[1]);
        }

        return false;
    }

    @Override
    public Set<String> getResourceDomains()
    {
        return defaultResourceDomains;
    }

    @Override
    public String getPackName()
    {
        return "Blockbuster's Actor Pack";
    }

    /* No metadata and no image */

    @Override
    public <T extends IMetadataSection> T getPackMetadata(MetadataSerializer metadataSerializer, String metadataSectionName) throws IOException
    {
        return null;
    }

    @Override
    public BufferedImage getPackImage() throws IOException
    {
        return null;
    }
}