package mchorse.blockbuster.common.item;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.CommonProxy;
import mchorse.blockbuster.client.render.tileentity.TileEntityGunItemStackRenderer;
import mchorse.blockbuster.client.render.tileentity.TileEntityGunItemStackRenderer.GunEntry;
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
import mchorse.mclib.utils.OpHelper;
import mchorse.metamorph.api.MorphUtils;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import javax.vecmath.Matrix3f;
import javax.vecmath.Vector3f;

import java.util.List;
import java.util.Objects;

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
    public boolean shouldCauseReequipAnimation(ItemStack from, ItemStack to, boolean changed)
    {
        if (!changed && to.getItem() instanceof ItemGun)
        {
            GunEntry entry = TileEntityGunItemStackRenderer.models.get(from);

            if (entry != null)
            {
                GunProps props = NBTUtils.getGunProps(to);
                boolean same = true;

                same &= Objects.equals(entry.props.defaultMorph, props.defaultMorph);
                same &= Objects.equals(entry.props.firingMorph, props.firingMorph);
                same &= Objects.equals(entry.props.crosshairMorph, props.crosshairMorph);
                same &= Objects.equals(entry.props.handsMorph, props.handsMorph);
                same &= Objects.equals(entry.props.reloadMorph, props.reloadMorph);
                same &= Objects.equals(entry.props.zoomOverlayMorph, props.zoomOverlayMorph);

                if (same)
                {
                    TileEntityGunItemStackRenderer.models.put(to, TileEntityGunItemStackRenderer.models.remove(from));
                }

                return !same;
            };
        }

        return true; // what animation to use when the player holds the "use" button
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

        if (props.storedShotDelay > 0)
        {
            props.storedShotDelay = Math.max(props.storedShotDelay - 1, 0);

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

        props.storedShotDelay = props.shotDelay;

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
            props.storedReloadingTime = props.storedReloadingTime - 1;

            if (props.storedReloadingTime <= 0)
            {
                props.storedReloadingTime = 0;
                props.state = GunState.READY_TO_SHOOT;
            }

            NBTUtils.saveGunProps(stack, props.toNBT());

            if (!player.world.isRemote)
            {
                Dispatcher.sendTo(new PacketGunInfo(props.toNBT(), player.getEntityId()), (EntityPlayerMP) player);
                Dispatcher.sendToTracked(player, new PacketGunInfo(props.toNBT(), player.getEntityId()));
            }
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
            int val = props.storedDurability - 1;

            if (val <= 0)
            {
                if (!props.destroyCommand.isEmpty())
                {
                    player.getServer().commandManager.executeCommand(player, props.destroyCommand);
                }

                player.getHeldItemMainhand().setCount(0);
            }

            props.storedDurability = val;
            
            if (NBTUtils.saveGunProps(stack, props.toNBT()))
            {
                IMessage packet = new PacketGunInfo(props.toNBT(), player.getEntityId());
                synchronize((EntityPlayerMP) player,props.toNBT());
                Dispatcher.sendTo(packet, (EntityPlayerMP) player);
                Dispatcher.sendToTracked(player, packet);
                
            }
    
            
        }
    }
    
    private void synchronize(EntityPlayerMP player, NBTTagCompound tag)
    {
        if (!OpHelper.isPlayerOp(player))
        {
            return;
        }
        
        ItemStack stack = player.getHeldItemMainhand();
        
        if (NBTUtils.saveGunProps(stack, tag))
        {
            IMessage packet = new PacketGunInfo(tag, player.getEntityId());
            Dispatcher.sendTo(packet, player);
            Dispatcher.sendToTracked(player, packet);
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
            if (props.staticRecoil)
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
                Vector3f offset = new Vector3f(props.shootingOffsetX, props.shootingOffsetY, props.shootingOffsetZ);
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

        if (!world.isRemote)
        {
            Entity entity = player instanceof EntityFakePlayer ? ((EntityFakePlayer) player).actor : player;
            int id = entity.getEntityId();

            if (player instanceof EntityPlayerMP)
            {
                Dispatcher.sendTo(new PacketGunShot(id), (EntityPlayerMP) player);
            }

            Dispatcher.sendToTracked(entity, new PacketGunShot(id));

            List<Action> events = CommonProxy.manager.getActions(player);

            if (events != null)
            {
                events.add(new ShootGunAction(stack));
            }
    
            
            decreaseDurability(NBTUtils.getGunProps(stack), stack, player);
            
            if (player instanceof EntityPlayerMP)
            {
                GunProps p = NBTUtils.getGunProps(stack);
                NBTUtils.saveGunProps(stack, p.toNBT());
                Dispatcher.sendTo(new PacketGunInfo(p.toNBT(), entity.getEntityId()), (EntityPlayerMP) player);
                Dispatcher.sendToTracked(player, new PacketGunInfo(p.toNBT(), entity.getEntityId()));
                
            }
            
        }

        return true;
    }

    private Vector3f rotate(Vector3f vector, float yaw, float pitch)
    {
        Matrix3f a = new Matrix3f();
        Matrix3f b = new Matrix3f();

        a.rotY((180 - yaw) / 180F * (float) Math.PI);
        b.rotX(-pitch / 180F * (float) Math.PI);
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

        if (props.storedAmmo <= 0 && props.useReloading && props.state == GunState.READY_TO_SHOOT)
        {
            props.state = GunState.NEED_TO_BE_RELOAD;
        }

        NBTUtils.saveGunProps(player.getHeldItemMainhand(), props.toNBT());

        if (!player.world.isRemote)
        {
            Dispatcher.sendTo(new PacketGunInfo(props.toNBT(), player.getEntityId()), (EntityPlayerMP) player);
            Dispatcher.sendToTracked(player, new PacketGunInfo(props.toNBT(), player.getEntityId()));
        }
    }

    public static void checkGunReload(ItemStack stack, EntityPlayer player)
    {
        if (!player.world.isRemote)
        {
            GunProps props = NBTUtils.getGunProps(stack);

            if (props.state == ItemGun.GunState.NEED_TO_BE_RELOAD && props.storedShotDelay == 0)
            {
                ItemGun gun = (ItemGun) stack.getItem();
                
                gun.reload(player, stack);
            }
        }
    }

    private boolean consumeInnerAmmo(ItemStack stack, EntityPlayer player)
    {
        GunProps props = NBTUtils.getGunProps(stack);

        if (props == null)
        {
            return false;
        }

        int ammo = props.storedAmmo;

        if (ammo <= 0)
        {
            if (props.useReloading)
            {
                return false;
            }
            else
            {
                props.storedAmmo = props.ammo;

                NBTUtils.saveGunProps(player.getHeldItemMainhand(), props.toNBT());

                if (!player.capabilities.isCreativeMode && !props.ammoStack.isEmpty())
                {
                    return this.consumeAmmoStack(player, props.ammoStack, props.ammoStack.getCount()) >= 0;
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

        props.storedAmmo -= 1;

        NBTUtils.saveGunProps(player.getHeldItemMainhand(), props.toNBT());

    }

    public int consumeAmmoStack(EntityPlayer player, ItemStack ammo, int count)
    {
        return player.inventory.clearMatchingItems(ammo.getItem(), -1, count, ammo.getTagCompound());
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

    public void reload(EntityPlayer player, ItemStack stack)
    {
        GunProps props = NBTUtils.getGunProps(stack);

        if (props == null)
        {
            return;
        }

        int count = 0;

        if (!player.capabilities.isCreativeMode && !props.ammoStack.isEmpty())
        {
            ItemStack ammo = props.ammoStack;

            count = this.consumeAmmoStack(player, ammo, props.ammo - props.storedAmmo);
        }
        else
        {
            count = props.ammo - props.storedAmmo;
        }

        if (count > 0)
        {
            props.state = ItemGun.GunState.RELOADING;
            props.storedAmmo += count;
            props.storedReloadingTime = props.reloadingTime;

            if (!props.reloadCommand.isEmpty())
            {
                player.getServer().commandManager.executeCommand(player, props.reloadCommand);
            }

            NBTUtils.saveGunProps(stack, props.toNBT());
            Dispatcher.sendTo(new PacketGunInfo(props.toNBT(), player.getEntityId()), (EntityPlayerMP) player);
            Dispatcher.sendToTracked(player, new PacketGunInfo(props.toNBT(), player.getEntityId()));
        }
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack)
    {
        GunProps props = NBTUtils.getGunProps(stack);

        if (props != null)
        {
            return props.state == GunState.RELOADING || props.ammo > 1 || props.durability > 0;
        }

        return super.showDurabilityBar(stack);
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack)
    {
        GunProps props = NBTUtils.getGunProps(stack);

        if (props != null)
        {
            if (props.state != GunState.READY_TO_SHOOT)
            {
                if (props.state == GunState.RELOADING && props.reloadingTime > 0)
                {
                    return ((double) props.storedReloadingTime / props.reloadingTime);
                }
                else
                {
                    return 1.0;
                }
            }
            else if (props.ammo > 1)
            {
                return 1.0 - ((double) props.storedAmmo / props.ammo);
            }
            else if (props.durability > 0)
            {
                return 1.0 - ((double) props.storedDurability / props.durability);
            }
        }

        return super.getDurabilityForDisplay(stack);
    }

    @Override
    public int getRGBDurabilityForDisplay(ItemStack stack)
    {
        GunProps props = NBTUtils.getGunProps(stack);

        if (props != null)
        {
            if (props.state != GunState.READY_TO_SHOOT)
            {
                return 0xFFFF0000;
            }
            else if (props.ammo > 1)
            {
                return 0xFF2FC0FF;
            }
        }

        return super.getRGBDurabilityForDisplay(stack);
    }

    public enum GunState
    {
        READY_TO_SHOOT,
        RELOADING,
        NEED_TO_BE_RELOAD
    }
}