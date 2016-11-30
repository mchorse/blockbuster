package mchorse.blockbuster.common.item;

import java.util.List;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.camera.CameraUtils;
import mchorse.blockbuster.common.tileentity.AbstractTileEntityDirector;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.camera.PacketCameraState;
import mchorse.blockbuster.utils.BlockPos;
import mchorse.blockbuster.utils.L10n;
import mchorse.blockbuster.utils.NBTUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

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
        this.setTextureName("blockbuster:playback");
        this.setUnlocalizedName("blockbuster.playback");
        this.setCreativeTab(Blockbuster.blockbusterTab);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced)
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
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
        if (!world.isRemote)
        {
            BlockPos pos = getBlockPos("Dir", stack);
            NBTTagCompound tag = stack.getTagCompound();

            if (pos == null || tag == null)
            {
                return stack;
            }

            if (player.isSneaking())
            {
                player.setPositionAndUpdate(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);

                return stack;
            }

            TileEntity tile = world.getTileEntity(pos.getX(), pos.getY(), pos.getZ());

            if (tile == null || !(tile instanceof AbstractTileEntityDirector))
            {
                L10n.error(player, "director.missing", pos.getX(), pos.getY(), pos.getZ());

                return stack;
            }

            AbstractTileEntityDirector director = (AbstractTileEntityDirector) tile;

            if (director.togglePlayback())
            {
                if (tag.hasKey("CameraPlay"))
                {
                    Dispatcher.sendTo(new PacketCameraState(true), (EntityPlayerMP) player);
                }
                else if (tag.hasKey("CameraProfile"))
                {
                    CameraUtils.sendProfileToPlayer(tag.getString("CameraProfile"), (EntityPlayerMP) player, true);
                }
            }
        }

        return stack;
    }
}