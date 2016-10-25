package mchorse.blockbuster.network.server.recording;

import java.io.IOException;

import mchorse.blockbuster.common.CommonProxy;
import mchorse.blockbuster.network.common.recording.PacketFramesSave;
import mchorse.blockbuster.network.server.ServerMessageHandler;
import mchorse.blockbuster.recording.data.Record;
import net.minecraft.entity.player.EntityPlayerMP;

public class ServerHandlerFrames extends ServerMessageHandler<PacketFramesSave>
{
    @Override
    public void run(EntityPlayerMP player, PacketFramesSave message)
    {
        Record serverRecord = CommonProxy.manager.records.get(message.filename);

        if (serverRecord == null)
        {
            return;
        }

        if (serverRecord.getLength() != message.frames.size())
        {
            int server = serverRecord.getLength();
            int client = message.frames.size();

            System.out.println("Difference (s:" + server + ", c:" + client + ")");
        }

        try
        {
            serverRecord.frames = message.frames;
            serverRecord.toBytes(CommonProxy.manager.replayFile(message.filename));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}