package mchorse.blockbuster.common.tileentity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mchorse.blockbuster.common.block.AbstractBlockDirector;
import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.common.tileentity.director.Replay;
import mchorse.blockbuster.utils.EntityUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Abstract Tile Entity Director
 *
 * This class is base class for director block's tile entities. This class
 * provides basic methods for changing state of the block, and defines
 * some abstract methods for playback.
 */
public abstract class AbstractTileEntityDirector extends TileEntity implements ITickable
{
    /**
     * Pattern for finding numbered
     */
    public static final Pattern NUMBERED_SUFFIX = Pattern.compile("_(\\d+)$");

    public List<String> _replays = new ArrayList<String>();
    public List<Replay> replays = new ArrayList<Replay>();

    /**
     * This tick used for checking if actors still playing
     */
    private int tick = 0;

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate)
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
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        this.saveListToNBT(compound, "Actors", this.replays);

        return compound;
    }

    /* NBT list utils */

    /**
     * Read replay typed list from NBT
     */
    protected void readListFromNBT(NBTTagCompound compound, String key, List<Replay> list)
    {
        NBTTagList tagList = compound.getTagList(key, 10);
        list.clear();

        if (tagList.tagCount() == 0)
        {
            NBTTagList ids = compound.getTagList(key, 8);

            for (int i = 0; i < ids.tagCount(); i++)
            {
                this._replays.add(ids.getStringTagAt(i));
            }

            return;
        }

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
            boolean hasName = actor.hasCustomName() ? replay.name.equals(actor.getCustomNameTag()) : false;

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
            actor.directorBlock = this.getPos();

            this.replays.add(result);
            this.markDirty();

            return true;
        }

        return false;
    }

    /**
     * Duplicate a replay by given index
     *
     * Also increments the numerical suffix, if it's not there suffix "_1" is
     * added.
     */
    public void duplicate(int index)
    {
        Replay replay = this.replays.get(index).clone(this.world.isRemote);
        Matcher matcher = NUMBERED_SUFFIX.matcher(replay.id);

        if (matcher.find())
        {
            replay.id = replay.id.substring(0, matcher.start()) + "_" + (Integer.parseInt(matcher.group(1)) + 1);
        }
        else
        {
            replay.id = replay.id + "_1";
        }

        this.replays.add(replay);
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
     * Spawn actors at given tick
     */
    public abstract void spawn(int tick);

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
        boolean isRemote = this.world.isRemote;

        if (!isRemote && !this._replays.isEmpty())
        {
            this.convertOldReplays();
        }

        if (isRemote || !this.isPlaying() || this.tick-- > 0)
        {
            return;
        }

        this.areActorsStillPlaying();
        this.tick = 4;
    }

    /**
     * Convert old replays to the new format.
     *
     * Unfortunately, it's impossible to recover old data without hacks and
     * workaround.
     */
    private void convertOldReplays()
    {
        Iterator<String> it = this._replays.iterator();

        while (it.hasNext())
        {
            String uuid = it.next();
            EntityActor actor = (EntityActor) EntityUtils.entityByUUID(this.world, uuid);

            if (actor != null && !actor._filename.isEmpty())
            {
                Replay replay = new Replay(actor);
                replay.id = actor._filename;

                this.replays.add(replay);
                it.remove();
            }
        }
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
        this.world.setBlockState(this.pos, this.world.getBlockState(this.pos).withProperty(AbstractBlockDirector.PLAYING, isPlaying));
    }

    /**
     * Checks if block's state isPlaying is true
     */
    public boolean isPlaying()
    {
        return this.world.getBlockState(this.pos).getValue(AbstractBlockDirector.PLAYING);
    }
}