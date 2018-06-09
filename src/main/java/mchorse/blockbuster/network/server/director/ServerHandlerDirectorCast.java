package mchorse.blockbuster.network.server.director;

import mchorse.blockbuster.common.tileentity.TileEntityDirector;
import mchorse.blockbuster.network.common.director.PacketDirectorCast;
import mchorse.blockbuster.network.server.ServerMessageHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;

public class ServerHandlerDirectorCast extends ServerMessageHandler<PacketDirectorCast>
{
    @Override
    public void run(EntityPlayerMP player, PacketDirectorCast message)
    {
        TileEntity tile = player.worldObj.getTileEntity(message.pos);

        if (tile instanceof TileEntityDirector)
        {
            ((TileEntityDirector) tile).director.copy(message.director);
        }
    }
}