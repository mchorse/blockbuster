package mchorse.blockbuster.network.client.recording;

import mchorse.blockbuster.common.ClientProxy;
import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.network.client.ClientMessageHandler;
import mchorse.blockbuster.network.common.recording.PacketRequestedFrames;
import mchorse.blockbuster.recording.data.Record;

public class ClientHandlerRequestedFrames extends ClientMessageHandler<PacketRequestedFrames>
{
    @Override
    public void run(net.minecraft.client.entity.EntityPlayerSP player, PacketRequestedFrames message)
    {
        Record record = new Record(message.filename);
        record.frames = message.frames;

        ClientProxy.manager.records.put(record.filename, record);

        EntityActor actor = (EntityActor) player.worldObj.getEntityByID(message.id);

        if (actor.playback != null)
        {
            actor.playback.record = record;
        }
    }
}