package mchorse.blockbuster.network.client.recording;

import mchorse.blockbuster.common.ClientProxy;
import mchorse.blockbuster.network.client.ClientMessageHandler;
import mchorse.blockbuster.network.common.recording.PacketUnloadFrames;
import net.minecraft.client.entity.EntityPlayerSP;

public class ClientHandlerUnloadFrames extends ClientMessageHandler<PacketUnloadFrames>
{
    @Override
    public void run(EntityPlayerSP player, PacketUnloadFrames message)
    {
        ClientProxy.manager.records.remove(message.filename);
    }
}