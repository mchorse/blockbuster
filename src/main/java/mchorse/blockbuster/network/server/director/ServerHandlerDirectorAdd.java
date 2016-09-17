package mchorse.blockbuster.network.server.director;

import mchorse.blockbuster.common.tileentity.TileEntityDirector;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.director.PacketDirectorAdd;
import mchorse.blockbuster.network.common.director.PacketDirectorCast;
import mchorse.blockbuster.network.server.ServerMessageHandler;
import net.minecraft.entity.player.EntityPlayerMP;

public class ServerHandlerDirectorAdd extends ServerMessageHandler<PacketDirectorAdd>
{
    @Override
    public void run(EntityPlayerMP player, PacketDirectorAdd message)
    {
        TileEntityDirector tile = ((TileEntityDirector) player.worldObj.getTileEntity(message.pos));

        tile.add(message.id);
        Dispatcher.sendTo(new PacketDirectorCast(message.pos, tile.replays), player);
    }
}
