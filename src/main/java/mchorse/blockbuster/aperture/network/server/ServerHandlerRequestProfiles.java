package mchorse.blockbuster.aperture.network.server;

import mchorse.aperture.camera.CameraAPI;
import mchorse.aperture.network.common.PacketCameraProfileList;
import mchorse.blockbuster.aperture.network.common.PacketRequestProfiles;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.server.ServerMessageHandler;
import net.minecraft.entity.player.EntityPlayerMP;

public class ServerHandlerRequestProfiles extends ServerMessageHandler<PacketRequestProfiles>
{
    @Override
    public void run(EntityPlayerMP player, PacketRequestProfiles message)
    {
        Dispatcher.sendTo(new PacketCameraProfileList(CameraAPI.getServerProfiles()), player);
    }
}