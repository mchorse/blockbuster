package mchorse.blockbuster.common;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.common.item.ItemBlockGreen;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
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
    public ItemStack getTabIconItem()
    {
        return new ItemStack(Item.getItemFromBlock(Blockbuster.directorBlock));
    }

    /**
     * Display all items and also an actor egg
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void displayAllRelevantItems(NonNullList<ItemStack> items)
    {
        Item.getItemFromBlock(Blockbuster.greenBlock).getSubItems(this, items);
        items.add(ItemStack.EMPTY);
        Item.getItemFromBlock(Blockbuster.dimGreenBlock).getSubItems(this, items);
        items.add(ItemStack.EMPTY);

        for (int i = 0; i < 9; i ++)
        {
            items.add(ItemStack.EMPTY);
        }

        for (Item item : Item.REGISTRY)
        {
            if (item instanceof ItemBlockGreen)
            {
                continue;
            }

            item.getSubItems(this, items);
        }

        ItemStack stack = new ItemStack(Items.SPAWN_EGG);
        ItemMonsterPlacer.applyEntityIdToItemStack(stack, new ResourceLocation("blockbuster", "actor"));

        items.add(stack);

        if (Blockbuster.addUtilityBlocks.get())
        {
            items.add(new ItemStack(Blocks.COMMAND_BLOCK));
            items.add(new ItemStack(Blocks.STRUCTURE_BLOCK));
            items.add(new ItemStack(Blocks.BARRIER));
        }
    }
}