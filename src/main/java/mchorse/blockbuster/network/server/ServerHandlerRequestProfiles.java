package mchorse.blockbuster.network.server;

import mchorse.aperture.camera.CameraAPI;
import mchorse.aperture.network.common.PacketCameraProfileList;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.camera.PacketRequestProfiles;
import net.minecraft.entity.player.EntityPlayerMP;

public class ServerHandlerRequestProfiles extends ServerMessageHandler<PacketRequestProfiles>
{
    @Override
    public void run(EntityPlayerMP player, PacketRequestProfiles message)
    {
        Dispatcher.sendTo(new PacketCameraProfileList(CameraAPI.getServerProfiles()), player);
    }
}