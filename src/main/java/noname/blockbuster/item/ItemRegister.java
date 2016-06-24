package noname.blockbuster.item;

import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import noname.blockbuster.Blockbuster;

/**
 * Register item
 *
 * Used to register an actor or a camera to director block (a scene)
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
     * Register an entity to a stack of register item
     */
    public void registerStack(ItemStack stack, Entity entity)
    {
        if (stack.getTagCompound() == null)
        {
            stack.setTagCompound(new NBTTagCompound());
        }

        stack.getTagCompound().setString("EntityID", entity.getUniqueID().toString());
    }
}