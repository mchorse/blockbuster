package mchorse.blockbuster.recording;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import mchorse.blockbuster.capabilities.recording.IRecording;
import mchorse.blockbuster.capabilities.recording.Recording;
import mchorse.blockbuster.common.CommonProxy;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.recording.PacketFramesLoad;
import mchorse.blockbuster.network.common.recording.PacketRequestedFrames;
import mchorse.blockbuster.network.common.recording.PacketUnloadFrames;
import mchorse.blockbuster.recording.data.Record;
import mchorse.blockbuster.utils.L10n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.FMLCommonHandler;

/**
 * Utilities methods mostly to be used with recording code. Stuff like
 * broadcasting a message and sending records to players are located here.
 */
public class Utils
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
        PlayerList players = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList();

        for (String username : players.getAllUsernames())
        {
            EntityPlayerMP player = players.getPlayerByUsername(username);

            if (player != null)
            {
                player.addChatMessage(message);
            }
        }
    }

    /**
     * Send given error to everyone on the server, to everyone.
     *
     * Invoke this method only on the server side.
     */
    public static void broadcastError(String string, Object... objects)
    {
        PlayerList players = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList();

        for (String username : players.getAllUsernames())
        {
            EntityPlayerMP player = players.getPlayerByUsername(username);

            if (player != null)
            {
                L10n.error(player, string, objects);
            }
        }
    }

    /**
     * Send given error to everyone on the server, to everyone.
     *
     * Invoke this method only on the server side.
     */
    public static void broadcastInfo(String string, Object... objects)
    {
        PlayerList players = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList();

        for (String username : players.getAllUsernames())
        {
            EntityPlayerMP player = players.getPlayerByUsername(username);

            if (player != null)
            {
                L10n.info(player, string, objects);
            }
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
        File file = new File(DimensionManager.getCurrentSaveRootDirectory() + "/blockbuster/records");

        if (!file.exists())
        {
            file.mkdirs();
        }

        return new File(file.getAbsolutePath() + "/" + filename + ".dat");
    }

    /**
     * Get list of all available replays
     */
    public static List<String> getReplays()
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

            if (file.isFile() && name.endsWith(".dat"))
            {
                int index = name.indexOf(".");

                list.add(name.substring(0, index));
            }
        }

        return list;
    }

    /**
     * Send record frames to given player (from the server)
     */
    public static void sendRecord(String filename, EntityPlayerMP player)
    {
        if (!playerNeedsAction(filename, player))
        {
            return;
        }

        RecordManager manager = CommonProxy.manager;
        Record record = null;

        if (manager.records.containsKey(filename))
        {
            record = manager.records.get(filename);
        }
        else
        {
            try
            {
                record = new Record(filename);
                record.load(replayFile(filename));

                manager.records.put(filename, record);
            }
            catch (FileNotFoundException e)
            {
                L10n.error(player, "recording.not_found", filename);
                record = null;
            }
            catch (Exception e)
            {
                L10n.error(player, "recording.read", filename);
                e.printStackTrace();
                record = null;
            }
        }

        if (record != null)
        {
            record.resetUnload();

            Dispatcher.sendTo(new PacketFramesLoad(filename, record.preDelay, record.postDelay, record.frames), player);
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
        }
        else if (record == null)
        {
            L10n.error(player, "recording.not_exist", filename);
        }
    }

    /**
     * Checks whether given player needs a new action, meaning, he has an older
     * version of given named action or he doesn't have this action at all.
     *
     * DON'T ever use this as API since it may mess up with the recording
     * tracking.
     */
    public static boolean playerNeedsAction(String filename, EntityPlayer player)
    {
        IRecording recording = Recording.get(player);

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
        PlayerList players = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList();
        String filename = record.filename;

        for (String username : players.getAllUsernames())
        {
            EntityPlayerMP player = players.getPlayerByUsername(username);

            if (player != null)
            {
                IRecording recording = Recording.get(player);

                if (recording.hasRecording(filename))
                {
                    recording.removeRecording(filename);

                    Dispatcher.sendTo(new PacketUnloadFrames(filename), player);
                }
            }
        }
    }
}