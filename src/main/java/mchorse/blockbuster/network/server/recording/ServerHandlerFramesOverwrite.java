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
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ServerHandlerFramesOverwrite extends ServerMessageHandler<PacketFramesOverwrite>
{
    /**
     * In case many people on a server want to overwrite the same record - avoid collision with packets.
     * The key identifies which record should have which ticks overwritten.
     * TODO If two people at the same time want to override the same ticks, this may be a problem.
     */
    private static Map<OverwriteIdentifier, List<Frame>> overwriteQueue = new HashMap<>();

    @Override
    public void run(EntityPlayerMP entityPlayerMP, PacketFramesOverwrite packet)
    {
        Record targetRecord;
        IKey answer = null;
        boolean status = false;

        if (packet.frames.isEmpty())
        {
            System.out.println("Received an empty chunk...");

            return;
        }

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

        OverwriteIdentifier key = null;

        /*
         * the constructor sorts from and to tick already.
         * It is important that from tick is always smaller than to tick, no matter what the client sends!
         */
        OverwriteIdentifier targetKey = new OverwriteIdentifier(packet.getFrom(), packet.getTo(), packet.filename);

        for (Map.Entry<OverwriteIdentifier, List<Frame>> entry : overwriteQueue.entrySet())
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
            if (frames.size() == (key.toTick - key.fromTick) + 1 && !frames.contains(null))
            {
                if (key.toTick >= targetRecord.frames.size())
                {
                    status = false;
                    answer = IKey.lang("blockbuster.gui.director.rotation_filter.record_save_error");

                    System.out.println("toTick " + key.toTick + " out of range of record frames size.");
                }
                else
                {
                    for (int i = key.fromTick; i <= key.toTick; i++)
                    {
                        targetRecord.frames.set(i, frames.get(i - key.fromTick));
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
        if (packet.getCallbackID().isPresent())
        {
            ClientHandlerAnswer.sendAnswerTo(player, packet.getAnswer(new AbstractMap.SimpleEntry<>(message, status)));
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
    public static void sendFramesToServer(String filename, List<Frame> frames, int from, int to,
                                          @Nullable Consumer<AbstractMap.SimpleEntry<IKey, Boolean>> callback)
    {
        int cap = 400;

        if (frames.size() <= cap)
        {
            if (callback != null)
            {
                ClientHandlerAnswer.requestServerAnswer(Dispatcher.DISPATCHER,
                        new PacketFramesOverwrite(from, to, 0, filename, frames), callback);
            }
            else
            {
                Dispatcher.sendToServer(new PacketFramesOverwrite(from, to, 0, filename, frames));
            }

            return;
        }

        List<Frame> chunk = new ArrayList<>();
        int chunkStart = 0;

        for (int i = 0; i < frames.size(); i++)
        {
            chunk.add(frames.get(i));

            if (chunk.size() == cap || i == frames.size() - 1)
            {
                if (callback != null)
                {
                    ClientHandlerAnswer.requestServerAnswer(Dispatcher.DISPATCHER, new PacketFramesOverwrite(from, to, chunkStart, filename, chunk), callback);
                }
                else
                {
                    Dispatcher.sendToServer(new PacketFramesOverwrite(from, to, chunkStart, filename, chunk));
                }

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
                /* the chunk doesn't fit in the slot because there are non null values */
                return false;
            }
        }

        return true;
    }

    /**
     * Identifier which record should get the specified ticks overwritten.
     */
    private static class OverwriteIdentifier
    {
        /**
         * From tick to overwrite
         */
        private int fromTick;
        /**
         * To tick (inclusive) to overwrite to
         */
        private int toTick;
        private String filename;

        /**
         * Sorts from and to
         * @param from
         * @param to
         * @param filename
         */
        public OverwriteIdentifier(int from, int to, String filename)
        {
            this.fromTick = Math.min(from, to);
            this.toTick = Math.max(from, to);
            this.filename = filename;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (obj instanceof OverwriteIdentifier)
            {
                OverwriteIdentifier framesOverwrite = (OverwriteIdentifier) obj;

                return framesOverwrite.filename.equals(this.filename)
                        && framesOverwrite.fromTick == this.fromTick
                        && framesOverwrite.toTick == this.toTick;
            }

            return false;
        }
    }
}
