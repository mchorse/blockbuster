package mchorse.blockbuster.common.block;

import java.util.List;

import mchorse.blockbuster.Blockbuster;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockGreen extends Block
{
    public BlockGreen()
    {
        super(Material.CLAY);
        this.setCreativeTab(Blockbuster.blockbusterTab);
        this.setResistance(6000000.0F);
        this.setRegistryName("green");
        this.setUnlocalizedName("blockbuster.green");
        this.setLightLevel(1.0F);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced)
    {
        tooltip.add(I18n.format("blockbuster.info.green_block"));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public float getAmbientOcclusionLightValue(IBlockState state)
    {
        return 1;
    }
}