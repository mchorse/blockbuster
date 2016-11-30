package mchorse.blockbuster.network.server.director;

import mchorse.blockbuster.common.tileentity.TileEntityDirector;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.director.PacketDirectorCast;
import mchorse.blockbuster.network.common.director.PacketDirectorEdit;
import mchorse.blockbuster.network.server.ServerMessageHandler;
import net.minecraft.entity.player.EntityPlayerMP;

public class ServerHandlerDirectorEdit extends ServerMessageHandler<PacketDirectorEdit>
{
    @Override
    public void run(EntityPlayerMP player, PacketDirectorEdit message)
    {
        TileEntityDirector tile = ((TileEntityDirector) player.worldObj.getTileEntity(message.pos.x, message.pos.y, message.pos.z));

        tile.edit(message.index, message.replay);

        if (message.update)
        {
            Dispatcher.sendTo(new PacketDirectorCast(message.pos, tile.replays), player);
        }
    }
}
