package mchorse.blockbuster.recording;

import java.util.HashMap;
import java.util.Map;

import mchorse.blockbuster.config.BlockbusterConfig;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Damage control
 *
 * This class is responsible for storing damaged blocks and be able to restore
 * them in the world.
 */
public class DamageControl
{
    public Map<BlockPos, BlockEntry> blocks = new HashMap<BlockPos, DamageControl.BlockEntry>();
    public EntityLivingBase target;

    public int maxDistance;

    public DamageControl(EntityLivingBase target, int maxDistance)
    {
        this.target = target;
        this.maxDistance = maxDistance;
    }

    /**
     * Add a block to damage control repository
     *
     * This method is responsible for adding only these blocks which are
     * in the radius of allowed {@link #maxDistance} range. Max distance gets
     * set from the config property {@link BlockbusterConfig#damage_control_distance}.
     */
    public void addBlock(BlockPos pos, IBlockState state)
    {
        double x = Math.abs(this.target.posX - pos.getX());
        double y = Math.abs(this.target.posY - pos.getY());
        double z = Math.abs(this.target.posZ - pos.getZ());

        if (x > this.maxDistance || y > this.maxDistance || z > this.maxDistance || this.blocks.containsKey(pos))
        {
            return;
        }

        this.blocks.put(pos, new BlockEntry(pos, state));
    }

    /**
     * Apply recorded damaged blocks back in the world
     */
    public void apply(World world)
    {
        for (BlockEntry entry : this.blocks.values())
        {
            world.setBlockState(entry.pos, entry.state);
        }

        this.blocks.clear();
    }

    /**
     * Block entry in the damage control class
     *
     * This class holds information about destroyed block, such as it's state
     */
    public static class BlockEntry
    {
        public BlockPos pos;
        public IBlockState state;

        public BlockEntry(BlockPos pos, IBlockState state)
        {
            this.pos = pos;
            this.state = state;
        }
    }
}