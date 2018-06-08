package mchorse.blockbuster.common.item;

import java.util.List;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.common.tileentity.TileEntityDirector;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Register item
 *
 * Used to register an actor to director block (a scene)
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

    /**
     * Register a director block to a stack of register item
     */
    public void registerStack(ItemStack stack, BlockPos pos)
    {
        ItemPlayback.saveBlockPos("Dir", stack, pos);
    }

    /**
     * Get block position out of item stack's NBT tag
     */
    public BlockPos getBlockPos(ItemStack stack)
    {
        return ItemPlayback.getBlockPos("Dir", stack);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced)
    {
        tooltip.add(I18n.format("blockbuster.info.register"));

        BlockPos pos = ItemPlayback.getBlockPos("Dir", stack);

        if (pos != null)
        {
            tooltip.add(I18n.format("blockbuster.info.playback_director", pos.getX(), pos.getY(), pos.getZ()));
        }
    }

    /**
     * On right click, show the director block GUI, if this item has attached
     * director block, then we can show the director block GUI.
     */
    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand)
    {
        BlockPos pos = this.getBlockPos(stack);

        if (pos != null)
        {
            if (!world.isRemote)
            {
                TileEntity tile = world.getTileEntity(pos);

                if (tile instanceof TileEntityDirector)
                {
                    ((TileEntityDirector) tile).open(player, pos);
                }
            }

            return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
        }

        return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
    }
}