package mchorse.blockbuster.common.block;

import java.util.List;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.aperture.CameraHandler;
import mchorse.blockbuster.common.GuiHandler;
import mchorse.blockbuster.common.item.ItemPlayback;
import mchorse.blockbuster.common.item.ItemRegister;
import mchorse.blockbuster.common.tileentity.TileEntityDirector;
import mchorse.blockbuster.utils.EntityUtils;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Director block
 *
 * This block is the one that responsible for machinimas creation.
 */
public class BlockDirector extends Block implements ITileEntityProvider
{
    /**
     * The playing state property of director block 
     */
    public static final PropertyBool PLAYING = PropertyBool.create("playing");

    /**
     * The hidden state property of director block 
     */
    public static final PropertyBool HIDDEN = PropertyBool.create("hidden");

    public BlockDirector()
    {
        super(Material.ROCK);
        this.setDefaultState(this.getDefaultState().withProperty(PLAYING, false).withProperty(HIDDEN, false));
        this.setCreativeTab(Blockbuster.blockbusterTab);
        this.setBlockUnbreakable();
        this.setResistance(6000000.0F);
        this.setRegistryName("director");
        this.setUnlocalizedName("blockbuster.director");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World player, List<String> tooltip, ITooltipFlag advanced)
    {
        tooltip.add(I18n.format("blockbuster.info.director_block"));
    }

    @Override
    public boolean canHarvestBlock(IBlockAccess world, BlockPos pos, EntityPlayer player)
    {
        return true;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return !state.getValue(HIDDEN);
    }

    @Override
    public boolean isFullCube(IBlockState state)
    {
        return !state.getValue(HIDDEN);
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return state.getValue(HIDDEN) ? EnumBlockRenderType.ENTITYBLOCK_ANIMATED : super.getRenderType(state);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        return state.getValue(HIDDEN) ? null : super.getCollisionBoundingBox(state, worldIn, pos);
    }

    /* States */

    @Override
    public int getMetaFromState(IBlockState state)
    {
        int meta = state.getValue(PLAYING) ? 0 : 1;
        meta |= state.getValue(HIDDEN) ? 0b10 : 0;

        return meta;
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(PLAYING, (meta & 0b1) == 0b1).withProperty(HIDDEN, (meta & 0b10) == 0b10);
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {PLAYING, HIDDEN});
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
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (!worldIn.isRemote)
        {
            EntityUtils.sendStatusMessage((EntityPlayerMP) playerIn, new TextComponentTranslation("blockbuster.bye_director_block"));
        }

        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileEntityDirector();
    }
}