package noname.blockbuster.network.server.camera;

import net.minecraft.entity.player.EntityPlayerMP;
import noname.blockbuster.camera.CameraUtils;
import noname.blockbuster.network.Dispatcher;
import noname.blockbuster.network.common.camera.PacketListCameraProfiles;
import noname.blockbuster.network.common.camera.PacketRequestCameraProfiles;
import noname.blockbuster.network.server.ServerMessageHandler;

public class ServerHandlerListCameraProfiles extends ServerMessageHandler<PacketRequestCameraProfiles>
{
    @Override
    public void run(EntityPlayerMP player, PacketRequestCameraProfiles message)
    {
        Dispatcher.sendTo(new PacketListCameraProfiles(CameraUtils.listProfiles()), player);
    }
}
