package mchorse.blockbuster.network.server.director;

import mchorse.blockbuster.common.tileentity.TileEntityDirector;
import mchorse.blockbuster.network.common.director.PacketDirectorReset;
import mchorse.blockbuster.network.server.ServerMessageHandler;
import net.minecraft.entity.player.EntityPlayerMP;

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
        tile.open(player, message.pos);
    }
}