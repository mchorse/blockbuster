package mchorse.blockbuster.common.entity;

import io.netty.buffer.ByteBuf;
import mchorse.blockbuster.common.GunInfo;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

public class EntityGunProjectile extends Entity implements IEntityAdditionalSpawnData
{
    public EntityGunProjectile(World worldIn, GunInfo info)
    {
        super(worldIn);
    }

    @Override
    protected void entityInit()
    {}

    @Override
    public void writeSpawnData(ByteBuf buffer)
    {

    }

    @Override
    public void readSpawnData(ByteBuf additionalData)
    {

    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound)
    {
        this.setDead();
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound)
    {}
}