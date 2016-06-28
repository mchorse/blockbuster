package noname.blockbuster.network.server;

import net.minecraft.entity.player.EntityPlayerMP;
import noname.blockbuster.network.Dispatcher;
import noname.blockbuster.network.common.director.PacketDirectorMapAdd;
import noname.blockbuster.network.common.director.PacketDirectorMapCast;
import noname.blockbuster.tileentity.TileEntityDirectorMap;

public class ServerHandlerDirectorMapAdd extends ServerMessageHandler<PacketDirectorMapAdd>
{
    @Override
    public void run(EntityPlayerMP player, PacketDirectorMapAdd message)
    {
        TileEntityDirectorMap tile = ((TileEntityDirectorMap) player.worldObj.getTileEntity(message.pos));

        if (tile.add(message.replay))
        {
            Dispatcher.getInstance().sendTo(new PacketDirectorMapCast(tile.getCast(), message.pos), player);
        }
    }
}