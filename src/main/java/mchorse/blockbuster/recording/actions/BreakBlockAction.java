package mchorse.blockbuster.recording.actions;

import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.utils.BlockPos;

/**
 * Breaking block action
 *
 * Actor breaks the block
 */
public class BreakBlockAction extends InteractBlockAction
{
    public BreakBlockAction()
    {}

    public BreakBlockAction(BlockPos pos)
    {
        super(pos);
    }

    @Override
    public byte getType()
    {
        return Action.BREAK_BLOCK;
    }

    @Override
    public void apply(EntityActor actor)
    {
        actor.worldObj.setBlock(this.pos.x, this.pos.y, this.pos.z, null);
    }
}
