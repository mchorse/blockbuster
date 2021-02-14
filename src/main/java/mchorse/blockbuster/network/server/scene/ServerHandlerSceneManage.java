package mchorse.blockbuster.network.server.scene;

import mchorse.blockbuster.CommonProxy;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.scene.PacketSceneManage;
import mchorse.mclib.network.ServerMessageHandler;
import mchorse.mclib.utils.OpHelper;
import net.minecraft.entity.player.EntityPlayerMP;

public class ServerHandlerSceneManage extends ServerMessageHandler<PacketSceneManage>
{
    @Override
    public void run(EntityPlayerMP player, PacketSceneManage message)
    {
        if (!OpHelper.isPlayerOp(player))
        {
            return;
        }

        if (message.action == PacketSceneManage.RENAME && CommonProxy.scenes.rename(message.source, message.destination))
        {
            Dispatcher.sendTo(message, player);
        }
        else if (message.action == PacketSceneManage.REMOVE && CommonProxy.scenes.remove(message.source))
        {
            Dispatcher.sendTo(message, player);
        }
    }
}