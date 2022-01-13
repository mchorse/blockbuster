package mchorse.blockbuster.common.item;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.CommonProxy;
import mchorse.blockbuster.common.GunProps;
import mchorse.blockbuster.common.entity.EntityActor.EntityFakePlayer;
import mchorse.blockbuster.common.entity.EntityGunProjectile;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.guns.PacketGunInfo;
import mchorse.blockbuster.network.common.guns.PacketGunShot;
import mchorse.blockbuster.recording.actions.Action;
import mchorse.blockbuster.recording.actions.ShootGunAction;
import mchorse.blockbuster.utils.NBTUtils;
import mchorse.blockbuster_pack.morphs.SequencerMorph;
import mchorse.mclib.utils.Interpolation;
import mchorse.metamorph.api.MorphUtils;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import javax.vecmath.Matrix3f;
import javax.vecmath.Vector3f;
import java.util.List;

public class ItemGun extends Item
{
    public static float getRandom(float a, float b)
    {
        return (float) Math.random() * (a - b) + b;
    }

    public ItemGun()
    {
        this.setMaxStackSize(1);
        this.setRegistryName("gun");
        this.setUnlocalizedName("blockbuster.gun");
        this.setCreativeTab(Blockbuster.blockbusterTab);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack stack, ItemStack stack1, boolean should)
    {
        return false; // what animation to use when the player holds the "use" button
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker)
    {
        GunProps props = NBTUtils.getGunProps(stack);
        boolean result = super.hitEntity(stack, target, attacker);

        if (props == null)
        {
            return result;
        }

        if (!result)
        {
            if (!props.meleeCommand.isEmpty())
            {
                if (attacker instanceof EntityPlayerMP)
                {
                    EntityPlayerMP player = (EntityPlayerMP) attacker;

                    player.getServer().commandManager.executeCommand(player, props.meleeCommand);
                }
            }

            target.setHealth(target.getHealth() - props.meleeDamage);

            return false;
        }

        return true;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack)
    {
        return EnumAction.NONE;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack)
    {
        return 0;
    }

    public static void decreaseTime(ItemStack stack, EntityPlayer player)
    {
        GunProps props = NBTUtils.getGunProps(stack);

        if (props == null)
        {
            return;
        }

        if (props.timeBetweenShoot > 0)
        {
            props.timeBetweenShoot = Math.max(props.timeBetweenShoot - 1, 0);

            NBTUtils.saveGunProps(stack, props.toNBT());

            if (!player.world.isRemote)
            {
                Dispatcher.sendTo(new PacketGunInfo(props.toNBT(), player.getEntityId()), (EntityPlayerMP) player);
                Dispatcher.sendToTracked(player, new PacketGunInfo(props.toNBT(), player.getEntityId()));
            }
        }
    }

    private void resetTime(ItemStack stack, EntityPlayer player)
    {
        GunProps props = NBTUtils.getGunProps(stack);

        if (props == null)
        {
            return;
        }

        props.timeBetweenShoot = props.inputTimeBetweenShoot;

        NBTUtils.saveGunProps(stack, props.toNBT());
    }

    public static void decreaseReload(ItemStack stack, EntityPlayer player)
    {
        GunProps props = NBTUtils.getGunProps(stack);
        if (props == null)
        {
            return;
        }
        if (props.state == GunState.RELOADING)
        {
            props.reloadTick = props.reloadTick - 1;

            if (props.reloadTick <= 0)
            {
                props.reloadTick = 0;
                props.state = GunState.READY_TO_SHOOT;
                props.innerAmmo = props.inputAmmo;
            }

            NBTUtils.saveGunProps(stack, props.toNBT());
        }
    }

    public void decreaseDurability(GunProps props, ItemStack stack, EntityPlayer player)
    {
        if (props == null)
        {
            return;
        }

        if (props.durability != 0)
        {
            int val = props.hidedurability - 1;

            if (val <= 0)
            {
                if (!props.destrCommand.isEmpty())
                {
                    player.getServer().commandManager.executeCommand(player, props.destrCommand);
                }

                player.getHeldItemMainhand().setCount(0);
            }

            props.hidedurability = val;

            NBTUtils.saveGunProps(player.getHeldItemMainhand(), props.toNBT());
        }
    }

    @Override
    public boolean onLeftClickEntity(ItemStack p_onLeftClickEntity_1_, EntityPlayer p_onLeftClickEntity_2_, Entity p_onLeftClickEntity_3_)
    {
        return false;
    }

    public void shootIt(ItemStack stack, EntityPlayer player, World world)
    {
        resetTime(stack, player);
        GunProps props = NBTUtils.getGunProps(stack);

        if (world.isRemote && props != null)
        {
            if (props.recoilSimple)
            {
                player.rotationPitch += Interpolation.QUINT_IN.interpolate(player.prevRotationPitch, player.prevRotationPitch + props.recoilXMin, 1F) - player.prevRotationPitch;
                player.rotationYaw += Interpolation.QUINT_IN.interpolate(player.prevRotationYaw, player.prevRotationYaw + props.recoilYMin, 1F) - player.prevRotationYaw;
            }
            else
            {
                player.rotationPitch += Interpolation.SINE_IN.interpolate(player.prevRotationPitch, player.prevRotationPitch + getRandom(props.recoilXMin, props.recoilXMax), 1F) - player.prevRotationPitch;
                player.rotationYaw += Interpolation.SINE_IN.interpolate(player.prevRotationYaw, player.prevRotationYaw + getRandom(props.recoilYMin, props.recoilYMax), 1F) - player.prevRotationYaw;
            }

            if (props.launch)
            {
                float pitch = player.rotationPitch + (float) ((Math.random() - 0.5) * props.scatterY);
                float yaw = player.rotationYaw + (float) ((Math.random() - 0.5) * props.scatterX);

                this.setThrowableHeading(player, pitch, yaw, 0, props.speed);
            }
        }

        this.shoot(stack, props, player, world);
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
            float pitch = player.rotationPitch + (float) ((Math.random() - 0.5) * props.scatterY);
            float yaw = player.rotationYaw + (float) ((Math.random() - 0.5) * props.scatterX);

            this.setThrowableHeading(player, pitch, yaw, 0, props.speed);

            if (!props.fireCommand.isEmpty())
            {
                player.getServer().commandManager.executeCommand(player, props.fireCommand);
            }
        }
        else
        {
            /* Or otherwise launch bullets */
            if (!this.consumeInnerAmmo(stack, player))
            {
                return false;
            }

            EntityGunProjectile last = null;

            for (int i = 0; i < Math.max(props.projectiles, 1); i++)
            {
                AbstractMorph morph = props.projectileMorph;

                if (props.sequencer && morph instanceof SequencerMorph)
                {
                    SequencerMorph seq = ((SequencerMorph) morph);
                    morph = props.random ? seq.getRandom() : seq.get(i % seq.morphs.size());
                }

                morph = MorphUtils.copy(morph);

                EntityGunProjectile projectile = new EntityGunProjectile(world, props, morph);

                float pitch = player.rotationPitch + (float) ((Math.random() - 0.5) * props.scatterY);
                float yaw = player.rotationYaw + (float) ((Math.random() - 0.5) * props.scatterX);
                double x = player.posX;
                double y = player.posY + player.getEyeHeight();
                double z = player.posZ;
                Vector3f offset = new Vector3f(props.srcShootX, props.srcShootY, props.srcShootZ);
                Vector3f vector = this.rotate(offset, player.rotationYaw, player.rotationPitch);

                x += vector.x;
                y += vector.y;
                z += vector.z;

                projectile.setPosition(x, y, z);
                projectile.shoot(player, pitch, yaw, 0, props.speed, 0);
                projectile.setInitialMotion();
                if (props.projectiles > 0)
                {
                    if (!world.isRemote)
                    {
                        world.spawnEntity(projectile);
                    }
                }

                last = projectile;
            }

            if (!props.fireCommand.isEmpty())
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

        if (!world.isRemote)
        {
            List<Action> events = CommonProxy.manager.getActions(player);
            if (events != null)
            {
                events.add(new ShootGunAction(stack));
            }
        }

        // McHorse: wtf?
        GunProps newProps = NBTUtils.getGunProps(stack);

        if (player instanceof EntityPlayerMP)
        {
            Dispatcher.sendTo(new PacketGunInfo(newProps.toNBT(), entity.getEntityId()), (EntityPlayerMP) player);
        }

        decreaseDurability(newProps, stack, player);

        GunProps newProps2 = NBTUtils.getGunProps(stack);

        if (player instanceof EntityPlayerMP)
        {
            Dispatcher.sendTo(new PacketGunInfo(newProps2.toNBT(), entity.getEntityId()), (EntityPlayerMP) player);
        }

        return true;
    }

    private Vector3f rotate(Vector3f vector, float yaw, float pitch)
    {
        Matrix3f a = new Matrix3f();
        Matrix3f b = new Matrix3f();

        a.rotY(yaw / 180F * (float) Math.PI);
        b.rotX(pitch / 180F * (float) Math.PI);
        a.mul(b);
        a.transform(vector);

        return vector;
    }

    public static void checkGunState(ItemStack stack, EntityPlayer player)
    {
        GunProps props = NBTUtils.getGunProps(stack);

        if (props == null)
        {
            return;
        }

        if (props.innerAmmo <= 0 && props.needToBeReloaded && props.state == GunState.READY_TO_SHOOT)
        {
            props.state = GunState.NEED_TO_BE_RELOAD;
        }

        NBTUtils.saveGunProps(player.getHeldItemMainhand(), props.toNBT());
    }

    private boolean consumeInnerAmmo(ItemStack stack, EntityPlayer player)
    {
        GunProps props = NBTUtils.getGunProps(stack);

        if (props == null)
        {
            return false;
        }

        int ammo = props.innerAmmo;

        if (ammo <= 0)
        {
            if (props.needToBeReloaded)
            {
                return false;
            }
            else
            {
                props.innerAmmo = props.inputAmmo;

                NBTUtils.saveGunProps(player.getHeldItemMainhand(), props.toNBT());

                if (!player.capabilities.isCreativeMode && !props.ammoStack.isEmpty())
                {
                    return this.consumeAmmoStack(player, props.ammoStack);
                }
                else
                {
                    return true;
                }
            }
        }

        this.consumeAmmo(stack, player);

        return true;
    }

    private void consumeAmmo(ItemStack stack, EntityPlayer player)
    {
        GunProps props = NBTUtils.getGunProps(stack);

        if (props == null)
        {
            return;
        }

        props.innerAmmo -= 1;

        NBTUtils.saveGunProps(player.getHeldItemMainhand(), props.toNBT());

    }

    public boolean consumeAmmoStack(EntityPlayer player, ItemStack ammo)
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

    private void setThrowableHeading(EntityLivingBase entityThrower, float rotationPitchIn, float rotationYawIn, float pitchOffset, float velocity)
    {
        float f = -MathHelper.sin(rotationYawIn * 0.017453292F) * MathHelper.cos(rotationPitchIn * 0.017453292F);
        float f1 = -MathHelper.sin((rotationPitchIn + pitchOffset) * 0.017453292F);
        float f2 = MathHelper.cos(rotationYawIn * 0.017453292F) * MathHelper.cos(rotationPitchIn * 0.017453292F);

        this.setThrowableHeading(entityThrower, (double) f, (double) f1, (double) f2, velocity);
    }

    public void setThrowableHeading(EntityLivingBase entity, double x, double y, double z, float velocity)
    {
        float distance = MathHelper.sqrt(x * x + y * y + z * z);

        entity.motionX = x / distance * velocity;
        entity.motionY = y / distance * velocity;
        entity.motionZ = z / distance * velocity;
    }

    public enum GunState
    {
        READY_TO_SHOOT,
        RELOADING,
        NEED_TO_BE_RELOAD
    }
}