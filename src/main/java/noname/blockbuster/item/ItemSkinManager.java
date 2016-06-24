package noname.blockbuster.item;

import net.minecraft.item.Item;
import noname.blockbuster.Blockbuster;

/**
 * This item is used for opening actor's skin configuration (picker) GUI.
 *
 * I really like the icon, it looks badass!
 */
public class ItemSkinManager extends Item
{
    public ItemSkinManager()
    {
        this.setMaxStackSize(1);
        this.setRegistryName("skin_manager");
        this.setUnlocalizedName("blockbuster.skin_manager");
        this.setCreativeTab(Blockbuster.blockbusterTab);
    }
}
