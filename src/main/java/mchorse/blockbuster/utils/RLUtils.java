package mchorse.blockbuster.utils;

import net.minecraft.util.ResourceLocation;

/**
 * {@link ResourceLocation} utility methods
 *
 * This class has utils for saving and reading {@link ResourceLocation} from
 * actor model and skin.
 */
public class RLUtils
{
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
            String suffix = (skin.indexOf("/") == -1 ? model + "/" : "");

            return new ResourceLocation("blockbuster.actors", suffix + skin);
        }

        return new ResourceLocation(skin);
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