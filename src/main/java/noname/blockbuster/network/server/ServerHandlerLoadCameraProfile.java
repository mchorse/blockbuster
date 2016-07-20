package noname.blockbuster.network.server;

import net.minecraft.entity.player.EntityPlayerMP;
import noname.blockbuster.camera.CameraUtils;
import noname.blockbuster.network.common.PacketLoadCameraProfile;

public class ServerHandlerLoadCameraProfile extends ServerMessageHandler<PacketLoadCameraProfile>
{
    @Override
    public void run(EntityPlayerMP player, PacketLoadCameraProfile message)
    {
        CameraUtils.sendProfileToPlayer(message.filename, player, false);
    }
}
