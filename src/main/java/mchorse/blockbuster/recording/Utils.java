package mchorse.blockbuster.recording;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import cpw.mods.fml.common.FMLCommonHandler;
import mchorse.blockbuster.common.CommonProxy;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.recording.PacketFramesLoad;
import mchorse.blockbuster.network.common.recording.PacketRequestedFrames;
import mchorse.blockbuster.network.common.recording.PacketUnloadFrames;
import mchorse.blockbuster.recording.data.Record;
import mchorse.blockbuster.utils.L10n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.DimensionManager;

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
        broadcastMessage(new ChatComponentText(message));
    }

    /**
     * I18n formatting version of {@link #broadcastMessage(ITextComponent)}
     */
    public static void broadcastMessage(String string, Object... args)
    {
        broadcastMessage(new ChatComponentTranslation(string, args));
    }

    /**
     * Send given message to everyone on the server, to everyone.
     *
     * Invoke this method only on the server side.
     */
    public static void broadcastMessage(IChatComponent message)
    {
        List players = FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().playerEntityList;

        for (Object object : players)
        {
            EntityPlayerMP player = (EntityPlayerMP) object;

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
        List players = FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().playerEntityList;

        for (Object object : players)
        {
            EntityPlayerMP player = (EntityPlayerMP) object;

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
        List players = FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().playerEntityList;

        for (Object object : players)
        {
            EntityPlayerMP player = (EntityPlayerMP) object;

            if (player != null)
            {
                L10n.info(player, string, objects);
            }
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

        return new File(file.getAbsolutePath() + "/" + filename + ".dat");
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

            Dispatcher.sendTo(new PacketFramesLoad(filename, record.frames), player);
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

            Dispatcher.sendTo(new PacketRequestedFrames(id, record.filename, record.frames), player);
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
        return true;
    }

    /**
     * Unload given record. It will send to all players a packet to unload a
     * record.
     */
    public static void unloadRecord(Record record)
    {
        String filename = record.filename;
        List players = FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().playerEntityList;

        for (Object object : players)
        {
            EntityPlayerMP player = (EntityPlayerMP) object;

            if (player != null)
            {
                Dispatcher.sendTo(new PacketUnloadFrames(filename), player);
            }
        }
    }
}