package noname.blockbuster.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import noname.blockbuster.Blockbuster;

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
        if (stack.getTagCompound() == null)
        {
            stack.setTagCompound(new NBTTagCompound());
        }

        NBTTagCompound tag = stack.getTagCompound();

        tag.setInteger("DirX", pos.getX());
        tag.setInteger("DirY", pos.getY());
        tag.setInteger("DirZ", pos.getZ());
    }

    /**
     * Get block position out of item stack's NBT tag
     */
    public BlockPos getBlockPos(ItemStack stack)
    {
        NBTTagCompound tag = stack.getTagCompound();

        if (tag == null || !tag.hasKey("DirX") || !tag.hasKey("DirY") || !tag.hasKey("DirZ"))
        {
            return null;
        }

        return new BlockPos(tag.getInteger("DirX"), tag.getInteger("DirY"), tag.getInteger("DirZ"));
    }
}