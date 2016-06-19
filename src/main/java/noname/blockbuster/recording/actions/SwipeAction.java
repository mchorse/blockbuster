package noname.blockbuster.recording.actions;

import net.minecraft.util.EnumHand;
import noname.blockbuster.entity.ActorEntity;

/**
 * Swipe action
 *
 * Swipes actor's hand.
 */
public class SwipeAction extends Action
{
    public SwipeAction()
    {
        super(Action.SWIPE);
    }

    @Override
    public void apply(ActorEntity actor)
    {
        actor.swingArm(EnumHand.MAIN_HAND);
    }
}
