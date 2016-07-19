package noname.blockbuster.network.server;

import net.minecraft.entity.player.EntityPlayerMP;
import noname.blockbuster.camera.CameraProfile;
import noname.blockbuster.camera.CameraUtils;
import noname.blockbuster.network.Dispatcher;
import noname.blockbuster.network.common.PacketCameraProfile;
import noname.blockbuster.network.common.PacketLoadCameraProfile;

public class ServerHandlerLoadCameraProfile extends ServerMessageHandler<PacketLoadCameraProfile>
{
    @Override
    public void run(EntityPlayerMP player, PacketLoadCameraProfile message)
    {
        try
        {
            CameraProfile profile = CameraUtils.readCameraProfile(message.filename);
            Dispatcher.getInstance().sendTo(new PacketCameraProfile(message.filename, profile), player);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
