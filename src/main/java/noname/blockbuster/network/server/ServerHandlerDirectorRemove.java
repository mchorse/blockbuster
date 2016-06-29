package noname.blockbuster.network.server;

import net.minecraft.entity.player.EntityPlayerMP;
import noname.blockbuster.network.Dispatcher;
import noname.blockbuster.network.common.director.PacketDirectorCast;
import noname.blockbuster.network.common.director.PacketDirectorRemove;
import noname.blockbuster.tileentity.TileEntityDirector;

public class ServerHandlerDirectorRemove extends ServerMessageHandler<PacketDirectorRemove>
{
    @Override
    public void run(EntityPlayerMP player, PacketDirectorRemove message)
    {
        TileEntityDirector tile = ((TileEntityDirector) player.worldObj.getTileEntity(message.pos));

        tile.remove(message.id, message.type);
        Dispatcher.getInstance().sendTo(new PacketDirectorCast(message.pos, tile.actors, tile.cameras), player);
    }
}