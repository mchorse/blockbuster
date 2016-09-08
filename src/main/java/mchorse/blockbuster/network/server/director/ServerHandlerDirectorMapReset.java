package mchorse.blockbuster.network.server.director;

import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.director.PacketDirectorMapCast;
import mchorse.blockbuster.network.common.director.PacketDirectorMapReset;
import mchorse.blockbuster.network.server.ServerMessageHandler;
import mchorse.blockbuster.tileentity.TileEntityDirectorMap;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * This handler resets director map block
 */
public class ServerHandlerDirectorMapReset extends ServerMessageHandler<PacketDirectorMapReset>
{
    @Override
    public void run(EntityPlayerMP player, PacketDirectorMapReset message)
    {
        TileEntityDirectorMap tile = ((TileEntityDirectorMap) player.worldObj.getTileEntity(message.pos));

        tile.reset();
        Dispatcher.sendTo(new PacketDirectorMapCast(tile.getCast(), message.pos), player);
    }
}