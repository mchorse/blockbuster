package mchorse.blockbuster.common.tileentity;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.network.common.PacketModifyModelBlock;
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
    public EntityLivingBase entity;

    public float rotateYawHead;
    public float rotatePitch;
    public float rotateBody;

    public float x;
    public float y;
    public float z;

    public float rx;
    public float ry;
    public float rz;

    public float sx = 1;
    public float sy = 1;
    public float sz = 1;

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
        compound.setFloat("Yaw", this.rotateYawHead);
        compound.setFloat("Pitch", this.rotatePitch);
        compound.setFloat("Body", this.rotateBody);
        compound.setFloat("ShiftX", this.x);
        compound.setFloat("ShiftY", this.y);
        compound.setFloat("ShiftZ", this.z);
        compound.setFloat("RotateX", this.rx);
        compound.setFloat("RotateY", this.ry);
        compound.setFloat("RotateZ", this.rz);
        compound.setFloat("ScaleX", this.sx);
        compound.setFloat("ScaleY", this.sy);
        compound.setFloat("ScaleZ", this.sz);

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

        this.rotateYawHead = compound.getFloat("Yaw");
        this.rotatePitch = compound.getFloat("Pitch");
        this.rotateBody = compound.getFloat("Body");
        this.x = compound.getFloat("ShiftX");
        this.y = compound.getFloat("ShiftY");
        this.z = compound.getFloat("ShiftZ");
        this.rx = compound.getFloat("RotateX");
        this.ry = compound.getFloat("RotateY");
        this.rz = compound.getFloat("RotateZ");
        this.sx = compound.getFloat("ScaleX");
        this.sy = compound.getFloat("ScaleY");
        this.sz = compound.getFloat("ScaleZ");

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
        float range = Blockbuster.proxy.config.actor_rendering_range;

        return range * range;
    }

    public void copyData(PacketModifyModelBlock message)
    {
        this.rotateYawHead = message.yaw;
        this.rotatePitch = message.pitch;
        this.rotateBody = message.body;
        this.x = message.x;
        this.y = message.y;
        this.z = message.z;
        this.rx = message.rx;
        this.ry = message.ry;
        this.rz = message.rz;
        this.sx = message.sx;
        this.sy = message.sy;
        this.sz = message.sz;
        this.setMorph(message.morph);
    }

    public void copyData(TileEntityModel model)
    {
        this.rotateYawHead = model.rotateYawHead;
        this.rotatePitch = model.rotatePitch;
        this.rotateBody = model.rotateBody;
        this.x = model.x;
        this.y = model.y;
        this.z = model.z;
        this.rx = model.rx;
        this.ry = model.ry;
        this.rz = model.rz;
        this.sx = model.sx;
        this.sy = model.sy;
        this.sz = model.sz;
        this.morph = model.morph;
    }
}