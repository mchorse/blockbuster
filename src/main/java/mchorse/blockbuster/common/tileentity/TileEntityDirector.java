package mchorse.blockbuster.common.tileentity;

import java.util.HashMap;
import java.util.Map;

import mchorse.blockbuster.common.CommonProxy;
import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.common.tileentity.director.Replay;
import mchorse.blockbuster.recording.Utils;
import mchorse.blockbuster.recording.data.Mode;
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

    /**
     * The same thing as startPlayback, but don't play the actor that is passed
     * in the arguments (because he might be recorded by the player)
     */
    public void startPlayback(EntityActor exception)
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

            actor.startPlaying(replay.id, notAttached);

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

        this.actors.clear();
        this.playBlock(false);

        CommonProxy.manager.restoreDamageControl(this, this.worldObj);
    }

    /**
     * Spawns actors at given tick in idle mode. This is pretty useful for
     * positioning cameras for exact positions.
     */
    @Override
    public void spawn(int tick)
    {
        if (this.replays.isEmpty())
        {
            return;
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

                return;
            }
        }

        this.collectActors();

        for (Map.Entry<Replay, EntityActor> entry : this.actors.entrySet())
        {
            Replay replay = entry.getKey();
            EntityActor actor = entry.getValue();
            boolean notAttached = replay.actor == null;

            actor.startPlaying(replay.id, notAttached);

            if (actor.playback != null)
            {
                actor.playback.playing = false;
                actor.playback.record.applyFrame(tick, actor, true);
                actor.noClip = true;
            }

            if (notAttached)
            {
                this.worldObj.spawnEntityInWorld(actor);
            }
        }

        this.playBlock(true);
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
            this.stopPlayback();
            this.playBlock(false);
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
}