package noname.blockbuster.network.server;

import net.minecraft.entity.player.EntityPlayerMP;
import noname.blockbuster.network.Dispatcher;
import noname.blockbuster.network.common.director.PacketDirectorMapCast;
import noname.blockbuster.network.common.director.PacketDirectorMapRemove;
import noname.blockbuster.tileentity.TileEntityDirectorMap;

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