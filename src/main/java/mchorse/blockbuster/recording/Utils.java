package mchorse.blockbuster.recording;

import java.io.File;
import java.io.FileNotFoundException;

import mchorse.blockbuster.capabilities.recording.IRecording;
import mchorse.blockbuster.capabilities.recording.Recording;
import mchorse.blockbuster.common.CommonProxy;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.recording.PacketFramesLoad;
import mchorse.blockbuster.network.common.recording.PacketRequestedFrames;
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
 * Utilities methods mostly to be used with recording code.
 *
 * @author mchorse
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
                record.fromBytes(replayFile(filename));

                manager.records.put(filename, record);
            }
            catch (FileNotFoundException e)
            {
                L10n.send(player, "blockbuster.mocap.cant_find_file", filename);
                record = null;
            }
            catch (Exception e)
            {
                e.printStackTrace();
                record = null;
            }
        }

        if (record != null)
        {
            Dispatcher.sendTo(new PacketFramesLoad(filename, record.frames), player);
        }
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

        return new File(file.getAbsolutePath() + "/" + filename);
    }

    /**
     * Send requested frames (for actor) to given player (from the server)
     */
    public static void sendRequestedRecord(int id, String filename, EntityPlayerMP player)
    {
        Record record = CommonProxy.manager.records.get(filename);

        if (playerNeedsAction(filename, player) && record != null)
        {
            Dispatcher.sendTo(new PacketRequestedFrames(id, record.filename, record.frames), player);
        }
        else
        {
            System.out.println("Record '" + filename + "' couldn't be loaded, because it doesn't exist!");
        }
    }

    /**
     * Checks whether given player needs a new action, meaning, he has an older
     * version of given named action or he doesn't have this action at all.
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

        return !has;
    }
}