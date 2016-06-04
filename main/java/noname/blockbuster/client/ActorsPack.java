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
import noname.blockbuster.api.Comment;

/**
 * Actors pack
 *
 * This class allows players to customize their actors (by providing a pack
 * that is responsible for loading skins from config/blockbuster/skins with
 * resource domain of blockbuster.actors)
 */
@Comment(comment = "Used to inject actor skins from config/blockbuster/skins folder. See ClientProxy for more info.")
public class ActorsPack implements IResourcePack
{
    public static final Set<String> defaultResourceDomains = ImmutableSet.<String> of("blockbuster.actors");

    /**
     * Hash map of actors
     */
    protected Map<String, File> skins = new HashMap<String, File>();

    public ActorsPack(String path)
    {
        File skins = new File(path);

        if (!skins.exists())
        {
            skins.mkdirs();
        }

        for (File file : skins.listFiles())
        {
            String name = file.getName();
            int suffix = name.length() - 4;

            if (name.indexOf(".png") == suffix)
            {
                this.skins.put(name.substring(0, suffix), file);
            }
        }
    }

    /**
     * Get available skins, used by GuiActorSkin
     */
    public List<String> getSkins()
    {
        return new ArrayList<String>(this.skins.keySet());
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