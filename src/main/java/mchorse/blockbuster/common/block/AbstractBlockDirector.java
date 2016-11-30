package mchorse.blockbuster.common.block;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.common.GuiHandler;
import mchorse.blockbuster.common.item.ItemPlayback;
import mchorse.blockbuster.utils.BlockPos;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
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
    public IIcon[] icons = new IIcon[4];

    public AbstractBlockDirector()
    {
        super(Material.rock);
        this.setBlockName("director");
        this.setBlockTextureName("blockbuster:director_block_blank");
        this.setCreativeTab(Blockbuster.blockbusterTab);
        this.setHardness(8);
    }

    @Override
    public void registerBlockIcons(IIconRegister register)
    {
        this.icons[0] = register.registerIcon("blockbuster:director_block_blank");
        this.icons[1] = register.registerIcon("blockbuster:director_block_side");
        this.icons[2] = register.registerIcon("blockbuster:director_block_start");
        this.icons[3] = register.registerIcon("blockbuster:director_block_stop");
    }

    @Override
    public IIcon getIcon(int side, int meta)
    {
        if (side == 3)
        {
            return this.icons[2];
        }
        else if (side == 2)
        {
            return this.icons[3];
        }
        else if (side == 4 || side == 5)
        {
            return this.icons[1];
        }

        return this.icons[0];
    }

    @Override
    public boolean canHarvestBlock(EntityPlayer player, int meta)
    {
        return true;
    }

    /* Redstone */

    @Override
    public boolean canProvidePower()
    {
        return true;
    }

    /**
     * Power west side of the block while block is playing and power east side
     * of the block while isn't playback actors.
     */
    @Override
    public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int meta)
    {
        return meta == 1 ? 15 : 0;
    }

    /* Player interaction */

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int meta, float hitX, float hitY, float hitZ)
    {
        ItemStack item = player.getHeldItem();
        BlockPos pos = new BlockPos(x, y, z);

        if (item != null && this.handleItem(item, world, pos, player))
        {
            return true;
        }

        if (!world.isRemote)
        {
            this.displayCast(player, world, pos);
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