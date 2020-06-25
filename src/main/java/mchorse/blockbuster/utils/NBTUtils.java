package mchorse.blockbuster.utils;

import mchorse.blockbuster.common.GunProps;
import mchorse.blockbuster.common.item.ItemGun;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

/**
 * NBT utilities
 *
 * This class provides different method for working with NBT
 */
public class NBTUtils
{
    public static boolean saveGunProps(ItemStack stack, NBTTagCompound tag)
    {
        if (!(stack.getItem() instanceof ItemGun))
        {
            return false;
        }

        if (!stack.hasTagCompound())
        {
            stack.setTagCompound(new NBTTagCompound());
        }

        if (stack.hasTagCompound())
        {
            stack.getTagCompound().setTag("Gun", tag);

            return true;
        }

        return false;
    }

    public static GunProps getGunProps(ItemStack stack)
    {
        if (!(stack.getItem() instanceof ItemGun))
        {
            return null;
        }

        if (stack.hasTagCompound())
        {
            NBTTagCompound tag = stack.getTagCompound();

            if (tag.hasKey("Gun"))
            {
                return new GunProps(tag.getCompoundTag("Gun"));
            }
        }

        return new GunProps();
    }

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