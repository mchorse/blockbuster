package mchorse.blockbuster.network.server.recording;

import mchorse.blockbuster.CommonProxy;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.recording.PacketFramesOverwrite;
import mchorse.blockbuster.recording.RecordUtils;
import mchorse.blockbuster.recording.data.Frame;
import mchorse.blockbuster.recording.data.Record;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.network.ServerMessageHandler;
import mchorse.mclib.network.mclib.client.ClientHandlerAnswer;
import mchorse.mclib.network.mclib.common.PacketStatusAnswer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

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
        IKey answer = null;
        boolean status = false;

        try
        {
            targetRecord = CommonProxy.manager.get(packet.filename);

            if (targetRecord == null)
            {
                this.sendAnswer(packet, entityPlayerMP, IKey.format("blockbuster.error.recording.not_found", packet.filename), false);

                return;
            }
        }
        catch (Exception e)
        {
            this.sendAnswer(packet, entityPlayerMP, IKey.lang("blockbuster.gui.director.rotation_filter.record_save_error"), false);

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

                    status = true;
                    answer = IKey.lang("blockbuster.gui.director.rotation_filter.success");
                }
                catch (IOException e)
                {
                    status = false;
                    answer = IKey.lang("blockbuster.gui.director.rotation_filter.record_save_error");

                    e.printStackTrace();
                }

                overwriteQueue.remove(key);
            }
        }
        else
        {
            status = false;
            answer = IKey.lang("blockbuster.gui.director.rotation_filter.frame_chunk_error");

            overwriteQueue.remove(key);
        }

        if (answer != null)
        {
            this.sendAnswer(packet, entityPlayerMP, answer, status);
        }
    }

    private void sendAnswer(PacketFramesOverwrite packet, EntityPlayerMP player, IKey message, boolean status)
    {
        if (packet.requiresAnswer())
        {
            PacketStatusAnswer answer = packet.getAnswer(new Object[]{message, status});

            mchorse.mclib.network.mclib.Dispatcher.sendTo(answer, player);
        }
    }

    @SideOnly(Side.CLIENT)
    public static void sendFramesToServer(String filename, List<Frame> frames, int from, int to)
    {
        sendFramesToServer(filename, frames, from, to, null);
    }

    /**
     *
     * @param filename
     * @param frames
     * @param from
     * @param to
     * @param callback the callback that should be called when the answer from the server returns
     */
    @SideOnly(Side.CLIENT)
    public static void sendFramesToServer(String filename, List<Frame> frames, int from, int to, @Nullable Consumer<Object[]> callback)
    {
        int cap = 400;

        int callbackID = callback != null ? ClientHandlerAnswer.registerConsumer(callback) : -1;

        if (frames.size() <= cap)
        {
            PacketFramesOverwrite packet = callbackID != -1 ? new PacketFramesOverwrite(from, to, 0, filename, frames, callbackID) :
                                            new PacketFramesOverwrite(from, to, 0, filename, frames);

            Dispatcher.sendToServer(packet);

            return;
        }

        List<Frame> chunk = new ArrayList<>();
        int chunkStart = 0;

        for (int i = 0; i < frames.size(); i++)
        {
            chunk.add(frames.get(i));

            if (chunk.size() == cap || i == frames.size() - 1)
            {
                PacketFramesOverwrite packet = callbackID != -1 ? new PacketFramesOverwrite(from, to, chunkStart, filename, chunk, callbackID) :
                                                new PacketFramesOverwrite(from, to, chunkStart, filename, chunk);

                Dispatcher.sendToServer(packet);

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

                /* if the part in frames only contains nulls insert chunk */
                if (i == targetIndex + chunk.size())
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
