package mchorse.blockbuster.network.server.camera;

import mchorse.blockbuster.camera.CameraUtils;
import mchorse.blockbuster.network.common.camera.PacketCameraProfile;
import mchorse.blockbuster.network.server.ServerMessageHandler;
import mchorse.blockbuster.utils.L10n;
import net.minecraft.entity.player.EntityPlayerMP;

public class ServerHandlerCameraProfile extends ServerMessageHandler<PacketCameraProfile>
{
    @Override
    public void run(EntityPlayerMP player, PacketCameraProfile message)
    {
        if (CameraUtils.saveCameraProfile(message.filename, message.profile, player))
        {
            L10n.success(player, "profile.save", message.filename);
        }
    }
}