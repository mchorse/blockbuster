package mchorse.blockbuster.network.server.director;

import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.director.PacketDirectorMapCast;
import mchorse.blockbuster.network.common.director.PacketDirectorMapEdit;
import mchorse.blockbuster.network.server.ServerMessageHandler;
import mchorse.blockbuster.tileentity.TileEntityDirectorMap;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * This handler substitutes replay by given index in the director map block
 */
public class ServerHandlerDirectorMapEdit extends ServerMessageHandler<PacketDirectorMapEdit>
{
    @Override
    public void run(EntityPlayerMP player, PacketDirectorMapEdit message)
    {
        TileEntityDirectorMap tile = ((TileEntityDirectorMap) player.worldObj.getTileEntity(message.pos));

        tile.edit(message.id, message.replay);
        Dispatcher.sendTo(new PacketDirectorMapCast(tile.getCast(), message.pos), player);
    }
}
