package mchorse.blockbuster.common.entity;

import io.netty.buffer.ByteBuf;
import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.common.GunInfo;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Gun projectile entity
 * 
 * This bad boy is responsible for being a gun projectile. It works in a 
 * similar fashion as a snowball, but holds 
 */
public class EntityGunProjectile extends EntityThrowable implements IEntityAdditionalSpawnData
{
    public GunInfo props;
    public AbstractMorph morph;
    public int timer;
    public int hits;

    public EntityGunProjectile(World worldIn)
    {
        this(worldIn, null);
    }

    public EntityGunProjectile(World worldIn, GunInfo props)
    {
        super(worldIn);

        this.props = props;

        if (this.props != null)
        {
            this.morph = props.projectileMorph == null ? null : this.props.projectileMorph.clone(worldIn.isRemote);
        }
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();

        this.timer++;

        if (this.morph != null)
        {
            this.props.createEntity(this.worldObj);
            this.morph.update(this.props.entity, null);
        }

        if (this.props == null)
        {
            return;
        }

        /* Apply friction */
        float friction = this.props.friction;

        this.motionX *= friction;
        this.motionY *= friction;
        this.motionZ *= friction;

        if (this.timer > this.props.lifeSpan)
        {
            this.setDead();

            if (!this.worldObj.isRemote && !this.props.impactCommand.isEmpty())
            {
                this.getServer().commandManager.executeCommand(this, this.props.impactCommand);
            }
        }

        if (this.props.ticking > 0 && this.timer % this.props.ticking == 0)
        {
            if (!this.worldObj.isRemote && !this.props.tickCommand.isEmpty())
            {
                this.getServer().commandManager.executeCommand(this, this.props.tickCommand);
            }
        }
    }

    @Override
    protected float getGravityVelocity()
    {
        return this.props == null ? super.getGravityVelocity() : this.props.gravity;
    }

    @Override
    public void writeSpawnData(ByteBuf buffer)
    {
        buffer.writeBoolean(this.props != null);

        if (this.props != null)
        {
            ByteBufUtils.writeTag(buffer, this.props.toNBT());
        }

        buffer.writeBoolean(this.morph != null);

        if (this.morph != null)
        {
            NBTTagCompound tag = new NBTTagCompound();
            this.morph.toNBT(tag);
            ByteBufUtils.writeTag(buffer, tag);
        }
    }

    @Override
    public void readSpawnData(ByteBuf additionalData)
    {
        if (additionalData.readBoolean())
        {
            this.props = new GunInfo(ByteBufUtils.readTag(additionalData));
        }

        if (additionalData.readBoolean())
        {
            this.morph = MorphManager.INSTANCE.morphFromNBT(ByteBufUtils.readTag(additionalData));
        }
    }

    @Override
    protected void onImpact(RayTraceResult result)
    {
        this.hits++;

        if (this.props != null && this.timer >= 2)
        {
            boolean shouldDie = this.props.vanish && this.hits >= this.props.hits;

            if (result.typeOfHit == Type.BLOCK && this.props.bounce && !shouldDie)
            {
                Axis axis = result.sideHit.getAxis();

                if (axis == Axis.X) this.motionX *= -1;
                if (axis == Axis.Y) this.motionY *= -1;
                if (axis == Axis.Z) this.motionZ *= -1;
            }

            if (!this.worldObj.isRemote)
            {
                if (!this.props.impactCommand.isEmpty())
                {
                    this.getServer().commandManager.executeCommand(this, this.props.impactCommand);
                }

                if (result.typeOfHit == Type.ENTITY)
                {
                    result.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, null), this.props.damage);
                }

                if (shouldDie)
                {
                    this.setDead();
                }
            }
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound)
    {
        super.readEntityFromNBT(compound);

        this.setDead();
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