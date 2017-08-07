package mchorse.blockbuster.common.item;

import java.util.List;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.aperture.CameraHandler;
import mchorse.blockbuster.common.tileentity.AbstractTileEntityDirector;
import mchorse.blockbuster.utils.L10n;
import mchorse.blockbuster.utils.NBTUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Record item
 *
 * Push to start recording the scene (actually it just makes all actors do their
 * business, i.e. playing their role)
 */
public class ItemPlayback extends Item
{
    /* BlockPos helpers */

    /**
     * Save given {@link BlockPos} into ItemStack's {@link NBTTagCompound} tag
     */
    public static void saveBlockPos(String key, ItemStack stack, BlockPos pos)
    {
        if (stack.getTagCompound() == null)
        {
            stack.setTagCompound(new NBTTagCompound());
        }

        NBTUtils.saveBlockPos(key, stack.getTagCompound(), pos);
    }

    /**
     * Get {@link BlockPos} position from ItemStack's {@link NBTTagCompound} tag
     */
    public static BlockPos getBlockPos(String key, ItemStack stack)
    {
        return NBTUtils.getBlockPos(key, stack.getTagCompound());
    }

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

        if (CameraHandler.isApertureLoaded())
        {
            if (tag.hasKey("CameraProfile"))
            {
                tooltip.add(I18n.format("blockbuster.info.playback_profile", tag.getString("CameraProfile")));
            }
            else if (tag.hasKey("CameraPlay"))
            {
                tooltip.add(I18n.format("blockbuster.info.playback_play"));
            }
        }

        BlockPos pos = getBlockPos("Dir", stack);

        if (pos != null)
        {
            tooltip.add(I18n.format("blockbuster.info.playback_director", pos.getX(), pos.getY(), pos.getZ()));
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
            BlockPos pos = getBlockPos("Dir", stack);
            NBTTagCompound tag = stack.getTagCompound();

            if (pos == null || tag == null)
            {
                return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
            }

            if (player.isSneaking() && !Blockbuster.proxy.config.disable_teleport_playback_button)
            {
                player.setPositionAndUpdate(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);

                return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
            }

            TileEntity tile = worldIn.getTileEntity(pos);

            if (tile == null || !(tile instanceof AbstractTileEntityDirector))
            {
                L10n.error(player, "director.missing", pos.getX(), pos.getY(), pos.getZ());

                return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
            }

            AbstractTileEntityDirector director = (AbstractTileEntityDirector) tile;

            if (director.togglePlayback() && CameraHandler.isApertureLoaded())
            {
                CameraHandler.handlePlaybackItem(player, tag);
            }
        }

        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
    }
}