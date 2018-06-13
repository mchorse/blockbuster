package mchorse.blockbuster.network.server.director.sync;

import mchorse.blockbuster.common.tileentity.TileEntityDirector;
import mchorse.blockbuster.common.tileentity.director.Director;
import mchorse.blockbuster.network.common.director.sync.PacketDirectorPlay;
import mchorse.blockbuster.network.server.ServerMessageHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;

public class ServerHandlerDirectorPlay extends ServerMessageHandler<PacketDirectorPlay>
{
    @Override
    public void run(EntityPlayerMP player, PacketDirectorPlay message)
    {
        TileEntity te = player.world.getTileEntity(message.pos);

        if (te instanceof TileEntityDirector)
        {
            TileEntityDirector tile = (TileEntityDirector) te;
            Director director = tile.director;

            if (message.isPlay())
            {
                if (!tile.isPlaying())
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
            else if (message.isRestart())
            {
                director.stopPlayback();
                director.spawn(message.tick);
            }
        }
    }
}