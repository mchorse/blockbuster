package mchorse.blockbuster.capabilities.morphing;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

/**
 * Default implementation of {@link IMorphing} interface.
 *
 * This class is responsible for storing current morphing, setting and retrieval
 * of current morphing.
 */
public class Morphing implements IMorphing
{
    private String model = "";
    private ResourceLocation skin;

    public static IMorphing get(EntityPlayer player)
    {
        return player.getCapability(MorphingProvider.MORPHING, null);
    }

    @Override
    public String getModel()
    {
        return this.model;
    }

    @Override
    public ResourceLocation getSkin()
    {
        return this.skin;
    }

    @Override
    public void reset()
    {
        this.setModel("");
        this.setSkin(null);
    }

    @Override
    public void setModel(String newModel)
    {
        this.model = newModel;
    }

    @Override
    public void setSkin(ResourceLocation newSkin)
    {
        this.skin = newSkin;
    }
}
