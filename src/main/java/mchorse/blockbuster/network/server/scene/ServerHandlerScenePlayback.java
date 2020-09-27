package mchorse.blockbuster.network.server.scene;

import mchorse.blockbuster.CommonProxy;
import mchorse.blockbuster.network.common.scene.PacketScenePlayback;
import mchorse.mclib.network.ServerMessageHandler;
import net.minecraft.entity.player.EntityPlayerMP;

public class ServerHandlerScenePlayback extends ServerMessageHandler<PacketScenePlayback>
{
    @Override
    public void run(EntityPlayerMP player, PacketScenePlayback message)
    {
        if (message.location.isEmpty())
        {
            return;
        }

        CommonProxy.scenes.toggle(message.location.getFilename(), player.world);
    }
}