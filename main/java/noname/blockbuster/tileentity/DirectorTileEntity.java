package noname.blockbuster.tileentity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import noname.blockbuster.block.DirectorBlock;
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
public class DirectorTileEntity extends TileEntity implements ITickable
{
    public List<String> actors = new ArrayList<String>();
    public List<String> cameras = new ArrayList<String>();
    private int tick = 0;

    /* Read/write this TE to disk */

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);

        NBTTagList actors = compound.getTagList("Actors", 8);
        NBTTagList cameras = compound.getTagList("Cameras", 8);

        this.actors.clear();
        this.cameras.clear();

        for (int i = 0; i < actors.tagCount(); i++)
        {
            this.actors.add(actors.getStringTagAt(i));
        }

        for (int i = 0; i < cameras.tagCount(); i++)
        {
            this.cameras.add(cameras.getStringTagAt(i));
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);

        this.saveListToNBT(compound, "Actors", this.actors);
        this.saveListToNBT(compound, "Cameras", this.cameras);
    }

    private void saveListToNBT(NBTTagCompound compound, String key, List<String> list)
    {
        NBTTagList tagList = new NBTTagList();

        for (int i = 0; i < list.size(); i++)
        {
            tagList.appendTag(new NBTTagString(list.get(i)));
        }

        compound.setTag(key, tagList);
    }

    /* Public API */

    /**
     * Something like factory method*/
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
     * Add an actor to this director block (dah, TE is part of the director
     * block)
     */
    public boolean add(ActorEntity actor)
    {
        String id = actor.getUniqueID().toString();

        if (!this.actors.contains(id))
        {
            actor.directorBlock = this.getPos();

            this.actors.add(id);
            this.markDirty();

            return true;
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

        for (String id : this.actors)
        {
            ActorEntity actor = (ActorEntity) Mocap.entityByUUID(this.worldObj, UUID.fromString(id));

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

        CameraEntity newCamera = (CameraEntity) Mocap.entityByUUID(this.worldObj, UUID.fromString(this.cameras.get(index)));
        EntityPlayer player = (EntityPlayer) camera.getControllingPassenger();

        player.dismountRidingEntity();
        player.setPositionAndUpdate(newCamera.posX, newCamera.posY, newCamera.posZ);
        player.rotationYaw = newCamera.rotationYaw;
        player.rotationPitch = newCamera.rotationPitch;
        player.startRiding(newCamera);
    }

    /**
     * Checks every 4 ticks if the actors (that registered by this TE) are
     * still playing their roles.
     */
    @Override
    public void update()
    {
        DirectorBlock block = (DirectorBlock) this.getBlockType();

        if (!block.isPlaying || this.worldObj.isRemote || this.tick-- > 0)
        {
            return;
        }

        this.areActorsStillPlaying();
        this.tick = 4;
    }

    /**
     * Does what it says to do – checking if the actors still playing their
     * roles (not finished playback).
     */
    private void areActorsStillPlaying()
    {
        int count = 0;

        for (String id : this.actors)
        {
            ActorEntity actor = (ActorEntity) Mocap.entityByUUID(this.worldObj, UUID.fromString(id));

            if (!Mocap.playbacks.containsKey(actor))
            {
                count++;
            }
        }

        /* Shutdown, muthafucka! */
        if (count == this.actors.size())
        {
            this.playBlock(false);
        }
    }

    /**
     * Set the state of the block playing (needed to update redstone thingy-stuff)
     */
    private void playBlock(boolean isPlaying)
    {
        ((DirectorBlock) this.getBlockType()).isPlaying = isPlaying;
        this.worldObj.notifyNeighborsOfStateChange(this.getPos(), this.getBlockType());

        for (String id : this.cameras)
        {
            CameraEntity camera = (CameraEntity) Mocap.entityByUUID(this.worldObj, UUID.fromString(id));

            camera.setRecording(isPlaying, true);
        }
    }
}
