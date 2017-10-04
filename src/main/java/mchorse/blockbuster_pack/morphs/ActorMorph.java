package mchorse.blockbuster_pack.morphs;

import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.utils.TextureLocation;
import mchorse.metamorph.api.EntityUtils;
import mchorse.metamorph.api.models.Model;
import mchorse.metamorph.api.models.Model.Pose;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.api.morphs.CustomMorph;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.client.gui.utils.GuiUtils;
import mchorse.metamorph.client.model.ModelCustom;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext.EntityTarget;
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
        this.settings.hands = true;
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
                if (this.pose == null)
                {
                    String poseName = EntityUtils.getPose(player, this.currentPose, this.currentPoseOnSneak);

                    this.pose = data.getPose(poseName);
                }

                model.pose = this.pose == null ? model.model.poses.get("standing") : this.pose;
                model.swingProgress = 0;

                Minecraft.getMinecraft().renderEngine.bindTexture(this.skin == null ? data.defaultTexture : this.skin);
                GuiUtils.drawModel(model, player, x, y, scale, alpha);
            }
        }
    }

    @Override
    public void updateSize(EntityLivingBase target, IMorphing cap)
    {
        String poseName = EntityUtils.getPose(target, this.currentPose, this.currentPoseOnSneak);

        if (target instanceof EntityActor)
        {
            poseName = ((EntityActor) target).isMounted ? "riding" : poseName;
        }

        this.pose = this.model.getPose(poseName);

        if (this.pose != null)
        {
            float[] pose = this.pose.size;

            this.updateSize(target, pose[0], pose[1]);
        }
    }

    @Override
    public Pose getPose(EntityLivingBase target)
    {
        String poseName = EntityUtils.getPose(target, this.currentPose, this.currentPoseOnSneak);

        if (target instanceof EntityActor)
        {
            poseName = ((EntityActor) target).isMounted ? "riding" : poseName;
        }

        return model.getPose(poseName);
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

        return result;
    }

    @Override
    public AbstractMorph clone(boolean isRemote)
    {
        ActorMorph morph = new ActorMorph();

        morph.name = this.name;
        morph.skin = this.skin;

        morph.currentPose = this.currentPose;
        morph.currentPoseOnSneak = this.currentPoseOnSneak;

        morph.settings = settings;
        morph.model = this.model;

        if (isRemote)
        {
            morph.renderer = this.renderer;
        }

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
            this.skin = new TextureLocation(tag.getString("Skin"));
        }
    }
}