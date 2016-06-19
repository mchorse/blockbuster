package noname.blockbuster.recording.actions;

import net.minecraft.util.math.BlockPos;
import noname.blockbuster.entity.ActorEntity;

/**
 * Breaking block action
 *
 * Actor breaks the block
 */
public class BreakBlockAction extends InteractBlockAction
{
    public BreakBlockAction()
    {
        super();
        this.type = Action.BREAK_BLOCK;
    }

    public BreakBlockAction(BlockPos pos)
    {
        super(pos);
        this.type = Action.BREAK_BLOCK;
    }

    @Override
    public void apply(ActorEntity actor)
    {
        actor.worldObj.destroyBlock(this.pos, false);
    }
}
