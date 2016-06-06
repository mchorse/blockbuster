package noname.blockbuster.tileentity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import noname.blockbuster.block.DirectorBlock;
import noname.blockbuster.entity.ActorEntity;
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

    /* Read/write this TE to disk */

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);

        NBTTagList list = compound.getTagList("Actors", 8);

        this.actors.clear();

        for (int i = 0; i < list.tagCount(); i++)
        {
            this.actors.add(list.getStringTagAt(i));
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);

        NBTTagList list = new NBTTagList();

        if (!this.actors.isEmpty())
        {
            for (int i = 0; i < this.actors.size(); i++)
            {
                list.appendTag(new NBTTagString(this.actors.get(i)));
            }
        }

        compound.setTag("Actors", list);
    }

    /* Public API */

    /**
     * Add an actor to this director block (dah, TE is part of the director
     * block)
     */
    public boolean addActor(String id)
    {
        if (!this.actors.contains(id))
        {
            this.actors.add(id);
            this.markDirty();

            return true;
        }

        return false;
    }

    /**
     * Start a playback
     */
    public void startPlayback()
    {
        if (this.worldObj.isRemote)
        {
            return;
        }

        for (String id : this.actors)
        {
            ActorEntity actor = (ActorEntity) Mocap.entityByUUID(this.worldObj, UUID.fromString(id));

            if (actor == null)
            {
                continue;
            }

            if (Mocap.playbacks.containsKey(actor))
            {
                Mocap.broadcastMessage("Actor is already playing!");
                return;
            }

            actor.startPlaying();
        }

        this.playBlock(true);
    }

    /* Tick-tock */

    private int tick = 0;

    /**
     * Checks every 4 ticks if the actors (that registered by this TE) are
     * still playing their roles.
     */
    @Override
    public void update()
    {
        DirectorBlock block = (DirectorBlock) this.getBlockType();

        if (!block.isPlaying || this.worldObj.isRemote)
        {
            return;
        }

        if (this.tick > 0)
        {
            this.tick--;
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
    }
}
