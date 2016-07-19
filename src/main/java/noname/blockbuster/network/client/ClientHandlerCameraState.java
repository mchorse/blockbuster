package noname.blockbuster.network.client;

import net.minecraft.client.entity.EntityPlayerSP;
import noname.blockbuster.ClientProxy;
import noname.blockbuster.network.common.PacketCameraState;

public class ClientHandlerCameraState extends ClientMessageHandler<PacketCameraState>
{
    @Override
    public void run(EntityPlayerSP player, PacketCameraState message)
    {
        if (message.toPlay)
        {
            ClientProxy.profileRunner.start();
        }
        else
        {
            ClientProxy.profileRunner.stop();
        }
    }
}
