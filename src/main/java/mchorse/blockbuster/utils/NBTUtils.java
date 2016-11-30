package mchorse.blockbuster.utils;

import net.minecraft.nbt.NBTTagCompound;

/**
 * NBT utilities
 *
 * This class provides different method for working with NBT
 */
public class NBTUtils
{
    /* BlockPos */

    /**
     * Save given {@link BlockPos} into {@link NBTTagCompound} tag
     */
    public static void saveBlockPos(String key, NBTTagCompound tag, BlockPos pos)
    {
        tag.setInteger(key + "X", pos.getX());
        tag.setInteger(key + "Y", pos.getY());
        tag.setInteger(key + "Z", pos.getZ());
    }

    /**
     * Get {@link BlockPos} position from {@link NBTTagCompound} tag
     */
    public static BlockPos getBlockPos(String key, NBTTagCompound tag)
    {
        String x = key + "X";
        String y = key + "Y";
        String z = key + "Z";

        if (tag == null || !tag.hasKey(x) || !tag.hasKey(y) || !tag.hasKey(z))
        {
            return null;
        }

        return new BlockPos(tag.getInteger(x), tag.getInteger(y), tag.getInteger(z));
    }
}