package mchorse.blockbuster.network.server.scene.sync;

import mchorse.blockbuster.network.common.scene.sync.PacketSceneGoto;
import mchorse.blockbuster.recording.scene.Scene;
import mchorse.mclib.network.ServerMessageHandler;
import mchorse.mclib.utils.OpHelper;
import net.minecraft.entity.player.EntityPlayerMP;

public class ServerHandlerSceneGoto extends ServerMessageHandler<PacketSceneGoto>
{
    @Override
    public void run(EntityPlayerMP player, PacketSceneGoto message)
    {
        if (!OpHelper.isPlayerOp(player))
        {
            return;
        }

        Scene scene = message.get(player.world);

        if (scene != null)
        {
            scene.goTo(message.tick, message.actions);
        }
    }
}