package noname.blockbuster.item;

import net.minecraft.item.Item;
import noname.blockbuster.Blockbuster;

public class RecordItem extends Item
{
    public RecordItem()
    {
        this.setMaxStackSize(1);
        this.setUnlocalizedName("recordItem");
        this.setRegistryName("recordItem");
        this.setCreativeTab(Blockbuster.blockbusterTab);
    }
}