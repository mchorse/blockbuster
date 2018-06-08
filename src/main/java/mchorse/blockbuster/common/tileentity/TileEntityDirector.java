package mchorse.blockbuster.common.tileentity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.common.CommonProxy;
import mchorse.blockbuster.common.block.BlockDirector;
import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.common.tileentity.director.Replay;
import mchorse.blockbuster.recording.Utils;
import mchorse.blockbuster.recording.data.Mode;
import mchorse.blockbuster.recording.data.Record;
import mchorse.blockbuster.utils.EntityUtils;
import mchorse.metamorph.api.MorphAPI;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Director tile entity
 *
 * Used to store actors and manipulate block isPlaying state. Not really sure if
 * it's the best way to implement activation of the redstone (See update method
 * for more information).
 */
public class TileEntityDirector extends TileEntity implements ITickable
{
    /**
     * Pattern for finding numbered
     */
    public static final Pattern NUMBERED_SUFFIX = Pattern.compile("_(\\d+)$");

    public List<Replay> replays = new ArrayList<Replay>();
    public boolean loops;
    public boolean disableStates;

    /**
     * This tick used for checking if actors still playing
     */
    private int tick = 0;

    private Map<Replay, EntityActor> actors = new HashMap<Replay, EntityActor>();

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate)
    {
        return oldState.getBlock() != newSate.getBlock();
    }

    /* Read/write this TE to disk */

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        this.readListFromNBT(compound, "Actors", this.replays);

        this.loops = compound.getBoolean("Loops");
        this.disableStates = compound.getBoolean("DisableState");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        this.saveListToNBT(compound, "Actors", this.replays);

        compound.setBoolean("Loops", this.loops);
        compound.setBoolean("DisableState", this.disableStates);

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
     * Also increments the numerical suffix
     */
    public void duplicate(int index)
    {
        Replay replay = this.replays.get(index).clone(this.worldObj.isRemote);
        Matcher matcher = NUMBERED_SUFFIX.matcher(replay.id);

        String prefix = replay.id;
        boolean found = matcher.find();
        int max = 0;

        if (found)
        {
            prefix = replay.id.substring(0, matcher.start());
        }

        for (Replay other : this.replays)
        {
            if (other.id.startsWith(prefix))
            {
                matcher = NUMBERED_SUFFIX.matcher(other.id);

                if (matcher.find() && other.id.substring(0, matcher.start()).equals(prefix))
                {
                    max = Math.max(max, Integer.parseInt(matcher.group(1)));
                }
            }
        }

        replay.id = prefix + "_" + (max + 1);
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
     * Start a playback (make actors play their roles from the files)
     */
    public void startPlayback()
    {
        this.startPlayback((EntityActor) null);
    }

    public void startPlayback(EntityActor exception)
    {
        this.startPlayback(exception, 0);
    }

    /**
     * The same thing as startPlayback, but don't play the actor that is passed
     * in the arguments (because he might be recorded by the player)
     */
    public void startPlayback(EntityActor exception, int tick)
    {
        if (this.worldObj.isRemote || this.isPlaying())
        {
            return;
        }

        if (this.replays.isEmpty())
        {
            return;
        }

        for (Replay replay : this.replays)
        {
            if (replay.id.isEmpty())
            {
                Utils.broadcastError("director.empty_filename");

                return;
            }
        }

        this.collectActors();

        EntityActor firstActor = null;

        for (Map.Entry<Replay, EntityActor> entry : this.actors.entrySet())
        {
            Replay replay = entry.getKey();
            EntityActor actor = entry.getValue();
            boolean notAttached = replay.actor == null;

            if (actor == exception) continue;

            if (firstActor == null)
            {
                firstActor = actor;
            }

            actor.startPlaying(replay.id, tick, notAttached && !this.loops);

            if (notAttached)
            {
                this.worldObj.spawnEntityInWorld(actor);
            }
            else
            {
                actor.directorBlock = this.getPos();
            }
        }

        this.playBlock(true);

        CommonProxy.manager.addDamageControl(this, firstActor);
    }

    /**
     * The same thing as startPlayback, but don't play the replay that is passed
     * in the arguments (because he might be recorded by the player)
     *
     * Used by recording code.
     */
    public void startPlayback(String exception)
    {
        if (this.worldObj.isRemote || this.isPlaying())
        {
            return;
        }

        this.collectActors();

        EntityActor firstActor = null;

        for (Map.Entry<Replay, EntityActor> entry : this.actors.entrySet())
        {
            Replay replay = entry.getKey();
            EntityActor actor = entry.getValue();
            boolean notAttached = replay.actor == null;

            if (replay.id.equals(exception)) continue;

            if (firstActor == null)
            {
                firstActor = actor;
            }

            actor.startPlaying(replay.id, notAttached);

            if (notAttached)
            {
                this.worldObj.spawnEntityInWorld(actor);
            }
        }

        this.playBlock(true);
    }

    /**
     * Collect actors.
     *
     * This method is responsible for collecting actors the ones that in the
     * world and also the ones that doesn't exist (they will be created and
     * spawned later on).
     */
    private void collectActors()
    {
        boolean dirty = false;

        this.actors.clear();

        for (Replay replay : this.replays)
        {
            EntityActor actor = null;

            if (replay.actor != null)
            {
                actor = (EntityActor) EntityUtils.entityByUUID(this.worldObj, replay.actor);

                if (actor == null)
                {
                    replay.actor = null;
                    dirty = true;
                }
            }

            if (actor == null)
            {
                actor = new EntityActor(this.worldObj);
                actor.wasAttached = true;
            }

            replay.apply(actor);
            actor.notifyPlayers();
            this.actors.put(replay, actor);
        }

        if (dirty)
        {
            this.markDirty();
        }
    }

    /**
     * Force stop playback
     */
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

        for (Map.Entry<Replay, EntityActor> entry : this.actors.entrySet())
        {
            EntityActor actor = entry.getValue();

            if (actor == exception) continue;

            actor.stopPlaying();
            actor.noClip = false;
        }

        CommonProxy.manager.restoreDamageControl(this, this.worldObj);

        this.actors.clear();
        this.playBlock(false);
    }

    /**
     * Spawns actors at given tick in idle mode. This is pretty useful for
     * positioning cameras for exact positions.
     */
    public boolean spawn(int tick)
    {
        if (this.replays.isEmpty())
        {
            return false;
        }

        if (!this.actors.isEmpty())
        {
            this.stopPlayback();
        }

        for (Replay replay : this.replays)
        {
            if (replay.id.isEmpty())
            {
                Utils.broadcastError("director.empty_filename");

                return false;
            }
        }

        this.collectActors();
        this.playBlock(true);

        int j = 0;

        for (Map.Entry<Replay, EntityActor> entry : this.actors.entrySet())
        {
            Replay replay = entry.getKey();
            EntityActor actor = entry.getValue();
            boolean notAttached = replay.actor == null;

            if (j == 0)
            {
                CommonProxy.manager.addDamageControl(this, actor);
            }

            actor.startPlaying(replay.id, notAttached);

            if (actor.playback != null)
            {
                actor.playback.playing = false;
                actor.playback.record.applyFrame(tick, actor, true);
                actor.noClip = true;

                for (int i = 0; i <= tick; i++)
                {
                    actor.playback.record.applyAction(i, actor);
                }
            }

            if (notAttached)
            {
                this.worldObj.spawnEntityInWorld(actor);
            }

            j++;
        }

        return true;
    }

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

    public void update()
    {
        if (Blockbuster.proxy.config.debug_playback_ticks && !this.actors.isEmpty())
        {
            EntityActor actor = this.actors.values().iterator().next();

            if (actor.playback != null)
            {
                Blockbuster.LOGGER.info("Director tick: " + actor.playback.getTick());
            }
        }

        boolean isRemote = this.worldObj.isRemote;

        if (isRemote || !this.isPlaying() || this.tick-- > 0)
        {
            return;
        }

        this.areActorsStillPlaying();
        this.tick = 4;
    }

    /**
     * Checks if are actors are still playing. This method gets invoked from
     * abstract parent in the tick method.
     */
    protected void areActorsStillPlaying()
    {
        int count = 0;

        for (EntityActor actor : this.actors.values())
        {
            if (actor.playback == null || actor.isDead)
            {
                count++;
            }
        }

        if (count == this.replays.size())
        {
            if (this.loops)
            {
                /* TODO: improve looping */
                for (Map.Entry<Replay, EntityActor> entry : this.actors.entrySet())
                {
                    Replay replay = entry.getKey();
                    EntityActor actor = entry.getValue();
                    boolean notAttached = replay.actor == null;

                    actor.stopPlaying();
                    actor.startPlaying(replay.id, 0, notAttached && !this.loops);
                    actor.directorBlock = this.getPos();
                }
            }
            else
            {
                this.stopPlayback();
                this.playBlock(false);
            }
        }
    }

    /**
     * Start recording player
     */
    public void startRecording(final EntityActor actor, final EntityPlayer player)
    {
        final Replay replay = this.byActor(actor);

        if (replay != null)
        {
            CommonProxy.manager.startRecording(replay.id, player, Mode.ACTIONS, true, new Runnable()
            {
                @Override
                public void run()
                {
                    if (!CommonProxy.manager.recorders.containsKey(player))
                    {
                        TileEntityDirector.this.startPlayback(actor);
                    }
                    else
                    {
                        TileEntityDirector.this.stopPlayback(actor);
                    }

                    TileEntityDirector.this.applyReplay(replay, player);
                }
            });
        }
    }

    /**
     * Start recording player
     */
    public void startRecording(final String filename, final EntityPlayer player)
    {
        final Replay replay = this.byFile(filename);

        if (replay != null)
        {
            CommonProxy.manager.startRecording(replay.id, player, Mode.ACTIONS, true, new Runnable()
            {
                @Override
                public void run()
                {
                    if (!CommonProxy.manager.recorders.containsKey(player))
                    {
                        TileEntityDirector.this.startPlayback(filename);
                    }
                    else
                    {
                        TileEntityDirector.this.stopPlayback();
                    }

                    TileEntityDirector.this.applyReplay(replay, player);
                }
            });
        }
    }

    /**
     * Start recording player
     */
    public void applyReplay(Replay replay, EntityPlayer player)
    {
        if (replay == null) return;

        MorphAPI.morph(player, replay.morph, true);
    }

    /**
     * Get a replay by actor. Comparison is based on actor's UUID.
     */
    public Replay byActor(EntityActor actor)
    {
        for (Replay replay : this.replays)
        {
            if (replay.actor != null && replay.actor.equals(actor.getUniqueID())) return replay;
        }

        return null;
    }

    /**
     * Get a replay by actor. Comparison is based on actor's UUID.
     */
    public Replay byFile(String filename)
    {
        for (Replay replay : this.replays)
        {
            if (replay.id.equals(filename)) return replay;
        }

        return null;
    }

    /**
     * Pause the director block playback (basically, pause all actors)
     */
    public void pause()
    {
        for (EntityActor actor : this.actors.values())
        {
            actor.pause();
        }
    }

    /**
     * Resume paused director block playback (basically, resume all actors)
     */
    public void resume(int tick)
    {
        for (EntityActor actor : this.actors.values())
        {
            actor.resume(tick);
        }
    }

    /**
     * Make actors go to the given tick
     */
    public void goTo(int tick, boolean actions)
    {
        for (Map.Entry<Replay, EntityActor> entry : this.actors.entrySet())
        {
            if (tick == 0)
            {
                entry.getKey().apply(entry.getValue());
            }

            entry.getValue().goTo(tick, actions);
        }
    }

    /**
     * Get maximum length of current director block
     */
    public int getMaxLength()
    {
        int max = 0;

        for (Replay replay : this.replays)
        {
            Record record = null;

            try
            {
                record = CommonProxy.manager.getRecord(replay.id);
            }
            catch (Exception e)
            {}

            if (record != null)
            {
                max = Math.max(max, record.getLength());
            }
        }

        return max;
    }

    /**
     * Set the state of the block playing (needed to update redstone thingy-stuff)
     */
    protected void playBlock(boolean isPlaying)
    {
        if (this.disableStates)
        {
            isPlaying = false;
        }

        this.worldObj.setBlockState(this.pos, this.worldObj.getBlockState(this.pos).withProperty(BlockDirector.PLAYING, isPlaying));
    }

    /**
     * Checks if block's state isPlaying is true
     */
    public boolean isPlaying()
    {
        return this.worldObj.getBlockState(this.pos).getValue(BlockDirector.PLAYING);
    }
}