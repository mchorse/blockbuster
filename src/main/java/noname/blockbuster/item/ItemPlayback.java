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
import noname.blockbuster.network.common.PacketCameraState;
import noname.blockbuster.tileentity.AbstractTileEntityDirector;

/**
 * Record item
 *
 * Push to start recording the scene (actually it just makes all actors do their
 * business, i.e. playing their role)
 */
public class ItemPlayback extends Item
{
    public ItemPlayback()
    {
        this.setMaxStackSize(1);
        this.setRegistryName("playback");
        this.setUnlocalizedName("blockbuster.playback");
        this.setCreativeTab(Blockbuster.blockbusterTab);
    }

    /**
     * Adds information about camera profile and */
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

        if (tag.hasKey("DirX") && tag.hasKey("DirY") && tag.hasKey("DirZ"))
        {
            int x = tag.getInteger("DirX");
            int y = tag.getInteger("DirY");
            int z = tag.getInteger("DirZ");

            tooltip.add(I18n.format("blockbuster.info.playback_director", x, y, z));
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
            if (stack.getTagCompound() == null)
            {
                return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
            }

            NBTTagCompound tag = stack.getTagCompound();

            int x = tag.getInteger("DirX");
            int y = tag.getInteger("DirY");
            int z = tag.getInteger("DirZ");

            TileEntity tile = worldIn.getTileEntity(new BlockPos(x, y, z));

            if (tile == null || !(tile instanceof AbstractTileEntityDirector))
            {
                player.addChatMessage(new TextComponentTranslation("blockbuster.director.missing"));

                return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
            }

            if (tag.hasKey("CameraPlay"))
            {
                Dispatcher.sendTo(new PacketCameraState(true), (EntityPlayerMP) player);
            }
            else if (tag.hasKey("CameraProfile"))
            {
                String profile = tag.getString("CameraProfile");
                CameraUtils.sendProfileToPlayer(profile, (EntityPlayerMP) player, true);
            }

            ((AbstractTileEntityDirector) tile).startPlayback();
        }

        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
    }
}