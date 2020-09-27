package mchorse.blockbuster.common.item;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.CommonProxy;
import mchorse.blockbuster.aperture.CameraHandler;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * Playback button item
 *
 * Push to start playing back the scene
 */
public class ItemPlayback extends Item
{
    /* ItemPlayback */

    public ItemPlayback()
    {
        this.setMaxStackSize(1);
        this.setRegistryName("playback");
        this.setUnlocalizedName("blockbuster.playback");
        this.setCreativeTab(Blockbuster.blockbusterTab);
    }

    /**
     * Adds information about camera profile and the location of director block
     * to which it's attached
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        tooltip.add(I18n.format("blockbuster.info.playback_button"));

        NBTTagCompound tag = stack.getTagCompound();

        if (tag == null)
        {
            return;
        }

        if (tag.hasKey("CameraProfile"))
        {
            tooltip.add(I18n.format("blockbuster.info.playback_profile", tag.getString("CameraProfile")));
        }
        else if (tag.hasKey("CameraPlay"))
        {
            tooltip.add(I18n.format("blockbuster.info.playback_play"));
        }

        if (tag.hasKey("Scene"))
        {
            tooltip.add(I18n.format("blockbuster.info.playback_scene", tag.getString("Scene")));
        }
    }

    /**
     * This method starts playback of the director block's actors (if the
     * director block is attached to this item stack).
     */
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer player, EnumHand handIn)
    {
        ItemStack stack = player.getHeldItem(handIn);

        if (!worldIn.isRemote)
        {
            NBTTagCompound tag = stack.getTagCompound();

            if (tag == null)
            {
                return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
            }

            if (stack.getTagCompound().hasKey("Scene"))
            {
                if (CommonProxy.scenes.toggle(stack.getTagCompound().getString("Scene"), player.world) && CameraHandler.isApertureLoaded())
                {
                    CameraHandler.handlePlaybackItem(player, tag);
                }
            }
        }

        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
    }
}