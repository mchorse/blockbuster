package mchorse.blockbuster.network.server.camera;

import mchorse.blockbuster.camera.CameraUtils;
import mchorse.blockbuster.network.common.camera.PacketLoadCameraProfile;
import mchorse.blockbuster.network.server.ServerMessageHandler;
import net.minecraft.entity.player.EntityPlayerMP;

public class ServerHandlerLoadCameraProfile extends ServerMessageHandler<PacketLoadCameraProfile>
{
    @Override
    public void run(EntityPlayerMP player, PacketLoadCameraProfile message)
    {
        CameraUtils.sendProfileToPlayer(message.filename, player, false);
    }
}
