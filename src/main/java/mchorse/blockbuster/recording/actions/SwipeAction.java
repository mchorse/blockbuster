package mchorse.blockbuster.recording.actions;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.recording.RecordPlayer;
import mchorse.blockbuster.utils.EntityUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.server.SPacketAnimation;
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
    public void apply(EntityLivingBase actor)
    {
        RecordPlayer player = EntityUtils.getRecordPlayer(actor);

        actor.swingArm(EnumHand.MAIN_HAND);

        /* Hack to swing the arm for the real player */
        if (player != null && player.realPlayer)
        {
            ((EntityPlayerMP) player.actor).connection.sendPacket(new SPacketAnimation(player.actor, 0));
        }

        if (Blockbuster.actorSwishSwipe.get())
        {
            actor.world.playSound(null, actor.posX, actor.posY, actor.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_WEAK, actor.getSoundCategory(), 1.0F, 1.0F);
        }
    }

    @Override
    public boolean isSafe()
    {
        return true;
    }
}