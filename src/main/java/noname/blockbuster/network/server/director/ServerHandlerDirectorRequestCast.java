package noname.blockbuster.network.server.director;

import net.minecraft.entity.player.EntityPlayerMP;
import noname.blockbuster.network.Dispatcher;
import noname.blockbuster.network.common.director.PacketDirectorCast;
import noname.blockbuster.network.common.director.PacketDirectorRequestCast;
import noname.blockbuster.network.server.ServerMessageHandler;
import noname.blockbuster.tileentity.TileEntityDirector;

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