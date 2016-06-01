package noname.blockbuster.item;

import net.minecraft.item.Item;
import noname.blockbuster.Blockbuster;

/**
 * Register item
 *
 * Used to register an actor or a camera to director block (a scene)
 */
public class RegisterItem extends Item
{
    public RegisterItem()
    {
        this.setMaxStackSize(1);
        this.setUnlocalizedName("registerItem");
        this.setRegistryName("registerItem");
        this.setCreativeTab(Blockbuster.blockbusterTab);
    }
}