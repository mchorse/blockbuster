package noname.blockbuster.network.server;

import net.minecraft.entity.player.EntityPlayerMP;
import noname.blockbuster.camera.CameraUtils;
import noname.blockbuster.network.Dispatcher;
import noname.blockbuster.network.common.PacketListCameraProfiles;
import noname.blockbuster.network.common.PacketRequestCameraProfiles;

public class ServerHandlerListCameraProfiles extends ServerMessageHandler<PacketRequestCameraProfiles>
{
    @Override
    public void run(EntityPlayerMP player, PacketRequestCameraProfiles message)
    {
        Dispatcher.sendTo(new PacketListCameraProfiles(CameraUtils.listProfiles()), player);
    }
}
