package mchorse.blockbuster.network.server.camera;

import mchorse.blockbuster.camera.CameraUtils;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.camera.PacketListCameraProfiles;
import mchorse.blockbuster.network.common.camera.PacketRequestCameraProfiles;
import mchorse.blockbuster.network.server.ServerMessageHandler;
import net.minecraft.entity.player.EntityPlayerMP;

public class ServerHandlerListCameraProfiles extends ServerMessageHandler<PacketRequestCameraProfiles>
{
    @Override
    public void run(EntityPlayerMP player, PacketRequestCameraProfiles message)
    {
        Dispatcher.sendTo(new PacketListCameraProfiles(CameraUtils.listProfiles()), player);
    }
}
