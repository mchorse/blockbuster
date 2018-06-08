package mchorse.blockbuster.network.server.director;

import mchorse.blockbuster.common.tileentity.TileEntityDirector;
import mchorse.blockbuster.network.common.director.PacketDirectorDuplicate;
import mchorse.blockbuster.network.server.ServerMessageHandler;
import net.minecraft.entity.player.EntityPlayerMP;

public class ServerHandlerDirectorDuplicate extends ServerMessageHandler<PacketDirectorDuplicate>
{
    @Override
    public void run(EntityPlayerMP player, PacketDirectorDuplicate message)
    {
        TileEntityDirector tile = ((TileEntityDirector) player.worldObj.getTileEntity(message.pos));

        tile.duplicate(message.index);
        tile.open(player, message.pos);
    }
}