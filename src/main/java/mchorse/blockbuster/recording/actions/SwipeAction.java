package mchorse.blockbuster.recording.actions;

import mchorse.blockbuster.common.entity.EntityActor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
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
        actor.worldObj.playSound((EntityPlayer) null, actor.posX, actor.posY, actor.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_WEAK, actor.getSoundCategory(), 1.0F, 1.0F);
    }
}
