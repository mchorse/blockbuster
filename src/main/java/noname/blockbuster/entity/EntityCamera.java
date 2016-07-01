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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import noname.blockbuster.Blockbuster;
import noname.blockbuster.item.ItemCameraConfig;
import noname.blockbuster.item.ItemRegister;
import noname.blockbuster.network.Dispatcher;
import noname.blockbuster.network.common.PacketCameraAttributes;
import noname.blockbuster.network.common.PacketCameraRecording;
import noname.blockbuster.tileentity.TileEntityDirector;

/**
 * Camera entity
 *
 * Freaking flying camera that lets you to whooshes the skies for a clearer
 * shot (movie). With director block you can instantly jump from one camera
 * to another (really useful during film making).
 */
public class EntityCamera extends EntityLiving implements IEntityAdditionalSpawnData
{
    public float speed = 0.4F;
    public float accelerationRate = 0.2F;
    public float accelerationMax = 1.5f;
    public boolean canFly = true;
    public float savedPitch = 0.0F;
    public boolean isRecording = false;

    public BlockPos directorBlock;
    public boolean renderName = true;

    protected float acceleration = 0.0F;

    public EntityCamera(World worldIn)
    {
        super(worldIn);
        this.setSize(1.0F, 1.0F);
    }

    /**
     * No knockback is allowed to camera
     */
    @Override
    public void knockBack(Entity entityIn, float magnitued, double a, double b)
    {}

    /**
     * Can't despawn a camera
     */
    @Override
    protected boolean canDespawn()
    {
        return false;
    }

    @Override
    public double getMountedYOffset()
    {
        return this.height * 0.4;
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

        if (item != null)
        {
            if (item.getItem() instanceof ItemCameraConfig)
            {
                if (this.worldObj.isRemote)
                {
                    player.openGui(Blockbuster.instance, 0, this.worldObj, this.getEntityId(), 0, 0);
                }

                return true;
            }
            else if (item.getItem() instanceof ItemRegister)
            {
                this.handleRegisterItem(item);

                return true;
            }
        }
        else if (!this.isBeingRidden())
        {
            player.rotationYaw = this.rotationYaw;
            player.rotationPitch = this.rotationPitch;

            return player.startRiding(this);
        }

        return false;
    }

    /**
     * Set actor's id on register item (while using register item on this
     * actor)
     */
    private void handleRegisterItem(ItemStack stack)
    {
        if (this.worldObj.isRemote)
        {
            return;
        }

        ItemRegister item = (ItemRegister) stack.getItem();

        item.registerStack(stack, this);
    }

    /**
     * Totally not taken from EntityPig class
     */
    @Override
    public Entity getControllingPassenger()
    {
        return this.getPassengers().isEmpty() ? null : (Entity) this.getPassengers().get(0);
    }

    /**
     * Yes, it can be steered!
     */
    @Override
    public boolean canBeSteered()
    {
        return true;
    }

    /**
     * Return the rotation pitch to last saved pitch by player. This is kind of
     * workaround, because EntityLookHelper is for some reason resets the
     * pitch.
     */
    @Override
    public void onLivingUpdate()
    {
        super.onLivingUpdate();

        this.rotationPitch = this.savedPitch;
    }

    /**
     * Totally not partially copy-pasted from EntityHorse/AnimalBikes classes
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
            float xcel = 0.0F;

            this.prevRotationYaw = this.rotationYaw = player.rotationYaw;
            this.prevRotationPitch = this.rotationPitch = player.rotationPitch;
            this.rotationYawHead = this.renderYawOffset = this.rotationYaw;
            this.setRotation(this.rotationYaw, this.rotationPitch);

            /* Acceleration logic */
            if (strafe != 0 || forward != 0)
            {
                this.acceleration = MathHelper.clamp_float(this.acceleration + this.accelerationRate, 0.0F, this.accelerationMax);
                xcel = (float) (this.acceleration * this.acceleration * 0.1);

                forward *= xcel;
                strafe *= xcel;
            }
            else if (this.acceleration != 0)
            {
                this.acceleration *= 0.9F;

                if (this.acceleration < 0.0005F)
                {
                    this.acceleration = 0;
                }
            }

            /* Flying logic */
            if (this.canFly)
            {
                forward = flyingMotion == 0 ? forward : forward * (1 - Math.abs(flyingMotion));
                this.motionY = flyingMotion * xcel * Math.copySign(1.0F, forward);
            }
            else
            {
                this.motionY = 0.0D;
            }

            this.onGround = true;
            this.setAIMoveSpeed(this.speed);
            super.moveEntityWithHeading(strafe, forward);
            this.onGround = oldOnGround;
            this.savedPitch = this.rotationPitch;
        }
    }

    /**
     * Update camera's custom attributes and send notification to tracking players
     * (if it's needed)
     */
    public void setConfiguration(String name, float speed2, float accelerationRate2, float accelerationMax2, boolean canFly2, boolean notify)
    {
        this.setCustomNameTag(name);
        this.speed = speed2;
        this.accelerationRate = accelerationRate2;
        this.accelerationMax = accelerationMax2;
        this.canFly = canFly2;

        if (!this.worldObj.isRemote && notify)
        {
            Dispatcher.updateTrackers(this, new PacketCameraAttributes(this.getEntityId(), name, this.speed, this.accelerationRate, this.accelerationMax, this.canFly));
        }
    }

    /**
     * Set if the camera is recording, and notify other clients if it's
     * needed.
     */
    public void setRecording(boolean recording, boolean notify)
    {
        this.isRecording = recording;

        if (!this.worldObj.isRemote && notify)
        {
            Dispatcher.updateTrackers(this, new PacketCameraRecording(this.getEntityId(), recording));
        }
    }

    /**
     * Switch (teleport) player to another camera. Looks really sick when you
     * switch the camera.
     */
    public void switchTo(int direction)
    {
        if (this.directorBlock == null)
        {
            return;
        }

        TileEntityDirector director = (TileEntityDirector) this.worldObj.getTileEntity(this.directorBlock);

        director.switchTo(this, direction);
    }

    /* Read/save to disk */

    @Override
    public void readEntityFromNBT(NBTTagCompound tag)
    {
        this.speed = tag.getFloat("CameraSpeed");
        this.accelerationRate = tag.getFloat("CameraRate");
        this.accelerationMax = tag.getFloat("CameraMax");
        this.canFly = tag.getBoolean("CanFly");

        if (tag.hasKey("DirX") && tag.hasKey("DirY") && tag.hasKey("DirZ"))
        {
            this.directorBlock = new BlockPos(tag.getInteger("DirX"), tag.getInteger("DirY"), tag.getInteger("DirZ"));
        }

        super.readEntityFromNBT(tag);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tag)
    {
        tag.setFloat("CameraSpeed", this.speed);
        tag.setFloat("CameraRate", this.accelerationRate);
        tag.setFloat("CameraMax", this.accelerationMax);
        tag.setBoolean("CanFly", this.canFly);

        if (this.directorBlock != null)
        {
            tag.setInteger("DirX", this.directorBlock.getX());
            tag.setInteger("DirY", this.directorBlock.getY());
            tag.setInteger("DirZ", this.directorBlock.getZ());
        }

        super.writeEntityToNBT(tag);
    }

    /* IEntityAdditionalSpawnData implementation */

    @Override
    public void writeSpawnData(ByteBuf buffer)
    {
        buffer.writeFloat(this.speed);
        buffer.writeFloat(this.accelerationRate);
        buffer.writeFloat(this.accelerationMax);
        buffer.writeBoolean(this.canFly);
        buffer.writeBoolean(this.isRecording);
    }

    @Override
    public void readSpawnData(ByteBuf buffer)
    {
        this.speed = buffer.readFloat();
        this.accelerationRate = buffer.readFloat();
        this.accelerationMax = buffer.readFloat();
        this.canFly = buffer.readBoolean();
        this.isRecording = buffer.readBoolean();
    }
}