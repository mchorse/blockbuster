package mchorse.blockbuster.recording;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.PacketPlayerRecording;
import mchorse.blockbuster.network.common.recording.PacketFramesLoad;
import mchorse.blockbuster.network.common.recording.PacketPlayback;
import mchorse.blockbuster.recording.actions.Action;
import mchorse.blockbuster.recording.data.Mode;
import mchorse.blockbuster.recording.data.Record;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.DimensionManager;

/**
 * Record manager
 *
 * This class responsible is responsible for managing record recorders and
 * players for entity players and actors.
 */
public class RecordManager
{
    /**
     * Loaded records
     */
    public Map<String, Record> records = new HashMap<String, Record>();

    /**
     * Currently running record recorders (I have something to do about the
     * name)
     */
    public Map<EntityPlayer, RecordRecorder> recorders = new HashMap<EntityPlayer, RecordRecorder>();

    /**
     * No, not {@link EntityPlayer}s, say record pla-yers, pla-yers...
     */
    public Map<EntityActor, RecordPlayer> players = new HashMap<EntityActor, RecordPlayer>();

    /**
     * Get action list for given player
     */
    public List<Action> getActions(EntityPlayer player)
    {
        RecordRecorder recorder = this.recorders.get(player);

        return recorder == null ? null : recorder.actions;
    }

    /**
     * Start recording given player to record with given filename
     */
    public boolean startRecording(String filename, EntityPlayer player, Mode mode, boolean notify)
    {
        if (filename.isEmpty() || this.stopRecording(player, notify))
        {
            return false;
        }

        for (RecordRecorder recorder : this.recorders.values())
        {
            if (recorder.record.filename.equals(filename))
            {
                Utils.broadcastMessage("blockbuster.mocap.already_recording", filename);

                return false;
            }
        }

        this.recorders.put(player, new RecordRecorder(new Record(filename), mode));

        if (notify)
        {
            Dispatcher.sendTo(new PacketPlayerRecording(true, filename), (EntityPlayerMP) player);
        }

        return true;
    }

    /**
     * Stop recording given player
     */
    public boolean stopRecording(EntityPlayer player, boolean notify)
    {
        RecordRecorder recorder = this.recorders.get(player);

        if (recorder != null)
        {
            this.records.put(recorder.record.filename, recorder.record);
            this.recorders.remove(player);

            if (notify)
            {
                Dispatcher.sendTo(new PacketPlayerRecording(false, recorder.record.filename), (EntityPlayerMP) player);
            }

            return true;
        }

        return false;
    }

    /**
     * Start playback from given filename and given actor. You also have to
     * specify the mode of playback.
     */
    public boolean startPlayback(String filename, EntityActor actor, Mode mode, boolean kill, boolean notify)
    {
        if (this.players.containsKey(actor))
        {
            return false;
        }

        File file = this.replayFile(filename);

        if (!file.exists())
        {
            Utils.broadcastMessage("blockbuster.mocap.cant_find_file", filename);

            return false;
        }

        try
        {
            Record record = new Record(filename);
            record.fromBytes(file);

            RecordPlayer player = new RecordPlayer(record, mode);

            actor.playback = player;
            actor.playback.record.applyFrame(0, actor);
            actor.playback.playing = true;
            actor.playback.kill = true;

            if (notify)
            {
                Dispatcher.sendToTracked(actor, new PacketFramesLoad(actor.getEntityId(), filename, record.frames));
            }

            this.players.put(actor, player);

            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Stop playback for the given actor. If the actor doesn't exist in players,
     * it simply does nothing.
     */
    public void stopPlayback(EntityActor actor)
    {
        if (!this.players.containsKey(actor))
        {
            return;
        }

        actor.playback.record.applyFrame(0, actor);

        if (actor.playback.kill)
        {
            actor.setDead();
        }

        actor.playback = null;

        this.players.remove(actor);
        Dispatcher.sendToTracked(actor, new PacketPlayback(actor.getEntityId(), false));
    }

    /**
     * Get path to replay file (located in current world save's folder)
     */
    public File replayFile(String filename)
    {
        File file = new File(DimensionManager.getCurrentSaveRootDirectory() + "/blockbuster/records");

        if (!file.exists())
        {
            file.mkdirs();
        }

        return new File(file.getAbsolutePath() + "/" + filename);
    }
}