package mchorse.blockbuster.recording.actions;

import mchorse.blockbuster.common.entity.EntityActor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

/**
 * Breaking block action
 *
 * Actor breaks the block
 */
public class BreakBlockAction extends InteractBlockAction
{
    public boolean drop = false;

    public BreakBlockAction()
    {}

    public BreakBlockAction(BlockPos pos, boolean drop)
    {
        super(pos);
        this.drop = drop;
    }

    @Override
    public byte getType()
    {
        return Action.BREAK_BLOCK;
    }

    @Override
    public void apply(EntityActor actor)
    {
        actor.worldObj.destroyBlock(this.pos, this.drop);
        actor.worldObj.sendBlockBreakProgress(actor.getEntityId(), this.pos, -1);
    }

    @Override
    public void fromNBT(NBTTagCompound tag)
    {
        super.fromNBT(tag);

        this.drop = tag.getBoolean("Drop");
    }

    @Override
    public void toNBT(NBTTagCompound tag)
    {
        super.toNBT(tag);

        tag.setBoolean("Drop", this.drop);
    }
}
