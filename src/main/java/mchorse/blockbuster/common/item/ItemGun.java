package mchorse.blockbuster.common.item;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.capabilities.gun.Gun;
import mchorse.blockbuster.capabilities.gun.IGun;
import mchorse.blockbuster.common.GuiHandler;
import mchorse.blockbuster.common.GunInfo;
import mchorse.blockbuster.common.entity.EntityGunProjectile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemGun extends Item
{
    public ItemGun()
    {
        this.setMaxStackSize(1);
        this.setRegistryName("gun");
        this.setUnlocalizedName("blockbuster.gun");
        this.setCreativeTab(Blockbuster.blockbusterTab);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand)
    {
        if (player.isSneaking())
        {
            if (world.isRemote)
            {
                GuiHandler.open(player, GuiHandler.GUN, 0, 0, 0);
            }

            return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
        }

        return new ActionResult<ItemStack>(this.shootIt(stack, player, world), stack);
    }

    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        return this.shootIt(stack, player, world);
    }

    public EnumActionResult shootIt(ItemStack stack, EntityPlayer player, World world)
    {
        if (world.isRemote)
        {
            return EnumActionResult.SUCCESS;
        }

        return this.shoot(stack, player, world) ? EnumActionResult.SUCCESS : EnumActionResult.FAIL;
    }

    public boolean shoot(ItemStack stack, EntityPlayer player, World world)
    {
        IGun gun = Gun.get(stack);

        if (gun == null)
        {
            return false;
        }

        GunInfo info = gun.getInfo();
        EntityGunProjectile last = null;

        for (int i = 0; i < info.projectiles; i++)
        {
            EntityGunProjectile projectile = new EntityGunProjectile(world, gun.getInfo());

            projectile.setPosition(player.posX, player.posY + player.getEyeHeight(), player.posZ);
            projectile.setHeadingFromThrower(player, player.rotationPitch, player.rotationYaw, 0, info.speed, info.accuracy);
            world.spawnEntityInWorld(projectile);
            last = projectile;
        }

        if (!info.fireCommand.isEmpty() && last != null)
        {
            player.getServer().commandManager.executeCommand(last, info.fireCommand);
        }

        return true;
    }
}