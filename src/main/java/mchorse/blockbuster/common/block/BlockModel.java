package mchorse.blockbuster.common.block;

import java.util.List;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.common.GuiHandler;
import mchorse.blockbuster.common.tileentity.TileEntityModel;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * Block model
 * 
 * This block is responsible for providing a tile entity which is 
 * responsible for rendering a morph. 
 */
public class BlockModel extends Block implements ITileEntityProvider
{
    /**
     * Used to setup the yaw for the tile entity
     */
    private float lastYaw;

    public BlockModel()
    {
        super(Material.ROCK);
        this.setCreativeTab(Blockbuster.blockbusterTab);
        this.setResistance(6000000.0F);
        this.setRegistryName("model");
        this.setUnlocalizedName("blockbuster.model");
    }

    @Override
    public void addInformation(ItemStack stack, World player, List<String> tooltip, ITooltipFlag advanced)
    {
        tooltip.add(I18n.format("blockbuster.info.model_block"));
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        GuiHandler.open(playerIn, GuiHandler.MODEL_BLOCK, pos.getX(), pos.getY(), pos.getZ());

        return true;
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand)
    {
        this.lastYaw = placer.rotationYaw;

        return super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer, hand);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        if (world.isRemote && stack.hasTagCompound() && hasTileEntity(state) && stack.getTagCompound().hasKey("BlockEntityTag"))
        {
            TileEntity tileEntity = world.getTileEntity(pos);

            if (tileEntity != null)
            {
                tileEntity.handleUpdateTag(stack.getTagCompound().getCompoundTag("BlockEntityTag"));
            }
        }
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileEntityModel(MathHelper.wrapDegrees(180 - this.lastYaw));
    }

    /* Setting up visual properties and collision box */

    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
    {
        return null;
    }
}