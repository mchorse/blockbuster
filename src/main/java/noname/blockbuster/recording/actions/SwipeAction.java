package noname.blockbuster.recording.actions;

import net.minecraft.util.EnumHand;
import noname.blockbuster.entity.EntityActor;

/**
 * Swipe action
 *
 * Swipes actor's hand.
 */
public class SwipeAction extends Action
{
    public SwipeAction()
    {}

    @Override
    public byte getType()
    {
        return Action.SWIPE;
    }

    @Override
    public void apply(EntityActor actor)
    {
        actor.swingArm(EnumHand.MAIN_HAND);
    }
}
