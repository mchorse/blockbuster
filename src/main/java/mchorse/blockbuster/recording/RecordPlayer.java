package mchorse.blockbuster.recording;

import java.util.Queue;

import com.google.common.collect.Queues;

import mchorse.blockbuster.CommonProxy;
import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.recording.scene.Replay;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.PacketActorPause;
import mchorse.blockbuster.network.common.recording.PacketPlayback;
import mchorse.blockbuster.network.common.recording.PacketSyncTick;
import mchorse.blockbuster.recording.data.Frame;
import mchorse.blockbuster.recording.data.Mode;
import mchorse.blockbuster.recording.data.Record;
import mchorse.blockbuster.utils.EntityUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketEntityTeleport;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

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
     * Whether to kill an actor when player finished playing
     */
    public boolean kill = false;

    /**
     * Is this player is playing
     */
    public boolean playing = true;

    /**
     * Sync mode - pauses the playback once hit the end
     */
    public boolean sync = false;

    /**
     * It might be null
     */
    public Replay replay;

    public boolean realPlayer;

    public Queue<IMessage> unsentPackets = Queues.<IMessage>newArrayDeque();

    public boolean actorUpdated;

    public RecordPlayer(Record record, Mode mode, EntityLivingBase actor)
    {
        this.record = record;
        this.mode = mode;
        this.actor = actor;
    }

    public RecordPlayer realPlayer()
    {
        this.realPlayer = true;

        return this;
    }

    /**
     * Check if the record player is finished
     */
    public boolean isFinished()
    {
        boolean isFinished = this.record != null && this.tick - this.record.preDelay - this.record.postDelay >= this.record.getLength();

        if (isFinished && this.sync && this.playing)
        {
            this.pause();

            return false;
        }

        return isFinished;
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

    /**
     * It should be called before world tick
     */
    public void playActions()
    {
        if (!this.playing || this.isFinished())
        {
            return;
        }

        if (this.record != null)
        {
            if (this.mode == Mode.ACTIONS || this.mode == Mode.BOTH) this.applyAction(this.tick, actor, false);

            this.record.resetUnload();
        }
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
        if (this.record != null)
        {
            this.record.resetUnload();
        }

        if (!this.playing || this.isFinished())
        {
            return;
        }

        if (this.record != null)
        {
            if (this.mode == Mode.FRAMES || this.mode == Mode.BOTH) this.applyFrame(this.tick, actor, false);

            this.record.resetUnload();
        }

        /* Align the body with the head on spawn */
        if (this.tick == 0)
        {
            actor.renderYawOffset = actor.rotationYaw;
        }

        this.tick++;
        this.actorUpdated = true;
    }

    /**
     * Pause the playing actor
     */
    public void pause()
    {
        this.playing = false;
        this.actor.noClip = true;
        this.actor.setEntityInvulnerable(true);

        this.applyFrame(this.tick - 1, this.actor, true);

        if (this.actor.isServerWorld())
        {
            this.record.applyPreviousMorph(this.actor, this.replay, this.tick, Record.MorphType.PAUSE);

            this.sendToTracked(new PacketActorPause(this.actor.getEntityId(), true, this.tick));
        }
    }

    /**
     * Resume the paused actor
     */
    public void resume(int tick)
    {
        if (tick >= 0)
        {
            this.tick = tick;
        }

        this.playing = true;
        this.actor.noClip = false;

        if (!this.actor.world.isRemote && this.replay != null)
        {
            this.actor.setEntityInvulnerable(this.replay.invincible);
        }

        this.applyFrame(this.tick, this.actor, true);

        if (this.actor.isServerWorld())
        {
            this.record.applyPreviousMorph(this.actor, this.replay, tick, Record.MorphType.FORCE);

            this.sendToTracked(new PacketActorPause(this.actor.getEntityId(), false, this.tick));
        }
    }

    /**
     * Make an actor go to the given tick
     */
    public void goTo(int tick, boolean actions)
    {
        int preDelay = this.record.preDelay;
        int original = tick;

        if (tick > this.record.frames.size() + this.record.preDelay)
        {
            tick = this.record.frames.size() + this.record.preDelay - 1;
        }

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
        this.record.applyFrame(this.playing ? tick : Math.max(0, tick - 1), this.actor, true, this.realPlayer);

        if (actions)
        {
            this.record.applyAction(tick, this.actor);

            if (this.replay != null)
            {
                this.record.applyPreviousMorph(this.actor, this.replay, tick, this.playing ? Record.MorphType.FORCE : Record.MorphType.PAUSE);
            }
        }

        if (this.actor.isServerWorld())
        {
            this.sendToTracked(new PacketActorPause(this.actor.getEntityId(), !this.playing, this.tick));
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
        this.sync = false;

        this.applyFrame(this.playing ? tick : tick - 1, this.actor, true);

        EntityUtils.setRecordPlayer(this.actor, this);

        this.sendToTracked(new PacketPlayback(this.actor.getEntityId(), true, this.realPlayer, filename));

        if (this.realPlayer && this.actor instanceof EntityPlayerMP)
        {
            Dispatcher.sendTo(new PacketPlayback(this.actor.getEntityId(), true, this.realPlayer, filename), (EntityPlayerMP) this.actor);
        }
    }

    /**
     * Stop playing
     */
    public void stopPlaying()
    {
        CommonProxy.manager.stop(this);

        this.actor.noClip = false;
    }

    public void applyFrame(int tick, EntityLivingBase target, boolean force)
    {
        tick -= this.record.preDelay;

        if (tick < 0)
        {
            tick = 0;
        }
        else if (tick >= this.record.frames.size())
        {
            tick = this.record.frames.size() - 1;
        }

        this.record.applyFrame(tick, target, force, this.realPlayer);
    }

    public void applyAction(int tick, EntityLivingBase target, boolean safe)
    {
        this.record.applyAction(tick - this.record.preDelay, target, safe);
    }

    public void sendToTracked(IMessage packet)
    {
        if (this.actor.world.getEntityByID(this.actor.getEntityId()) != this.actor)
        {
            this.unsentPackets.add(packet);
        }
        else
        {
            Dispatcher.sendToTracked(this.actor, packet);
        }
    }

    public void checkAndSpawn()
    {
        /* Checks whether actor isn't already spawned in the world */
        if (this.actor.world.getEntityByID(this.actor.getEntityId()) != this.actor)
        {
            if (this.actor instanceof EntityActor)
            {
                if (!this.actor.isDead)
                {
                    this.actor.world.spawnEntity(this.actor);

                    EntityPlayer player = ((EntityActor) this.actor).fakePlayer;

                    player.posX = this.actor.posX;
                    player.posY = this.actor.posY;
                    player.posZ = this.actor.posZ;

                    this.actor.world.loadedEntityList.add(((EntityActor) this.actor).fakePlayer);
                }
            }
            else if (this.actor instanceof EntityPlayer)
            {
                if (this.record.playerData != null)
                {
                    if (!this.realPlayer)
                    {
                        this.actor.readEntityFromNBT(this.record.playerData);
                    }

                    if (MPMHelper.isLoaded() && this.record.playerData.hasKey("MPMData", NBT.TAG_COMPOUND))
                    {
                        MPMHelper.setMPMData((EntityPlayer) this.actor, this.record.playerData.getCompoundTag("MPMData"));
                    }
                }

                if (!this.realPlayer && !this.actor.isDead)
                {
                    this.actor.world.getMinecraftServer().getPlayerList().playerLoggedIn((EntityPlayerMP) this.actor);
                }
            }

            while (!this.unsentPackets.isEmpty())
            {
                Dispatcher.sendToTracked(this.actor, this.unsentPackets.poll());
            }
        }
    }
}