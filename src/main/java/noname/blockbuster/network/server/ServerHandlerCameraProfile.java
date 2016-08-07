package noname.blockbuster.network.server;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentTranslation;
import noname.blockbuster.camera.CameraUtils;
import noname.blockbuster.network.common.PacketCameraProfile;

public class ServerHandlerCameraProfile extends ServerMessageHandler<PacketCameraProfile>
{
    @Override
    public void run(EntityPlayerMP player, PacketCameraProfile message)
    {
        if (CameraUtils.saveCameraProfile(message.filename, message.profile, player))
        {
            player.addChatMessage(new TextComponentTranslation("blockbuster.profile.save", message.filename));
        }
    }
}
