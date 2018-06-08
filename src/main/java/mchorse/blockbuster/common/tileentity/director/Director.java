package mchorse.blockbuster.common.tileentity.director;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.common.CommonProxy;
import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.common.tileentity.TileEntityDirector;
import mchorse.blockbuster.recording.Utils;
import mchorse.blockbuster.recording.data.Record;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

/**
 * Director data class
 * 
 * This class is responsible for holding information about replays
 */
public class Director
{
    /**
     * Pattern for finding numbered
     */
    public static final Pattern NUMBERED_SUFFIX = Pattern.compile("_(\\d+)$");

    /**
     * List of replays
     */
    public List<Replay> replays = new ArrayList<Replay>();

    /**
     * Command which should be executed when director block starts 
     * playing 
     */
    public String startCommand = "";

    /**
     * Command which should be executed when director block stops 
     * playing  
     */
    public String stopCommand = "";

    /**
     * Whether this director block is playing 
     */
    public boolean playing;

    /**
     * Whether director's playback is looping
     */
    public boolean loops;

    /**
     * Whether director block's state changes are disabled
     */
    public boolean disableStates;

    /**
     * Map of currently playing actors 
     */
    private Map<Replay, EntityActor> actors = new HashMap<Replay, EntityActor>();

    /**
     * Reference to owning tile entity 
     */
    private TileEntityDirector tile;

    public Director(TileEntityDirector tile)
    {
        this.tile = tile;
    }

    /* Info accessors */

    /**
     * Get a replay by actor. Comparison is based on actor's UUID.
     */
    public Replay getByFile(String filename)
    {
        for (Replay replay : this.replays)
        {
            if (replay.id.equals(filename))
            {
                return replay;
            }
        }

        return null;
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

    /**
     * Check whether collected actors are still playing 
     */
    public boolean areActorsPlay()
    {
        int count = 0;

        for (EntityActor actor : this.actors.values())
        {
            if (actor.playback == null || actor.isDead)
            {
                count++;
            }
        }

        return count == this.replays.size();
    }

    /* Playback and editing */

    /**
     * Check whether actors are still playing, if they're stop the whole 
     * thing 
     */
    public void checkActors()
    {
        if (this.areActorsPlay())
        {
            if (this.loops)
            {
                /* TODO: improve looping */
                for (Map.Entry<Replay, EntityActor> entry : this.actors.entrySet())
                {
                    Replay replay = entry.getKey();
                    EntityActor actor = entry.getValue();
                    boolean notAttached = true;

                    actor.stopPlaying();
                    actor.startPlaying(replay.id, 0, notAttached && !this.loops);
                }
            }
            else
            {
                this.stopPlayback();
            }
        }
    }

    /**
     * The same thing as startPlayback, but don't play the actor that is passed
     * in the arguments (because he might be recorded by the player)
     */
    public void startPlayback(int tick)
    {
        if (this.tile.getWorld().isRemote || this.playing || this.replays.isEmpty())
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

            if (firstActor == null)
            {
                firstActor = actor;
            }

            actor.startPlaying(replay.id, tick, !this.loops);

            this.tile.getWorld().spawnEntityInWorld(actor);
        }

        this.setPlaying(true);

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
        if (this.tile.getWorld().isRemote || this.playing)
        {
            return;
        }

        this.collectActors();

        EntityActor firstActor = null;

        for (Map.Entry<Replay, EntityActor> entry : this.actors.entrySet())
        {
            Replay replay = entry.getKey();
            EntityActor actor = entry.getValue();
            boolean notAttached = true;

            if (replay.id.equals(exception)) continue;

            if (firstActor == null)
            {
                firstActor = actor;
            }

            actor.startPlaying(replay.id, notAttached);

            if (notAttached)
            {
                this.tile.getWorld().spawnEntityInWorld(actor);
            }
        }

        this.setPlaying(true);
    }

    /**
     * Spawns actors at given tick in idle mode. This is pretty useful for
     * positioning cameras for exact positions.
     */
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
        this.setPlaying(true);

        int j = 0;

        for (Map.Entry<Replay, EntityActor> entry : this.actors.entrySet())
        {
            Replay replay = entry.getKey();
            EntityActor actor = entry.getValue();
            boolean notAttached = true;

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
                this.tile.getWorld().spawnEntityInWorld(actor);
            }

            j++;
        }

        return true;
    }

    /**
     * Force stop playback (except one actor)
     */
    public void stopPlayback()
    {
        if (this.tile.getWorld().isRemote || !this.playing)
        {
            return;
        }

        for (Map.Entry<Replay, EntityActor> entry : this.actors.entrySet())
        {
            EntityActor actor = entry.getValue();

            actor.stopPlaying();
            actor.noClip = false;
        }

        CommonProxy.manager.restoreDamageControl(this, this.tile.getWorld());

        this.actors.clear();
        this.setPlaying(false);
    }

    /**
     * Toggle scene's playback
     */
    public boolean togglePlayback()
    {
        if (this.playing)
        {
            this.stopPlayback();
        }
        else
        {
            this.startPlayback(0);
        }

        return this.playing;
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

            if (actor == null)
            {
                actor = new EntityActor(this.tile.getWorld());
                actor.wasAttached = true;
            }

            replay.apply(actor);
            this.actors.put(replay, actor);
        }
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
     * Duplicate  
     */
    public void dupe(int index)
    {
        Replay replay = this.replays.get(index).clone(this.tile.getWorld().isRemote);
        Matcher matcher = NUMBERED_SUFFIX.matcher(replay.id);

        String prefix = replay.id;
        boolean found = matcher.find();
        int max = 0;

        if (found)
        {
            prefix = replay.id.substring(0, matcher.start());
        }

        for (Replay other : this.replays)
        {
            if (other.id.startsWith(prefix))
            {
                matcher = NUMBERED_SUFFIX.matcher(other.id);

                if (matcher.find() && other.id.substring(0, matcher.start()).equals(prefix))
                {
                    max = Math.max(max, Integer.parseInt(matcher.group(1)));
                }
            }
        }

        replay.id = prefix + "_" + (max + 1);
        this.replays.add(replay);
    }

    /**
     * Set this director playing
     */
    public void setPlaying(boolean playing)
    {
        this.playing = playing;
        this.tile.playBlock(playing);
    }

    /**
     * Log first actor's ticks (for debug purposes) 
     */
    public void logTicks()
    {
        if (this.actors.isEmpty())
        {
            return;
        }

        EntityActor actor = this.actors.values().iterator().next();

        if (actor.playback != null)
        {
            Blockbuster.LOGGER.info("Director tick: " + actor.playback.getTick());
        }
    }

    /* NBT methods */

    public void fromNBT(NBTTagCompound compound)
    {
        this.replays.clear();

        NBTTagList tagList = compound.getTagList("Actors", 10);

        for (int i = 0; i < tagList.tagCount(); i++)
        {
            Replay replay = new Replay();

            replay.fromNBT(tagList.getCompoundTagAt(i));
            this.replays.add(replay);
        }

        this.startCommand = compound.getString("StartCommand");
        this.stopCommand = compound.getString("StopCommand");
        this.loops = compound.getBoolean("Loops");
        this.disableStates = compound.getBoolean("DisableState");
    }

    public void toNBT(NBTTagCompound compound)
    {
        NBTTagList tagList = new NBTTagList();

        for (int i = 0; i < this.replays.size(); i++)
        {
            NBTTagCompound tag = new NBTTagCompound();

            this.replays.get(i).toNBT(tag);
            tagList.appendTag(tag);
        }

        compound.setTag("Actors", tagList);
        compound.setString("StartCommand", this.startCommand);
        compound.setString("StopCommand", this.stopCommand);
        compound.setBoolean("Loops", this.loops);
        compound.setBoolean("DisableState", this.disableStates);
    }
}