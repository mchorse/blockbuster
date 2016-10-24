package mchorse.blockbuster.common;

import mchorse.blockbuster.Blockbuster;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Blockbuster creative tab
 *
 * This is a creative tab for Blockbuster mod. What it does, it basically
 * provides the icon for this tab and also appends actor egg to this tab (since
 * it's really annoying to find actor egg when it's in other tab).
 */
public class BlockbusterTab extends CreativeTabs
{
    public BlockbusterTab()
    {
        super("blockbuster");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Item getTabIconItem()
    {
        return Item.getItemFromBlock(Blockbuster.directorBlock);
    }

    /**
     * Display all items and also an actor egg
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void displayAllRelevantItems(java.util.List<net.minecraft.item.ItemStack> items)
    {
        super.displayAllRelevantItems(items);

        ItemStack stack = new ItemStack(Items.SPAWN_EGG);
        ItemMonsterPlacer.applyEntityIdToItemStack(stack, "blockbuster.Actor");

        items.add(stack);
    }
}