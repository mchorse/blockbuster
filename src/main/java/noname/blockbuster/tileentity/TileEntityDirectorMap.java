package noname.blockbuster.tileentity;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
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

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate)
    {
        return false;
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

        for (String replay : this.actors)
        {
            String[] splits = replay.split(":");
            Entity entity = null;

            if (splits.length == 2)
            {
                entity = Mocap.startPlayback(splits[0], splits[0], splits[1], this.worldObj, true);
            }
            else if (splits.length == 1)
            {
                entity = Mocap.startPlayback(splits[0], splits[0], splits[0], this.worldObj, true);
            }

            this.actorMap.put(replay, (EntityActor) entity);
        }

        this.playBlock(true);
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
