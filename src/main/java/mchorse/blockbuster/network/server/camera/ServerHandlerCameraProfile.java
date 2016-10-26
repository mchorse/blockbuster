package mchorse.blockbuster.network.server.camera;

import mchorse.blockbuster.camera.CameraUtils;
import mchorse.blockbuster.capabilities.recording.IRecording;
import mchorse.blockbuster.capabilities.recording.Recording;
import mchorse.blockbuster.network.common.camera.PacketCameraProfile;
import mchorse.blockbuster.network.server.ServerMessageHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentTranslation;

public class ServerHandlerCameraProfile extends ServerMessageHandler<PacketCameraProfile>
{
    @Override
    public void run(EntityPlayerMP player, PacketCameraProfile message)
    {
        if (CameraUtils.saveCameraProfile(message.filename, message.profile, player))
        {
            IRecording recording = Recording.get(player);

            recording.setCurrentProfile(message.filename);
            recording.setCurrentProfileTimestamp(System.currentTimeMillis());

            player.addChatMessage(new TextComponentTranslation("blockbuster.profile.save", message.filename));
        }
    }
}