package mchorse.blockbuster.common.item;

import java.util.List;

import mchorse.blockbuster.Blockbuster;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        tooltip.add(I18n.format("blockbuster.info.actor_config"));
    }
}