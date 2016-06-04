package noname.blockbuster.recording;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

public class Mocap
{
    public static Map<EntityPlayer, Recorder> records = Collections.synchronizedMap(new HashMap());
    public static ArrayList<PlayThread> playThreads = new ArrayList();

    public static final short signature = 3208;
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

    public static EntityEquipmentSlot getSlotByIndex(int index)
    {
        for (EntityEquipmentSlot slot : EntityEquipmentSlot.values())
        {
            if (slot.func_188452_c() == index)
                return slot;
        }

        return null;
    }

    public static String replayFile(String filename)
    {
        /* You spin me round round, baby round round */
        File file = new File(DimensionManager.getCurrentSaveRootDirectory() + "/records");

        if (!file.exists())
        {
            file.mkdirs();
        }

        return file.getAbsolutePath() + "/" + filename;
    }

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
            recorder.recordThread.capture = false;
            broadcastMessage("Stopped recording " + username + " to file " + recorder.fileName);
            records.remove(player);
            return;
        }

        for (Recorder ar : records.values())
        {
            if (ar.fileName.equals(filename))
            {
                broadcastMessage(ar.fileName + " is already being recorded to?");
                return;
            }
        }

        broadcastMessage("Started recording " + username + " to file " + filename);
        Recorder newRecorder = new Recorder();
        records.put(player, newRecorder);

        newRecorder.fileName = filename;
        newRecorder.recordThread = new RecordThread(player, filename);
    }

    /**
     * Start playback
     */
    public static void startPlayback(String filename, World world, boolean killOnDead)
    {
        ActorEntity actor = new ActorEntity(world);

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
            broadcastMessage("Can't find " + filename + " replay file!");
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
                broadcastMessage(filename + " isn't a record file (or is an old version?)");
                in.close();
                return;
            }

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

        new PlayThread(entity, filename, killOnDead);
    }
}