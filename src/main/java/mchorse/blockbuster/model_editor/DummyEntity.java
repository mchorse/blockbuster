package mchorse.blockbuster.model_editor;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraft.world.World;

/**
 * Dummy entity
 *
 * This class is used in model editor as a player substitution for the model
 * methods.
 */
public class DummyEntity extends EntityLivingBase
{
    private final ItemStack[] held;

    public DummyEntity(World worldIn)
    {
        super(worldIn);

        ItemStack iron = new ItemStack(Items.IRON_INGOT);
        ItemStack sword = new ItemStack(Items.DIAMOND_SWORD);

        this.held = new ItemStack[] {sword, iron};
    }

    @Override
    public Iterable<ItemStack> getArmorInventoryList()
    {
        return null;
    }

    @Override
    public ItemStack getItemStackFromSlot(EntityEquipmentSlot slotIn)
    {
        if (slotIn.equals(EntityEquipmentSlot.MAINHAND))
        {
            return this.held[0];
        }
        else if (slotIn.equals(EntityEquipmentSlot.OFFHAND))
        {
            return this.held[1];
        }

        return null;
    }

    @Override
    public void setItemStackToSlot(EntityEquipmentSlot slotIn, ItemStack stack)
    {}

    @Override
    public EnumHandSide getPrimaryHand()
    {
        return EnumHandSide.RIGHT;
    }
}