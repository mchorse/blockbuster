package mchorse.blockbuster.network.server.director.sync;

import mchorse.blockbuster.common.tileentity.TileEntityDirector;
import mchorse.blockbuster.network.common.director.sync.PacketDirectorGoto;
import mchorse.blockbuster.network.server.ServerMessageHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;

public class ServerHandlerDirectorGoto extends ServerMessageHandler<PacketDirectorGoto>
{
    @Override
    public void run(EntityPlayerMP player, PacketDirectorGoto message)
    {
        TileEntity tile = player.worldObj.getTileEntity(message.pos);

        if (tile instanceof TileEntityDirector)
        {
            ((TileEntityDirector) tile).goTo(message.tick, message.actions);
        }
    }
}