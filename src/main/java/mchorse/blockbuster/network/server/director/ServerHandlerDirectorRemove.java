package mchorse.blockbuster.network.server.director;

import mchorse.blockbuster.common.tileentity.TileEntityDirector;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.director.PacketDirectorCast;
import mchorse.blockbuster.network.common.director.PacketDirectorRemove;
import mchorse.blockbuster.network.server.ServerMessageHandler;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * This handler is responsible for removing actor from director block.
 */
public class ServerHandlerDirectorRemove extends ServerMessageHandler<PacketDirectorRemove>
{
    @Override
    public void run(EntityPlayerMP player, PacketDirectorRemove message)
    {
        TileEntityDirector tile = ((TileEntityDirector) player.worldObj.getTileEntity(message.pos.x, message.pos.y, message.pos.z));

        tile.remove(message.id);
        Dispatcher.sendTo(new PacketDirectorCast(message.pos, tile.replays), player);
    }
}