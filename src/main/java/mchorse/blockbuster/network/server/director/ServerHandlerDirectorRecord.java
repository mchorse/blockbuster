package mchorse.blockbuster.network.server.director;

import mchorse.blockbuster.common.tileentity.TileEntityDirector;
import mchorse.blockbuster.network.common.director.PacketDirectorRecord;
import mchorse.mclib.network.ServerMessageHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;

public class ServerHandlerDirectorRecord extends ServerMessageHandler<PacketDirectorRecord>
{
    @Override
    public void run(EntityPlayerMP player, PacketDirectorRecord message)
    {
        TileEntity te = player.worldObj.getTileEntity(message.pos);

        if (te instanceof TileEntityDirector)
        {
            ((TileEntityDirector) te).startRecording(message.record, player);
        }
    }
}