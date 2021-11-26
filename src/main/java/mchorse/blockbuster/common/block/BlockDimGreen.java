package mchorse.blockbuster.common.block;

import net.minecraft.block.BlockSlab;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class BlockDimGreen extends BlockGreen
{
    public BlockDimGreen()
    {
        super();
        this.setLightLevel(0.0F);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World player, List<String> tooltip, ITooltipFlag advanced)
    {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(I18n.format("blockbuster.info.dim_green_block"));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public float getAmbientOcclusionLightValue(IBlockState state)
    {
        return 1;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getPackedLightmapCoords(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        int i = source.getCombinedLight(pos, state.getLightValue(source, pos));

        if (i == 0 && state.getBlock() instanceof BlockSlab)
        {
            pos = pos.down();
            state = source.getBlockState(pos);
            return source.getCombinedLight(pos, state.getLightValue(source, pos));
        }
        else
        {
            return i;
        }
    }
}