package mchorse.blockbuster.aperture.network.server;

import mchorse.blockbuster.aperture.network.common.PacketRequestLength;
import mchorse.blockbuster.aperture.network.common.PacketSceneLength;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.recording.scene.Scene;
import mchorse.mclib.network.ServerMessageHandler;
import net.minecraft.entity.player.EntityPlayerMP;

public class ServerHandlerRequestLength extends ServerMessageHandler<PacketRequestLength>
{
    @Override
    public void run(EntityPlayerMP player, PacketRequestLength message)
    {
        Scene scene = message.get(player.world);

        if (scene != null)
        {
            Dispatcher.sendTo(new PacketSceneLength(scene.getMaxLength(), scene.audioShift), player);
        }
    }
}