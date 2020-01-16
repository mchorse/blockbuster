package mchorse.blockbuster.network.server.scene.sync;

import mchorse.blockbuster.common.tileentity.TileEntityDirector;
import mchorse.blockbuster.network.common.scene.sync.PacketSceneGoto;
import mchorse.mclib.network.ServerMessageHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;

public class ServerHandlerSceneGoto extends ServerMessageHandler<PacketSceneGoto>
{
    @Override
    public void run(EntityPlayerMP player, PacketSceneGoto message)
    {
        TileEntity tile = this.getTE(player, message.pos);

        if (tile instanceof TileEntityDirector)
        {
            ((TileEntityDirector) tile).director.goTo(message.tick, message.actions);
        }
    }
}