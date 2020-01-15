package mchorse.blockbuster.recording;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.CommonProxy;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.PacketCaption;
import mchorse.blockbuster.network.common.recording.PacketPlayback;
import mchorse.blockbuster.network.common.recording.PacketPlayerRecording;
import mchorse.blockbuster.recording.actions.Action;
import mchorse.blockbuster.recording.actions.DamageAction;
import mchorse.blockbuster.recording.capturing.DamageControl;
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
import net.minecraft.world.World;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

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

    /**
     * Start recording given player to record with given filename
     */
    public boolean startRecording(String filename, EntityPlayer player, Mode mode, boolean notify, Runnable runnable)
    {
        int countdown = Blockbuster.proxy.config.recording_countdown;

        if (runnable != null && (countdown == 0 || this.recorders.containsKey(player)))
        {
            runnable.run();
        }

        if (filename.isEmpty() || this.stopRecording(player, false, notify))
        {
            if (filename.isEmpty())
            {
                Utils.broadcastError("recording.empty_filename");
            }

            return false;
        }

        for (RecordRecorder recorder : this.recorders.values())
        {
            if (recorder.record.filename.equals(filename))
            {
                Utils.broadcastInfo("recording.recording", filename);

                return false;
            }
        }

        RecordRecorder recorder = new RecordRecorder(new Record(filename), mode);
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

        if (!player.worldObj.isRemote)
        {
            CommonProxy.damage.addDamageControl(recorder, player);
        }

        if (countdown == 0 || player.worldObj.isRemote)
        {
            this.recorders.put(player, recorder);

            if (notify)
            {
                Dispatcher.sendTo(new PacketPlayerRecording(true, filename), (EntityPlayerMP) player);
            }
        }
        else if (!player.worldObj.isRemote)
        {
            this.scheduled.put(player, new ScheduledRecording(recorder, player, runnable, countdown * 20));
        }

        return true;
    }

    /**
     * Stop recording given player
     */
    public boolean stopRecording(EntityPlayer player, boolean hasDied, boolean notify)
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

            this.records.put(filename, record);
            this.recorders.remove(player);
            MorphAPI.demorph(player);

            if (notify)
            {
                CommonProxy.damage.restoreDamageControl(recorder, player.worldObj);

                Dispatcher.sendTo(new PacketPlayerRecording(false, ""), (EntityPlayerMP) player);
            }

            return true;
        }

        return false;
    }

    /**
     * Version with default tick parameter
     */
    public RecordPlayer startPlayback(String filename, EntityLivingBase actor, Mode mode, boolean kill, boolean notify)
    {
        return this.startPlayback(filename, actor, mode, 0, kill, notify);
    }

    /**
     * Start playback from given filename and given actor. You also have to
     * specify the mode of playback.
     */
    public RecordPlayer startPlayback(String filename, EntityLivingBase actor, Mode mode, int tick, boolean kill, boolean notify)
    {
        if (this.players.containsKey(actor))
        {
            return null;
        }

        try
        {
            Record record = this.getRecord(filename);

            if (record.frames.size() == 0)
            {
                Utils.broadcastError("recording.empty_record", filename);

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
            Utils.broadcastError("recording.not_found", filename);
        }
        catch (Exception e)
        {
            Utils.broadcastError("recording.read", filename);
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Stop playback for the given record player
     */
    public void stopPlayback(RecordPlayer actor)
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
                actor.actor.setDead();

                if (actor.actor instanceof EntityPlayer)
                {
                    actor.actor.worldObj.getMinecraftServer().getPlayerList().playerLoggedOut((EntityPlayerMP) actor.actor);
                }
            }
            else
            {
                Dispatcher.sendToTracked(actor.actor, new PacketPlayback(actor.actor.getEntityId(), false, ""));
            }
        }

        this.players.remove(actor.actor);
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
                    record.save(Utils.replayFile(record.filename));
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
    public void abortRecording(EntityPlayer player)
    {
        if (this.recorders.containsKey(player))
        {
            RecordRecorder recorder = this.recorders.remove(player);

            Utils.broadcastError("recording.logout", recorder.record.filename);
        }
    }

    /**
     * Get record by the filename
     *
     * If a record by the filename doesn't exist, then record manager tries to
     * load this record.
     */
    public Record getRecord(String filename) throws Exception
    {
        Record record = this.records.get(filename);

        if (record == null)
        {
            File file = Utils.replayFile(filename);

            record = new Record(filename);
            record.load(file);

            this.records.put(filename, record);
        }

        return record;
    }

    public void tick()
    {
        if (Blockbuster.proxy.config.record_unload && !this.records.isEmpty())
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
                Utils.unloadRecord(record);

                try
                {
                    if (record.dirty)
                    {
                        record.save(Utils.replayFile(record.filename));
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

            if (record.countdown % 20 == 0)
            {
                IMessage message = new PacketCaption(new TextComponentTranslation("blockbuster.start_recording", record.recorder.record.filename, record.countdown / 20));
                Dispatcher.sendTo(message, (EntityPlayerMP) record.player);
            }

            if (record.countdown <= 0)
            {
                record.run();
                this.recorders.put(record.player, record.recorder);
                Dispatcher.sendTo(new PacketPlayerRecording(true, record.recorder.record.filename), (EntityPlayerMP) record.player);

                it.remove();

                continue;
            }

            record.countdown--;
        }
    }

    /**
     * Scheduled recorder class
     */
    public static class ScheduledRecording
    {
        public RecordRecorder recorder;
        public EntityPlayer player;
        public Runnable runnable;
        public int countdown;

        public ScheduledRecording(RecordRecorder recorder, EntityPlayer player, Runnable runnable, int countdown)
        {
            this.recorder = recorder;
            this.player = player;
            this.runnable = runnable;
            this.countdown = countdown;
        }

        public void run()
        {
            if (this.runnable != null)
            {
                this.runnable.run();
            }
        }
    }
}