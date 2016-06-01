package noname.blockbuster.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import noname.blockbuster.Blockbuster;
import noname.blockbuster.tileentity.DirectorTileEntity;

/**
 * Record item
 *
 * Push to start recording the scene (actually it just makes all actors do their
 * business, i.e. playing their role)
 */
public class RecordItem extends Item
{
    public RecordItem()
    {
        this.setMaxStackSize(1);
        this.setUnlocalizedName("recordItem");
        this.setRegistryName("recordItem");
        this.setCreativeTab(Blockbuster.blockbusterTab);
    }

    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, net.minecraft.util.math.BlockPos pos, EnumHand hand, net.minecraft.util.EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (!worldIn.isRemote)
        {
            if (stack.getTagCompound() == null)
            {
                return EnumActionResult.PASS;
            }

            NBTTagCompound tag = stack.getTagCompound();

            int x = tag.getInteger("DirX");
            int y = tag.getInteger("DirY");
            int z = tag.getInteger("DirZ");

            TileEntity tile = worldIn.getTileEntity(new BlockPos(x, y, z));

            if (tile == null || !(tile instanceof DirectorTileEntity))
            {
                playerIn.addChatMessage(new TextComponentString("The director block, that was attached to this device, was destroyed. Attach this device to another director block!"));

                return EnumActionResult.PASS;
            }

            DirectorTileEntity director = (DirectorTileEntity) tile;

            director.startRecording();
        }

        return EnumActionResult.SUCCESS;
    }
}