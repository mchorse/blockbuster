package noname.blockbuster.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import noname.blockbuster.Blockbuster;
import noname.blockbuster.item.RegisterItem;

/**
 * <p>
 * This block is responsible for launching and stopping scene recording/play.
 * It's basically the block that ties everything together.
 * </p>
 *
 * <p>
 * It also has three hooks:
 * </p>
 *
 * <ul>
 * <li>Start hook – director block sends redstone signal on east side when the
 * scene starts playing</li>
 * <li>Stop hook – director block sends redstone signal on west side when the
 * scene stops playing</li>
 * <li>Force stop hook – if you'll send redstone signal on the south side of the
 * director block, it will force stop the scene</li>
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
 * complement stop hook.
 * </p>
 */
public class DirectorBlock extends Block
{
    public DirectorBlock()
    {
        super(Material.rock);
        this.setCreativeTab(Blockbuster.blockbusterTab);
        this.setRegistryName("directorBlock");
        this.setUnlocalizedName("directorBlock");
    }

    @Override
    public boolean canProvidePower(IBlockState state)
    {
        return true;
    }

    @Override
    public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
    {
        return side == EnumFacing.WEST || side == EnumFacing.EAST ? 15 : 0;
    }

    @Override
    public int getStrongPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
    {
        return 0;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        ItemStack item = playerIn.getHeldItemMainhand();

        if (item != null && item.getItem() instanceof RegisterItem)
        {
            /* Register a camera or an actor */

            return true;
        }

        /* Open the GUI with scene configuration and panel with utility items */

        return true;
    }
}
