package mchorse.blockbuster.network.server.scene;

import mchorse.blockbuster.CommonProxy;
import mchorse.blockbuster.common.tileentity.TileEntityDirector;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.scene.PacketSceneCast;
import mchorse.blockbuster.network.common.scene.PacketSceneRequestCast;
import mchorse.mclib.network.ServerMessageHandler;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * This handler is used to force request of the cast by the director.
 */
public class ServerHandlerSceneRequestCast extends ServerMessageHandler<PacketSceneRequestCast>
{
    @Override
    public void run(EntityPlayerMP player, PacketSceneRequestCast message)
    {
        if (message.isDirector())
        {
            TileEntityDirector tile = ((TileEntityDirector) this.getTE(player, message.pos));

            tile.open(player, message.pos);
        }
        else
        {
            try
            {
                Dispatcher.sendTo(new PacketSceneCast(CommonProxy.scenes.load(message.filename)), player);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}