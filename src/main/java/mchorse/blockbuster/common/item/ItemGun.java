package mchorse.blockbuster.common.item;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.common.GunProps;
import mchorse.blockbuster.common.entity.EntityActor.EntityFakePlayer;
import mchorse.blockbuster.common.entity.EntityGunProjectile;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.guns.PacketGunShot;
import mchorse.blockbuster.utils.NBTUtils;
import mchorse.blockbuster_pack.morphs.SequencerMorph;
import mchorse.metamorph.api.MorphUtils;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;

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
        GunProps props = NBTUtils.getGunProps(stack);

        if (world.isRemote)
        {
            if (props != null && props.launch)
            {
                this.setThrowableHeading(player, player.rotationPitch, player.rotationYaw, 0, props.speed, props.scatter);
            }

            return EnumActionResult.PASS;
        }

        return this.shoot(stack, props, player, world) ? EnumActionResult.PASS : EnumActionResult.FAIL;
    }

    public boolean shoot(ItemStack stack, GunProps props, EntityPlayer player, World world)
    {
        if (props == null)
        {
            return false;
        }

        /* Launch the player is enabled */
        if (props.launch)
        {
            this.setThrowableHeading(player, player.rotationPitch, player.rotationYaw, 0, props.speed, props.scatter);

            if (!props.fireCommand.isEmpty())
            {
                player.getServer().commandManager.executeCommand(player, props.fireCommand);
            }
        }
        /* Or otherwise launch bullets */
        else
        {
            if (!player.capabilities.isCreativeMode && !props.ammoStack.isEmpty())
            {
                ItemStack ammo = props.ammoStack;

                if (!this.consumeAmmoStack(player, ammo))
                {
                    return false;
                }
            }

            EntityGunProjectile last = null;

            for (int i = 0; i < props.projectiles; i++)
            {
                AbstractMorph morph = props.projectileMorph;

                if (props.sequencer && morph instanceof SequencerMorph)
                {
                    SequencerMorph seq = ((SequencerMorph) morph);

                    morph = props.random ? seq.getRandom() : seq.get(i % seq.morphs.size());
                }

                morph = MorphUtils.copy(morph);

                EntityGunProjectile projectile = new EntityGunProjectile(world, props, morph);

                projectile.setPosition(player.posX, player.posY + player.getEyeHeight(), player.posZ);
                projectile.shoot(player, player.rotationPitch, player.rotationYaw, 0, props.speed, props.scatter);
                projectile.setInitialMotion();
                world.spawnEntity(projectile);
                last = projectile;
            }

            if (!props.fireCommand.isEmpty() && last != null)
            {
                player.getServer().commandManager.executeCommand(last, props.fireCommand);
            }
        }

        Entity entity = player instanceof EntityFakePlayer ? ((EntityFakePlayer) player).actor : player;
        int id = entity.getEntityId();

        if (player instanceof EntityPlayerMP)
        {
            Dispatcher.sendTo(new PacketGunShot(id), (EntityPlayerMP) player);
        }

        Dispatcher.sendToTracked(entity, new PacketGunShot(id));

        return true;
    }

    private boolean consumeAmmoStack(EntityPlayer player, ItemStack ammo)
    {
        int total = 0;

        for (int i = 0, c = player.inventory.getSizeInventory(); i < c; i++)
        {
            ItemStack stack = player.inventory.getStackInSlot(i);

            if (stack.isItemEqual(ammo))
            {
                total += stack.getCount();

                if (total >= ammo.getCount())
                {
                    break;
                }
            }
        }

        if (total < ammo.getCount())
        {
            return false;
        }

        return player.inventory.clearMatchingItems(ammo.getItem(), -1, ammo.getCount(), null) >= 0;
    }

    private void setThrowableHeading(EntityLivingBase entityThrower, float rotationPitchIn, float rotationYawIn, float pitchOffset, float velocity, float inaccuracy)
    {
        float f = -MathHelper.sin(rotationYawIn * 0.017453292F) * MathHelper.cos(rotationPitchIn * 0.017453292F);
        float f1 = -MathHelper.sin((rotationPitchIn + pitchOffset) * 0.017453292F);
        float f2 = MathHelper.cos(rotationYawIn * 0.017453292F) * MathHelper.cos(rotationPitchIn * 0.017453292F);
        this.setThrowableHeading(entityThrower, (double) f, (double) f1, (double) f2, velocity, inaccuracy);
    }

    public void setThrowableHeading(EntityLivingBase entity, double x, double y, double z, float velocity, float inaccuracy)
    {
        float f = MathHelper.sqrt(x * x + y * y + z * z);
        x = x / (double) f;
        y = y / (double) f;
        z = z / (double) f;
        x = x + entity.getRNG().nextGaussian() * 0.007499999832361937D * (double) inaccuracy;
        y = y + entity.getRNG().nextGaussian() * 0.007499999832361937D * (double) inaccuracy;
        z = z + entity.getRNG().nextGaussian() * 0.007499999832361937D * (double) inaccuracy;
        x = x * (double) velocity;
        y = y * (double) velocity;
        z = z * (double) velocity;
        entity.motionX = x;
        entity.motionY = y;
        entity.motionZ = z;
    }
}