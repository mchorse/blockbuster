package mchorse.blockbuster.network.client.recording;

import mchorse.blockbuster.common.ClientProxy;
import mchorse.blockbuster.network.client.ClientMessageHandler;
import mchorse.blockbuster.network.common.recording.PacketUnloadRecordings;
import net.minecraft.client.entity.EntityPlayerSP;

public class ClientHandlerUnloadRecordings extends ClientMessageHandler<PacketUnloadRecordings>
{
    @Override
    public void run(EntityPlayerSP player, PacketUnloadRecordings message)
    {
        ClientProxy.manager.records.clear();
    }
}