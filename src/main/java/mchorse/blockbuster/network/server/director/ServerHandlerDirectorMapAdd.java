package mchorse.blockbuster.network.server.director;

import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.director.PacketDirectorMapAdd;
import mchorse.blockbuster.network.common.director.PacketDirectorMapCast;
import mchorse.blockbuster.network.server.ServerMessageHandler;
import mchorse.blockbuster.tileentity.TileEntityDirectorMap;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * Server handle director map add cast member
 *
 * This handler responsible for adding a cast member (a replay of actor) to
 * the director map block
 */
public class ServerHandlerDirectorMapAdd extends ServerMessageHandler<PacketDirectorMapAdd>
{
    @Override
    public void run(EntityPlayerMP player, PacketDirectorMapAdd message)
    {
        TileEntityDirectorMap tile = ((TileEntityDirectorMap) player.worldObj.getTileEntity(message.pos));

        if (tile.add(message.replay))
        {
            Dispatcher.sendTo(new PacketDirectorMapCast(tile.getCast(), message.pos), player);
        }
    }
}