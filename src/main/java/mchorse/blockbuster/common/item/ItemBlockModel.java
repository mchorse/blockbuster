package mchorse.blockbuster.common.item;

import mchorse.blockbuster.Blockbuster;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockModel extends ItemBlock
{
    public ItemBlockModel(Block block)
    {
        super(block);
        this.setHasSubtypes(true);
        this.setRegistryName("model_light");
        this.setUnlocalizedName("blockbuster.model_light");
        this.setCreativeTab(Blockbuster.blockbusterTab);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        int i = stack.getMetadata();

        return i >= 0 && i < 16 ? this.getUnlocalizedName() + i : this.getUnlocalizedName();
    }

    @Override
    public String getUnlocalizedName()
    {
        return "item.blockbuster.model_light";
    }

    @Override
    public int getMetadata(int damage)
    {
        return damage;
    }
}
