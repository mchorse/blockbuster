package noname.blockbuster.network.server.director;

import net.minecraft.entity.player.EntityPlayerMP;
import noname.blockbuster.network.Dispatcher;
import noname.blockbuster.network.common.director.PacketDirectorMapAdd;
import noname.blockbuster.network.common.director.PacketDirectorMapCast;
import noname.blockbuster.network.server.ServerMessageHandler;
import noname.blockbuster.tileentity.TileEntityDirectorMap;

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