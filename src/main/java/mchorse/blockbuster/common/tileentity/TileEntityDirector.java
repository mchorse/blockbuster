package mchorse.blockbuster.common.tileentity;

import java.util.HashMap;
import java.util.Map;

import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.common.tileentity.director.Replay;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.PacketMorph;
import mchorse.blockbuster.recording.Mocap;
import mchorse.blockbuster.recording.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * Director tile entity
 *
 * Used to store actors and manipulate block isPlaying state. Not really sure
 * if it's the best way to implement activation of the redstone (See update
 * method for more information).
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
     * The same thing as startPlayback, but don't play the actor that is
     * passed in the arguments (because he might be recorded by the player)
     */
    public void startPlayback(EntityActor exception)
    {
        if (this.worldObj.isRemote || this.isPlaying())
        {
            return;
        }

        this.collectActors();

        for (Map.Entry<Replay, EntityActor> entry : this.actors.entrySet())
        {
            Replay replay = entry.getKey();
            EntityActor actor = entry.getValue();
            boolean notAttached = replay.actor == null;

            if (actor == exception) continue;

            actor.startPlaying(replay.id, notAttached);

            if (notAttached)
            {
                this.worldObj.spawnEntityInWorld(actor);
            }
        }

        this.playBlock(true);
    }

    /**
     * The same thing as startPlayback, but don't play the replay that is
     * passed in the arguments (because he might be recorded by the player)
     */
    public void startPlayback(String exception)
    {
        if (this.worldObj.isRemote || this.isPlaying())
        {
            return;
        }

        this.collectActors();

        for (Map.Entry<Replay, EntityActor> entry : this.actors.entrySet())
        {
            Replay replay = entry.getKey();
            EntityActor actor = entry.getValue();
            boolean notAttached = replay.actor == null;

            if (replay.id.equals(exception)) continue;

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
        this.actors.clear();

        for (Replay replay : this.replays)
        {
            EntityActor actor = null;

            if (replay.actor != null)
            {
                actor = (EntityActor) Utils.entityByUUID(this.worldObj, replay.actor);
            }

            if (actor == null)
            {
                actor = new EntityActor(this.worldObj);
            }

            replay.apply(actor);
            actor.notifyPlayers();
            this.actors.put(replay, actor);
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
            Replay replay = entry.getKey();
            EntityActor actor = entry.getValue();

            if (actor == exception) continue;

            actor.stopPlaying();
        }

        this.actors.clear();
        this.playBlock(false);
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
            if (!Mocap.playbacks.containsKey(actor)) count++;
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
    public void startRecording(EntityActor actor, EntityPlayer player)
    {
        Replay replay = this.byActor(actor);

        if (replay != null)
        {
            this.applyReplay(replay, player);
            Mocap.startRecording(replay.id, player);
        }
    }

    /**
     * Start recording player
     */
    public void applyReplay(Replay replay, EntityPlayer player)
    {
        if (replay == null) return;

        Dispatcher.sendTo(new PacketMorph(replay.model, replay.skin), (EntityPlayerMP) player);
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
