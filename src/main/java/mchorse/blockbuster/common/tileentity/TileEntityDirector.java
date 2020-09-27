package mchorse.blockbuster.common.tileentity;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.CommonProxy;
import mchorse.blockbuster.recording.scene.Scene;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFlowerPot;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Director tile entity
 *
 * Goodbye director blocks...
 */
public class TileEntityDirector extends TileEntityFlowerPot
{
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

        /* At least the data wouldn't be lost */
        if (compound.hasKey("Actors"))
        {
            Scene scene = new Scene();

            scene.fromNBT(compound);
            scene.setId("director_block_" + this.pos.getX() + "_" + this.pos.getY() + "_" + this.pos.getZ());

            try
            {
                CommonProxy.scenes.save(scene.getId(), scene);
            }
            catch (Exception e)
            {}
        }
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
        float range = Blockbuster.actorRenderingRange.get();

        return range * range;
    }
}