package noname.blockbuster.entity;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import noname.blockbuster.Blockbuster;
import noname.blockbuster.item.CameraConfigItem;
import noname.blockbuster.network.Dispatcher;
import noname.blockbuster.network.common.PacketCameraAttributes;

public class CameraEntity extends EntityLiving implements IEntityAdditionalSpawnData
{
    public float speed = 0.4F;
    public float accelerationRate = 0.02F;
    public float accelerationMax = 1.5f;
    public boolean canFly = true;

    protected float acceleration = 0.0F;

    public CameraEntity(World worldIn)
    {
        super(worldIn);
        this.setSize(0.9F, 0.9F);
    }

    /**
     * No knockback is allowed
     */
    @Override
    public void knockBack(Entity entityIn, float magnitued, double a, double b)
    {}

    @Override
    public double getMountedYOffset()
    {
        return this.height * 0.3;
    }

    @Override
    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();

        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(6.0D);
    }

    /**
     * Camera is invincible against fall damage
     */
    @Override
    public boolean isEntityInvulnerable(DamageSource source)
    {
        return source == DamageSource.fall;
    }

    /* Riding logic */

    /**
     * Processes player's right clicking on the entity
     *
     * If the player holds camera configuration item, then GUI with camera
     * configuration properties should pop up, otherwise start riding
     */
    @Override
    public boolean processInteract(EntityPlayer player, EnumHand p_184645_2_, ItemStack stack)
    {
        ItemStack item = player.getHeldItemMainhand();

        if (item != null && item.getItem() instanceof CameraConfigItem)
        {
            if (this.worldObj.isRemote)
            {
                player.openGui(Blockbuster.instance, 0, this.worldObj, this.getEntityId(), 0, 0);
            }

            return true;
        }
        else if (!this.isBeingRidden())
        {
            return player.startRiding(this);
        }

        return false;
    }

    /**
     * Totally not taken from EntityPig class
     */
    @Override
    public Entity getControllingPassenger()
    {
        return this.getPassengers().isEmpty() ? null : (Entity) this.getPassengers().get(0);
    }

    @Override
    public boolean canBeSteered()
    {
        return true;
    }

    /**
     * Totally not copy-pasted from EntityHorse/AnimalBikes classes
     */
    @Override
    public void moveEntityWithHeading(float strafe, float forward)
    {
        if (this.isBeingRidden())
        {
            EntityLivingBase player = (EntityLivingBase) this.getControllingPassenger();

            forward = player.moveForward;
            strafe = player.moveStrafing * 0.65F;

            boolean oldOnGround = this.onGround;
            float flyingMotion = forward != 0 ? -player.rotationPitch / 90.0F : 0.0F;

            this.prevRotationYaw = this.rotationYaw = player.rotationYaw;
            this.prevRotationPitch = this.rotationPitch = player.rotationPitch;
            this.rotationYawHead = this.renderYawOffset = this.rotationYaw;
            this.setRotation(this.rotationYaw, this.rotationPitch);

            /* Acceleration logic */
            if (strafe != 0 || forward != 0)
            {
                this.acceleration = MathHelper.clamp_float(this.acceleration + this.accelerationRate, 0.0F, this.accelerationMax);

                forward *= this.acceleration;
                strafe *= this.acceleration;
            }
            else
            {
                this.acceleration = 0.0F;
            }

            /* Flying logic */
            if (this.canFly)
            {
                forward = flyingMotion == 0 ? forward : forward * (1 - Math.abs(flyingMotion));
                this.motionY = flyingMotion * this.acceleration * Math.copySign(1.0F, forward);
            }
            else
            {
                this.motionY = 0.0D;
            }

            /* Hacks */
            this.onGround = true;
            this.setAIMoveSpeed(this.speed);
            super.moveEntityWithHeading(strafe, forward);
            this.onGround = oldOnGround;
        }
    }

    /**
     * Update camera's custom attributes and send notification to tracking players
     * (if it's needed)
     */
    public void setConfiguration(float speed2, float accelerationRate2, float accelerationMax2, boolean canFly2, boolean notify)
    {
        this.speed = speed2;
        this.accelerationRate = accelerationRate2;
        this.accelerationMax = accelerationMax2;
        this.canFly = canFly2;

        if (!this.worldObj.isRemote && notify)
        {
            Dispatcher.updateTrackers(this, new PacketCameraAttributes(this.getEntityId(), this.speed, this.accelerationRate, this.accelerationMax, this.canFly));
        }
    }

    /* Saving to disk */

    @Override
    public void readEntityFromNBT(NBTTagCompound tagCompound)
    {
        this.speed = tagCompound.getFloat("CameraSpeed");
        this.accelerationRate = tagCompound.getFloat("CameraRate");
        this.accelerationMax = tagCompound.getFloat("CameraMax");
        this.canFly = tagCompound.getBoolean("CanFly");

        super.readEntityFromNBT(tagCompound);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tagCompound)
    {
        tagCompound.setFloat("CameraSpeed", this.speed);
        tagCompound.setFloat("CameraRate", this.accelerationRate);
        tagCompound.setFloat("CameraMax", this.accelerationMax);
        tagCompound.setBoolean("CanFly", this.canFly);

        super.writeEntityToNBT(tagCompound);
    }

    /* IEntityAdditionalSpawnData implementation */

    @Override
    public void writeSpawnData(ByteBuf buffer)
    {
        buffer.writeFloat(this.speed);
        buffer.writeFloat(this.accelerationRate);
        buffer.writeFloat(this.accelerationMax);
        buffer.writeBoolean(this.canFly);
    }

    @Override
    public void readSpawnData(ByteBuf buffer)
    {
        this.speed = buffer.readFloat();
        this.accelerationRate = buffer.readFloat();
        this.accelerationMax = buffer.readFloat();
        this.canFly = buffer.readBoolean();
    }
}
