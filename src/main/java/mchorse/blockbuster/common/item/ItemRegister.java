package mchorse.blockbuster.common.item;

import mchorse.blockbuster.common.Blockbuster;
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
}