package mchorse.blockbuster.recording;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.PacketPlayerRecording;
import mchorse.blockbuster.recording.actions.Action;
import mchorse.blockbuster.recording.data.Record;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.DimensionManager;

/**
 * Record manager
 *
 * This class responsible is responsible for managing record recorders and
 * players for entity players and actors.
 */
public class RecordManager
{
    public Map<EntityPlayer, RecordRecorder> recorders = new HashMap<EntityPlayer, RecordRecorder>();
    public Map<EntityActor, RecordPlayer> players = new HashMap<EntityActor, RecordPlayer>();

    /**
     * Get action list for given player
     */
    public List<Action> getActions(EntityPlayer player)
    {
        RecordRecorder recorder = this.recorders.get(player);

        return recorder == null ? null : recorder.record.actions;
    }

    /**
     * Start recording given player to record with given filename
     */
    public boolean startRecording(String filename, EntityPlayer player)
    {
        if (this.stopRecording(player)) return false;

        for (RecordRecorder recorder : this.recorders.values())
        {
            if (recorder.record.filename.equals(filename))
            {
                Utils.broadcastMessage(new TextComponentTranslation("blockbuster.mocap.already_recording", filename));

                return false;
            }
        }

        this.recorders.put(player, new RecordRecorder(new Record(filename), RecordPlayer.Mode.BOTH));
        Dispatcher.sendTo(new PacketPlayerRecording(true, filename), (EntityPlayerMP) player);

        return true;
    }

    /**
     * Stop recording given player
     */
    public boolean stopRecording(EntityPlayer player)
    {
        RecordRecorder recorder = this.recorders.get(player);

        if (recorder != null)
        {
            this.recorders.remove(player);
            Dispatcher.sendTo(new PacketPlayerRecording(false, recorder.record.filename), (EntityPlayerMP) player);

            return true;
        }

        return false;
    }

    /**
     * Start playback from given filename and given actor
     */
    public boolean startPlayback(String filename, EntityActor actor, boolean kill)
    {
        if (this.players.containsKey(actor)) return false;

        File file = new File(this.replayFile(filename));

        if (!file.exists())
        {
            Utils.broadcastMessage(new TextComponentTranslation("blockbuster.mocap.cant_find_file", filename));
            return false;
        }

        try
        {
            Record record = new Record(filename);
            record.fromBytes(file);

            RecordPlayer player = new RecordPlayer(record, RecordPlayer.Mode.BOTH);

            actor.playback = player;
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
     * Stop playback for given actor
     */
    public void stopPlayback(EntityActor actor)
    {
        this.players.remove(actor);
    }

    /**
     * Get path to replay file (located in current world save's folder)
     */
    public String replayFile(String filename)
    {
        File file = new File(DimensionManager.getCurrentSaveRootDirectory() + "/blockbuster/records");

        if (!file.exists())
        {
            file.mkdirs();
        }

        return file.getAbsolutePath() + "/" + filename;
    }
}
