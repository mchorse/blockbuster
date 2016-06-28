package noname.blockbuster.network.server;

import net.minecraft.entity.player.EntityPlayerMP;
import noname.blockbuster.network.Dispatcher;
import noname.blockbuster.network.common.director.PacketDirectorMapCast;
import noname.blockbuster.network.common.director.PacketDirectorMapEdit;
import noname.blockbuster.tileentity.TileEntityDirectorMap;

public class ServerHandlerDirectorMapEdit extends ServerMessageHandler<PacketDirectorMapEdit>
{
    @Override
    public void run(EntityPlayerMP player, PacketDirectorMapEdit message)
    {
        TileEntityDirectorMap tile = ((TileEntityDirectorMap) player.worldObj.getTileEntity(message.pos));

        tile.edit(message.id, message.replay);
        Dispatcher.getInstance().sendTo(new PacketDirectorMapCast(tile.getCast(), message.pos), player);
    }
}
