package mchorse.blockbuster.recording.capturing;

import java.util.List;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.CommonProxy;
import mchorse.blockbuster.common.block.BlockDirector;
import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.recording.actions.Action;
import mchorse.blockbuster.recording.actions.BreakBlockAnimation;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldEventListener;
import net.minecraft.world.World;

/**
 * Blockbuster's world event listener
 *
 * This dude is responsible only for adding breaking block animation during
 * player recording.
 */
public class WorldEventListener implements IWorldEventListener
{
    public World world;

    public static void setBlockState(World world, BlockPos pos, IBlockState newState, int flags)
    {
        if (Blockbuster.damageControl.get())
        {
            ActionHandler.lastTE = world.getTileEntity(pos);
        }
    }

    public WorldEventListener(World world)
    {
        this.world = world;
    }

    /**
     * Used by damage control
     */
    @Override
    public void notifyBlockUpdate(World worldIn, BlockPos pos, IBlockState oldState, IBlockState newState, int flags)
    {
        if (Blockbuster.damageControl.get())
        {
            if (oldState.getBlock() instanceof BlockDirector)
            {
                return;
            }
            else if (oldState.getBlock() == Blocks.PISTON_EXTENSION)
            {
                oldState = Blocks.AIR.getDefaultState();
            }

            CommonProxy.damage.addBlock(pos, oldState, worldIn);
        }
    }

    @Override
    public void notifyLightSet(BlockPos pos)
    {}

    @Override
    public void markBlockRangeForRenderUpdate(int x1, int y1, int z1, int x2, int y2, int z2)
    {}

    @Override
    public void playSoundToAllNearExcept(EntityPlayer player, SoundEvent soundIn, SoundCategory category, double x, double y, double z, float volume, float pitch)
    {}

    @Override
    public void playRecord(SoundEvent soundIn, BlockPos pos)
    {}

    @Override
    public void spawnParticle(int particleID, boolean ignoreRange, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int... parameters)
    {}

    @Override
    public void spawnParticle(int p_190570_1_, boolean p_190570_2_, boolean p_190570_3_, double p_190570_4_, double p_190570_6_, double p_190570_8_, double p_190570_10_, double p_190570_12_, double p_190570_14_, int... p_190570_16_)
    {}

    @Override
    public void onEntityAdded(Entity entity)
    {
        if (entity instanceof EntityActor || entity instanceof EntityPlayer)
        {
            return;
        }

        CommonProxy.damage.addEntity(entity);
    }

    @Override
    public void onEntityRemoved(Entity entityIn)
    {}

    @Override
    public void broadcastSound(int soundID, BlockPos pos, int data)
    {}

    @Override
    public void playEvent(EntityPlayer player, int type, BlockPos blockPosIn, int data)
    {}

    @Override
    public void sendBlockBreakProgress(int breakerId, BlockPos pos, int progress)
    {
        Entity breaker = this.world.getEntityByID(breakerId);

        if (breaker instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) breaker;
            List<Action> events = CommonProxy.manager.getActions(player);

            if (!player.world.isRemote && events != null)
            {
                events.add(new BreakBlockAnimation(pos, progress));
            }
        }
    }
}