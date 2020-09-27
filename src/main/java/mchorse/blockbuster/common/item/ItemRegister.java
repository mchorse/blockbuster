package mchorse.blockbuster.common.item;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.utils.EntityUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * Register item
 *
 * Used to register an actor to director block (a scene) and
 * open a director block remotely
 */
public class ItemRegister extends Item
{
    public ItemRegister()
    {
        this.setMaxStackSize(1);
        this.setRegistryName("register");
        this.setUnlocalizedName("blockbuster.register");
        this.setCreativeTab(Blockbuster.blockbusterTab);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        tooltip.add(I18n.format("blockbuster.info.register"));
    }

    /**
     * On right click, show the director block GUI, if this item has attached
     * director block, then we can show the director block GUI.
     */
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
    {
        if (!world.isRemote)
        {
            EntityUtils.sendStatusMessage((EntityPlayerMP) player, new TextComponentString("Director blocks are now deprecated. This item is now useless..."));
        }

        return new ActionResult<ItemStack>(EnumActionResult.PASS, player.getHeldItem(hand));
    }
}