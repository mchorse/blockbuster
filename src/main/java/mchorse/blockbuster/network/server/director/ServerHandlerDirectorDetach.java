package mchorse.blockbuster.network.server.director;

import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.common.tileentity.TileEntityDirector;
import mchorse.blockbuster.network.common.director.PacketDirectorDetach;
import mchorse.blockbuster.network.server.ServerMessageHandler;
import mchorse.blockbuster.utils.EntityUtils;
import net.minecraft.entity.player.EntityPlayerMP;

public class ServerHandlerDirectorDetach extends ServerMessageHandler<PacketDirectorDetach>
{
    @Override
    public void run(EntityPlayerMP player, PacketDirectorDetach message)
    {
        TileEntityDirector tile = ((TileEntityDirector) player.world.getTileEntity(message.pos));
        EntityActor actor = (EntityActor) EntityUtils.entityByUUID(player.world, tile.replays.get(message.index).actor);

        actor.directorBlock = null;
    }
}