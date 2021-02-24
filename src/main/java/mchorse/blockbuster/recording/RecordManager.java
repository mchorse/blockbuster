package mchorse.blockbuster.recording;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.CommonProxy;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.PacketCaption;
import mchorse.blockbuster.network.common.recording.PacketPlayback;
import mchorse.blockbuster.network.common.recording.PacketPlayerRecording;
import mchorse.blockbuster.recording.actions.Action;
import mchorse.blockbuster.recording.actions.DamageAction;
import mchorse.blockbuster.recording.data.FrameChunk;
import mchorse.blockbuster.recording.data.Mode;
import mchorse.blockbuster.recording.data.Record;
import mchorse.blockbuster.utils.EntityUtils;
import mchorse.metamorph.api.MorphAPI;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
     * Incomplete chunk frame only records (for recording big records)
     */
    public Map<String, FrameChunk> chunks = new HashMap<String, FrameChunk>();

    /**
     * Currently running record recorders (I have something to do about the
     * name)
     */
    public Map<EntityPlayer, RecordRecorder> recorders = new HashMap<EntityPlayer, RecordRecorder>();

    /**
     * Me: No, not {@link EntityPlayer}s, say record pla-yers, pla-yers...
     * Also me in 2020: What a cringe...
     */
    public Map<EntityLivingBase, RecordPlayer> players = new HashMap<EntityLivingBase, RecordPlayer>();

    /**
     * Scheduled recordings
     */
    public Map<EntityPlayer, ScheduledRecording> scheduled = new HashMap<EntityPlayer, ScheduledRecording>();

    /**
     * Get action list for given player
     */
    public List<Action> getActions(EntityPlayer player)
    {
        RecordRecorder recorder = this.recorders.get(player);

        return recorder == null ? null : recorder.actions;
    }

    public boolean record(String filename, EntityPlayer player, Mode mode, boolean teleportBack, boolean notify, Runnable runnable)
    {
        return this.record(filename, player, mode, teleportBack, notify, 0, runnable);
    }

    /**
     * Start recording given player to record with given filename
     */
    public boolean record(String filename, EntityPlayer player, Mode mode, boolean teleportBack, boolean notify, int offset, Runnable runnable)
    {
        Runnable proxy = () ->
        {
            if (offset > 0 && this.records.get(filename) != null && notify)
            {
                RecordPlayer recordPlayer = this.play(filename, player, Mode.ACTIONS, false);

                recordPlayer.tick = offset;
                EntityUtils.setRecordPlayer(player, recordPlayer.realPlayer());
            }

            if (runnable != null)
            {
                runnable.run();
            }
        };

        if (this.recorders.containsKey(player))
        {
            proxy.run();
        }

        if (filename.isEmpty() || this.halt(player, false, notify))
        {
            if (filename.isEmpty())
            {
                RecordUtils.broadcastError("recording.empty_filename");
            }

            return false;
        }

        for (RecordRecorder recorder : this.recorders.values())
        {
            if (recorder.record.filename.equals(filename))
            {
                RecordUtils.broadcastInfo("recording.recording", filename);

                return false;
            }
        }

        RecordRecorder recorder = new RecordRecorder(new Record(filename), mode, player, teleportBack);

        recorder.offset = offset;

        if (player.world.isRemote)
        {
            this.recorders.put(player, recorder);
        }
        else
        {
            this.setupPlayerData(recorder, player);
            CommonProxy.damage.addDamageControl(recorder, player);

            this.scheduled.put(player, new ScheduledRecording(recorder, player, proxy, (int) (Blockbuster.recordingCountdown.get() * 20), offset));
        }

        return true;
    }

    private void setupPlayerData(RecordRecorder recorder, EntityPlayer player)
    {
        NBTTagCompound tag = new NBTTagCompound();

        player.writeEntityToNBT(tag);
        recorder.record.playerData = tag;

        if (MPMHelper.isLoaded())
        {
            tag = MPMHelper.getMPMData(player);

            if (tag != null)
            {
                recorder.record.playerData.setTag("MPMData", tag);
            }
        }
    }

    /**
     * Stop recording given player
     */
    public boolean halt(EntityPlayer player, boolean hasDied, boolean notify)
    {
        /* Stop countdown */
        ScheduledRecording scheduled = this.scheduled.get(player);

        if (scheduled != null)
        {
            this.scheduled.remove(player);
            Dispatcher.sendTo(new PacketCaption(), (EntityPlayerMP) player);

            return true;
        }

        /* Stop the recording via command or whatever the source is */
        RecordRecorder recorder = this.recorders.get(player);

        if (recorder != null)
        {
            Record record = recorder.record;
            String filename = record.filename;

            if (hasDied && !record.actions.isEmpty())
            {
                record.addAction(record.actions.size() - 1, new DamageAction(200.0F));
            }
            else
            {
                recorder.stop(player);
            }

            /* Remove action preview for previously recorded actions */
            RecordPlayer recordPlayer = this.players.get(player);

            if (recordPlayer != null && recordPlayer.realPlayer)
            {
                this.players.remove(player);

                EntityUtils.setRecordPlayer(player, null);
            }

            /* Apply old player recording */
            try
            {
                Record oldRecord = this.get(filename);

                recorder.applyOld(oldRecord);
            }
            catch (Exception e)
            {}

            this.records.put(filename, record);
            this.recorders.remove(player);
            MorphAPI.demorph(player);

            if (notify)
            {
                CommonProxy.damage.restoreDamageControl(recorder, player.world);

                Dispatcher.sendTo(new PacketPlayerRecording(false, "", 0), (EntityPlayerMP) player);
            }

            return true;
        }

        return false;
    }

    /**
     * Version with default tick parameter
     */
    public RecordPlayer play(String filename, EntityLivingBase actor, Mode mode, boolean kill)
    {
        return this.play(filename, actor, mode, 0, kill);
    }

    /**
     * Start playback from given filename and given actor. You also have to
     * specify the mode of playback.
     */
    public RecordPlayer play(String filename, EntityLivingBase actor, Mode mode, int tick, boolean kill)
    {
        if (this.players.containsKey(actor))
        {
            return null;
        }

        try
        {
            Record record = this.get(filename);

            if (record.frames.size() == 0)
            {
                RecordUtils.broadcastError("recording.empty_record", filename);

                return null;
            }

            RecordPlayer playback = new RecordPlayer(record, mode, actor);

            playback.tick = tick;
            playback.kill = kill;
            playback.applyFrame(tick, actor, true);

            EntityUtils.setRecordPlayer(actor, playback);

            this.players.put(actor, playback);

            return playback;
        }
        catch (FileNotFoundException e)
        {
            RecordUtils.broadcastError("recording.not_found", filename);
        }
        catch (Exception e)
        {
            RecordUtils.broadcastError("recording.read", filename);
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Stop playback for the given record player
     */
    public void stop(RecordPlayer actor)
    {
        if (!this.players.containsKey(actor.actor))
        {
            return;
        }

        if (actor.actor.getHealth() > 0.0F)
        {
            if (actor.kill)
            {
                actor.actor.dismountRidingEntity();

                if (actor.realPlayer)
                {
                    if (actor.actor instanceof EntityPlayerMP)
                    {
                        Dispatcher.sendTo(new PacketPlayback(actor.actor.getEntityId(), false, actor.realPlayer, ""), (EntityPlayerMP) actor.actor);
                    }
                }
                else
                {
                    actor.actor.setDead();

                    if (actor.actor instanceof EntityPlayer)
                    {
                        actor.actor.world.getMinecraftServer().getPlayerList().playerLoggedOut((EntityPlayerMP) actor.actor);
                    }
                }
            }
            else
            {
                Dispatcher.sendToTracked(actor.actor, new PacketPlayback(actor.actor.getEntityId(), false, actor.realPlayer, ""));
            }
        }

        this.players.remove(actor.actor);
        EntityUtils.setRecordPlayer(actor.actor, null);
    }

    /**
     * Reset the tracking manager data
     */
    public void reset()
    {
        for (Record record : this.records.values())
        {
            if (record.dirty)
            {
                try
                {
                    record.save(RecordUtils.replayFile(record.filename));
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }

        this.records.clear();
        this.chunks.clear();
        this.recorders.clear();
        this.players.clear();
    }

    /**
     * Abort the recording of action for given player
     */
    public void abort(EntityPlayer player)
    {
        if (this.recorders.containsKey(player))
        {
            RecordRecorder recorder = this.recorders.remove(player);

            RecordUtils.broadcastError("recording.logout", recorder.record.filename);
        }
    }

    /**
     * Get record by the filename
     *
     * If a record by the filename doesn't exist, then record manager tries to
     * load this record.
     */
    public Record get(String filename) throws Exception
    {
        Record record = this.records.get(filename);

        if (record == null)
        {
            File file = RecordUtils.replayFile(filename);

            record = new Record(filename);
            record.load(file);

            this.records.put(filename, record);
        }

        return record;
    }

    /**
     * Unload old records and check scheduled actions
     */
    public void tick()
    {
        if (Blockbuster.recordUnload.get() && !this.records.isEmpty())
        {
            this.checkAndUnloadRecords();
        }

        if (!this.scheduled.isEmpty())
        {
            this.checkScheduled();
        }
    }

    /**
     * Check for any unloaded record and unload it if needed requirements are
     * met.
     */
    private void checkAndUnloadRecords()
    {
        Iterator<Map.Entry<String, Record>> iterator = this.records.entrySet().iterator();

        while (iterator.hasNext())
        {
            Record record = iterator.next().getValue();

            record.unload--;

            if (record.unload <= 0)
            {
                iterator.remove();
                RecordUtils.unloadRecord(record);

                try
                {
                    if (record.dirty)
                    {
                        record.save(RecordUtils.replayFile(record.filename));
                        record.dirty = false;
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Check for scheduled records and countdown them.
     */
    private void checkScheduled()
    {
        Iterator<ScheduledRecording> it = this.scheduled.values().iterator();

        while (it.hasNext())
        {
            ScheduledRecording record = it.next();

            if (record.countdown % 2 == 0)
            {
                IMessage message = new PacketCaption(new TextComponentTranslation("blockbuster.start_recording", record.recorder.record.filename, record.countdown / 20F));
                Dispatcher.sendTo(message, (EntityPlayerMP) record.player);
            }

            if (record.countdown <= 0)
            {
                record.run();
                this.recorders.put(record.player, record.recorder);
                Dispatcher.sendTo(new PacketPlayerRecording(true, record.recorder.record.filename, record.offset), (EntityPlayerMP) record.player);

                it.remove();

                continue;
            }

            record.countdown--;
        }
    }

    public void rename(String old, Record record)
    {
        RecordUtils.unloadRecord(record);

        this.records.remove(old);
        this.records.put(record.filename, record);

        for (String iter : RecordUtils.getReplayIterations(old))
        {
            File oldIter = new File(RecordUtils.replayFile(old).getAbsolutePath() + "~" + iter);

            oldIter.renameTo(new File(RecordUtils.replayFile(record.filename).getAbsolutePath() + "~" + iter));
        }

        RecordUtils.replayFile(old).delete();
    }
}