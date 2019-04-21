package mchorse.blockbuster.common.item;

import mchorse.blockbuster.common.entity.EntityGunProjectile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemGun extends Item
{
    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand)
    {
        if (world.isRemote)
        {
            return super.onItemRightClick(stack, world, player, hand);
        }

        return new ActionResult<ItemStack>(this.shoot(stack.getTagCompound(), player, world) ? EnumActionResult.SUCCESS : EnumActionResult.FAIL, stack);
    }

    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (world.isRemote)
        {
            return EnumActionResult.SUCCESS;
        }

        return this.shoot(stack.getTagCompound(), player, world) ? EnumActionResult.SUCCESS : EnumActionResult.FAIL;
    }

    public boolean shoot(NBTTagCompound tag, EntityPlayer player, World world)
    {
        if (tag == null)
        {
            return false;
        }

        EntityGunProjectile projectile = new EntityGunProjectile(world, tag);

        world.spawnEntityInWorld(projectile);

        return true;
    }
}