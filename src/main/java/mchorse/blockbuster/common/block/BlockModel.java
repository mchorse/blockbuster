package mchorse.blockbuster.common.block;

import java.util.List;
import java.util.Random;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.common.GuiHandler;
import mchorse.blockbuster.common.tileentity.TileEntityModel;
import mchorse.blockbuster.recording.capturing.ActionHandler;
import mchorse.mclib.utils.EntityUtils;
import mchorse.mclib.utils.OpHelper;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Block model
 * 
 * This block is responsible for providing a tile entity which is 
 * responsible for rendering a morph. 
 */
public class BlockModel extends Block implements ITileEntityProvider
{
    public static final PropertyInteger LIGHT = PropertyInteger.create("light",0,15);

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

        this.setDefaultState(this.getDefaultState().withProperty(LIGHT, 0));
    }

    public static Item getItemFromMeta(int meta)
    {
        switch(meta)
        {
            case 1:
                return Blockbuster.modelBlockItem1;
            case 2:
                return Blockbuster.modelBlockItem2;
            case 3:
                return Blockbuster.modelBlockItem3;
            case 4:
                return Blockbuster.modelBlockItem4;
            case 5:
                return Blockbuster.modelBlockItem5;
            case 6:
                return Blockbuster.modelBlockItem6;
            case 7:
                return Blockbuster.modelBlockItem7;
            case 8:
                return Blockbuster.modelBlockItem8;
            case 9:
                return Blockbuster.modelBlockItem9;
            case 10:
                return Blockbuster.modelBlockItem10;
            case 11:
                return Blockbuster.modelBlockItem11;
            case 12:
                return Blockbuster.modelBlockItem12;
            case 13:
                return Blockbuster.modelBlockItem13;
            case 14:
                return Blockbuster.modelBlockItem14;
            case 15:
                return Blockbuster.modelBlockItem15;
        }

        return Blockbuster.modelBlockItem0;
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        return world.getBlockState(pos).getValue(LIGHT);
    }

    @Override
    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state)
    {
        return this.getItemStack(state);
    }

    private ItemStack getItemStack(IBlockState state)
    {
        int meta = this.damageDropped(state);

        return new ItemStack(this.getItemFromMeta(meta), 1, meta);
    }

    @Override
    public int damageDropped(IBlockState state)
    {
        return this.getMetaFromState(state);
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, LIGHT);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(LIGHT);
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(LIGHT, meta);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World player, List<String> tooltip, ITooltipFlag advanced)
    {
        tooltip.add(I18n.format("blockbuster.info.model_block"));
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
        ItemStack stack = this.getItemStack(state);
        TileEntity te = ActionHandler.lastTE;

        if (te instanceof TileEntityModel)
        {
            TileEntityModel model = (TileEntityModel) te;
            NBTTagCompound tag = model.writeToNBT(new NBTTagCompound());
            NBTTagCompound block = new NBTTagCompound();

            tag.removeTag("x");
            tag.removeTag("y");
            tag.removeTag("z");

            block.setTag("BlockEntityTag", tag);

            if (!(tag.getSize() == 2 && tag.hasKey("id") && tag.hasKey("Morph") && tag.getTag("Morph").equals(TileEntityModel.getDefaultMorph().toNBT())))
            {
                stack.setTagCompound(block);
            }
        }

        drops.add(stack);
    }

    @Override
    public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager manager)
    {
        return true;
    }

    @Override
    public boolean canHarvestBlock(IBlockAccess world, BlockPos pos, EntityPlayer player)
    {
        return true;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (worldIn.isRemote && this.canOpenMenu(playerIn))
        {
            GuiHandler.open(playerIn, GuiHandler.MODEL_BLOCK, pos.getX(), pos.getY(), pos.getZ());
        }

        return true;
    }

    @SideOnly(Side.CLIENT)
    private boolean canOpenMenu(EntityPlayer player)
    {
        return OpHelper.isPlayerOp() && !EntityUtils.isAdventureMode(player);
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand)
    {
        this.lastYaw = placer.isSneaking() ? MathHelper.wrapDegrees(180 - placer.rotationYaw) : 0;

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
        TileEntity entity = new TileEntityModel(this.lastYaw);

        this.lastYaw = 0;

        return entity;
    }

    /* Setting up visual properties and collision box */

    /**
     * Don't connect to fences 
     */
    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
    {
        return BlockFaceShape.UNDEFINED;
    }

    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean canSpawnInBlock()
    {
        return true;
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