package mchorse.blockbuster.common.tileentity;

import java.util.ArrayList;
import java.util.List;

import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.common.tileentity.director.Replay;
import mchorse.blockbuster.utils.BlockPos;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Abstract Tile Entity Director
 *
 * This class is base class for director block's tile entities. This class
 * provides basic methods for changing state of the block, and defines
 * some abstract methods for playback.
 */
public abstract class AbstractTileEntityDirector extends TileEntity implements IUpdatePlayerListBox
{
    public List<Replay> replays = new ArrayList<Replay>();

    /**
     * This tick used for checking if actors still playing
     */
    private int tick = 0;

    @Override
    public boolean shouldRefresh(Block oldBlock, Block newBlock, int oldMeta, int newMeta, World world, int x, int y, int z)
    {
        return false;
    }

    /* Read/write this TE to disk */

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        this.readListFromNBT(compound, "Actors", this.replays);
    }

    @Override
    public void writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        this.saveListToNBT(compound, "Actors", this.replays);
    }

    /* NBT list utils */

    /**
     * Read replay typed list from NBT
     */
    protected void readListFromNBT(NBTTagCompound compound, String key, List<Replay> list)
    {
        NBTTagList tagList = compound.getTagList(key, 10);
        list.clear();

        for (int i = 0; i < tagList.tagCount(); i++)
        {
            Replay replay = new Replay();

            replay.fromNBT(tagList.getCompoundTagAt(i));
            list.add(replay);
        }
    }

    /**
     * Write replay typed list from NBT
     */
    protected void saveListToNBT(NBTTagCompound compound, String key, List<Replay> list)
    {
        NBTTagList tagList = new NBTTagList();

        for (int i = 0; i < list.size(); i++)
        {
            NBTTagCompound tag = new NBTTagCompound();

            list.get(i).toNBT(tag);
            tagList.appendTag(tag);
        }

        compound.setTag(key, tagList);
    }

    /* Public API */

    /**
     * Remove everything
     */
    public void reset()
    {
        this.replays = new ArrayList<Replay>();
        this.markDirty();
    }

    /**
     * Add a replay with given recording id
     */
    public void add(String id)
    {
        Replay replay = new Replay();
        replay.id = id;

        this.replays.add(replay);
    }

    /**
     * Add an actor to this director block (dah, TE is part of the director
     * block)
     */
    public boolean add(EntityActor actor)
    {
        boolean exist = false;
        Replay result = new Replay(actor);

        for (Replay replay : this.replays)
        {
            boolean hasActor = replay.actor != null && replay.actor.equals(actor.getUniqueID());
            boolean hasName = replay.name.equals(actor.getCustomNameTag());

            if (hasActor)
            {
                exist = true;
                break;
            }

            if (hasName && replay.actor == null)
            {
                replay.copy(actor);

                return true;
            }
        }

        if (!exist)
        {
            actor.directorBlock = new BlockPos(this.xCoord, this.yCoord, this.zCoord);

            this.replays.add(result);
            this.markDirty();

            return true;
        }

        return false;
    }

    /**
     * Edit a replay, find similar from given old replay and change it to a
     * new value.
     */
    public void edit(int index, Replay replay)
    {
        this.replays.set(index, replay);
        this.markDirty();
    }

    /**
     * Remove an actor by id.
     */
    public void remove(int id)
    {
        this.replays.remove(id);
        this.markDirty();
    }

    /**
     * Get the cast
     *
     * Basically, return all entities/entity ids for display
     */
    public List<Replay> getCast()
    {
        return this.replays;
    }

    /**
     * Start scene's playback
     */
    public abstract void startPlayback();

    /**
     * Stop scene's playback
     */
    public abstract void stopPlayback();

    /**
     * Toggle scene's playback
     */
    public boolean togglePlayback()
    {
        if (this.isPlaying())
        {
            this.stopPlayback();
        }
        else
        {
            this.startPlayback();
        }

        return this.isPlaying();
    }

    /**
     * Checks every 4 ticks if the actors (that registered by this TE) are
     * still playing their roles.
     */
    @Override
    public void update()
    {
        if (this.worldObj.isRemote || this.tick-- > 0 || !this.isPlaying())
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
    protected abstract void areActorsStillPlaying();

    /**
     * Set the state of the block playing (needed to update redstone thingy-stuff)
     */
    protected void playBlock(boolean isPlaying)
    {
        this.worldObj.setBlock(this.xCoord, this.yCoord, this.zCoord, this.getBlockType(), isPlaying ? 1 : 0, 3);
    }

    /**
     * Checks if block's state isPlaying is true
     */
    public boolean isPlaying()
    {
        return this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord) != 0;
    }
}