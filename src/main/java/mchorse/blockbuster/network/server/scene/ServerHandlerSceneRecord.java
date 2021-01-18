package mchorse.blockbuster.network.server.scene;

import mchorse.blockbuster.CommonProxy;
import mchorse.blockbuster.network.common.scene.PacketSceneRecord;
import mchorse.mclib.network.ServerMessageHandler;
import net.minecraft.entity.player.EntityPlayerMP;

public class ServerHandlerSceneRecord extends ServerMessageHandler<PacketSceneRecord>
{
    @Override
    public void run(EntityPlayerMP player, PacketSceneRecord message)
    {
        if (message.location.isEmpty())
        {
            return;
        }

        CommonProxy.scenes.record(message.location.getFilename(), message.record, message.offset, player);
    }
}