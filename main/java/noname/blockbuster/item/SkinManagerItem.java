package noname.blockbuster.item;

import net.minecraft.item.Item;
import noname.blockbuster.Blockbuster;

public class SkinManagerItem extends Item
{
    public SkinManagerItem()
    {
        this.setMaxStackSize(1);
        this.setUnlocalizedName("skinManagerItem");
        this.setRegistryName("skinManagerItem");
        this.setCreativeTab(Blockbuster.blockbusterTab);
    }
}
