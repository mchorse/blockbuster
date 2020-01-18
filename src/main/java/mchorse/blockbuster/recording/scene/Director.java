package mchorse.blockbuster.recording.scene;

import io.netty.buffer.ByteBuf;
import mchorse.blockbuster.common.tileentity.TileEntityDirector;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * Director data class
 * 
 * This class is responsible for holding information about replays
 */
public class Director extends Scene
{
    /**
     * Whether director block's state changes are disabled
     */
    public boolean disableStates;

    /**
     * Whether director block should be hidden when playback starts
     */
    public boolean hide = true;

    /* Runtime properties */

    /**
     * Reference to owning tile entity
     */
    private TileEntityDirector tile;

    public Director(TileEntityDirector tile)
    {
        this.tile = tile;
    }

    /* Info accessors */

    @Override
    public World getWorld()
    {
        return this.tile.getWorld();
    }

    /**
     * Get tile entity
     */
    public TileEntityDirector getTile()
    {
        return this.tile;
    }

    @Override
    public void setPlaying(boolean playing)
    {
        boolean changed = playing != this.playing;

        super.setPlaying(playing);

        this.tile.playBlock(playing);

        if (changed && !this.loops)
        {
            TileEntityDirector.playing += playing ? 1 : -1;
        }
    }

    public void copy(Director director)
    {
        super.copy(director);

        this.disableStates = director.disableStates;
        this.hide = director.hide;
    }

    @Override
    public void fromNBT(NBTTagCompound compound)
    {
        super.fromNBT(compound);

        this.disableStates = compound.getBoolean("DisableState");
        this.hide = compound.getBoolean("Hide");
    }

    @Override
    public void toNBT(NBTTagCompound compound)
    {
        super.toNBT(compound);

        compound.setBoolean("DisableState", this.disableStates);
        compound.setBoolean("Hide", this.hide);
    }

    @Override
    public void fromBuf(ByteBuf buffer)
    {
        super.fromBuf(buffer);

        this.disableStates = buffer.readBoolean();
        this.hide = buffer.readBoolean();
    }

    @Override
    public void toBuf(ByteBuf buffer)
    {
        super.toBuf(buffer);

        buffer.writeBoolean(this.disableStates);
        buffer.writeBoolean(this.hide);
    }
}