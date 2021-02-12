package mchorse.blockbuster.network.server.scene;

import mchorse.blockbuster.CommonProxy;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.scene.PacketRequestScenes;
import mchorse.blockbuster.network.common.scene.PacketScenes;
import mchorse.mclib.network.ServerMessageHandler;
import net.minecraft.entity.player.EntityPlayerMP;

public class ServerHandlerRequestScenes extends ServerMessageHandler<PacketRequestScenes>
{
    @Override
    public void run(EntityPlayerMP entityPlayerMP, PacketRequestScenes packetRequestScenes)
    {
        Dispatcher.sendTo(new PacketScenes(CommonProxy.scenes.sceneFiles()), entityPlayerMP);
    }
}