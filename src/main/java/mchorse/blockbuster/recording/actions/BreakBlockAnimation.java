package mchorse.blockbuster.recording.actions;

import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.recording.WorldEventListener;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

/**
 * Breaking block animation
 *
 * This action is responsible for animating blocks which are about to be
 * broken. This action is recorded in the {@link WorldEventListener}.
 */
public class BreakBlockAnimation extends InteractBlockAction
{
    public int progress;

    public BreakBlockAnimation()
    {}

    public BreakBlockAnimation(BlockPos pos, int progress)
    {
        super(pos);
        this.progress = progress;
    }

    @Override
    public byte getType()
    {
        return Action.BREAK_ANIMATION;
    }

    @Override
    public void apply(EntityActor actor)
    {
        if (this.progress >= 10)
        {
            actor.world.destroyBlock(this.pos, true);
        }
        else
        {
            actor.world.sendBlockBreakProgress(actor.getEntityId(), this.pos, this.progress);
        }
    }

    @Override
    public void fromNBT(NBTTagCompound tag)
    {
        super.fromNBT(tag);

        this.progress = tag.getInteger("Progress");
    }

    @Override
    public void toNBT(NBTTagCompound tag)
    {
        super.toNBT(tag);

        tag.setInteger("Progress", this.progress);
    }
}