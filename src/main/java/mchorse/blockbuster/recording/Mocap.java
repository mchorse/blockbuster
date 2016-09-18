package mchorse.blockbuster.recording;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.PacketPlayerRecording;
import mchorse.blockbuster.recording.actions.Action;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.FMLCommonHandler;

/**
 * Mocap utility class
 *
 * Some of this (or most of it) code was borrowed from the Mocap mod (for
 * 1.7.10) and rewritten for 1.9. That relates to all package
 * mchorse.blockbuster.recording and EntityActor.
 *
 * @author EchebKeso
 * @author mchorse
 * @link http://www.minecraftforum.net/forums/mapping-and-modding/minecraft-mods/1445402-minecraft-motion-capture-mod-mocap-16-000
 */

public class Mocap
{
    public static Map<EntityPlayer, RecordThread> records = Collections.synchronizedMap(new HashMap<EntityPlayer, RecordThread>());
    public static Map<EntityActor, PlayThread> playbacks = Collections.synchronizedMap(new HashMap<EntityActor, PlayThread>());

    /**
     * Signature used for replay files, this is usually the first entry to read.
     */
    public static final short signature = 3210;

    /**
     * Timing delay between two replay frames. Default is 100 milliseconds or 10
     * ticks per second.
     */
    public static final long delay = 100L;

    public static List<Action> getActionListForPlayer(EntityPlayer ep)
    {
        RecordThread record = records.get(ep);

        if (record == null)
        {
            return null;
        }

        return record.eventList;
    }

    public static void broadcastMessage(String message)
    {
        broadcastMessage(new TextComponentString(message));
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

    /* Action utilities */

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
        for (Entity entity : world.loadedEntityList)
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
        if (stopRecording(player)) return;

        for (RecordThread registeredRecorder : records.values())
        {
            if (registeredRecorder.filename.equals(filename))
            {
                broadcastMessage(I18n.format("blockbuster.mocap.already_recording", filename));
                return;
            }
        }

        RecordThread recorder = new RecordThread(player, filename);
        records.put(player, recorder);

        Dispatcher.sendTo(new PacketPlayerRecording(true, filename), (EntityPlayerMP) player);
    }

    /**
     * Stop the recording
     */
    public static boolean stopRecording(EntityPlayer player)
    {
        RecordThread recorder = records.get(player);

        if (recorder != null)
        {
            recorder.capture = false;
            records.remove(player);

            Dispatcher.sendTo(new PacketPlayerRecording(false, recorder.filename), (EntityPlayerMP) player);

            return true;
        }

        return false;
    }

    /**
     * Create an actor from command line arguments (i.e. String array)
     */
    public static EntityActor actorFromArgs(String[] args, World world)
    {
        EntityActor actor = null;

        String name = args.length >= 2 ? args[1] : "";
        String skin = args.length >= 3 ? args[2] : "";
        String model = args.length >= 4 ? args[3] : "alex";
        boolean invincible = args.length >= 5 && args[4].equals("1");

        actor = new EntityActor(world);
        actor.modify(model, skin, true, true);
        actor.setEntityInvulnerable(invincible);
        actor.setCustomNameTag(name);

        return actor;
    }

    /**
     * Start playback with command line arguments
     */
    public static EntityActor startPlayback(String[] args, World world, boolean killOnDead)
    {
        EntityActor actor = actorFromArgs(args, world);

        startPlayback(args[0], actor, killOnDead);
        world.spawnEntityInWorld(actor);

        return actor;
    }

    /**
     * Start playback with given entity
     */
    public static void startPlayback(String filename, EntityActor entity, boolean killOnDead)
    {
        File file = new File(replayFile(filename));

        if (!file.exists())
        {
            broadcastMessage(I18n.format("blockbuster.mocap.cant_find_file", filename));
            return;
        }

        float yaw = 0.0F;
        float pitch = 0.0F;
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

            in.readLong();

            yaw = in.readFloat();
            pitch = in.readFloat();
            x = in.readDouble();
            y = in.readDouble();
            z = in.readDouble();

            in.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        entity.setPositionAndRotation(x, y, z, yaw, pitch);
        entity.setNoAI(true);

        playbacks.put(entity, new PlayThread(entity, filename, killOnDead));
    }

    /**
     * Get path to replay file (located in current world save's folder)
     */
    public static String replayFile(String filename)
    {
        File file = new File(DimensionManager.getCurrentSaveRootDirectory() + "/blockbuster/records");

        if (!file.exists())
        {
            file.mkdirs();
        }

        return file.getAbsolutePath() + "/" + filename;
    }
}