package mchorse.blockbuster.recording.actions;

import io.netty.buffer.ByteBuf;
import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster_pack.MorphUtils;
import mchorse.metamorph.api.MorphAPI;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;

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
        AbstractMorph morph = mchorse.metamorph.api.MorphUtils.copy(this.morph);

        if (actor instanceof EntityPlayer)
        {
            MorphAPI.morph((EntityPlayer) actor, morph, true);
        }
        else if (actor instanceof EntityActor)
        {
            EntityActor act = (EntityActor) actor;

            act.morph.set(morph);
            act.notifyPlayers();
        }
    }

    @Override
    public void fromBuf(ByteBuf buf)
    {
        super.fromBuf(buf);
        this.morph = MorphUtils.morphFromNBT(ByteBufUtils.readTag(buf));
    }

    @Override
    public void toBuf(ByteBuf buf)
    {
        super.toBuf(buf);
        NBTTagCompound tag = new NBTTagCompound();

        MorphUtils.morphToNBT(tag, this.morph);
        ByteBufUtils.writeTag(buf, tag);
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

    @Override
    public boolean isSafe()
    {
        return true;
    }
}