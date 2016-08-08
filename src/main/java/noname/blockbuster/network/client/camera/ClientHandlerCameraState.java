package noname.blockbuster.network.client.camera;

import net.minecraft.client.entity.EntityPlayerSP;
import noname.blockbuster.ClientProxy;
import noname.blockbuster.network.client.ClientMessageHandler;
import noname.blockbuster.network.common.camera.PacketCameraState;

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
