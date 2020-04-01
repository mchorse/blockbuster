package mchorse.blockbuster.common.item;

import mchorse.blockbuster.Blockbuster;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

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

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {
        throw new AprilFoolsException("Haha, funny!");
    }

    public static class AprilFoolsException extends RuntimeException
    {
        public AprilFoolsException(String message)
        {
            super(message);
        }
    }
}