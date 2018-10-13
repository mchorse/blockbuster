package mchorse.blockbuster.network.server.recording;

import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.network.common.recording.PacketPlayback;
import mchorse.mclib.network.ServerMessageHandler;
import net.minecraft.entity.player.EntityPlayerMP;

public class ServerHandlerPlayback extends ServerMessageHandler<PacketPlayback>
{
    @Override
    public void run(EntityPlayerMP player, PacketPlayback message)
    {
        EntityActor actor = (EntityActor) player.world.getEntityByID(message.id);

        if (actor.playback != null)
        {
            actor.playback.playing = message.state;
        }
    }
}
