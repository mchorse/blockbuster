package mchorse.blockbuster.common.item;

import com.google.common.collect.ImmutableMap;
import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.CommonProxy;
import mchorse.blockbuster.common.GunProps;
import mchorse.blockbuster.common.entity.EntityActor.EntityFakePlayer;
import mchorse.blockbuster.common.entity.EntityGunProjectile;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.guns.PacketGunInfo;
import mchorse.blockbuster.network.common.guns.PacketGunInteract;
import mchorse.blockbuster.network.common.guns.PacketGunShot;
import mchorse.blockbuster.recording.actions.Action;
import mchorse.blockbuster.recording.actions.ShootGunAction;
import mchorse.blockbuster.utils.NBTUtils;
import mchorse.blockbuster_pack.morphs.SequencerMorph;
import mchorse.mclib.utils.Interpolation;
import mchorse.metamorph.api.MorphUtils;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.common.animation.ITimeValue;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.geom.Point2D;
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
    public boolean shouldCauseReequipAnimation(ItemStack stack, ItemStack stack1, boolean should)
    {
        return false;
    }
    // what animation to use when the player holds the "use" button

    @Override
    public EnumAction getItemUseAction(ItemStack p_getItemUseAction_1_)
    {
        return EnumAction.NONE;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack p_getMaxItemUseDuration_1_)
    {
        return 0;
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int p_onUpdate_4_, boolean p_onUpdate_5_)
    {
        GunProps props = NBTUtils.getGunProps(stack);
        
        if (props == null)
        {
            return;
        }
        
        if (props.getGUNState() == GunState.RELOADING)
        {
            props.reloadTick = props.reloadTick - 1;
            if (props.reloadTick <= 0)
            {
                props.reloadTick = 0;
                props.setGUNState(GunState.READY_TO_SHOOT);
                props.innerAmmo = props.inputAmmo;
            }
            Dispatcher.sendToServer(new PacketGunInfo(props.toNBT(), entity.getEntityId()));

        }
        
        if (props.timeBetweenShoot != 0)
        {
            props.timeBetweenShoot = props.timeBetweenShoot - 1;
            if (props.timeBetweenShoot < 0)
            {
                props.timeBetweenShoot = 0;
            }
            Dispatcher.sendToServer(new PacketGunInfo(props.toNBT(), entity.getEntityId()));
        }
        
        if (entity instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) entity;
        }
        super.onUpdate(stack, world, entity, p_onUpdate_4_, p_onUpdate_5_);
    }

    @SideOnly(Side.CLIENT)
    public EnumActionResult clientShoot(ItemStack stack, EntityPlayer player, World world)
    {

        GunProps props = NBTUtils.getGunProps(stack);
        if (world.isRemote) {
            
            if (props.recoilSimple)
            {
                player.rotationPitch += Interpolation.SINE_IN.interpolate(player.prevRotationPitch, player.prevRotationPitch + props.recoilXMin, 1F) - player.prevRotationPitch;
                player.rotationYaw += Interpolation.SINE_IN.interpolate(player.prevRotationYaw, player.prevRotationYaw + props.recoilYMin, 1F) - player.prevRotationYaw;
            }else {
                player.rotationPitch += Interpolation.SINE_IN.interpolate(player.prevRotationPitch, player.prevRotationPitch +random(props.recoilXMin,props.recoilXMax), 1F) - player.prevRotationPitch;
                player.rotationYaw += Interpolation.SINE_IN.interpolate(player.prevRotationYaw, player.prevRotationYaw + random(props.recoilYMin,props.recoilYMax), 1F) - player.prevRotationYaw;

            }
            
            if (props != null && props.launch) {
                float pitch = player.rotationPitch + (float) ((Math.random() - 0.5) * props.scatterY);
                float yaw = player.rotationYaw + (float) ((Math.random() - 0.5) * props.scatterX);
                this.setThrowableHeading(player, pitch, yaw, 0, props.speed);

            }
        }
        return EnumActionResult.PASS;
    }

    public float random(float min, float max)
    {
        max -= min;
        return (float) ((Math.random() * ++max) + min);
    }
    public EnumActionResult shootIt(ItemStack stack, EntityPlayer player, World world)
    {
        GunProps props = NBTUtils.getGunProps(stack);
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
            float pitch = player.rotationPitch + (float) ((Math.random() - 0.5) * props.scatterY);
            float yaw = player.rotationYaw + (float) ((Math.random() - 0.5) * props.scatterX);

            this.setThrowableHeading(player, pitch, yaw, 0, props.speed);

            if (!props.fireCommand.isEmpty())
            {
                player.getServer().commandManager.executeCommand(player, props.fireCommand);
            }
        }
        /* Or otherwise launch bullets */
        else
        {

            if (!this.consumeInnerAmmo(props, player))
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
                double originalX = player.posX;
                double originalZ = player.posZ;
                double[] coordsAfterYRotation = rotate(new double[]{originalX,originalZ},new double[]{originalX+props.srcShootX,originalZ+props.srcShootZ},player.getPitchYaw().y,true);
                double[] coordsAfterXRotation = rotateX(new double[]{player.posY + player.getEyeHeight(),coordsAfterYRotation[1]},new double[]{player.posY + player.getEyeHeight() + props.srcShootY,coordsAfterYRotation[1]},player.getPitchYaw().x, player.getPitchYaw().x > 0);

                projectile.setPosition(coordsAfterYRotation[0],coordsAfterXRotation[0],  coordsAfterXRotation[1]);
                projectile.shoot(player, pitch, yaw, 0, props.speed, 0);
                projectile.setInitialMotion();

                if (props.projectiles > 0)
                {
                    world.spawnEntity(projectile);
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

        if (props.innerAmmo <= 0)
        {
            props.setGUNState(GunState.NEED_TO_BE_RELOAD);
        }

        if (props.timeBetweenShoot <= 0)
        {
            props.timeBetweenShoot = props.inputTimeBetweenShoot;
        }

        Dispatcher.sendToServer(new PacketGunInfo(props.toNBT(), ((EntityPlayerMP) player).getEntityId()));

        if (!world.isRemote)
        {
            List<Action> events = CommonProxy.manager.getActions(player);
            if (events != null)
            {
                GunProps props1 = new GunProps(NBTUtils.getGunProps(stack).toNBT());
                ItemStack stack1 = new ItemStack(Blockbuster.gunItem, 1, 1);
                NBTUtils.saveGunProps(stack1, props1.toNBT());
                events.add(new ShootGunAction(stack1));
            }
        }

        return true;
    }

    private double[] rotate(double[] origin, double[] point, double angle, boolean staff)
    {
        if (staff)
        {
            angle = normalise_angle(angle);
        }

        angle = angle*Math.PI/180;
        double ox = origin[0];
        double oy = origin[1];
        double px = point[0];
        double py = point[1];
        double _px = px - ox;
        double _py = py - oy;
        double qx =(Math.cos(angle)*_px)-(Math.sin(angle)*_py);
        double qy =(Math.sin(angle)*_px)+(Math.cos(angle)*_py);
        qx =ox + qx;
        qy = oy + qy;
        return new double[]{qx,qy};
    }
    private double[] rotateX(double[] origin, double[] point, double angle, boolean staff)
    {
        angle = normalise_angle(angle);
        angle = angle*Math.PI/180;
        double ox = origin[0];
        double oy = origin[1];
        double px = point[0];
        double py = point[1];
        double _px = px - ox;
        double _py = py - oy;
        double qx =(Math.sin(angle)*_px)+(Math.cos(angle)*_py);
        double qy =(Math.cos(angle)*_px)-(Math.sin(angle)*_py);
        qx =ox + qx;
        qy = oy + qy;
        return new double[]{qx,qy};
    }
    private double normalise_angle(double angle)
    {
        if ((angle != 0) && (Math.abs(angle) == (angle * -1)))
        {
        angle = 360 + angle;
        }
        return angle;
    }



    private boolean consumeInnerAmmo(GunProps props, EntityPlayer player)
    {
      int ammo =  props.innerAmmo;

      if (ammo<=0){
          return false;
      }

      if (props.needToBeReloaded) {
          props.innerAmmo = props.innerAmmo - 1;
      }

      Dispatcher.sendToServer(new PacketGunInfo(props.toNBT(),player.getEntityId()));
      return true;
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
        GunProps props = NBTUtils.getGunProps(entityThrower.getHeldItem(EnumHand.MAIN_HAND));
        float f = -MathHelper.sin(rotationYawIn * 0.017453292F)* MathHelper.cos(rotationPitchIn * 0.017453292F);
        float f1 = -MathHelper.sin((rotationPitchIn + pitchOffset) * 0.017453292F );
        float f2 = MathHelper.cos(rotationYawIn * 0.017453292F) * MathHelper.cos(rotationPitchIn * 0.017453292F);
        this.setThrowableHeading(entityThrower, (double) f, (double) f1, (double) f2, velocity);
    }

    public void setThrowableHeading(EntityLivingBase entity, double x, double y, double z, float velocity)
    {
        float f = MathHelper.sqrt(x * x + y * y + z * z);
        x = x / (double) f;
        y = y / (double) f;
        z = z / (double) f;
        x = x * (double) velocity;
        y = y * (double) velocity;
        z = z * (double) velocity;
        entity.motionX = x;
        entity.motionY = y;
        entity.motionZ = z;
    }


    public enum GunState
    {
        READY_TO_SHOOT,
        RELOADING,
        NEED_TO_BE_RELOAD,
        UNDEF

    }
}