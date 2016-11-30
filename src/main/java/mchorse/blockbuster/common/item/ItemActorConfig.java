package mchorse.blockbuster.common.item;

import java.util.List;

import mchorse.blockbuster.Blockbuster;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

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
        this.setTextureName("blockbuster:actor_config");
        this.setUnlocalizedName("blockbuster.actor_config");
        this.setCreativeTab(Blockbuster.blockbusterTab);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced)
    {
        tooltip.add(I18n.format("blockbuster.info.actor_config"));
    }
}