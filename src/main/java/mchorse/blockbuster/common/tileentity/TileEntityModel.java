package mchorse.blockbuster.common.tileentity;

import mchorse.blockbuster.Blockbuster;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;

public class TileEntityModel extends TileEntity implements ITickable
{
    public AbstractMorph morph;
    public float rotateX;
    public float rotateY;
    public EntityLivingBase entity;
    public float x;
    public float y;
    public float z;

    public TileEntityModel()
    {}

    public void setMorph(AbstractMorph morph)
    {
        this.morph = morph;
        this.markDirty();
    }

    @Override
    public void update()
    {
        if (this.entity != null)
        {
            this.entity.ticksExisted++;
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        compound.setFloat("RotateX", this.rotateX);
        compound.setFloat("RotateY", this.rotateY);
        compound.setFloat("ShiftX", this.x);
        compound.setFloat("ShiftY", this.y);
        compound.setFloat("ShiftZ", this.z);

        if (morph != null)
        {
            NBTTagCompound morph = new NBTTagCompound();
            this.morph.toNBT(morph);

            compound.setTag("Morph", morph);
        }

        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);

        this.rotateX = compound.getFloat("RotateX");
        this.rotateY = compound.getFloat("RotateY");
        this.x = compound.getFloat("ShiftX");
        this.y = compound.getFloat("ShiftY");
        this.z = compound.getFloat("ShiftZ");

        if (compound.hasKey("Morph", 10))
        {
            this.morph = MorphManager.INSTANCE.morphFromNBT(compound.getCompoundTag("Morph"));
        }
    }

    @Override
    public NBTTagCompound getUpdateTag()
    {
        return this.writeToNBT(new NBTTagCompound());
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox()
    {
        return TileEntity.INFINITE_EXTENT_AABB;
    }

    @Override
    public double getMaxRenderDistanceSquared()
    {
        return Blockbuster.proxy.config.actor_rendering_range * Blockbuster.proxy.config.actor_rendering_range;
    }
}