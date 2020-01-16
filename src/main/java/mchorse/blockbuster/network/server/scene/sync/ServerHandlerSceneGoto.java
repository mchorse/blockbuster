package mchorse.blockbuster.network.server.scene.sync;

import mchorse.blockbuster.network.common.scene.sync.PacketSceneGoto;
import mchorse.blockbuster.recording.director.Scene;
import mchorse.mclib.network.ServerMessageHandler;
import net.minecraft.entity.player.EntityPlayerMP;

public class ServerHandlerSceneGoto extends ServerMessageHandler<PacketSceneGoto>
{
    @Override
    public void run(EntityPlayerMP player, PacketSceneGoto message)
    {
        Scene scene = message.get(player.worldObj);

        if (scene != null)
        {
            scene.goTo(message.tick, message.actions);
        }
    }
}