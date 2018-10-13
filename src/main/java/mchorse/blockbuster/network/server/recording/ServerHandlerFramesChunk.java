package mchorse.blockbuster.network.server.recording;

import java.io.IOException;

import mchorse.blockbuster.capabilities.recording.Recording;
import mchorse.blockbuster.common.CommonProxy;
import mchorse.blockbuster.network.common.recording.PacketFramesChunk;
import mchorse.blockbuster.recording.Utils;
import mchorse.blockbuster.recording.data.FrameChunk;
import mchorse.blockbuster.recording.data.Record;
import mchorse.mclib.network.ServerMessageHandler;
import net.minecraft.entity.player.EntityPlayerMP;

public class ServerHandlerFramesChunk extends ServerMessageHandler<PacketFramesChunk>
{
    @Override
    public void run(EntityPlayerMP player, PacketFramesChunk message)
    {
        Record serverRecord = CommonProxy.manager.records.get(message.filename);
        FrameChunk chunk = CommonProxy.manager.chunks.get(message.filename);

        if (serverRecord == null)
        {
            return;
        }

        if (chunk == null)
        {
            chunk = new FrameChunk(message.count);

            CommonProxy.manager.chunks.put(message.filename, chunk);
        }

        chunk.add(message.index, message.frames);

        if (chunk.isFilled())
        {
            try
            {
                Recording.get(player).addRecording(message.filename, System.currentTimeMillis());

                serverRecord.frames = chunk.compile();
                serverRecord.save(Utils.replayFile(message.filename));

                CommonProxy.manager.chunks.remove(message.filename);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}