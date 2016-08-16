package noname.blockbuster.item;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import noname.blockbuster.Blockbuster;
import noname.blockbuster.camera.CameraUtils;
import noname.blockbuster.network.Dispatcher;
import noname.blockbuster.network.common.camera.PacketCameraState;
import noname.blockbuster.tileentity.AbstractTileEntityDirector;

/**
 * Record item
 *
 * Push to start recording the scene (actually it just makes all actors do their
 * business, i.e. playing their role)
 */
public class ItemPlayback extends Item
{
    /* NBTTag + BlockPos helpers */

    /**
     * Save given {@link BlockPos} into ItemStack's {@link NBTTagCompound} tag
     */
    public static void saveBlockPos(String key, ItemStack stack, BlockPos pos)
    {
        if (stack.getTagCompound() == null)
        {
            stack.setTagCompound(new NBTTagCompound());
        }

        saveBlockPos(key, stack.getTagCompound(), pos);
    }

    /**
     * Save given {@link BlockPos} into {@link NBTTagCompound} tag
     */
    public static void saveBlockPos(String key, NBTTagCompound tag, BlockPos pos)
    {
        tag.setInteger(key + "X", pos.getX());
        tag.setInteger(key + "Y", pos.getY());
        tag.setInteger(key + "Z", pos.getZ());
    }

    /**
     * Get {@link BlockPos} position from {@link NBTTagCompound} tag
     */
    public static BlockPos getBlockPos(String key, NBTTagCompound tag)
    {
        String x = key + "X";
        String y = key + "Y";
        String z = key + "Z";

        if (tag == null || !tag.hasKey(x) || !tag.hasKey(y) || !tag.hasKey(z))
        {
            return null;
        }

        return new BlockPos(tag.getInteger(x), tag.getInteger(y), tag.getInteger(z));
    }

    /**
     * Get {@link BlockPos} position from ItemStack's {@link NBTTagCompound} tag
     */
    public static BlockPos getBlockPos(String key, ItemStack stack)
    {
        return getBlockPos(key, stack.getTagCompound());
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
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced)
    {
        NBTTagCompound tag = stack.getTagCompound();

        if (tag == null) return;

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
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World worldIn, EntityPlayer player, EnumHand hand)
    {
        if (!worldIn.isRemote)
        {
            BlockPos pos = getBlockPos("Dir", stack);
            NBTTagCompound tag = stack.getTagCompound();

            if (pos == null || tag == null)
            {
                return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
            }

            if (player.isSneaking())
            {
                player.setPositionAndUpdate(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);

                return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
            }

            TileEntity tile = worldIn.getTileEntity(pos);

            if (tile == null || !(tile instanceof AbstractTileEntityDirector))
            {
                player.addChatMessage(new TextComponentTranslation("blockbuster.director.missing", pos.getX(), pos.getY(), pos.getZ()));

                return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
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

        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
    }
}