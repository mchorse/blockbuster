package mchorse.blockbuster.network.server;

import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.network.common.recording.PacketPlayback;
import net.minecraft.entity.player.EntityPlayerMP;

public class ServerHandlerPlayback extends ServerMessageHandler<PacketPlayback>
{
    @Override
    public void run(EntityPlayerMP player, PacketPlayback message)
    {
        EntityActor actor = (EntityActor) player.worldObj.getEntityByID(message.id);

        if (message.state)
        {
            actor.playback.playing = true;
        }
    }
}
