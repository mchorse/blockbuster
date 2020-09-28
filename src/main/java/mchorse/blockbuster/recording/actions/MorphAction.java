package mchorse.blockbuster.recording.actions;

import io.netty.buffer.ByteBuf;
import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.metamorph.api.MorphAPI;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.MorphUtils;
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
    public void apply(EntityLivingBase actor)
    {
        AbstractMorph morph = MorphUtils.copy(this.morph);

        if (actor instanceof EntityPlayer)
        {
            MorphAPI.morph((EntityPlayer) actor, morph, true);
        }
        else if (actor instanceof EntityActor)
        {
            EntityActor act = (EntityActor) actor;

            act.morph(morph);
            act.notifyPlayers();
        }
    }

    public void applyWithOffset(EntityLivingBase actor, int offset, AbstractMorph previous, int previousOffset)
    {
        AbstractMorph morph = MorphUtils.copy(this.morph);

        /* Sorry, fake players can't be synced */
        if (actor instanceof EntityPlayer)
        {
            MorphAPI.morph((EntityPlayer) actor, morph, true);
        }
        else if (actor instanceof EntityActor)
        {
            EntityActor act = (EntityActor) actor;

            if (act.world.isRemote)
            {
                act.applyPause(MorphUtils.copy(morph), offset, MorphUtils.copy(previous), previousOffset);
            }
            else
            {
                act.morphPause(morph, offset, previous, previousOffset);
                act.notifyPlayers();
            }
        }
    }

    @Override
    public void fromBuf(ByteBuf buf)
    {
        super.fromBuf(buf);

        this.morph = MorphUtils.morphFromBuf(buf);
    }

    @Override
    public void toBuf(ByteBuf buf)
    {
        super.toBuf(buf);

        MorphUtils.morphToBuf(buf, this.morph);
    }

    @Override
    public void fromNBT(NBTTagCompound tag)
    {
        this.morph = MorphManager.INSTANCE.morphFromNBT(tag.getCompoundTag("Morph"));
    }

    @Override
    public void toNBT(NBTTagCompound tag)
    {
        if (this.morph != null)
        {
            tag.setTag("Morph", this.morph.toNBT());
        }
    }

    @Override
    public boolean isSafe()
    {
        return true;
    }
}