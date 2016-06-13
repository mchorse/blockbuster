package noname.blockbuster.recording;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.FMLCommonHandler;
import noname.blockbuster.entity.ActorEntity;

/**
 * Mocap utility class
 *
 * Some of this (or most of it) code was borrowed from the Mocap mod (for
 * 1.7.10) and rewritten for 1.9. That relates to all package
 * noname.blockbuster.recording and ActorEntity.
 *
 * @author EchebKeso
 * @author mchorse
 * @link http://www.minecraftforum.net/forums/mapping-and-modding/minecraft-mods/1445402-minecraft-motion-capture-mod-mocap-16-000
 */
public class Mocap
{
    public static Map<EntityPlayer, Recorder> records = Collections.synchronizedMap(new HashMap());
    public static Map<ActorEntity, PlayThread> playbacks = Collections.synchronizedMap(new HashMap());

    /**
     * Signature used for replay files, this is usually the first entry to
     * read.
     */
    public static final short signature = 3208;

    /**
     * Timing delay between two replay frames. Default is 100 milliseconds or
     * 10 ticks per second.
     */
    public static final long delay = 100L;

    public static List<Action> getActionListForPlayer(EntityPlayer ep)
    {
        Recorder aRecorder = records.get(ep);

        if (aRecorder == null)
        {
            return null;
        }

        return aRecorder.eventsList;
    }

    /**
     * Send given message to everyone on the server, to everyone.
     *
     * Invoke this method only on the server side.
     */
    public static void broadcastMessage(String message)
    {
        ITextComponent chatMessage = new TextComponentString(message);
        PlayerList players = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList();

        for (String username : players.getAllUsernames())
        {
            EntityPlayerMP player = players.getPlayerByUsername(username);

            if (player != null)
            {
                player.addChatMessage(chatMessage);
            }
        }
    }

    /* Action utilities */

    public static EntityEquipmentSlot getSlotByIndex(int index)
    {
        for (EntityEquipmentSlot slot : EntityEquipmentSlot.values())
        {
            if (slot.func_188452_c() == index)
                return slot;
        }

        return null;
    }

    /**
     * Simple method that decreases the need for writing additional
     * UUID.fromString line
     */
    public static Entity entityByUUID(World world, String id)
    {
        return entityByUUID(world, UUID.fromString(id));
    }

    /**
     * Get entity by UUID in the server world.
     *
     * Looked up on minecraft forge forum, I don't remember where's exactly...
     */
    public static Entity entityByUUID(World world, UUID target)
    {
        for (Entity entity : world.getLoadedEntityList())
        {
            if (entity.getUniqueID().equals(target))
            {
                return entity;
            }
        }

        return null;
    }

    /* Record/play methods */

    /**
     * Start recording player's actions
     */
    public static void startRecording(String filename, EntityPlayer player)
    {
        Recorder recorder = records.get(player);
        String username = player.getDisplayName().getFormattedText();

        if (recorder != null)
        {
            recorder.thread.capture = false;
            broadcastMessage(I18n.format("blockbuster.mocap.stopped", username, recorder.fileName));
            records.remove(player);
            return;
        }

        for (Recorder registeredRecorder : records.values())
        {
            if (registeredRecorder.fileName.equals(filename))
            {
                broadcastMessage(I18n.format("blockbuster.mocap.already_recording", filename));
                return;
            }
        }

        broadcastMessage(I18n.format("blockbuster.mocap.started", username, filename));
        Recorder newRecorder = new Recorder();
        records.put(player, newRecorder);

        newRecorder.fileName = filename;
        newRecorder.thread = new RecordThread(player, filename);
    }

    /**
     * Start playback with new actor entity (used by CommandPlay class)
     */
    public static void startPlayback(String filename, String name, String skin, World world, boolean killOnDead)
    {
        ActorEntity actor = new ActorEntity(world);
        actor.setCustomNameTag(name);
        actor.setSkin(skin, true);

        startPlayback(filename, actor, killOnDead);
        world.spawnEntityInWorld(actor);
    }

    /**
     * Start playback with given entity
     */
    public static void startPlayback(String filename, ActorEntity entity, boolean killOnDead)
    {
        File file = new File(replayFile(filename));

        if (!file.exists())
        {
            broadcastMessage(I18n.format("blockbuster.mocap.cant_find_file", filename));
            return;
        }

        double x = 0.0D;
        double y = 0.0D;
        double z = 0.0D;

        try
        {
            RandomAccessFile in = new RandomAccessFile(file, "r");

            if (in.readShort() != signature)
            {
                broadcastMessage(I18n.format("blockbuster.mocap.wrong_signature", filename));
                in.close();
                return;
            }

            /* Skips entity's rotation */
            in.skipBytes(16);
            x = in.readDouble();
            y = in.readDouble();
            z = in.readDouble();

            in.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        entity.setPosition(x, y, z);
        entity.setNoAI(true);

        playbacks.put(entity, new PlayThread(entity, filename, killOnDead));
    }

    /**
     * Get path to replay file (located in current world save's folder)
     */
    public static String replayFile(String filename)
    {
        File file = new File(DimensionManager.getCurrentSaveRootDirectory() + "/records");

        if (!file.exists())
        {
            file.mkdirs();
        }

        return file.getAbsolutePath() + "/" + filename;
    }
}