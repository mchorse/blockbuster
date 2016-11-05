package mchorse.blockbuster.network.server.camera;

import mchorse.blockbuster.capabilities.recording.IRecording;
import mchorse.blockbuster.capabilities.recording.Recording;
import mchorse.blockbuster.network.common.camera.PacketCameraReset;
import mchorse.blockbuster.network.server.ServerMessageHandler;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * Server handler camera reset
 *
 * Reset camera profile
 */
public class ServerHandlerCameraReset extends ServerMessageHandler<PacketCameraReset>
{
    @Override
    public void run(EntityPlayerMP player, PacketCameraReset message)
    {
        IRecording recording = Recording.get(player);

        recording.setCurrentProfile("");
        recording.setCurrentProfileTimestamp(-1);
    }
}