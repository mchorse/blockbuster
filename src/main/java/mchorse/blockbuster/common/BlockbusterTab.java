package mchorse.blockbuster.common;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mchorse.blockbuster.Blockbuster;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

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
    public void displayAllReleventItems(List items)
    {
        super.displayAllReleventItems(items);

        ItemStack stack = new ItemStack(Items.spawn_egg);
        stack.setItemDamage(500);

        items.add(stack);
    }
}