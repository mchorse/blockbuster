package noname.blockbuster.tileentity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.Entity;
import noname.blockbuster.entity.EntityActor;
import noname.blockbuster.recording.Mocap;

/**
 * Director tile entity
 *
 * Used to store actors and manipulate block isPlaying state. Not really sure
 * if it's the best way to implement activation of the redstone (See update
 * method for more information).
 */
public class TileEntityDirector extends AbstractTileEntityDirector
{
    /* Public API */

    /**
     * Remove all registered actors from this TE
     */
    public void reset()
    {
        this.actors = new ArrayList<String>();
        this.markDirty();
    }

    /**
     * Remove an actor by id.
     */
    public void remove(int id)
    {
        this.actors.remove(id);
        this.markDirty();
    }

    /**
     * Start a playback (make actors play their roles from the files)
     */
    @Override
    public void startPlayback()
    {
        this.startPlayback(null);
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

        this.removeUnusedEntities(this.actors);

        for (EntityActor actor : this.getActors(exception))
        {
            actor.startPlaying();
        }

        this.playBlock(true);
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

        for (EntityActor actor : this.getActors(exception))
        {
            actor.stopPlaying();
        }

        this.playBlock(false);
    }

    /**
     * Remove unused entitites
     */
    protected void removeUnusedEntities(List<String> list)
    {
        Iterator<String> iterator = list.iterator();

        while (iterator.hasNext())
        {
            String id = iterator.next();
            Entity entity = Mocap.entityByUUID(this.worldObj, id);

            if (entity == null)
            {
                iterator.remove();
            }
        }
    }

    /**
     * Get all actors
     */
    public List<EntityActor> getActors(EntityActor exception)
    {
        List<EntityActor> actors = new ArrayList<EntityActor>();

        for (String id : this.actors)
        {
            EntityActor actor = (EntityActor) Mocap.entityByUUID(this.worldObj, id);

            if (actor == null || actor == exception)
            {
                continue;
            }

            actors.add(actor);
        }

        return actors;
    }
}