package mchorse.blockbuster.network.client.camera;

import mchorse.blockbuster.ClientProxy;
import mchorse.blockbuster.network.client.ClientMessageHandler;
import mchorse.blockbuster.network.common.camera.PacketCameraState;
import net.minecraft.client.entity.EntityPlayerSP;

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
