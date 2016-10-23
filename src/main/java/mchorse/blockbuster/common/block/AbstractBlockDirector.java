package mchorse.blockbuster.common.block;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.common.GuiHandler;
import mchorse.blockbuster.common.item.ItemPlayback;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * <p>
 * This block is responsible for launching and stopping scene recording/play.
 * It's basically the block that ties everything together.
 * </p>
 *
 * <p>
 * It also has two hooks:
 * </p>
 *
 * <ul>
 * <li>Start hook – director block sends redstone signal on east side when the
 * scene starts playing</li>
 * <li>Stop hook – director block sends redstone signal on west side when the
 * scene stops playing</li>
 * </ul>
 *
 * <p>
 * Stop hook is very useful when you need to reset the scene, like you want to
 * use TNT in your scene, but don't want to rebuild the same house over and over
 * again, you can use this redstone hook to /clone already built environment
 * after the scene was stopped.
 * </p>
 *
 * <p>
 * I don't really know a good use for the start hook, maybe start playing one of
 * these sick minecraft crafted tunes or do something else. I added just to
 * complement the stop hook.
 * </p>
 */
public abstract class AbstractBlockDirector extends Block implements ITileEntityProvider
{
    public static final PropertyBool PLAYING = PropertyBool.create("playing");

    public AbstractBlockDirector()
    {
        super(Material.ROCK);
        this.setDefaultState(this.getDefaultState().withProperty(PLAYING, false));
        this.setCreativeTab(Blockbuster.blockbusterTab);
        this.setHardness(8);
    }

    @Override
    public boolean canHarvestBlock(IBlockAccess world, BlockPos pos, EntityPlayer player)
    {
        return true;
    }

    /* States */

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(PLAYING) ? 1 : 0;
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(PLAYING, meta == 1);
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {PLAYING});
    }

    /* Redstone */

    @Override
    public boolean canProvidePower(IBlockState state)
    {
        return true;
    }

    /**
     * Power west side of the block while block is playing and power east side
     * of the block while isn't playback actors.
     */
    @Override
    public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
    {
        boolean isPlaying = blockState.getValue(PLAYING);

        return (isPlaying && side == EnumFacing.WEST) || (!isPlaying && side == EnumFacing.EAST) ? 15 : 0;
    }

    /* Player interaction */

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        ItemStack item = playerIn.getHeldItemMainhand();

        if (item != null && this.handleItem(item, worldIn, pos, playerIn))
        {
            return true;
        }

        if (!worldIn.isRemote)
        {
            this.displayCast(playerIn, worldIn, pos);
        }

        return true;
    }

    /**
     * Display director block's cast
     */
    protected abstract void displayCast(EntityPlayer playerIn, World worldIn, BlockPos pos);

    /**
     * Handle item on block activated.
     */
    protected abstract boolean handleItem(ItemStack item, World world, BlockPos pos, EntityPlayer player);

    /**
     * Attach recording item to current director block
     */
    protected boolean handlePlaybackItem(ItemStack item, World world, BlockPos pos, EntityPlayer player)
    {
        if (!(item.getItem() instanceof ItemPlayback))
        {
            return false;
        }

        ItemPlayback.saveBlockPos("Dir", item, pos);
        GuiHandler.open(player, GuiHandler.PLAYBACK, 0, 0, 0);

        return true;
    }
}