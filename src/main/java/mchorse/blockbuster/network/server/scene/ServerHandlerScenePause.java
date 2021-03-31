package mchorse.blockbuster.network.server.scene;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.CommonProxy;
import mchorse.blockbuster.network.common.scene.PacketScenePause;
import mchorse.blockbuster.recording.scene.Scene;
import mchorse.mclib.network.ServerMessageHandler;
import mchorse.mclib.utils.OpHelper;
import net.minecraft.entity.player.EntityPlayerMP;

public class ServerHandlerScenePause extends ServerMessageHandler<PacketScenePause>
{
    @Override
    public void run(EntityPlayerMP player, PacketScenePause message)
    {
        if (!OpHelper.isPlayerOp(player))
        {
            return;
        }

        if (CommonProxy.manager.recorders.containsKey(player))
        {
            if (CommonProxy.manager.cancel(player))
            {
                Blockbuster.l10n.info(player, "action.cancel");
            }
        }
        else
        {
            Scene scene = message.get(player.world);

            if (!scene.isPlaying())
            {
                scene.resume(-1);
            }
            else
            {
                scene.pause();
            }
        }
    }
}