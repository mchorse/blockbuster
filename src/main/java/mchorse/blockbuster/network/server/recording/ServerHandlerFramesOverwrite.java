package mchorse.blockbuster.network.server.recording;

import mchorse.blockbuster.CommonProxy;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.recording.PacketFramesOverwrite;
import mchorse.blockbuster.recording.RecordUtils;
import mchorse.blockbuster.recording.data.Frame;
import mchorse.blockbuster.recording.data.Record;
import mchorse.mclib.network.ServerMessageHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerHandlerFramesOverwrite extends ServerMessageHandler<PacketFramesOverwrite>
{
    /**
     * In case many people on a server want to overwrite the same record - avoid collision with packets
     */
    private static Map<FramesOverwrite, List<Frame>> overwriteQueue = new HashMap<>();

    @Override
    public void run(EntityPlayerMP entityPlayerMP, PacketFramesOverwrite packet)
    {
        Record targetRecord;

        try
        {
            targetRecord = CommonProxy.manager.get(packet.filename);

            if (targetRecord == null)
            {
                return;
            }
        }
        catch (Exception e)
        {
            return;
        }

        FramesOverwrite key = null;

        FramesOverwrite targetKey = new FramesOverwrite(packet.getFrom(), packet.getTo(), packet.filename);

        for (Map.Entry<FramesOverwrite, List<Frame>> entry : overwriteQueue.entrySet())
        {
            if (entry.getKey().equals(targetKey))
            {
                key = entry.getKey();

                break;
            }
        }

        if (key == null)
        {
            key = targetKey;

            overwriteQueue.put(key, new ArrayList<>());
        }

        List<Frame> frames = overwriteQueue.get(key);

        if (this.insertChunk(packet.frames, packet.getIndex(), frames))
        {
            if (frames.size() == (key.to - key.from) + 1 && !frames.contains(null))
            {
                for (int i = key.from; i <= key.to; i++)
                {
                    targetRecord.frames.set(i, frames.get(i - key.from));
                }

                try
                {
                    RecordUtils.saveRecord(targetRecord);
                }
                catch (IOException e)
                {
                    System.out.println("Error while saving Record.");

                    e.printStackTrace();
                }

                overwriteQueue.remove(key);
            }
        }
        else
        {
            System.out.println("Frames overwrite error. The received frames chunk cannot be inserted properly.");
            System.out.println("Error with overwrite task for file " + key.filename + " from tick " + key.from + " to tick " + key.to + ".");
            System.out.println("The respective queued frame chunks have been cleared.");

            overwriteQueue.remove(key);
        }
    }

    @SideOnly(Side.CLIENT)
    public static void sendFramesToServer(String filename, List<Frame> frames, int from, int to)
    {
        int cap = 400;

        if (frames.size() <= cap)
        {
            Dispatcher.sendToServer(new PacketFramesOverwrite(from, to, 0, filename, frames));

            return;
        }

        List<Frame> chunk = new ArrayList<>();
        int chunkStart = 0;

        for (int i = 0; i < frames.size(); i++)
        {
            chunk.add(frames.get(i));

            if (chunk.size() == cap)
            {
                Dispatcher.sendToServer(new PacketFramesOverwrite(from, to, chunkStart, filename, chunk));

                chunk.clear();

                chunkStart += cap;
            }
        }
    }

    protected boolean insertChunk(List<Frame> chunk, int targetIndex, List<Frame> frames)
    {
        /*
         * in case packets got shuffled on the way to the server,
         * check how to insert the chunk properly
         */
        if (targetIndex > frames.size())
        {
            Frame[] nulls = new Frame[targetIndex - frames.size()];

            frames.addAll(Arrays.asList(nulls));
            frames.addAll(chunk);
        }
        else if (targetIndex == frames.size())
        {
            frames.addAll(chunk);
        }
        else
        {
            if (frames.get(targetIndex) == null)
            {
                int i = targetIndex;

                while (i < frames.size() && i < targetIndex + chunk.size())
                {
                    if (frames.get(i) != null)
                    {
                        break;
                    }

                    i++;
                }

                if (i == targetIndex + chunk.size() - 1)
                {
                    for (int j = targetIndex; j < targetIndex + chunk.size(); j++)
                    {
                        frames.set(j, chunk.get(j - targetIndex));
                    }
                }
                else
                {
                    return false;
                }
            }
            else
            {
                return false;
            }
        }

        return true;
    }

    private static class FramesOverwrite
    {
        private int from;
        private int to;
        private String filename;

        public FramesOverwrite(int from, int to, String filename)
        {
            this.from = from;
            this.to = to;
            this.filename = filename;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (obj instanceof FramesOverwrite)
            {
                FramesOverwrite framesOverwrite = (FramesOverwrite) obj;

                return framesOverwrite.filename.equals(this.filename)
                        && framesOverwrite.from == this.from
                        && framesOverwrite.to == this.to;
            }

            return false;
        }
    }
}
