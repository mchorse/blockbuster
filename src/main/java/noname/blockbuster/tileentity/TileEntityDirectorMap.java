package noname.blockbuster.tileentity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import noname.blockbuster.entity.EntityActor;
import noname.blockbuster.recording.Mocap;
import noname.blockbuster.recording.PlayThread;

/**
 * Director map tile entity
 *
 * This TE is responsible for main logic of  */
public class TileEntityDirectorMap extends AbstractTileEntityDirector
{
    /**
     * Temporary map of actor entities during playback. This map is used
     * to determine if the registered actors are still playing their roles.
     */
    protected Map<String, EntityActor> actorMap = new HashMap<String, EntityActor>();

    /**
     * Remove everything
     */
    public void reset()
    {
        this.actors = new ArrayList<String>();
        this.markDirty();
    }

    /**
     * Remove a replay
     */
    public void remove(String replay)
    {
        this.actors.remove(replay);
        this.markDirty();
    }

    /**
     * Add a replay string to list of actors
     */
    public boolean add(String replay)
    {
        if (!this.actors.contains(replay))
        {
            this.actors.add(replay);
            this.markDirty();

            return true;
        }

        return false;
    }

    /**
     * Starts a playback
     *
     * This method is different from the method in DirectorTileEntity, instead
     * of finding all entities and making them play, this method is basically
     * do the same thing as CommandPlay#execute, launching the playback
     * and adding new created entity to actors map.
     */
    @Override
    public void startPlayback()
    {
        if (this.isPlaying())
        {
            return;
        }

        this.removeDuplicates();

        for (String replay : this.actors)
        {
            this.actorMap.put(replay, Mocap.startPlayback(replay.split(":"), this.worldObj, true));
        }

        this.playBlock(true);
    }

    /**
     * Remove duplicates from actor list
     */
    private void removeDuplicates()
    {
        List<String> replays = new ArrayList<String>();
        Iterator<String> iterator = this.actors.iterator();

        while (iterator.hasNext())
        {
            String replay = iterator.next().split(":")[0];

            if (replays.contains(replay))
            {
                iterator.remove();
            }

            replays.add(replay);
        }

        replays.clear();
    }

    /**
     * Stop playback
     */
    @Override
    public void stopPlayback()
    {
        for (PlayThread thread : Mocap.playbacks.values())
        {
            if (this.actorMap.containsValue(thread.actor))
            {
                thread.playing = false;
            }
        }
    }

    /**
     * Does what it says to do – checking if the actors still playing their
     * roles (not finished playback).
     */
    @Override
    protected void areActorsStillPlaying()
    {
        int count = 0;

        for (String replay : this.actors)
        {
            if (Mocap.playbacks.containsKey(this.actorMap.get(replay)))
            {
                count++;
            }
        }

        if (count == 0)
        {
            this.playBlock(false);
            this.actorMap.clear();
        }
    }
}
