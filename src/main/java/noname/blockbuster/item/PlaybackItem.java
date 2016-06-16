package noname.blockbuster.item;

import net.minecraft.entity.player.EntityPlayer;
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
import noname.blockbuster.tileentity.AbstractDirectorTileEntity;

/**
 * Record item
 *
 * Push to start recording the scene (actually it just makes all actors do their
 * business, i.e. playing their role)
 */
public class PlaybackItem extends Item
{
    public PlaybackItem()
    {
        this.setMaxStackSize(1);
        this.setUnlocalizedName("playbackItem");
        this.setRegistryName("playbackItem");
        this.setCreativeTab(Blockbuster.blockbusterTab);
    }

    /**
     * This method starts playback of the director block's actors (if the
     * director block is attached to this item stack).
     */
    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World worldIn, EntityPlayer playerIn, EnumHand hand)
    {
        if (!worldIn.isRemote)
        {
            if (stack.getTagCompound() == null)
            {
                return new ActionResult(EnumActionResult.PASS, stack);
            }

            NBTTagCompound tag = stack.getTagCompound();

            int x = tag.getInteger("DirX");
            int y = tag.getInteger("DirY");
            int z = tag.getInteger("DirZ");

            TileEntity tile = worldIn.getTileEntity(new BlockPos(x, y, z));

            if (tile == null || !(tile instanceof AbstractDirectorTileEntity))
            {
                playerIn.addChatMessage(new TextComponentTranslation("blockbuster.director.missing"));

                return new ActionResult(EnumActionResult.PASS, stack);
            }

            AbstractDirectorTileEntity director = (AbstractDirectorTileEntity) tile;

            director.startPlayback();
        }

        return new ActionResult(EnumActionResult.SUCCESS, stack);
    }
}