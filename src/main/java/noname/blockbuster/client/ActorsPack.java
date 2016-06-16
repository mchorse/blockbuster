package noname.blockbuster.client;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.DimensionManager;
import noname.blockbuster.api.Comment;

/**
 * Actors pack
 *
 * This class allows players to customize their actors (by providing a pack
 * that is responsible for loading skins from config/blockbuster/skins and
 * world's save skins folder with resource domain of blockbuster.actors)
 */
@Comment(comment = "Used to inject actor skins from config/blockbuster/skins folder and save's skins folder. See ClientProxy for more info.")
public class ActorsPack implements IResourcePack
{
    /**
     * Default resource domains, this property is responsible for setting
     * domain in the ResourceLocation's first argument.
     */
    public static final Set<String> defaultResourceDomains = ImmutableSet.<String> of("blockbuster.actors");

    /**
     * Cached skins, usually this map is getting reloaded while Skin Manager
     * GUI is getting opened
     */
    protected Map<String, File> skins = new HashMap<String, File>();

    /**
     * Config folder where skins are located on the client side
     */
    protected File skinsFolder;

    public ActorsPack(String path)
    {
        this.skinsFolder = new File(path);

        if (!this.skinsFolder.exists())
        {
            this.skinsFolder.mkdirs();
        }

        this.reloadSkins();
    }

    /**
     * Get available skins
     */
    public List<String> getSkins()
    {
        return new ArrayList<String>(this.skins.keySet());
    }

    /**
     * Get available skins, used by GuiActorSkin
     */
    public List<String> getReloadedSkins()
    {
        this.reloadSkins();

        return this.getSkins();
    }

    /**
     * Reload skins
     *
     * Damn, that won't be fun to reload the game every time you want to put
     * another skin in the skins folder, so why not just reload it every time
     * the GUI is showed? It's easy to implement and requires no extra code.
     *
     * This method reloads skins from config/blockbuster/skins and current's
     * world folder skins (so people could transfer their adventure map skins
     * with the world's save).
     *
     * World skins are only available in single-player, because if those skins
     * on server, then they couldn't be accessed via file-system. I'll figure
     * out something, if this feature going to be requested (primarily by
     * adventure map makers who want their map to be co-op).
     */
    protected void reloadSkins()
    {
        this.skins.clear();

        for (File file : this.skinsFolder.listFiles())
        {
            this.addSkin(file, "");
        }

        if (DimensionManager.getCurrentSaveRootDirectory() == null)
        {
            return;
        }

        File worldSkins = new File(DimensionManager.getCurrentSaveRootDirectory() + "/blockbuster/skins");

        if (!worldSkins.exists())
        {
            worldSkins.mkdirs();
        }

        for (File file : worldSkins.listFiles())
        {
            this.addSkin(file, "(Save)");
        }
    }

    /**
     * Check if file is a skin and add it to the map cache.
     */
    protected void addSkin(File skin, String prefix)
    {
        String name = skin.getName();
        int suffix = name.length() - 4;

        if (name.indexOf(".png") == suffix)
        {
            this.skins.put(prefix + name.substring(0, suffix), skin);
        }
    }

    @Override
    public InputStream getInputStream(ResourceLocation location) throws IOException
    {
        return new FileInputStream(this.skins.get(location.getResourcePath()));
    }

    @Override
    public boolean resourceExists(ResourceLocation location)
    {
        return this.skins.containsKey(location.getResourcePath());
    }

    @Override
    public Set<String> getResourceDomains()
    {
        return defaultResourceDomains;
    }

    @Override
    public <T extends IMetadataSection> T getPackMetadata(IMetadataSerializer metadataSerializer, String metadataSectionName) throws IOException
    {
        return null;
    }

    @Override
    public BufferedImage getPackImage() throws IOException
    {
        return null;
    }

    @Override
    public String getPackName()
    {
        return "Blockbuster's Actor Skin Pack";
    }
}