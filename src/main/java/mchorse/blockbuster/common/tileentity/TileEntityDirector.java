package mchorse.blockbuster.common.tileentity;

import java.util.List;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.CommonProxy;
import mchorse.blockbuster.common.block.BlockDirector;
import mchorse.blockbuster.recording.director.Director;
import mchorse.blockbuster.recording.director.DirectorSender;
import mchorse.blockbuster.recording.director.Replay;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.director.PacketConfirmBreak;
import mchorse.blockbuster.network.common.director.PacketDirectorCast;
import mchorse.blockbuster.recording.data.Mode;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFlowerPot;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Director tile entity
 *
 * Used to store actors and manipulate block isPlaying state. Not really sure if
 * it's the best way to implement activation of the redstone (See update method
 * for more information).
 */
public class TileEntityDirector extends TileEntityFlowerPot implements ITickable
{
    public static int playing = 0;

    /**
     * Director instance which is responsible for managing and storing 
     * director block information and actors
     */
    public Director director;

    public TileEntityDirector()
    {
        this.director = new Director(this);
        this.director.setSender(new DirectorSender(this.director));
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate)
    {
        return oldState.getBlock() != newSate.getBlock();
    }

    /**
     * Debug ticks and check whether actors are still playing 
     */
    @Override
    public void update()
    {
        if (this.director.hide)
        {
            IBlockState state = this.getWorld().getBlockState(this.pos);

            /* Somehow this happens, so I must check before accessing 
             * any of the block properties */
            if (state.getBlock() != Blockbuster.directorBlock)
            {
                this.invalidate();

                return;
            }

            boolean hidden = state.getValue(BlockDirector.HIDDEN);

            if (playing > 0 && !hidden)
            {
                this.getWorld().setBlockState(this.pos, state.withProperty(BlockDirector.HIDDEN, true));
            }
            else if (playing <= 0 && hidden)
            {
                this.getWorld().setBlockState(this.pos, state.withProperty(BlockDirector.HIDDEN, false));
            }
        }

        boolean isRemote = this.worldObj.isRemote;

        if (isRemote || !this.isPlaying())
        {
            return;
        }

        this.director.tick();
    }

    /* Read/write this TE to disk */

    @Override
    public SPacketUpdateTileEntity getUpdatePacket()
    {
        return new SPacketUpdateTileEntity(this.pos, 5, this.getUpdateTag());
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);

        this.director.fromNBT(compound);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);

        this.director.toNBT(compound);

        return compound;
    }

    /* Getters */

    /**
     * Get the cast
     *
     * Basically, return all entities/entity ids for display
     */
    public List<Replay> getCast()
    {
        return this.director.replays;
    }

    /* Public API */

    /**
     * Remove everything
     */
    public void reset()
    {
        this.director.replays.clear();
        this.markDirty();
    }

    /**
     * Add a replay with given recording id
     */
    public void add(String id)
    {
        this.director.replays.add(new Replay(id));
        this.markDirty();
    }

    /**
     * Duplicate a replay on given index
     */
    public void duplicate(int index)
    {
        this.director.dupe(index, this.worldObj.isRemote);
        this.markDirty();
    }

    /**
     * Edit a replay, find similar from given old replay and change it to a
     * new value.
     */
    public void edit(int index, Replay replay)
    {
        this.director.replays.set(index, replay);
        this.markDirty();
    }

    /**
     * Remove an actor by id.
     */
    public void remove(int id)
    {
        this.director.replays.remove(id);
        this.markDirty();
    }

    /**
     * Start recording player
     */
    public void startRecording(final String filename, final EntityPlayer player)
    {
        final Replay replay = this.director.getByFile(filename);

        if (replay != null)
        {
            CommonProxy.manager.record(replay.id, player, Mode.ACTIONS, true, new Runnable()
            {
                @Override
                public void run()
                {
                    if (!CommonProxy.manager.recorders.containsKey(player))
                    {
                        TileEntityDirector.this.director.startPlayback(filename);
                    }
                    else
                    {
                        TileEntityDirector.this.director.stopPlayback();
                    }

                    replay.apply(player);
                }
            });
        }
    }

    /**
     * Set the state of the block playing (needed to update redstone 
     * thingy-stuff)
     */
    public void playBlock(boolean isPlaying)
    {
        this.playBlock(isPlaying, this.director.hide ? isPlaying : false);
    }

    /**
     * Set the state of the block playing (needed to update redstone 
     * thingy-stuff)
     */
    public void playBlock(boolean isPlaying, boolean isHidden)
    {
        IBlockState state = this.worldObj.getBlockState(this.pos);

        if (state.getBlock() != Blockbuster.directorBlock)
        {
            this.invalidate();

            return;
        }

        if (!this.director.disableStates)
        {
            state = state.withProperty(BlockDirector.PLAYING, isPlaying);
        }

        state = state.withProperty(BlockDirector.HIDDEN, isHidden);

        this.worldObj.setBlockState(this.getPos(), state);
    }

    /**
     * Checks if block's state isPlaying is true
     */
    public boolean isPlaying()
    {
        return this.director.playing;
    }

    /* Packet methods */

    /**
     * Open the GUI for the player 
     */
    public void open(EntityPlayer player, BlockPos pos)
    {
        if (player instanceof EntityPlayerMP)
        {
            Dispatcher.sendTo(new PacketDirectorCast(pos, this.director), (EntityPlayerMP) player);
        }
    }

    /**
     * Send break block confirmation GUI 
     */
    public boolean sendConfirm(BlockPos pos, EntityPlayer player)
    {
        int size = this.director.replays.size();

        if (size == 0)
        {
            return false;
        }

        Dispatcher.sendTo(new PacketConfirmBreak(pos, size), (EntityPlayerMP) player);

        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox()
    {
        return TileEntity.INFINITE_EXTENT_AABB;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public double getMaxRenderDistanceSquared()
    {
        float range = Blockbuster.proxy.config.actor_rendering_range;

        return range * range;
    }
}