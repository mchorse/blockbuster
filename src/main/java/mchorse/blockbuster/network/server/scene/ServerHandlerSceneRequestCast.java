package mchorse.blockbuster.network.server.scene;

import mchorse.blockbuster.CommonProxy;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.scene.PacketSceneCast;
import mchorse.blockbuster.network.common.scene.PacketSceneRequestCast;
import mchorse.blockbuster.recording.scene.Scene;
import mchorse.blockbuster.recording.scene.SceneLocation;
import mchorse.mclib.network.ServerMessageHandler;
import mchorse.mclib.utils.OpHelper;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * This handler is used to force request of the cast by the director.
 */
public class ServerHandlerSceneRequestCast extends ServerMessageHandler<PacketSceneRequestCast>
{
    @Override
    public void run(EntityPlayerMP player, PacketSceneRequestCast message)
    {
        if (!OpHelper.isPlayerOp(player) || message.location.isEmpty())
        {
            return;
        }

        try
        {
            Scene scene = CommonProxy.scenes.load(message.location.getFilename());

            Dispatcher.sendTo(new PacketSceneCast(new SceneLocation(scene)), player);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}