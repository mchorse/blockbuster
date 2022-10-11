package mchorse.blockbuster.recording;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.CommonProxy;
import mchorse.blockbuster.capabilities.recording.IRecording;
import mchorse.blockbuster.capabilities.recording.Recording;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.recording.PacketApplyFrame;
import mchorse.blockbuster.network.common.recording.PacketFramesLoad;
import mchorse.blockbuster.network.common.recording.PacketRequestedFrames;
import mchorse.blockbuster.network.common.recording.PacketUnloadFrames;
import mchorse.blockbuster.recording.data.Frame;
import mchorse.blockbuster.recording.data.Record;
import mchorse.mclib.utils.ForgeUtils;
import mchorse.mclib.utils.MathUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.DimensionManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Utilities methods mostly to be used with recording code. Stuff like
 * broadcasting a message and sending records to players are located here.
 */
public class RecordUtils
{
    /**
     * String version of {@link #broadcastMessage(ITextComponent)}
     */
    public static void broadcastMessage(String message)
    {
        broadcastMessage(new TextComponentString(message));
    }

    /**
     * I18n formatting version of {@link #broadcastMessage(ITextComponent)}
     */
    public static void broadcastMessage(String string, Object... args)
    {
        broadcastMessage(new TextComponentTranslation(string, args));
    }

    /**
     * Send given message to everyone on the server, to everyone.
     *
     * Invoke this method only on the server side.
     */
    public static void broadcastMessage(ITextComponent message)
    {
        for (EntityPlayerMP player : ForgeUtils.getServerPlayers())
        {
            player.sendMessage(message);
        }
    }

    /**
     * Send given error to everyone on the server, to everyone.
     *
     * Invoke this method only on the server side.
     */
    public static void broadcastError(String string, Object... objects)
    {
        for (EntityPlayerMP player : ForgeUtils.getServerPlayers())
        {
            Blockbuster.l10n.error(player, string, objects);
        }
    }

    /**
     * Send given error to everyone on the server, to everyone.
     *
     * Invoke this method only on the server side.
     */
    public static void broadcastInfo(String string, Object... objects)
    {
        for (EntityPlayerMP player : ForgeUtils.getServerPlayers())
        {
            Blockbuster.l10n.info(player, string, objects);
        }
    }

    /**
     * Checks whether player recording exists
     */
    public static boolean isReplayExists(String filename)
    {
        return replayFile(filename).exists() || CommonProxy.manager.records.containsKey(filename);
    }

    /**
     * Get path to replay file (located in current world save's folder)
     */
    public static File replayFile(String filename)
    {
        return Utils.serverFile("blockbuster/records", filename);
    }

    /**
     * This method gets a record that has been saved in the mod's jar file
     * @param filename
     * @return An {@link InputStream} object or null if no resource with this name is found
     */
    public static InputStream getLocalReplay(String filename)
    {
        return RecordUtils.class.getResourceAsStream("/assets/blockbuster/records/" + filename + ".dat");
    }

    /**
     * Get list of all available replays
     */
    public static List<String> getReplays()
    {
        return Utils.serverFiles("blockbuster/records");
    }

    /**
     * Get list of all available replays
     */
    public static List<String> getReplayIterations(String replay)
    {
        List<String> list = new ArrayList<String>();
        File replays = new File(DimensionManager.getCurrentSaveRootDirectory() + "/blockbuster/records");
        File[] files = replays.listFiles();

        if (files == null)
        {
            return list;
        }

        for (File file : files)
        {
            String name = file.getName();

            if (file.isFile() && name.startsWith(replay) && name.contains(".dat~"))
            {
                list.add(name.substring(name.indexOf("~") + 1));
            }
        }

        return list;
    }

    /**
     * Send record frames to given player from the server.
     * @param filename
     * @param player
     */
    public static void sendRecordTo(String filename, EntityPlayerMP player)
    {
        sendRecordTo(filename, player, -1);
    }

    /**
     * Send record frames to given player from the server.
     * @param filename
     * @param player
     * @param callbackID the id of the callback that should be executed on the client.
     *                   -1 if no callback was created or should be executed.
     */
    public static void sendRecordTo(String filename, EntityPlayerMP player, int callbackID)
    {
        if (!playerNeedsAction(filename, player))
        {
            PacketFramesLoad packet = callbackID == -1 ? new PacketFramesLoad(filename, PacketFramesLoad.State.NOCHANGES) :
                                      new PacketFramesLoad(filename, PacketFramesLoad.State.NOCHANGES, callbackID);

            Dispatcher.sendTo(packet, player);

            return;
        }

        RecordManager manager = CommonProxy.manager;
        Record record = manager.records.get(filename);

        if (record == null)
        {
            try
            {
                record = new Record(filename);
                record.load(replayFile(filename));

                manager.records.put(filename, record);
            }
            catch (FileNotFoundException e)
            {
                Blockbuster.l10n.error(player, "recording.not_found", filename);
                record = null;
            }
            catch (Exception e)
            {
                Blockbuster.l10n.error(player, "recording.read", filename);
                e.printStackTrace();
                record = null;
            }
        }

        if (record != null)
        {
            record.resetUnload();

            PacketFramesLoad packet = callbackID == -1 ? new PacketFramesLoad(filename, record.preDelay, record.postDelay, record.frames) :
                                      new PacketFramesLoad(filename, record.preDelay, record.postDelay, record.frames, callbackID);

            Dispatcher.sendTo(packet, player);
            System.out.println("Sent " + filename + " to " + player.getName());
        }
        else
        {
            PacketFramesLoad packet = callbackID == -1 ? new PacketFramesLoad(filename, PacketFramesLoad.State.ERROR) :
                                      new PacketFramesLoad(filename, PacketFramesLoad.State.ERROR, callbackID);

            Dispatcher.sendTo(packet, player);
        }
    }

    /**
     * Send requested frames (for actor) to given player (from the server)
     */
    public static void sendRequestedRecord(int id, String filename, EntityPlayerMP player)
    {
        Record record = CommonProxy.manager.records.get(filename);

        if (playerNeedsAction(filename, player) && record != null)
        {
            record.resetUnload();

            Dispatcher.sendTo(new PacketRequestedFrames(id, record.filename, record.preDelay, record.postDelay, record.frames), player);
            System.out.println("Sent " + filename + " to " + player.getName() + " with " + id);
        }
        else if (record == null)
        {
            Blockbuster.l10n.error(player, "recording.not_found", filename);
        }
    }

    /**
     * Checks whether given player needs a new action, meaning, he has an older
     * version of given named action or he doesn't have this action at all.
     */
    private static boolean playerNeedsAction(String filename, EntityPlayer player)
    {
        if (RecordUtils.getLocalReplay(filename) != null)
        {
            return false;
        }

        IRecording recording = Recording.get(player);

        if (recording.isFakePlayer())
        {
            return false;
        }

        boolean has = recording.hasRecording(filename);
        long time = replayFile(filename).lastModified();

        if (has && time > recording.recordingTimestamp(filename))
        {
            recording.updateRecordingTimestamp(filename, time);

            return true;
        }

        if (!has)
        {
            recording.addRecording(filename, time);
        }

        return !has;
    }

    /**
     * Unload given record. It will send to all players a packet to unload a
     * record.
     */
    public static void unloadRecord(Record record)
    {
        String filename = record.filename;

        for (EntityPlayerMP player : ForgeUtils.getServerPlayers())
        {
             IRecording recording = Recording.get(player);

            if (recording.hasRecording(filename))
            {
                recording.removeRecording(filename);

                Dispatcher.sendTo(new PacketUnloadFrames(filename), player);
            }
        }
    }

    /* records are saved on the server side */

    public static void saveRecord(Record record) throws IOException
    {
        saveRecord(record, true);
    }

    public static void saveRecord(Record record, boolean unload) throws IOException
    {
        saveRecord(record, true, unload);
    }

    public static void saveRecord(Record record, boolean savePast, boolean unload) throws IOException
    {
        record.dirty = false;
        record.save(replayFile(record.filename), savePast);

        if (unload)
        {
            unloadRecord(record);
        }
    }

    public static void dirtyRecord(Record record)
    {
        record.dirty = true;

        unloadRecord(record);
    }

    /**
     * This method filters 360 degrees flips in the given frame list in the given rotation channel.
     * It does not modify the original list but returns a new list of frame copies.
     * @param frames the frames to filter
     * @param from from tick
     * @param to to tick (this tick will also be filtered)
     * @param channel the rotation channel of the frames to filter
     * @return the filtered frames. Returns an empty list if not enough frames are present to filter.
     */
    public static List<Frame> discontinuityEulerFilter(List<Frame> frames, int from, int to, Frame.RotationChannel channel)
    {
        List<Frame> filteredFrames = new ArrayList<>();

        if (to - from + 1 < 2) return filteredFrames;

        for (int i = from; i < frames.size() && i <= to; i++)
        {
            if (i == 0)
            {
                filteredFrames.add(frames.get(i));

                continue;
            }

            Frame filteredFrame = frames.get(i).copy();
            Frame prevFrame = frames.get(i - 1);

            if (i > from)
            {
                prevFrame = filteredFrames.get(i - from - 1);
            }

            switch (channel)
            {
                case BODY_YAW:
                    float prev = (float) Math.toRadians(prevFrame.bodyYaw);
                    float current = (float) Math.toRadians(frames.get(i).bodyYaw);
                    filteredFrame.bodyYaw = (float) Math.toDegrees(MathUtils.filterFlips(prev, current));

                    break;
                case HEAD_PITCH:
                    prev = (float) Math.toRadians(prevFrame.pitch);
                    current = (float) Math.toRadians(frames.get(i).pitch);
                    filteredFrame.pitch = (float) Math.toDegrees(MathUtils.filterFlips(prev, current));

                    break;
                case HEAD_YAW:
                    /* filter both yawHead and yaw... I hope that is correct, Minecraft is weird */
                    prev = (float) Math.toRadians(prevFrame.yawHead);
                    current = (float) Math.toRadians(frames.get(i).yawHead);
                    filteredFrame.yawHead = (float) Math.toDegrees(MathUtils.filterFlips(prev, current));

                    prev = (float) Math.toRadians(prevFrame.yaw);
                    current = (float) Math.toRadians(frames.get(i).yaw);
                    filteredFrame.yaw = (float) Math.toDegrees(MathUtils.filterFlips(prev, current));

                    break;
            }

            filteredFrames.add(filteredFrame);
        }

        return filteredFrames;
    }

    /**
     * This method applies a frame at the given tick on the given entity
     * and synchronises with all players depending on which side this method has been executed on
     * @param entity the entity where the frame should be applied
     * @param record the recording with the frames
     * @param tick the tick to apply
     */
    public static void applyFrameOnEntity(EntityLivingBase entity, Record record, int tick)
    {
        tick = MathUtils.clamp(tick, 0, record.frames.size() - 1);

        Frame frame = record.frames.get(tick);

        frame.apply(entity, true);

        /* Frame does not apply bodyYaw, EntityActor.updateDistance() does... TODO refactor this*/
        entity.renderYawOffset = frame.bodyYaw;

        PacketApplyFrame packet = new PacketApplyFrame(frame, entity.getEntityId());

        if (entity.world.isRemote)
        {
            /* send to server which will also sync it with all other players */
            Dispatcher.sendToServer(packet);
        }
        else
        {
            /* already on server - sync with all players */
            for (EntityPlayerMP player : ForgeUtils.getServerPlayers())
            {
                Dispatcher.sendTo(packet, player);
            }
        }
    }
}