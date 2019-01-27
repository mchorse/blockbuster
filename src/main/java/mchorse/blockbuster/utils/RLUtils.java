package mchorse.blockbuster.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;

/**
 * {@link ResourceLocation} utility methods
 *
 * This class has utils for saving and reading {@link ResourceLocation} from
 * actor model and skin.
 */
public class RLUtils
{
    public static ResourceLocation create(String path)
    {
        return new TextureLocation(path);
    }

    public static ResourceLocation create(String domain, String path)
    {
        return new TextureLocation(domain, path);
    }

    public static ResourceLocation create(NBTBase base)
    {
        if (base instanceof NBTTagList)
        {
            NBTTagList list = (NBTTagList) base;

            if (!list.hasNoTags())
            {
                MultiResourceLocation multi = new MultiResourceLocation(list.getStringTagAt(0));

                for (int i = 1; i < list.tagCount(); i++)
                {
                    multi.children.add(RLUtils.create(list.getStringTagAt(i)));
                }

                return multi;
            }
        }
        else if (base instanceof NBTTagString)
        {
            return create(((NBTTagString) base).getString());
        }

        return null;
    }

    public static ResourceLocation create(JsonElement jsonElement)
    {
        if (jsonElement.isJsonArray())
        {
            JsonArray array = jsonElement.getAsJsonArray();
            int size = array.size();

            if (size > 0)
            {
                JsonElement first = array.get(0);

                if (first.isJsonPrimitive())
                {
                    MultiResourceLocation location = new MultiResourceLocation(first.getAsString());

                    for (int i = 1; i < size; i++)
                    {
                        location.children.add(create(array.get(i)));
                    }
                }
            }
        }
        else if (jsonElement.isJsonPrimitive())
        {
            return create(jsonElement.getAsString());
        }

        return null;
    }

    public static NBTBase writeNbt(ResourceLocation location)
    {
        if (location instanceof MultiResourceLocation)
        {
            MultiResourceLocation multi = (MultiResourceLocation) location;
            NBTTagList list = new NBTTagList();

            for (ResourceLocation child : multi.children)
            {
                list.appendTag(new NBTTagString(child.toString()));
            }

            return list;
        }
        else if (location != null)
        {
            return new NBTTagString(location.toString());
        }

        return null;
    }

    public static JsonElement writeJson(ResourceLocation location)
    {
        if (location instanceof MultiResourceLocation)
        {
            MultiResourceLocation multi = (MultiResourceLocation) location;
            JsonArray array = new JsonArray();

            for (ResourceLocation child : multi.children)
            {
                array.add(new JsonPrimitive(child.toString()));
            }

            return array;
        }
        else if (location != null)
        {
            return new JsonPrimitive(location.toString());
        }

        return JsonNull.INSTANCE;
    }

    public static ResourceLocation clone(ResourceLocation location)
    {
        if (location instanceof MultiResourceLocation)
        {
            MultiResourceLocation multi = (MultiResourceLocation) location;
            MultiResourceLocation newMulti = new MultiResourceLocation(multi.toString());

            newMulti.children.clear();
            newMulti.children.addAll(multi.children);

            return newMulti;
        }
        else if (location != null)
        {
            return create(location.toString());
        }

        return null;
    }

    /**
     * Get resource location from actor's model and skin strings
     */
    public static ResourceLocation fromString(String skin, String model)
    {
        if (skin.isEmpty())
        {
            return null;
        }

        if (skin.indexOf(":") == -1)
        {
            String prefix = (skin.indexOf("/") == -1 ? model + "/" : "");

            return create("blockbuster.actors", prefix + skin);
        }

        return create(skin);
    }

    /**
     * Get string from resource location in human readable format
     */
    public static String fromResource(ResourceLocation skin)
    {
        if (skin == null)
        {
            return "";
        }

        if (skin.getResourceDomain().equals("blockbuster.actors"))
        {
            String[] splits = skin.getResourcePath().split("/");

            /* Returns name of the skin ("$model/$skin") */
            return splits[splits.length - 1];
        }

        return skin.toString();
    }
}