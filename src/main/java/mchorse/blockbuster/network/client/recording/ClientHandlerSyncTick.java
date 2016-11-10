package mchorse.blockbuster.network.client.recording;

import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.network.client.ClientMessageHandler;
import mchorse.blockbuster.network.common.recording.PacketSyncTick;
import mchorse.blockbuster.recording.RecordPlayer;
import net.minecraft.client.entity.EntityPlayerSP;

/**
 * Client handler synchronize ticks.
 *
 * This client handler is responsible for synchronizing client's ticks.
 */
public class ClientHandlerSyncTick extends ClientMessageHandler<PacketSyncTick>
{
    @Override
    public void run(EntityPlayerSP player, PacketSyncTick message)
    {
        EntityActor actor = (EntityActor) player.worldObj.getEntityByID(message.id);
        RecordPlayer playback = actor.playback;

        if (playback != null)
        {
            playback.tick = message.tick;
            playback.delay = playback.record != null ? playback.delay : playback.recordDelay;
        }
    }
}