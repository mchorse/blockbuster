package mchorse.blockbuster.common.item;

import java.util.List;

import mchorse.blockbuster.Blockbuster;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

/**
 * Register item
 *
 * Used to register an actor to director block (a scene)
 */
public class ItemRegister extends Item
{
    public ItemRegister()
    {
        this.setMaxStackSize(1);
        this.setRegistryName("register");
        this.setUnlocalizedName("blockbuster.register");
        this.setCreativeTab(Blockbuster.blockbusterTab);
    }

    /**
     * Register a director block to a stack of register item
     */
    public void registerStack(ItemStack stack, BlockPos pos)
    {
        ItemPlayback.saveBlockPos("Dir", stack, pos);
    }

    /**
     * Get block position out of item stack's NBT tag
     */
    public BlockPos getBlockPos(ItemStack stack)
    {
        return ItemPlayback.getBlockPos("Dir", stack);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced)
    {
        tooltip.add(I18n.format("blockbuster.info.register"));

        BlockPos pos = ItemPlayback.getBlockPos("Dir", stack);

        if (pos != null)
        {
            tooltip.add(I18n.format("blockbuster.info.playback_director", pos.getX(), pos.getY(), pos.getZ()));
        }
    }
}