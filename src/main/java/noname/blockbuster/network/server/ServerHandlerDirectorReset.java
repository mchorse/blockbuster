package noname.blockbuster.network.server;

import net.minecraft.entity.player.EntityPlayerMP;
import noname.blockbuster.network.Dispatcher;
import noname.blockbuster.network.common.director.PacketDirectorCast;
import noname.blockbuster.network.common.director.PacketDirectorReset;
import noname.blockbuster.tileentity.TileEntityDirector;

/**
 * Server handler director reset
 *
 * This handler is responsible for reseting the director block given at position
 * received from message.
 */
public class ServerHandlerDirectorReset extends ServerMessageHandler<PacketDirectorReset>
{
    @Override
    public void run(EntityPlayerMP player, PacketDirectorReset message)
    {
        TileEntityDirector tile = (TileEntityDirector) player.worldObj.getTileEntity(message.pos);

        tile.reset();
        Dispatcher.sendTo(new PacketDirectorCast(message.pos, tile.actors), player);
    }
}