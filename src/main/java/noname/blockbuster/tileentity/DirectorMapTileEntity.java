package noname.blockbuster.tileentity;

import java.util.HashMap;
import java.util.Map;

import noname.blockbuster.entity.ActorEntity;
import noname.blockbuster.recording.Mocap;

public class DirectorMapTileEntity extends AbstractDirector
{
    protected Map<String, ActorEntity> actorMap = new HashMap<String, ActorEntity>();

    /**
     * Add an actor to this director block (dah, TE is part of the director
     * block)
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

    @Override
    public void startPlayback()
    {
        if (this.isPlaying())
        {
            return;
        }

        for (String replay : this.actors)
        {
            String[] splits = replay.split(":");

            if (splits.length == 2)
            {
                this.actorMap.put(replay, Mocap.startPlayback(splits[0], splits[0], splits[1], this.worldObj, true));
            }
            else if (splits.length == 1)
            {
                this.actorMap.put(replay, Mocap.startPlayback(splits[0], splits[0], splits[0], this.worldObj, true));
            }
        }

        this.playBlock(true);
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
