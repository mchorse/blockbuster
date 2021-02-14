package mchorse.blockbuster.network.server.scene;

import mchorse.blockbuster.CommonProxy;
import mchorse.blockbuster.network.common.scene.PacketSceneRecord;
import mchorse.blockbuster.recording.data.Mode;
import mchorse.mclib.network.ServerMessageHandler;
import mchorse.mclib.utils.OpHelper;
import net.minecraft.entity.player.EntityPlayerMP;

public class ServerHandlerSceneRecord extends ServerMessageHandler<PacketSceneRecord>
{
    @Override
    public void run(EntityPlayerMP player, PacketSceneRecord message)
    {
        if (!OpHelper.isPlayerOp(player))
        {
            return;
        }

        if (message.location.isScene())
        {
            CommonProxy.scenes.record(message.location.getFilename(), message.record, message.offset, player);
        }
        else
        {
            CommonProxy.manager.record(message.record, player, Mode.ACTIONS, true, true, message.offset, null);
        }
    }
}