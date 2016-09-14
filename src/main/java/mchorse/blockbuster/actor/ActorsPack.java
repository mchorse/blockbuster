package mchorse.blockbuster.actor;

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

/**
 * Actors pack
 *
 * This class allows players to customize their actors with custom models and
 * skins.
 *
 * This class is used both on server and on client. I just realized that you
 * can't use IResourcePack on server...
 *
 * @todo make things work on dedicated server
 */
public class ActorsPack implements IResourcePack
{
    /**
     * Cached models
     */
    protected Map<String, File> models = new HashMap<String, File>();

    /**
     * Cached skins
     */
    protected Map<String, Map<String, File>> skins = new HashMap<String, Map<String, File>>();

    /**
     * Folders which to check when collecting all models and skins
     */
    protected List<File> folders = new ArrayList<File>();

    /**
     * Add a folder to the list of folders to where to look up models and skins
     */
    public void addFolder(String path)
    {
        File folder = new File(path);

        folder.mkdirs();

        if (folder.isDirectory()) this.folders.add(folder);
    }

    /**
     * Get available skins for model
     */
    public List<String> getSkins(String model)
    {
        Set<String> keys = this.skins.containsKey(model) ? this.skins.get(model).keySet() : Collections.<String> emptySet();

        return new ArrayList<String>(keys);
    }

    /**
     * Get all available skins
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
     * Reload actor resources
     *
     * Damn, that won't be fun to reload the game every time you want to put
     * another skin in the skins folder, so why not just reload it every time
     * the GUI is showed? It's easy to implement and requires no extra code.
     *
     * This method reloads models from config/blockbuster/models/ and skins from
     * config/blockbuster/models/$model/skins/
     */
    public void reload()
    {
        this.models.clear();
        this.skins.clear();

        for (File folder : this.folders)
        {
            this.reloadModels(folder);
            this.reloadSkins(folder);
        }
    }

    /**
     * Reload models
     *
     * Simply caches file instances in the map for retrieval in actor GUI
     */
    protected void reloadModels(File folder)
    {
        for (File file : folder.listFiles())
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
    protected void reloadSkins(File folder)
    {
        for (File file : folder.listFiles())
        {
            File skins = new File(file.getAbsolutePath() + "/skins/");

            if (file.isDirectory())
            {
                Map<String, File> map = new HashMap<String, File>();

                skins.mkdirs();

                for (File skin : skins.listFiles())
                {
                    int suffix = skin.getName().indexOf(".png");

                    if (suffix != -1)
                    {
                        map.put(skin.getName().substring(0, suffix), skin);
                    }
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
        String path = location.getResourcePath();
        String[] splits = path.split("/");

        if (splits.length == 1 && path.indexOf("/") == -1)
        {
            return new FileInputStream(this.models.get(splits[0]));
        }
        else if (splits.length == 2)
        {
            return new FileInputStream(this.skins.get(splits[0]).get(splits[1]));
        }

        return null;
    }

    /**
     * Check if model or skin (texture mapped on the model) is existing
     * in the actor's pack. Uses a pretty ugly hack to remap those:
     *
     * - $model/model.json -> $model
     * - $model/skins/$skin.png -> $model/$skin
     */
    @Override
    public boolean resourceExists(ResourceLocation location)
    {
        String path = location.getResourcePath();
        String[] splits = path.split("/");

        if (splits.length == 1 && path.indexOf("/") == -1)
        {
            return this.models.containsKey(splits[0]);
        }
        else if (splits.length == 2)
        {
            Map<String, File> skins = this.skins.get(splits[0]);

            return skins != null && skins.containsKey(splits[1]);
        }

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
        return ImmutableSet.<String> of("blockbuster.actors");
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