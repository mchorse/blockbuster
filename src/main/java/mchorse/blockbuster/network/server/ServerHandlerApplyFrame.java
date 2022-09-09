package mchorse.blockbuster.network.server;

import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.recording.PacketApplyFrame;
import mchorse.mclib.network.ServerMessageHandler;
import mchorse.mclib.utils.ForgeUtils;
import mchorse.mclib.utils.OpHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;

public class ServerHandlerApplyFrame  extends ServerMessageHandler<PacketApplyFrame>
{
    @Override
    public void run(EntityPlayerMP player, PacketApplyFrame packet)
    {
        if (!OpHelper.isPlayerOp(player))
        {
            return;
        }

        Entity entity = player.world.getEntityByID(packet.getEntityID());

        if (entity instanceof EntityLivingBase)
        {
            packet.getFrame().apply((EntityLivingBase) entity, true);

            /* Frame does not apply bodyYaw, EntityActor.updateDistance() does... TODO refactor this*/
            ((EntityLivingBase) entity).renderYawOffset = packet.getFrame().bodyYaw;

            for (EntityPlayerMP target : ForgeUtils.getServerPlayers())
            {
                Dispatcher.sendTo(packet, target);
            }
        }
    }
}
