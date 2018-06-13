package mchorse.blockbuster.recording.actions;

import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster_pack.MorphUtils;
import mchorse.metamorph.api.EntityUtils;
import mchorse.metamorph.api.MorphAPI;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Morph action
 *
 * This action is responsible for changing the model and skin of actor during
 * the playback. This action is submitted when player morphs with Metamorph's
 * API.
 */
public class MorphAction extends Action
{
    public AbstractMorph morph;

    public MorphAction()
    {}

    public MorphAction(AbstractMorph morph)
    {
        this.morph = morph;
    }

    @Override
    public byte getType()
    {
        return Action.MORPH;
    }

    @Override
    public void apply(EntityLivingBase actor)
    {
        AbstractMorph morph = EntityUtils.getMorph(actor);

        if (this.morph != null)
        {
            morph = morph.clone(false);
        }

        if (actor instanceof EntityPlayer)
        {
            MorphAPI.morph((EntityPlayer) actor, morph, true);
        }
        else if (actor instanceof EntityActor)
        {
            EntityActor act = (EntityActor) actor;

            act.morph = morph;
            act.notifyPlayers();
        }
    }

    @Override
    public void fromNBT(NBTTagCompound tag)
    {
        this.morph = MorphUtils.morphFromNBT(tag);
    }

    @Override
    public void toNBT(NBTTagCompound tag)
    {
        MorphUtils.morphToNBT(tag, this.morph);
    }
}