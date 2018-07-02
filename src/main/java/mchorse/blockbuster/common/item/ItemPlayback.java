package mchorse.blockbuster.common.item;

import java.util.List;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.aperture.CameraHandler;
import mchorse.blockbuster.capabilities.recording.IRecording;
import mchorse.blockbuster.capabilities.recording.Recording;
import mchorse.blockbuster.common.tileentity.TileEntityDirector;
import mchorse.blockbuster.utils.EntityUtils;
import mchorse.blockbuster.utils.NBTUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
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
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
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
                IRecording recording = Recording.get(player);

                float dx = (float) Math.abs(pos.getX() + 0.5F - player.posX);
                float dy = (float) Math.abs(pos.getY() + 0.5F - player.posY);
                float dz = (float) Math.abs(pos.getZ() + 0.5F - player.posZ);

                if (dx > 5 || dy > 5 || dz > 5)
                {
                    if (recording != null)
                    {
                        recording.setLastTeleportedBlockPos(new BlockPos(player));
                    }

                    this.teleportPlayerToDirectorBlock(pos, player, worldIn);
                }
                else
                {
                    if (recording != null && recording.getLastTeleportedBlockPos() != null)
                    {
                        pos = recording.getLastTeleportedBlockPos();
                        player.setPositionAndUpdate(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                        recording.setLastTeleportedBlockPos(null);
                    }
                }

                return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
            }

            TileEntity te = worldIn.getTileEntity(pos);

            if (te == null || !(te instanceof TileEntityDirector))
            {
                EntityUtils.sendStatusMessage((EntityPlayerMP) player, new TextComponentTranslation("blockbuster.error.director.missing", pos.getX(), pos.getY(), pos.getZ()));

                return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
            }

            TileEntityDirector tile = (TileEntityDirector) te;

            if (tile.director.togglePlayback() && CameraHandler.isApertureLoaded())
            {
                CameraHandler.handlePlaybackItem(player, tag);
            }
        }

        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
    }

    /**
     * Teleport given player to director block
     * 
     * This method is a bit complex than just teleport an entity on top 
     * of director block. Instead it's uses some algorithm to find 
     * closest available space.
     */
    private void teleportPlayerToDirectorBlock(BlockPos pos, EntityPlayer player, World worldIn)
    {
        BlockPos newPos = this.findPerfectSpotToTeleport(pos, worldIn);

        double dX = (pos.getX() + 0.5) - (newPos.getX() + 0.5);
        double dY = (pos.getY() + 0.5) - (newPos.getY() + player.eyeHeight * player.height);
        double dZ = (pos.getZ() + 0.5) - (newPos.getZ() + 0.5);
        double horizontalDistance = MathHelper.sqrt(dX * dX + dZ * dZ);

        float yaw = (float) (MathHelper.atan2(dZ, dX) * (180D / Math.PI)) - 90.0F;
        float pitch = (float) (-(MathHelper.atan2(dY, horizontalDistance) * (180D / Math.PI)));

        player.setPositionAndRotation(newPos.getX() + 0.5, newPos.getY() + 1, newPos.getZ() + 0.5, yaw, pitch);
        player.setPositionAndUpdate(newPos.getX() + 0.5, newPos.getY() + 1, newPos.getZ() + 0.5);
    }

    /**
     * Find the perfect spot for teleporting the player within 5x5x5 
     * radius away from director block
     */
    private BlockPos findPerfectSpotToTeleport(BlockPos pos, World worldIn)
    {
        for (int y = 0; y < 5; y++)
        {
            for (int x = 0; x < 5; x++)
            {
                for (int z = 0; z < 5; z++)
                {
                    /* Centerized indices (hopefully it's going to be 
                     * faster out than from left-top corner) */
                    int nx = x > 2 ? -x + 2 : x;
                    int ny = y > 2 ? -y + 2 : y;
                    int nz = z > 2 ? -z + 2 : z;

                    BlockPos newPos = new BlockPos(pos.getX() + nx, pos.getY() + ny + 1, pos.getZ() + nz);

                    if (worldIn.isAirBlock(newPos))
                    {
                        BlockPos topPos = new BlockPos(newPos.getX(), newPos.getY() + 1, newPos.getZ());

                        if (worldIn.isAirBlock(topPos))
                        {
                            BlockPos belowPos = new BlockPos(newPos.getX(), newPos.getY() - 1, newPos.getZ());

                            return new BlockPos(newPos.getX(), newPos.getY() - (worldIn.isAirBlock(belowPos) ? 2 : 1), newPos.getZ());
                        }
                    }
                }
            }
        }

        return pos;
    }
}