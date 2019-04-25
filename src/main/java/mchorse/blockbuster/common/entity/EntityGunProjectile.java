package mchorse.blockbuster.common.entity;

import io.netty.buffer.ByteBuf;
import mchorse.blockbuster.common.GunInfo;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

public class EntityGunProjectile extends EntityThrowable implements IEntityAdditionalSpawnData
{
    public GunInfo props;
    public int timer;

    public EntityGunProjectile(World worldIn)
    {
        this(worldIn, null);
    }

    public EntityGunProjectile(World worldIn, GunInfo props)
    {
        super(worldIn);

        this.props = props;
        this.timer = 2;
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();

        if (this.props != null)
        {
            float friction = this.props.friction;

            this.motionX *= friction;
            this.motionY *= friction;
            this.motionZ *= friction;
        }

        this.timer--;

        if (this.props == null)
        {
            return;
        }

        if (-(this.timer - 2) > this.props.lifeSpan)
        {
            this.setDead();

            if (!this.worldObj.isRemote && !this.props.impactCommand.isEmpty())
            {
                this.getServer().commandManager.executeCommand(this, this.props.impactCommand);
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
    }

    @Override
    public void readSpawnData(ByteBuf additionalData)
    {
        if (additionalData.readBoolean())
        {
            this.props = new GunInfo(ByteBufUtils.readTag(additionalData));
        }
    }

    @Override
    protected void onImpact(RayTraceResult result)
    {
        if (this.worldObj.isRemote)
        {
            return;
        }

        if (this.props != null && this.timer <= 0)
        {
            if (!this.props.impactCommand.isEmpty())
            {
                this.getServer().commandManager.executeCommand(this, this.props.impactCommand);
            }

            if (result.typeOfHit == Type.ENTITY)
            {
                result.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, null), this.props.damage);
            }

            if (this.props.vanish)
            {
                this.setDead();
            }
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound)
    {
        super.readEntityFromNBT(compound);

        this.setDead();
    }
}