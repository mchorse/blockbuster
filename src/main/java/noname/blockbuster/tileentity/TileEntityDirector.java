package noname.blockbuster.tileentity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import noname.blockbuster.entity.EntityActor;
import noname.blockbuster.entity.EntityCamera;
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
    public List<String> cameras = new ArrayList<String>();

    /* Read/write this TE to disk */

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        this.readListFromNBT(compound, "Cameras", this.cameras);
    }

    @Override
    public void writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        this.saveListToNBT(compound, "Cameras", this.cameras);
    }

    /* Public API */

    @Override
    public List<String> getCast()
    {
        List<String> cast = new ArrayList<String>();

        cast.addAll(this.actors);
        cast.addAll(this.cameras);

        return cast;
    }

    /**
     * Something like factory method
     */
    public boolean add(Entity entity)
    {
        if (entity instanceof EntityCamera)
        {
            return this.add((EntityCamera) entity);
        }
        else if (entity instanceof EntityActor)
        {
            return this.add((EntityActor) entity);
        }

        return false;
    }

    /**
     * Add a camera to director block
     */
    public boolean add(EntityCamera camera)
    {
        String id = camera.getUniqueID().toString();

        if (!this.cameras.contains(id))
        {
            camera.directorBlock = this.getPos();

            this.cameras.add(id);
            this.markDirty();

            return true;
        }

        return false;
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
        this.removeUnusedEntities(this.cameras);

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

    /**
     * Switch (teleport/jump) to another camera
     *
     * Some of the code has been looked up from classes such as CommandTeleport
     * and... that's it.
     */
    public void switchTo(EntityCamera camera, int direction)
    {
        int index = this.cameras.indexOf(camera.getUniqueID().toString()) + direction;

        if (index >= this.cameras.size())
        {
            index = 0;
        }
        else if (index < 0)
        {
            index = this.cameras.size() - 1;
        }

        EntityCamera newCamera = (EntityCamera) Mocap.entityByUUID(this.worldObj, this.cameras.get(index));
        EntityPlayer player = (EntityPlayer) camera.getControllingPassenger();

        player.dismountRidingEntity();
        player.setLocationAndAngles(newCamera.posX, newCamera.posY, newCamera.posZ, newCamera.rotationYaw, newCamera.rotationPitch);
        player.setRotationYawHead(newCamera.rotationYaw);
        player.startRiding(newCamera);
    }

    /**
     * Set the state of the block playing (needed to update redstone thingy-stuff)
     *
     * And make cameras invisible or visible (depending on passed boolean), so
     * they aren't seen in the main shot.
     */
    @Override
    protected void playBlock(boolean isPlaying)
    {
        super.playBlock(isPlaying);

        for (String id : this.cameras)
        {
            EntityCamera camera = (EntityCamera) Mocap.entityByUUID(this.worldObj, id);

            camera.setRecording(isPlaying, true);
        }
    }
}
