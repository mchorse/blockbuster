package mchorse.blockbuster.network.server.director;

import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.director.PacketDirectorMapCast;
import mchorse.blockbuster.network.common.director.PacketDirectorMapRemove;
import mchorse.blockbuster.network.server.ServerMessageHandler;
import mchorse.blockbuster.tileentity.TileEntityDirectorMap;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * This handler removes replay from director map block
 */
public class ServerHandlerDirectorMapRemove extends ServerMessageHandler<PacketDirectorMapRemove>
{
    @Override
    public void run(EntityPlayerMP player, PacketDirectorMapRemove message)
    {
        TileEntityDirectorMap tile = ((TileEntityDirectorMap) player.worldObj.getTileEntity(message.pos));

        tile.remove(message.id);
        Dispatcher.sendTo(new PacketDirectorMapCast(tile.getCast(), message.pos), player);
    }
}