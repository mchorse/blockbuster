package mchorse.blockbuster.network.server.director;

import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.director.PacketDirectorCast;
import mchorse.blockbuster.network.common.director.PacketDirectorRequestCast;
import mchorse.blockbuster.network.server.ServerMessageHandler;
import mchorse.blockbuster.tileentity.TileEntityDirector;
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

        Dispatcher.sendTo(new PacketDirectorCast(message.pos, tile.actors), player);
    }
}