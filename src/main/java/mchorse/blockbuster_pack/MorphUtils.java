package mchorse.blockbuster_pack;

import io.netty.buffer.ByteBuf;
import mchorse.mclib.utils.resources.RLUtils;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.ByteBufUtils;

/**
 * Morphs utility methods
 *
 * This class provides static methods for saving and reading {@link AbstractMorph}
 * from different sources like {@link NBTTagCompound} and {@link ByteBuf}.
 *
 * This class also provides a method for initiating a morph from old format.
 */
public class MorphUtils
{
    /**
     * Write a morph to {@link ByteBuf}
     *
     * This method will simply write a boolean indicating whether a morph was
     * saved and morph's data.
     *
     * Important: use this method in conjunction with
     * {@link #morphFromBuf(ByteBuf)}
     */
    public static void morphToBuf(ByteBuf buffer, AbstractMorph morph)
    {
        buffer.writeBoolean(morph != null);

        if (morph != null)
        {
            NBTTagCompound tag = new NBTTagCompound();
            morph.toNBT(tag);

            ByteBufUtils.writeTag(buffer, tag);
        }
    }

    /**
     * Create a morph from {@link ByteBuf}
     *
     * This method will read a morph from {@link ByteBuf} which should contain
     * a boolean indicating whether a morph was written and the morph data.
     *
     * Important: use this method in conjunction with
     * {@link #morphToBuf(ByteBuf, AbstractMorph)}!
     */
    public static AbstractMorph morphFromBuf(ByteBuf buffer)
    {
        if (buffer.readBoolean())
        {
            return MorphManager.INSTANCE.morphFromNBT(ByteBufUtils.readTag(buffer));
        }

        return null;
    }

    /**
     * Write a morph to {@link NBTTagCompound}
     *
     * If given morph is null, the operation is canceled.
     */
    public static void morphToNBT(NBTTagCompound tag, AbstractMorph morph)
    {
        if (morph != null)
        {
            NBTTagCompound morphTag = new NBTTagCompound();
            morph.toNBT(morphTag);

            tag.setTag("Morph", morphTag);
        }
    }

    /**
     * Create a morph from {@link NBTTagCompound}
     *
     * This method is responsible for reading a morph from a NBT tag. It first
     * tries to read a morph using old model/skin format, and only after it
     * failed, it will try to read it from original morph format.
     */
    public static AbstractMorph morphFromNBT(NBTTagCompound tag)
    {
        if (tag.hasKey("Model", 8) && tag.hasKey("Skin", 8))
        {
            return morphFromModel(tag.getString("Model"), tag.getString("Skin"));
        }
        else if (tag.hasKey("Morph", 10))
        {
            return MorphManager.INSTANCE.morphFromNBT(tag.getCompoundTag("Morph"));
        }

        return null;
    }

    /**
     * Create a morph from model/skin format
     *
     * This is a legacy support code. This method will create an abstract morph
     * which is part of Blockbuster's custom models.
     */
    public static AbstractMorph morphFromModel(String model, String skin)
    {
        NBTTagCompound morph = new NBTTagCompound();
        ResourceLocation rl = RLUtils.fromString(skin, model.isEmpty() ? "steve" : model);

        morph.setString("Name", "blockbuster." + model);

        if (rl != null)
        {
            morph.setString("Skin", rl.toString());
        }

        return MorphManager.INSTANCE.morphFromNBT(morph);
    }
}