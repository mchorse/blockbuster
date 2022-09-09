package mchorse.blockbuster.network.client.recording;

import mchorse.blockbuster.network.common.recording.PacketApplyFrame;
import mchorse.mclib.network.ClientMessageHandler;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

public class ClientHandlerApplyFrame extends ClientMessageHandler<PacketApplyFrame>
{
    @Override
    public void run(EntityPlayerSP entityPlayerSP, PacketApplyFrame packetApplyFrame)
    {
        Entity entity = entityPlayerSP.world.getEntityByID(packetApplyFrame.getEntityID());

        if (entity instanceof EntityLivingBase)
        {
            packetApplyFrame.getFrame().apply((EntityLivingBase) entity, true);

            /* Frame does not apply bodyYaw, EntityActor.updateDistance() does... TODO refactor this*/
            ((EntityLivingBase) entity).renderYawOffset = packetApplyFrame.getFrame().bodyYaw;
        }
    }
}
