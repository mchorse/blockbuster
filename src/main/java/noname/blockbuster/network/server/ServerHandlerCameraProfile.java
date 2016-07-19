package noname.blockbuster.network.server;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayerMP;
import noname.blockbuster.camera.CameraUtils;
import noname.blockbuster.network.common.PacketCameraProfile;

public class ServerHandlerCameraProfile extends ServerMessageHandler<PacketCameraProfile>
{
    @Override
    public void run(EntityPlayerMP player, PacketCameraProfile message)
    {
        try
        {
            CameraUtils.writeCameraProfile(message.filename, message.profile);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
