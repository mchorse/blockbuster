package mchorse.blockbuster.network.server.director;

import mchorse.blockbuster.common.tileentity.TileEntityDirector;
import mchorse.blockbuster.network.common.director.PacketDirectorRequestCast;
import mchorse.blockbuster.network.server.ServerMessageHandler;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * This handler is used to force request of the cast by the director.
 */
public class ServerHandlerDirectorRequestCast extends ServerMessageHandler<PacketDirectorRequestCast>
{
    @Override
    public void run(EntityPlayerMP player, PacketDirectorRequestCast message)
    {
        TileEntityDirector tile = ((TileEntityDirector) player.worldObj.getTileEntity(message.pos));

        tile.open(player, message.pos);
    }
}