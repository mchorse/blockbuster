package mchorse.blockbuster.common.entity;

import java.util.List;

import io.netty.buffer.ByteBuf;
import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.common.GunProps;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.guns.PacketGunProjectile;
import mchorse.metamorph.api.Morph;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Gun projectile entity
 * 
 * This bad boy is responsible for being a gun projectile. It works in a 
 * similar fashion as a snowball, but holds a morph
 */
public class EntityGunProjectile extends EntityThrowable implements IEntityAdditionalSpawnData
{
    public GunProps props;
    public AbstractMorph original;
    public Morph morph = new Morph();
    public int timer;
    public int hits;
    public int impact;

    /* Syncing on the client side the position */
    public int updatePos;
    public double targetX;
    public double targetY;
    public double targetZ;

    public EntityGunProjectile(World worldIn)
    {
        this(worldIn, null, null);
    }

    public EntityGunProjectile(World worldIn, GunProps props, AbstractMorph morph)
    {
        super(worldIn);

        this.props = props;
        this.morph.setDirect(morph);
        this.original = this.morph.clone(worldIn.isRemote);

        if (props != null)
        {
            this.setSize(props.hitboxX, props.hitboxY);
        }

        this.impact = -1;
    }

    @Override
    public void onUpdate()
    {
        if (!this.world.isBlockLoaded(this.getPosition(), false))
        {
            this.setDead();
        }

        this.lastTickPosX = this.posX;
        this.lastTickPosY = this.posY;
        this.lastTickPosZ = this.posZ;

        if (!this.world.isRemote)
        {
            this.setFlag(6, this.isGlowing());
        }

        this.onEntityUpdate();

        /* Ray trace for impact */
        Vec3d position = new Vec3d(this.posX, this.posY, this.posZ);
        Vec3d next = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
        RayTraceResult result = this.world.rayTraceBlocks(position, next);

        if (result != null)
        {
            next = new Vec3d(result.hitVec.xCoord, result.hitVec.yCoord, result.hitVec.zCoord);
        }

        Entity entity = null;
        List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().addCoord(this.motionX, this.motionY, this.motionZ).expandXyz(1.0D));
        double dist = 0.0D;

        for (int i = 0; i < list.size(); ++i)
        {
            Entity current = list.get(i);

            if (current.canBeCollidedWith())
            {
                AxisAlignedBB box = current.getEntityBoundingBox().expandXyz(0.30000001192092896D);
                RayTraceResult ray = box.calculateIntercept(position, next);

                if (ray != null)
                {
                    double d1 = position.squareDistanceTo(ray.hitVec);

                    if (d1 < dist || dist == 0.0D)
                    {
                        entity = current;
                        dist = d1;
                    }
                }
            }
        }

        if (entity != null)
        {
            result = new RayTraceResult(entity);
        }

        /* Update position */
        this.posX += this.motionX;
        this.posY += this.motionY;
        this.posZ += this.motionZ;

        if (result != null)
        {
            if (result.typeOfHit == RayTraceResult.Type.BLOCK && this.world.getBlockState(result.getBlockPos()).getBlock() == Blocks.PORTAL)
            {
                this.setPortal(result.getBlockPos());
            }
            else
            {
                if (!net.minecraftforge.common.ForgeHooks.onThrowableImpact(this, result)) this.onImpact(result);
            }
        }

        /* Update position, motion and rotation */
        float distance = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
        this.rotationYaw = (float) (MathHelper.atan2(this.motionX, this.motionZ) * (180D / Math.PI));

        for (this.rotationPitch = (float) (MathHelper.atan2(this.motionY, distance) * (180D / Math.PI)); this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F)
        {}

        while (this.rotationPitch - this.prevRotationPitch >= 180.0F)
            this.prevRotationPitch += 360.0F;
        while (this.rotationYaw - this.prevRotationYaw < -180.0F)
            this.prevRotationYaw -= 360.0F;
        while (this.rotationYaw - this.prevRotationYaw >= 180.0F)
            this.prevRotationYaw += 360.0F;

        this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
        this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;
        float friction = this.props == null ? 1 : this.props.friction;

        if (this.isInWater())
        {
            for (int j = 0; j < 4; ++j)
            {
                this.world.spawnParticle(EnumParticleTypes.WATER_BUBBLE, this.posX - this.motionX * 0.25D, this.posY - this.motionY * 0.25D, this.posZ - this.motionZ * 0.25D, this.motionX, this.motionY, this.motionZ, new int[0]);
            }

            friction *= 0.8F;
        }

        if (this.onGround)
        {
            friction *= 0.9F;
        }

        this.motionX *= friction;
        this.motionY *= friction;
        this.motionZ *= friction;

        if (!this.hasNoGravity())
        {
            this.motionY -= this.getGravityVelocity();
        }

        if (this.hits > this.props.hits)
        {
            this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
        }
        else
        {
            this.setPosition(this.posX, this.posY, this.posZ);
        }

        this.updateProjectile();
    }

    /**
     * Update projectile's properties 
     */
    private void updateProjectile()
    {
        if (this.world.isRemote && this.updatePos > 0)
        {
            double d0 = this.posX + (this.targetX - this.posX) / this.updatePos;
            double d1 = this.posY + (this.targetY - this.posY) / this.updatePos;
            double d2 = this.posZ + (this.targetZ - this.posZ) / this.updatePos;

            this.updatePos--;
            this.setPosition(d0, d1, d2);
        }

        this.timer++;

        AbstractMorph morph = this.morph.get();

        if (morph != null)
        {
            this.props.createEntity(this.world);
            this.props.entity.posX = this.posX;
            this.props.entity.posY = this.posY;
            this.props.entity.posZ = this.posZ;
            morph.update(this.props.getEntity(this), null);
        }

        if (this.props == null || this.world.isRemote)
        {
            return;
        }

        if (this.timer > this.props.lifeSpan)
        {
            this.setDead();

            if (!this.props.impactCommand.isEmpty())
            {
                this.getServer().commandManager.executeCommand(this, this.props.impactCommand);
            }
        }

        if (this.props.ticking > 0 && this.timer % this.props.ticking == 0 && !this.props.tickCommand.isEmpty())
        {
            this.getServer().commandManager.executeCommand(this, this.props.tickCommand);
        }

        if (this.impact >= 0)
        {
            if (this.impact == 0)
            {
                boolean remote = this.world.isRemote;
                AbstractMorph original = this.original == null ? null : this.original.clone(remote);

                this.morph.set(original, remote);

                Dispatcher.sendToTracked(this, new PacketGunProjectile(this.getEntityId(), original));
            }

            this.impact--;
        }
    }

    @Override
    protected void onImpact(RayTraceResult result)
    {
        if (this.props != null && this.timer >= 2)
        {
            boolean shouldDie = this.props.vanish && this.hits >= this.props.hits;

            if (result.typeOfHit == Type.BLOCK)
            {
                Axis axis = result.sideHit.getAxis();
                float factor = this.props.bounce && this.hits <= this.props.hits ? -1 : 0;

                if (axis == Axis.X) this.motionX *= factor;
                if (axis == Axis.Y) this.motionY *= factor;
                if (axis == Axis.Z) this.motionZ *= factor;

                this.posX = result.hitVec.xCoord + this.width / 2 * result.sideHit.getFrontOffsetX();
                this.posY = result.hitVec.yCoord - this.height * (result.sideHit == EnumFacing.DOWN ? 1 : 0);
                this.posZ = result.hitVec.zCoord + this.width / 2 * result.sideHit.getFrontOffsetZ();
            }

            if (!this.world.isRemote)
            {
                if (!this.props.impactCommand.isEmpty())
                {
                    String command = this.props.impactCommand;
                    int x = Math.round((float) this.posX);
                    int y = Math.round((float) this.posY);
                    int z = Math.round((float) this.posZ);

                    if (result.typeOfHit == Type.BLOCK)
                    {
                        x = result.getBlockPos().getX();
                        y = result.getBlockPos().getY();
                        z = result.getBlockPos().getZ();
                    }

                    command = command.replaceAll("\\$\\{x\\}", String.valueOf(x));
                    command = command.replaceAll("\\$\\{y\\}", String.valueOf(y));
                    command = command.replaceAll("\\$\\{z\\}", String.valueOf(z));

                    this.getServer().commandManager.executeCommand(this, command);
                }

                if (result.typeOfHit == Type.ENTITY && this.props.damage > 0)
                {
                    result.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, null), this.props.damage);
                }

                if (shouldDie)
                {
                    this.setDead();
                    return;
                }

                /* Change to impact morph */
                if (this.props.impactDelay > 0)
                {
                    boolean remote = this.world.isRemote;
                    AbstractMorph morph = this.props.impactMorph == null ? null : this.props.impactMorph.clone(remote);

                    this.morph.set(morph, remote);
                    this.impact = this.props.impactDelay;

                    Dispatcher.sendToTracked(this, new PacketGunProjectile(this.getEntityId(), morph));
                }
            }
        }

        this.hits++;
    }

    @Override
    protected float getGravityVelocity()
    {
        return this.props == null ? super.getGravityVelocity() : this.props.gravity;
    }

    /* NBT and ByteBuf read/write methods */

    @Override
    public void writeSpawnData(ByteBuf buffer)
    {
        buffer.writeBoolean(this.props != null);

        if (this.props != null)
        {
            ByteBufUtils.writeTag(buffer, this.props.toNBT());
        }

        buffer.writeBoolean(this.morph.get() != null);

        if (this.morph.get() != null)
        {
            ByteBufUtils.writeTag(buffer, this.morph.toNBT());
        }
    }

    @Override
    public void readSpawnData(ByteBuf additionalData)
    {
        if (additionalData.readBoolean())
        {
            this.props = new GunProps(ByteBufUtils.readTag(additionalData));
            this.setSize(this.props.hitboxX, this.props.hitboxY);
        }

        if (additionalData.readBoolean())
        {
            this.morph.fromNBT(ByteBufUtils.readTag(additionalData));
        }
    }

    /**
     * Don't restore the entity from NBT, kill the projectile immediately 
     */
    @Override
    public void readEntityFromNBT(NBTTagCompound compound)
    {
        super.readEntityFromNBT(compound);

        this.setDead();
    }

    /* Client side methods */

    /**
     * Update position from the server, only in case if there is a big 
     * desync enough to be noticeable 
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport)
    {
        double dx = this.posX - x;
        double dy = this.posY - y;
        double dz = this.posZ - z;
        double dist = dx * dx + dy * dy + dz * dz;
        double threshold = this.props == null ? 1 : this.props.speed * this.props.speed;

        if (threshold < 1)
        {
            threshold = 1;
        }

        if (dist > 2 * 2)
        {
            this.updatePos = posRotationIncrements;
            this.targetX = x;
            this.targetY = y;
            this.targetZ = z;
        }
    }

    /**
     * Is projectile in range in render distance
     *
     * This method is responsible for checking if this entity is 
     * available for rendering. Rendering range is configurable.
     */
    @SideOnly(Side.CLIENT)
    @Override
    public boolean isInRangeToRenderDist(double distance)
    {
        double d0 = Blockbuster.proxy.config.actor_rendering_range;
        return distance < d0 * d0;
    }
}