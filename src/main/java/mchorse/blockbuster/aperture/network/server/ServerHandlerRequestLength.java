package mchorse.blockbuster.aperture.network.server;

import mchorse.blockbuster.aperture.network.common.PacketRequestLength;
import mchorse.blockbuster.aperture.network.common.PacketSceneLength;
import mchorse.blockbuster.common.tileentity.TileEntityDirector;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.mclib.network.ServerMessageHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;

public class ServerHandlerRequestLength extends ServerMessageHandler<PacketRequestLength>
{
    @Override
    public void run(EntityPlayerMP player, PacketRequestLength message)
    {
        TileEntity tile = this.getTE(player, message.pos);

        if (tile != null && tile instanceof TileEntityDirector)
        {
            Dispatcher.sendTo(new PacketSceneLength(((TileEntityDirector) tile).director.getMaxLength()), player);
        }
    }
}