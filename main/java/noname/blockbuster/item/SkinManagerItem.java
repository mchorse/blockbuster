package noname.blockbuster.item;

import net.minecraft.item.Item;
import noname.blockbuster.Blockbuster;

/**
 * This item is used for opening actor's skin configuration (picker) GUI.
 *
 * I really like the icon, it looks badass!
 */
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
