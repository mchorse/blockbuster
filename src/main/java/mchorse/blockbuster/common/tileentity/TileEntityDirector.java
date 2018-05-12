package mchorse.blockbuster.common.tileentity;

import java.util.HashMap;
import java.util.Map;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.common.CommonProxy;
import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.common.tileentity.director.Replay;
import mchorse.blockbuster.recording.Utils;
import mchorse.blockbuster.recording.data.Mode;
import mchorse.blockbuster.recording.data.Record;
import mchorse.blockbuster.utils.EntityUtils;
import mchorse.metamorph.api.MorphAPI;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Director tile entity
 *
 * Used to store actors and manipulate block isPlaying state. Not really sure if
 * it's the best way to implement activation of the redstone (See update method
 * for more information).
 */
public class TileEntityDirector extends AbstractTileEntityDirector
{
    private Map<Replay, EntityActor> actors = new HashMap<Replay, EntityActor>();

    /* Public API */

    /**
     * Start a playback (make actors play their roles from the files)
     */
    @Override
    public void startPlayback()
    {
        this.startPlayback((EntityActor) null);
    }

    public void startPlayback(EntityActor exception)
    {
        this.startPlayback(exception, 0);
    }

    /**
     * The same thing as startPlayback, but don't play the actor that is passed
     * in the arguments (because he might be recorded by the player)
     */
    public void startPlayback(EntityActor exception, int tick)
    {
        if (this.worldObj.isRemote || this.isPlaying())
        {
            return;
        }

        if (this.replays.isEmpty())
        {
            return;
        }

        for (Replay replay : this.replays)
        {
            if (replay.id.isEmpty())
            {
                Utils.broadcastError("director.empty_filename");

                return;
            }
        }

        this.collectActors();

        EntityActor firstActor = null;

        for (Map.Entry<Replay, EntityActor> entry : this.actors.entrySet())
        {
            Replay replay = entry.getKey();
            EntityActor actor = entry.getValue();
            boolean notAttached = replay.actor == null;

            if (actor == exception) continue;

            if (firstActor == null)
            {
                firstActor = actor;
            }

            actor.startPlaying(replay.id, tick, notAttached && !this.loops);

            if (notAttached)
            {
                this.worldObj.spawnEntityInWorld(actor);
            }
            else
            {
                actor.directorBlock = this.getPos();
            }
        }

        this.playBlock(true);

        CommonProxy.manager.addDamageControl(this, firstActor);
    }

    /**
     * The same thing as startPlayback, but don't play the replay that is passed
     * in the arguments (because he might be recorded by the player)
     *
     * Used by recording code.
     */
    public void startPlayback(String exception)
    {
        if (this.worldObj.isRemote || this.isPlaying())
        {
            return;
        }

        this.collectActors();

        EntityActor firstActor = null;

        for (Map.Entry<Replay, EntityActor> entry : this.actors.entrySet())
        {
            Replay replay = entry.getKey();
            EntityActor actor = entry.getValue();
            boolean notAttached = replay.actor == null;

            if (replay.id.equals(exception)) continue;

            if (firstActor == null)
            {
                firstActor = actor;
            }

            actor.startPlaying(replay.id, notAttached);

            if (notAttached)
            {
                this.worldObj.spawnEntityInWorld(actor);
            }
        }

        this.playBlock(true);
    }

    /**
     * Collect actors.
     *
     * This method is responsible for collecting actors the ones that in the
     * world and also the ones that doesn't exist (they will be created and
     * spawned later on).
     */
    private void collectActors()
    {
        boolean dirty = false;

        this.actors.clear();

        for (Replay replay : this.replays)
        {
            EntityActor actor = null;

            if (replay.actor != null)
            {
                actor = (EntityActor) EntityUtils.entityByUUID(this.worldObj, replay.actor);

                if (actor == null)
                {
                    replay.actor = null;
                    dirty = true;
                }
            }

            if (actor == null)
            {
                actor = new EntityActor(this.worldObj);
                actor.wasAttached = true;
            }

            replay.apply(actor);
            actor.notifyPlayers();
            this.actors.put(replay, actor);
        }

        if (dirty)
        {
            this.markDirty();
        }
    }

    /**
     * Force stop playback
     */
    @Override
    public void stopPlayback()
    {
        this.stopPlayback(null);
    }

    /**
     * Force stop playback (except one actor)
     */
    public void stopPlayback(EntityActor exception)
    {
        if (this.worldObj.isRemote || !this.isPlaying())
        {
            return;
        }

        for (Map.Entry<Replay, EntityActor> entry : this.actors.entrySet())
        {
            EntityActor actor = entry.getValue();

            if (actor == exception) continue;

            actor.stopPlaying();
            actor.noClip = false;
        }

        CommonProxy.manager.restoreDamageControl(this, this.worldObj);

        this.actors.clear();
        this.playBlock(false);
    }

    /**
     * Spawns actors at given tick in idle mode. This is pretty useful for
     * positioning cameras for exact positions.
     */
    @Override
    public boolean spawn(int tick)
    {
        if (this.replays.isEmpty())
        {
            return false;
        }

        if (!this.actors.isEmpty())
        {
            this.stopPlayback();
        }

        for (Replay replay : this.replays)
        {
            if (replay.id.isEmpty())
            {
                Utils.broadcastError("director.empty_filename");

                return false;
            }
        }

        this.collectActors();
        this.playBlock(true);

        int j = 0;

        for (Map.Entry<Replay, EntityActor> entry : this.actors.entrySet())
        {
            Replay replay = entry.getKey();
            EntityActor actor = entry.getValue();
            boolean notAttached = replay.actor == null;

            if (j == 0)
            {
                CommonProxy.manager.addDamageControl(this, actor);
            }

            actor.startPlaying(replay.id, notAttached);

            if (actor.playback != null)
            {
                actor.playback.playing = false;
                actor.playback.record.applyFrame(tick, actor, true);
                actor.noClip = true;

                for (int i = 0; i <= tick; i++)
                {
                    actor.playback.record.applyAction(i, actor);
                }
            }

            if (notAttached)
            {
                this.worldObj.spawnEntityInWorld(actor);
            }

            j++;
        }

        return true;
    }

    @Override
    public void update()
    {
        if (Blockbuster.proxy.config.debug_playback_ticks && !this.actors.isEmpty())
        {
            EntityActor actor = this.actors.values().iterator().next();

            if (actor.playback != null)
            {
                Blockbuster.LOGGER.info("Director tick: " + actor.playback.getTick());
            }
        }

        super.update();
    }

    /**
     * Checks if are actors are still playing. This method gets invoked from
     * abstract parent in the tick method.
     */
    @Override
    protected void areActorsStillPlaying()
    {
        int count = 0;

        for (EntityActor actor : this.actors.values())
        {
            if (actor.playback == null || actor.isDead)
            {
                count++;
            }
        }

        if (count == this.replays.size())
        {
            if (this.loops)
            {
                /* TODO: improve looping */
                for (Map.Entry<Replay, EntityActor> entry : this.actors.entrySet())
                {
                    Replay replay = entry.getKey();
                    EntityActor actor = entry.getValue();
                    boolean notAttached = replay.actor == null;

                    actor.stopPlaying();
                    actor.startPlaying(replay.id, 0, notAttached && !this.loops);
                    actor.directorBlock = this.getPos();
                }
            }
            else
            {
                this.stopPlayback();
                this.playBlock(false);
            }
        }
    }

    /**
     * Start recording player
     */
    public void startRecording(final EntityActor actor, final EntityPlayer player)
    {
        final Replay replay = this.byActor(actor);

        if (replay != null)
        {
            CommonProxy.manager.startRecording(replay.id, player, Mode.ACTIONS, true, new Runnable()
            {
                @Override
                public void run()
                {
                    if (!CommonProxy.manager.recorders.containsKey(player))
                    {
                        TileEntityDirector.this.startPlayback(actor);
                    }
                    else
                    {
                        TileEntityDirector.this.stopPlayback(actor);
                    }

                    TileEntityDirector.this.applyReplay(replay, player);
                }
            });
        }
    }

    /**
     * Start recording player
     */
    public void startRecording(final String filename, final EntityPlayer player)
    {
        final Replay replay = this.byFile(filename);

        if (replay != null)
        {
            CommonProxy.manager.startRecording(replay.id, player, Mode.ACTIONS, true, new Runnable()
            {
                @Override
                public void run()
                {
                    if (!CommonProxy.manager.recorders.containsKey(player))
                    {
                        TileEntityDirector.this.startPlayback(filename);
                    }
                    else
                    {
                        TileEntityDirector.this.stopPlayback();
                    }

                    TileEntityDirector.this.applyReplay(replay, player);
                }
            });
        }
    }

    /**
     * Start recording player
     */
    public void applyReplay(Replay replay, EntityPlayer player)
    {
        if (replay == null) return;

        MorphAPI.morph(player, replay.morph, true);
    }

    /**
     * Get a replay by actor. Comparison is based on actor's UUID.
     */
    public Replay byActor(EntityActor actor)
    {
        for (Replay replay : this.replays)
        {
            if (replay.actor != null && replay.actor.equals(actor.getUniqueID())) return replay;
        }

        return null;
    }

    /**
     * Get a replay by actor. Comparison is based on actor's UUID.
     */
    public Replay byFile(String filename)
    {
        for (Replay replay : this.replays)
        {
            if (replay.id.equals(filename)) return replay;
        }

        return null;
    }

    /**
     * Pause the director block playback (basically, pause all actors)
     */
    public void pause()
    {
        for (EntityActor actor : this.actors.values())
        {
            actor.pause();
        }
    }

    /**
     * Resume paused director block playback (basically, resume all actors)
     */
    public void resume(int tick)
    {
        for (EntityActor actor : this.actors.values())
        {
            actor.resume(tick);
        }
    }

    /**
     * Make actors go to the given tick
     */
    public void goTo(int tick, boolean actions)
    {
        for (Map.Entry<Replay, EntityActor> entry : this.actors.entrySet())
        {
            if (tick == 0)
            {
                entry.getKey().apply(entry.getValue());
            }

            entry.getValue().goTo(tick, actions);
        }
    }

    /**
     * Get maximum length of current director block
     */
    public int getMaxLength()
    {
        int max = 0;

        for (Replay replay : this.replays)
        {
            Record record = null;

            try
            {
                record = CommonProxy.manager.getRecord(replay.id);
            }
            catch (Exception e)
            {}

            if (record != null)
            {
                max = Math.max(max, record.getLength());
            }
        }

        return max;
    }
}