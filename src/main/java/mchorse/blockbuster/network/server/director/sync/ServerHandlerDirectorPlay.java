package mchorse.blockbuster.network.server.director.sync;

import mchorse.blockbuster.common.tileentity.TileEntityDirector;
import mchorse.blockbuster.network.common.director.sync.PacketDirectorPlay;
import mchorse.blockbuster.network.server.ServerMessageHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;

public class ServerHandlerDirectorPlay extends ServerMessageHandler<PacketDirectorPlay>
{
    @Override
    public void run(EntityPlayerMP player, PacketDirectorPlay message)
    {
        TileEntity tile = player.world.getTileEntity(message.pos);

        if (tile instanceof TileEntityDirector)
        {
            TileEntityDirector director = (TileEntityDirector) tile;

            if (message.isPlay())
            {
                if (!director.isPlaying())
                {
                    director.spawn(message.tick);
                }

                director.resume(message.tick);
            }
            else if (message.isStop())
            {
                director.stopPlayback();
            }
            else if (message.isPause())
            {
                director.pause();
            }
            else if (message.isStart())
            {
                director.spawn(message.tick);
            }
        }
    }
}