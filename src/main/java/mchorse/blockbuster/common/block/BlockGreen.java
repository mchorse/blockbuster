package mchorse.blockbuster.common.block;

import java.util.List;

import mchorse.blockbuster.Blockbuster;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockGreen extends Block
{
    public static final PropertyEnum<ChromaColor> COLOR = PropertyEnum.<ChromaColor>create("color", ChromaColor.class);

    public BlockGreen()
    {
        super(Material.CLAY);
        this.setCreativeTab(Blockbuster.blockbusterTab);
        this.setResistance(6000000.0F);
        this.setRegistryName("green");
        this.setUnlocalizedName("blockbuster.green");
        this.setLightLevel(1.0F);

        this.setDefaultState(this.getDefaultState().withProperty(COLOR, ChromaColor.GREEN));
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        ChromaColor[] values = ChromaColor.values();

        if (meta >= values.length || meta < 0)
        {
            return this.getDefaultState();
        }

        return this.getDefaultState().withProperty(COLOR, values[meta]);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(COLOR).ordinal();
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {COLOR});
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player)
    {
        int meta = state.getValue(COLOR).ordinal();

        return new ItemStack(Item.getItemFromBlock(this), 1, meta);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list)
    {
        for (ChromaColor color : ChromaColor.values())
        {
            list.add(new ItemStack(itemIn, 1, color.ordinal()));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced)
    {
        tooltip.add(I18n.format("blockbuster.info.green_block", I18n.format("blockbuster.chroma_blocks." + stack.getMetadata())));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public float getAmbientOcclusionLightValue(IBlockState state)
    {
        return 1;
    }

    public static enum ChromaColor implements IStringSerializable
    {
        GREEN("green"), BLUE("blue"), RED("red"), YELLOW("yellow"), CYAN("cyan"), PURPLE("purple"), WHITE("white"), BLACK("black");

        public final String name;

        private ChromaColor(String name)
        {
            this.name = name;
        }

        @Override
        public String getName()
        {
            return this.name;
        }
    }
}