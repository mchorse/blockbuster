package noname.blockbuster.network.server.camera;

import net.minecraft.entity.player.EntityPlayerMP;
import noname.blockbuster.camera.CameraUtils;
import noname.blockbuster.network.common.camera.PacketLoadCameraProfile;
import noname.blockbuster.network.server.ServerMessageHandler;

public class ServerHandlerLoadCameraProfile extends ServerMessageHandler<PacketLoadCameraProfile>
{
    @Override
    public void run(EntityPlayerMP player, PacketLoadCameraProfile message)
    {
        CameraUtils.sendProfileToPlayer(message.filename, player, false);
    }
}
