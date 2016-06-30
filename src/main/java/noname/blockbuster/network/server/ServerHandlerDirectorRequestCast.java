package noname.blockbuster.network.server;

import net.minecraft.entity.player.EntityPlayerMP;
import noname.blockbuster.network.Dispatcher;
import noname.blockbuster.network.common.director.PacketDirectorCast;
import noname.blockbuster.network.common.director.PacketDirectorRequestCast;
import noname.blockbuster.tileentity.TileEntityDirector;

public class ServerHandlerDirectorRequestCast extends ServerMessageHandler<PacketDirectorRequestCast>
{
    @Override
    public void run(EntityPlayerMP player, PacketDirectorRequestCast message)
    {
        TileEntityDirector tile = ((TileEntityDirector) player.worldObj.getTileEntity(message.pos));

        Dispatcher.getInstance().sendTo(new PacketDirectorCast(message.pos, tile.actors, tile.cameras), player);
    }
}