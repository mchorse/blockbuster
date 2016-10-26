package mchorse.blockbuster.network.client.recording;

import mchorse.blockbuster.common.ClientProxy;
import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.client.ClientMessageHandler;
import mchorse.blockbuster.network.common.recording.PacketPlayback;
import mchorse.blockbuster.network.common.recording.PacketRequestFrames;
import mchorse.blockbuster.recording.RecordPlayer;
import mchorse.blockbuster.recording.data.Mode;
import mchorse.blockbuster.recording.data.Record;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Client handler actor playback
 *
 * This client handler is responsible for starting actor playback. There are
 * few cases to consider.
 *
 * If the message says to stop playback, it is quite simple, but for start
 * playback there are few checks required to be made.
 *
 * If record exists on the client, we'll simply create new record player and
 * request tick and delay, just in case, for synchronization purpose, but if
 * client doesn't have a record, it should request the server to provide one.
 */
public class ClientHandlerPlayback extends ClientMessageHandler<PacketPlayback>
{
    @Override
    @SideOnly(Side.CLIENT)
    public void run(EntityPlayerSP player, PacketPlayback message)
    {
        EntityActor actor = (EntityActor) player.worldObj.getEntityByID(message.id);

        if (message.state)
        {
            Record record = ClientProxy.manager.records.get(message.filename);
            actor.playback = new RecordPlayer(record, Mode.FRAMES);

            if (record != null)
            {
                /* TODO: sync tick and delay */
            }
            else
            {
                Dispatcher.sendToServer(new PacketRequestFrames(message.id, message.filename));
            }
        }
        else
        {
            actor.playback = null;
        }
    }
}