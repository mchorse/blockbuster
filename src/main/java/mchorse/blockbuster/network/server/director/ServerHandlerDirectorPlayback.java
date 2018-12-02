package mchorse.blockbuster.network.server.director;

import mchorse.blockbuster.common.tileentity.TileEntityDirector;
import mchorse.blockbuster.network.common.director.PacketDirectorPlayback;
import mchorse.mclib.network.ServerMessageHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;

public class ServerHandlerDirectorPlayback extends ServerMessageHandler<PacketDirectorPlayback>
{
    @Override
    public void run(EntityPlayerMP player, PacketDirectorPlayback message)
    {
        TileEntity te = player.world.getTileEntity(message.pos);

        if (te instanceof TileEntityDirector)
        {
            ((TileEntityDirector) te).director.togglePlayback();
        }
    }
}