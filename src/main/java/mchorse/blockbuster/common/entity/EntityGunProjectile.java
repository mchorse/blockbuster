package mchorse.blockbuster.common.entity;

import io.netty.buffer.ByteBuf;
import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.common.GunProps;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.guns.PacketGunProjectile;
import mchorse.blockbuster.network.common.guns.PacketGunProjectileVanish;
import mchorse.blockbuster.network.common.guns.PacketGunStuck;
import mchorse.mclib.utils.NBTUtils;
import mchorse.metamorph.api.Morph;
import mchorse.metamorph.api.MorphUtils;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
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

import java.util.List;

/**
 * Gun projectile entity
 * <p>
 * This bad boy is responsible for being a gun projectile. It works in a
 * similar fashion as a snowball, but holds a morph
 */
public class EntityGunProjectile extends EntityThrowable implements IEntityAdditionalSpawnData
{
    public GunProps props;
    public AbstractMorph original;
    public Morph morph = new Morph();
    public int hits;
    public int impact;
    public boolean vanish;
    public int vanishDelay;
    public boolean stuck;

    /* Syncing on the client side the position */
    public int updatePos;
    public double targetX;
    public double targetY;
    public double targetZ;

    public double initMX;
    public double initMY;
    public double initMZ;

    public boolean setInit;

    public EntityGunProjectile(World worldIn)
    {
        this(worldIn, null, null);
    }

    public EntityGunProjectile(World worldIn, GunProps props, AbstractMorph morph)
    {
        super(worldIn);

        this.props = props;
        this.morph.setDirect(morph);
        this.original = this.morph.copy();
        if (props != null)
        {
            this.setSize(props.hitboxX, props.hitboxY);
        }

        this.impact = -1;
    }

    @Override
    public void shoot(Entity entityThrower, float rotationPitchIn, float rotationYawIn, float pitchOffset, float velocity, float inaccuracy)
    {
        if (entityThrower instanceof EntityActor.EntityFakePlayer)
        {
            this.thrower = ((EntityActor.EntityFakePlayer) entityThrower).actor;
        }
        else if (entityThrower instanceof EntityLivingBase)
        {
            this.thrower = (EntityLivingBase) entityThrower;
        }

        super.shoot(entityThrower, rotationPitchIn, rotationYawIn, pitchOffset, velocity, inaccuracy);
    }

    public void setInitialMotion()
    {
        this.initMX = this.motionX;
        this.initMY = this.motionY;
        this.initMZ = this.motionZ;
    }

    @Override
    public void onUpdate()
    {
        if (this.vanish)
        {
            if (!this.world.isRemote && this.vanishDelay <= 0)
            {
                this.setDead();
            }

            if (this.vanishDelay > 0)
            {
                this.vanishDelay--;
            }
        }

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
        else if (!this.setInit)
        {
            this.setInit = true;
            this.motionX = this.initMX;
            this.motionY = this.initMY;
            this.motionZ = this.initMZ;
        }

        this.onEntityUpdate();

        if (!this.stuck)
        {
            /* Ray trace for impact */
            Vec3d position = new Vec3d(this.posX, this.posY, this.posZ);
            Vec3d next = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
            Entity entity = null;
            RayTraceResult result = this.world.rayTraceBlocks(position, next, false, true, false);

            if (result != null)
            {
                next = new Vec3d(result.hitVec.x, result.hitVec.y, result.hitVec.z);
            }

            if (!this.props.ignoreEntities)
            {
                List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().expand(this.motionX, this.motionY, this.motionZ).grow(1.0D));
                double dist = 0.0D;

                for (int i = 0; i < list.size(); ++i)
                {
                    Entity current = list.get(i);

                    if (current.canBeCollidedWith())
                    {
                        AxisAlignedBB box = current.getEntityBoundingBox().grow(0.30000001192092896D);
                        RayTraceResult ray = box.calculateIntercept(position, next);

                        if (ray != null)
                        {
                            double d1 = position.squareDistanceTo(ray.hitVec);

                            if (!(current instanceof EntityGunProjectile) && current != this.thrower && (d1 < dist || dist == 0.0D))
                            {
                                entity = current;
                                dist = d1;
                            }
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
                    if (!net.minecraftforge.common.ForgeHooks.onThrowableImpact(this, result))
                    {
                        this.onImpact(result);
                    }
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

            if (this.hits < this.props.hits)
            {
                double diff = this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ;

                if (diff < 100 * 100)
                {
                    this.noClip = this.props.ignoreBlocks;
                    this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
                }
                else
                {
                    this.setDead();
                }
            }
            else
            {
                this.setPosition(this.posX, this.posY, this.posZ);
            }
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

        AbstractMorph morph = this.morph.get();

        if (morph != null)
        {
            this.props.createEntity(this.world);
            this.props.target.posX = this.posX;
            this.props.target.posY = this.posY;
            this.props.target.posZ = this.posZ;
            morph.update(this.props.getEntity(this));
        }

        if (this.props == null || this.world.isRemote)
        {
            return;
        }

        if (this.ticksExisted > this.props.lifeSpan)
        {
            this.setDead();

            if (!this.props.vanishCommand.isEmpty())
            {
                this.getServer().commandManager.executeCommand(this, this.props.vanishCommand);
            }
        }

        if (this.props.ticking > 0 && this.ticksExisted % this.props.ticking == 0 && !this.props.tickCommand.isEmpty())
        {
            this.getServer().commandManager.executeCommand(this, this.props.tickCommand);
        }

        if (this.impact >= 0)
        {
            if (this.impact == 0)
            {
                AbstractMorph original = MorphUtils.copy(this.original);

                this.morph.set(original);

                Dispatcher.sendToTracked(this, new PacketGunProjectile(this.getEntityId(), original));
            }

            this.impact--;
        }
    }

    @Override
    protected void onImpact(RayTraceResult result)
    {
        if (this.stuck || this.vanish || this.props == null)
        {
            return;
        }

        this.hits++;

        boolean shouldDie = this.props.vanish && this.hits >= this.props.hits && !this.props.sticks;
        boolean impactMorph = false;

        if (result.typeOfHit == Type.BLOCK && !this.props.ignoreBlocks)
        {
            Axis axis = result.sideHit.getAxis();
            float factor = (this.props.bounce && this.hits <= this.props.hits ? -1 : 0);

            if (axis == Axis.X) this.motionX *= factor;
            if (axis == Axis.Y) this.motionY *= factor;
            if (axis == Axis.Z) this.motionZ *= factor;

            this.motionX *= this.props.bounceFactor;
            this.motionY *= this.props.bounceFactor;
            this.motionZ *= this.props.bounceFactor;

            this.posX = result.hitVec.x + this.width / 2 * result.sideHit.getFrontOffsetX();
            this.posY = result.hitVec.y - this.height * (result.sideHit == EnumFacing.DOWN ? 1 : 0);
            this.posZ = result.hitVec.z + this.width / 2 * result.sideHit.getFrontOffsetZ();

            if (this.props.sticks)
            {
                this.stuck = true;

                if (!this.world.isRemote)
                {
                    if (result.sideHit == EnumFacing.WEST || result.sideHit == EnumFacing.EAST) this.posX += this.props.penetration * result.sideHit.getFrontOffsetX();
                    else if (result.sideHit == EnumFacing.UP || result.sideHit == EnumFacing.DOWN) this.posY += this.props.penetration * result.sideHit.getFrontOffsetY();
                    else if (result.sideHit == EnumFacing.NORTH || result.sideHit == EnumFacing.SOUTH) this.posZ += this.props.penetration * result.sideHit.getFrontOffsetZ();

                    Dispatcher.sendToTracked(this, new PacketGunStuck(this.getEntityId(), (float) this.posX, (float) this.posY, (float) this.posZ));
                }
            }
        }

        if (!this.world.isRemote)
        {
            if (result.typeOfHit == Type.BLOCK && !this.props.ignoreBlocks)
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

                    if (this.getServer() != null)
                    {
                        this.getServer().commandManager.executeCommand(this, command);
                    }
                }

                impactMorph = true;
            }

            if (result.typeOfHit == Type.ENTITY && !this.props.ignoreEntities)
            {
                if (!this.props.impactEntityCommand.isEmpty())
                {
                    this.getServer().commandManager.executeCommand(this, this.props.impactEntityCommand);
                }

                if (this.props.damage > 0)
                {
                    if (result.entityHit instanceof EntityLiving)
                    {
                        EntityLiving living = (EntityLiving) result.entityHit;

                        result.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, null), 0);
                        living.setHealth(living.getHealth() - this.props.damage);
                    }
                    else
                    {
                        result.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, null), this.props.damage);
                    }
                }

                if (this.props.knockbackHorizontal != 0 && result.entityHit instanceof EntityLivingBase)
                {
                    ((EntityLivingBase) result.entityHit).knockBack(this, Math.abs(this.props.knockbackHorizontal), -this.motionX, -this.motionZ);

                    if (this.props.knockbackHorizontal < 0)
                    {
                        result.entityHit.motionX *= -1;
                        result.entityHit.motionZ *= -1;
                    }
                }

                result.entityHit.motionY += this.props.knockbackVertical;

                impactMorph = true;
            }

            if (shouldDie)
            {
                this.vanish = true;
                this.vanishDelay = this.props.vanishDelay;

                if (this.vanishDelay > 0)
                {
                    Dispatcher.sendToTracked(this, new PacketGunProjectileVanish(this.getEntityId(), this.vanishDelay));
                }

                return;
            }

            /* Change to impact morph */
            if (impactMorph && this.props.impactDelay > 0)
            {
                AbstractMorph morph = MorphUtils.copy(this.props.impactMorph);

                this.morph.set(morph);
                this.impact = this.props.impactDelay;

                Dispatcher.sendToTracked(this, new PacketGunProjectile(this.getEntityId(), morph));
            }
        }
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

        buffer.writeDouble(this.initMX);
        buffer.writeDouble(this.initMY);
        buffer.writeDouble(this.initMZ);
    }

    @Override
    public void readSpawnData(ByteBuf additionalData)
    {
        if (additionalData.readBoolean())
        {
            this.props = new GunProps(NBTUtils.readInfiniteTag(additionalData));
            this.setSize(this.props.hitboxX, this.props.hitboxY);
        }

        if (additionalData.readBoolean())
        {
            this.morph.fromNBT(NBTUtils.readInfiniteTag(additionalData));
        }

        this.initMX = additionalData.readDouble();
        this.initMY = additionalData.readDouble();
        this.initMZ = additionalData.readDouble();
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
        if (this.stuck)
        {
            return;
        }

        double dx = this.posX - x;
        double dy = this.posY - y;
        double dz = this.posZ - z;
        double dist = dx * dx + dy * dy + dz * dz;
        double syncDistance = Blockbuster.bbGunSyncDistance.get();

        if (syncDistance > 0 && dist > syncDistance * syncDistance)
        {
            this.updatePos = posRotationIncrements;
            this.targetX = x;
            this.targetY = y;
            this.targetZ = z;
        }
    }

    /**
     * Is projectile in range in render distance
     * <p>
     * This method is responsible for checking if this entity is
     * available for rendering. Rendering range is configurable.
     */
    @SideOnly(Side.CLIENT)
    @Override
    public boolean isInRangeToRenderDist(double distance)
    {
        double d0 = Blockbuster.actorRenderingRange.get();
        return distance < d0 * d0;
    }
}