package mchorse.blockbuster.network.server.recording;

import java.io.IOException;

import mchorse.blockbuster.capabilities.recording.Recording;
import mchorse.blockbuster.common.CommonProxy;
import mchorse.blockbuster.network.common.recording.PacketFramesLoad;
import mchorse.blockbuster.network.server.ServerMessageHandler;
import mchorse.blockbuster.recording.Utils;
import mchorse.blockbuster.recording.data.Record;
import net.minecraft.entity.player.EntityPlayerMP;

public class ServerHandlerFrames extends ServerMessageHandler<PacketFramesLoad>
{
    @Override
    public void run(EntityPlayerMP player, PacketFramesLoad message)
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
            Recording.get(player).addRecording(message.filename, System.currentTimeMillis());

            serverRecord.frames = message.frames;
            serverRecord.save(Utils.replayFile(message.filename));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}