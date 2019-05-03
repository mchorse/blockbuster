package mchorse.blockbuster.common.item;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.capabilities.gun.Gun;
import mchorse.blockbuster.capabilities.gun.IGun;
import mchorse.blockbuster.common.GunProps;
import mchorse.blockbuster.common.entity.EntityGunProjectile;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.guns.PacketGunShot;
import mchorse.blockbuster_pack.morphs.SequencerMorph;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
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
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
    {
        ItemStack stack = player.getHeldItem(hand);

        return new ActionResult<ItemStack>(this.shootIt(stack, player, world), stack);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        ItemStack stack = player.getHeldItem(hand);

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

        GunProps props = gun.getProps();
        EntityGunProjectile last = null;

        for (int i = 0; i < props.projectiles; i++)
        {
            AbstractMorph morph = props.projectileMorph;

            if (props.sequencer && morph instanceof SequencerMorph)
            {
                SequencerMorph seq = ((SequencerMorph) morph);

                morph = props.random ? seq.getRandom() : seq.get(i % seq.morphs.size());
            }

            if (morph != null)
            {
                morph = morph.clone(world.isRemote);
            }

            EntityGunProjectile projectile = new EntityGunProjectile(world, gun.getProps(), morph);

            projectile.setPosition(player.posX, player.posY + player.getEyeHeight(), player.posZ);
            projectile.shoot(player, player.rotationPitch, player.rotationYaw, 0, props.speed, props.accuracy);
            world.spawnEntity(projectile);
            last = projectile;
        }

        if (!props.fireCommand.isEmpty() && last != null)
        {
            player.getServer().commandManager.executeCommand(last, props.fireCommand);
        }

        if (player instanceof EntityPlayerMP)
        {
            Dispatcher.sendTo(new PacketGunShot(player.getEntityId()), (EntityPlayerMP) player);
        }

        Dispatcher.sendToTracked(player, new PacketGunShot(player.getEntityId()));

        return true;
    }
}