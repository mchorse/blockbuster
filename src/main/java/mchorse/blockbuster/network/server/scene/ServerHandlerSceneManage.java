package mchorse.blockbuster.network.server.scene;

import mchorse.blockbuster.CommonProxy;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.scene.PacketSceneManage;
import mchorse.mclib.network.ServerMessageHandler;
import net.minecraft.entity.player.EntityPlayerMP;

public class ServerHandlerSceneManage extends ServerMessageHandler<PacketSceneManage>
{
    @Override
    public void run(EntityPlayerMP entityPlayerMP, PacketSceneManage packetSceneManage)
    {
        if (packetSceneManage.action == PacketSceneManage.RENAME && CommonProxy.scenes.rename(packetSceneManage.source, packetSceneManage.destination))
        {
            Dispatcher.sendTo(packetSceneManage, entityPlayerMP);
        }
        else if (packetSceneManage.action == PacketSceneManage.REMOVE && CommonProxy.scenes.remove(packetSceneManage.source))
        {
            Dispatcher.sendTo(packetSceneManage, entityPlayerMP);
        }
    }
}