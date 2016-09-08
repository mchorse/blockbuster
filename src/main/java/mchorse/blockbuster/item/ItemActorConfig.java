package mchorse.blockbuster.item;

import mchorse.blockbuster.Blockbuster;
import net.minecraft.item.Item;

/**
 * This item is used for opening actor's configuration GUI.
 *
 * I really like the icon, it looks badass!
 */
public class ItemActorConfig extends Item
{
    public ItemActorConfig()
    {
        this.setMaxStackSize(1);
        this.setRegistryName("actor_config");
        this.setUnlocalizedName("blockbuster.actor_config");
        this.setCreativeTab(Blockbuster.blockbusterTab);
    }
}
