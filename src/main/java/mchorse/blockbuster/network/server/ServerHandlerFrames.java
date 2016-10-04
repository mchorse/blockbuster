package mchorse.blockbuster.network.server;

import java.io.File;
import java.io.IOException;

import mchorse.blockbuster.common.CommonProxy;
import mchorse.blockbuster.network.common.recording.PacketFramesSave;
import mchorse.blockbuster.recording.data.Record;
import net.minecraft.entity.player.EntityPlayerMP;

public class ServerHandlerFrames extends ServerMessageHandler<PacketFramesSave>
{
    @Override
    public void run(EntityPlayerMP player, PacketFramesSave message)
    {
        Record serverRecord = CommonProxy.manager.save.get(message.filename);

        if (serverRecord == null)
        {
            return;
        }

        if (serverRecord.getLength() != message.frames.size())
        {
            System.out.println(serverRecord.getLength() + " :server - client: " + message.frames.size());
        }

        try
        {
            serverRecord.frames = message.frames;
            serverRecord.toBytes(new File(CommonProxy.manager.replayFile(message.filename)));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}