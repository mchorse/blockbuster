package mchorse.blockbuster.recording.actions;

import mchorse.blockbuster.common.entity.EntityActor;
import net.minecraft.util.EnumHand;

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
