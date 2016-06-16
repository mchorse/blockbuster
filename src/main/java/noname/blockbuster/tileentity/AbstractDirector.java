package noname.blockbuster.tileentity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import noname.blockbuster.block.AbstractDirectorBlock;
import noname.blockbuster.entity.ActorEntity;
import noname.blockbuster.recording.Mocap;

public abstract class AbstractDirector extends TileEntity implements ITickable
{
    public List<String> actors = new ArrayList<String>();
    private int tick = 0;

    /* Read/write this TE to disk */

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        this.readListFromNBT(compound, "Actors", this.actors);
    }

    @Override
    public void writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        this.saveListToNBT(compound, "Actors", this.actors);
    }

    /* NBT list utils */

    /**
     * Read string typed list from NBT
     */
    protected void readListFromNBT(NBTTagCompound compound, String key, List<String> list)
    {
        NBTTagList tagList = compound.getTagList(key, 8);
        list.clear();

        for (int i = 0; i < tagList.tagCount(); i++)
        {
            list.add(tagList.getStringTagAt(i));
        }
    }

    /**
     * Write string typed list from NBT
     */
    protected void saveListToNBT(NBTTagCompound compound, String key, List<String> list)
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
     * Get the cast
     */
    public List<String> getCast()
    {
        return this.actors;
    }

    /**
     * Start scene's playback
     */
    public abstract void startPlayback();

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
     * Checks every 4 ticks if the actors (that registered by this TE) are
     * still playing their roles.
     */
    @Override
    public void update()
    {
        AbstractDirectorBlock block = (AbstractDirectorBlock) this.getBlockType();

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
    protected void areActorsStillPlaying()
    {
        int count = 0;

        for (String id : this.actors)
        {
            ActorEntity actor = (ActorEntity) Mocap.entityByUUID(this.worldObj, id);

            if (!Mocap.playbacks.containsKey(actor))
            {
                count++;
            }
        }

        if (count == this.actors.size())
        {
            this.playBlock(false);
        }
    }

    /**
     * Set the state of the block playing (needed to update redstone thingy-stuff)
     */
    protected void playBlock(boolean isPlaying)
    {
        ((AbstractDirectorBlock) this.getBlockType()).isPlaying = isPlaying;
        this.worldObj.notifyNeighborsOfStateChange(this.getPos(), this.getBlockType());
    }

    protected boolean isPlaying()
    {
        return ((AbstractDirectorBlock) this.getBlockType()).isPlaying;
    }
}
