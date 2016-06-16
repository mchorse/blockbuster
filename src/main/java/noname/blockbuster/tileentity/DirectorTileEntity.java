package noname.blockbuster.tileentity;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import noname.blockbuster.entity.ActorEntity;
import noname.blockbuster.entity.CameraEntity;
import noname.blockbuster.recording.Mocap;

/**
 * Director tile entity
 *
 * Used to store actors and manipulate block isPlaying state. Not really sure
 * if it's the best way to implement activation of the redstone (See update
 * method for more information).
 */
public class DirectorTileEntity extends AbstractDirector
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
        if (entity instanceof CameraEntity)
        {
            return this.add((CameraEntity) entity);
        }
        else if (entity instanceof ActorEntity)
        {
            return this.add((ActorEntity) entity);
        }

        return false;
    }

    /**
     * Add a camera to director block
     */
    public boolean add(CameraEntity camera)
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
    public void startPlayback(ActorEntity exception)
    {
        if (this.worldObj.isRemote)
        {
            return;
        }

        this.removeUnusedEntities(this.actors);
        this.removeUnusedEntities(this.cameras);

        for (String id : this.actors)
        {
            ActorEntity actor = (ActorEntity) Mocap.entityByUUID(this.worldObj, id);

            if (actor == null || actor == exception)
            {
                continue;
            }

            actor.startPlaying();
        }

        this.playBlock(true);
    }

    /**
     * Switch (teleport) to another camera
     */
    public void switchTo(CameraEntity camera, int direction)
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

        CameraEntity newCamera = (CameraEntity) Mocap.entityByUUID(this.worldObj, this.cameras.get(index));
        EntityPlayer player = (EntityPlayer) camera.getControllingPassenger();

        player.dismountRidingEntity();
        player.setLocationAndAngles(newCamera.posX, newCamera.posY, newCamera.posZ, newCamera.rotationYaw, newCamera.rotationPitch);
        player.setRotationYawHead(newCamera.rotationYaw);
        player.startRiding(newCamera);
    }

    /**
     * Set the state of the block playing (needed to update redstone thingy-stuff)
     */
    @Override
    protected void playBlock(boolean isPlaying)
    {
        super.playBlock(isPlaying);

        for (String id : this.cameras)
        {
            CameraEntity camera = (CameraEntity) Mocap.entityByUUID(this.worldObj, id);

            camera.setRecording(isPlaying, true);
        }
    }
}
