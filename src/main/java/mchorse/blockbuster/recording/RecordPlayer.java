package mchorse.blockbuster.recording;

import mchorse.blockbuster.CommonProxy;
import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.common.tileentity.director.Replay;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.PacketActorPause;
import mchorse.blockbuster.network.common.recording.PacketPlayback;
import mchorse.blockbuster.network.common.recording.PacketSyncTick;
import mchorse.blockbuster.recording.data.Frame;
import mchorse.blockbuster.recording.data.Mode;
import mchorse.blockbuster.recording.data.Record;
import mchorse.blockbuster.utils.EntityUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.util.Constants.NBT;

/**
 * Record player class
 *
 * This thing is responsible for playing given record. It applies frames and
 * actions from the record instance on the given actor.
 */
public class RecordPlayer
{
    /**
     * Record from which this player is going to play
     */
    public Record record;

    /**
     * Play mode
     */
    public Mode mode;

    /**
     * Entity which is used by this record player to replay the action 
     */
    public EntityLivingBase actor;

    /**
     * Current tick
     */
    public int tick = 0;

    /**
     * Speed of playback (or delay between frames) in frames
     */
    public int delay = 1;

    /**
     * Temporary speed of playback. This is used only when record is absent
     */
    public int recordDelay = 1;

    /**
     * Whether to kill an actor when player finished playing
     */
    public boolean kill = false;

    /**
     * Is this player is playing
     */
    public boolean playing = true;

    public RecordPlayer(Record record, Mode mode, EntityLivingBase actor)
    {
        this.record = record;
        this.mode = mode;
        this.actor = actor;
    }

    /**
     * Check if the record player is finished
     */
    public boolean isFinished()
    {
        return this.record != null && this.tick - this.record.preDelay - this.record.postDelay >= this.record.getLength();
    }

    /**
     * Get appropriate amount of real ticks (for accessing current 
     * action or something like this)
     */
    public int getTick()
    {
        return Math.max(0, this.record == null ? this.tick : this.tick - this.record.preDelay);
    }

    /**
     * Get current frame 
     */
    public Frame getCurrentFrame()
    {
        return this.record.getFrame(this.getTick());
    }

    public void next()
    {
        this.next(this.actor);
    }

    /**
     * Apply current frame and advance to the next one
     */
    public void next(EntityLivingBase actor)
    {
        if (this.isFinished() || --this.delay > 0)
        {
            return;
        }

        if (this.record != null)
        {
            boolean both = this.mode == Mode.BOTH;

            if (this.mode == Mode.ACTIONS || both) this.applyAction(this.tick, actor, false);
            if (this.mode == Mode.FRAMES || both) this.applyFrame(this.tick, actor, false);

            this.delay = this.record.delay;
            this.record.resetUnload();
        }
        else
        {
            this.delay = this.recordDelay;
        }

        /* Align the body with the head on spawn */
        if (this.tick == 0)
        {
            actor.renderYawOffset = actor.rotationYaw;
        }

        this.tick++;
    }

    /**
     * Pause the playing actor
     */
    public void pause()
    {
        this.playing = false;
        this.actor.noClip = true;
        this.actor.setEntityInvulnerable(true);

        if (this.actor.isServerWorld())
        {
            Dispatcher.sendToTracked(this.actor, new PacketActorPause(this.actor.getEntityId(), true, this.tick));
        }
    }

    /**
     * Resume the paused actor
     */
    public void resume(int tick, Replay replay)
    {
        this.tick = tick;
        this.playing = true;
        this.actor.noClip = false;

        if (!this.actor.worldObj.isRemote && replay != null)
        {
            this.actor.setEntityInvulnerable(replay.invincible);
        }

        if (this.actor.isServerWorld())
        {
            Dispatcher.sendToTracked(this.actor, new PacketActorPause(this.actor.getEntityId(), false, this.tick));
        }
    }

    /**
     * Make an actor go to the given tick
     * @param replay 
     */
    public void goTo(int tick, boolean actions, Replay replay)
    {
        int preDelay = this.record.preDelay;
        int original = tick;

        tick -= preDelay;

        int min = Math.min(this.tick - this.record.preDelay, tick);
        int max = Math.max(this.tick - this.record.preDelay, tick);

        if (actions)
        {
            for (int i = min; i < max; i++)
            {
                this.record.applyAction(i, this.actor);
            }
        }

        this.tick = original;
        this.record.resetUnload();
        this.record.applyFrame(tick, this.actor, true);

        if (actions)
        {
            this.record.applyAction(tick, this.actor);

            if (tick != 0 && replay != null)
            {
                this.record.seekMorphAction(this.actor, tick, replay);
            }
        }

        if (this.actor.isServerWorld())
        {
            Dispatcher.sendToTracked(this.actor, new PacketSyncTick(this.actor.getEntityId(), tick));
        }
    }

    /**
     * Start the playback, but with default tick argument
     */
    public void startPlaying(String filename, boolean kill)
    {
        this.startPlaying(filename, 0, kill);
    }

    /**
     * Start the playback, invoked by director block (more specifically by
     * DirectorTileEntity).
     */
    public void startPlaying(String filename, int tick, boolean kill)
    {
        this.tick = tick;
        this.kill = kill;

        this.applyFrame(tick, this.actor, true);
        EntityUtils.setRecordPlayer(this.actor, this);

        if (this.actor instanceof EntityActor)
        {
            this.actor.worldObj.spawnEntityInWorld(this.actor);
        }
        else if (this.actor instanceof EntityPlayer)
        {
            if (this.record.playerData != null)
            {
                this.actor.readEntityFromNBT(this.record.playerData);

                if (MPMHelper.isLoaded() && this.record.playerData.hasKey("MPMData", NBT.TAG_COMPOUND))
                {
                    MPMHelper.setMPMData((EntityPlayer) this.actor, this.record.playerData.getCompoundTag("MPMData"));
                }
            }

            this.actor.worldObj.getMinecraftServer().getPlayerList().playerLoggedIn((EntityPlayerMP) this.actor);
        }

        Dispatcher.sendToTracked(this.actor, new PacketPlayback(this.actor.getEntityId(), true, filename));
    }

    /**
     * Stop playing
     */
    public void stopPlaying()
    {
        CommonProxy.manager.stopPlayback(this);

        this.actor.noClip = false;
    }

    public void applyFrame(int tick, EntityLivingBase target, boolean force)
    {
        tick -= this.record.preDelay;

        if (tick < 0)
        {
            tick = 0;
        }

        this.record.applyFrame(tick, target, force);
    }

    public void applyAction(int tick, EntityLivingBase target, boolean safe)
    {
        this.record.applyAction(tick - this.record.preDelay, target, safe);
    }
}