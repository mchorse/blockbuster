package mchorse.blockbuster.network.server.scene;

import mchorse.blockbuster.CommonProxy;
import mchorse.blockbuster.capabilities.recording.Recording;
import mchorse.blockbuster.network.common.scene.PacketSceneCast;
import mchorse.mclib.network.ServerMessageHandler;
import net.minecraft.entity.player.EntityPlayerMP;

public class ServerHandlerSceneCast extends ServerMessageHandler<PacketSceneCast>
{
    @Override
    public void run(EntityPlayerMP player, PacketSceneCast message)
    {
        if (message.location.isEmpty())
        {
            return;
        }

        try
        {
            CommonProxy.scenes.save(message.location.getFilename(), message.location.getScene());
            Recording.get(player).setLastScene(message.location.getFilename());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}