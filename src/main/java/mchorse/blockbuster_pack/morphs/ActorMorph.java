package mchorse.blockbuster_pack.morphs;

import mchorse.metamorph.api.models.Model;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.api.morphs.CustomMorph;
import mchorse.metamorph.client.gui.utils.GuiUtils;
import mchorse.metamorph.client.model.ModelCustom;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Actor morph
 *
 * This is a simple extension of {@link CustomMorph} which adds skin property
 * for customization of a rendered skin.
 */
public class ActorMorph extends CustomMorph
{
    /**
     * Skin for custom morph
     */
    public ResourceLocation skin;

    /**
     * Make hands true!
     */
    public ActorMorph()
    {
        this.hands = true;
    }

    /**
     * Render actor morph on the screen
     *
     * This method overrides parent class method to take in account current
     * morph's skin.
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void renderOnScreen(EntityPlayer player, int x, int y, float scale, float alpha)
    {
        ModelCustom model = ModelCustom.MODELS.get(this.name);

        if (model != null)
        {
            Model data = model.model;

            if (data != null && (data.defaultTexture != null || this.skin != null))
            {
                model.pose = model.model.poses.get("standing");
                model.swingProgress = 0;

                Minecraft.getMinecraft().renderEngine.bindTexture(this.skin == null ? data.defaultTexture : this.skin);
                GuiUtils.drawModel(model, player, x, y, scale, alpha);
            }
        }
    }

    /**
     * Check whether given object equals to this object
     *
     * This method is responsible for checking whether other {@link ActorMorph}
     * has the same skin as this morph. This method plays very big role in
     * morphing and morph acquiring.
     */
    @Override
    public boolean equals(Object object)
    {
        boolean result = super.equals(object);

        if (object instanceof ActorMorph)
        {
            ActorMorph actor = (ActorMorph) object;

            if (this.skin == null && actor.skin == null)
            {
                return result;
            }
            else if (this.skin != null && actor.skin != null && actor.skin.equals(this.skin))
            {
                return result;
            }
            else
            {
                return false;
            }
        }

        return super.equals(object);
    }

    @Override
    public AbstractMorph clone()
    {
        ActorMorph morph = new ActorMorph();

        morph.name = this.name;
        morph.skin = this.skin;

        morph.abilities = this.abilities;
        morph.attack = this.attack;
        morph.action = this.action;

        morph.model = this.model;
        morph.renderer = this.renderer;

        return morph;
    }

    @Override
    public void toNBT(NBTTagCompound tag)
    {
        super.toNBT(tag);

        if (this.skin != null)
        {
            tag.setString("Skin", this.skin.toString());
        }
    }

    @Override
    public void fromNBT(NBTTagCompound tag)
    {
        super.fromNBT(tag);

        if (tag.hasKey("Skin", 8))
        {
            this.skin = new ResourceLocation(tag.getString("Skin"));
        }
    }
}