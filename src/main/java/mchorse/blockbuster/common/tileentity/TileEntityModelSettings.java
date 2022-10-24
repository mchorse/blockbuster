package mchorse.blockbuster.common.tileentity;

import io.netty.buffer.ByteBuf;
import mchorse.mclib.client.gui.framework.elements.input.GuiTransformations.TransformOrientation;
import mchorse.mclib.config.values.ValueBoolean;
import mchorse.mclib.config.values.ValueFloat;
import mchorse.mclib.config.values.ValueInt;
import mchorse.mclib.config.values.ValueItemSlots;
import mchorse.mclib.config.values.ValueRotationOrder;
import mchorse.mclib.network.IByteBufSerializable;
import mchorse.mclib.network.INBTSerializable;
import mchorse.mclib.utils.ICopy;
import mchorse.mclib.utils.ITransformationObject;
import mchorse.mclib.utils.MatrixUtils;
import mchorse.mclib.utils.ValueSerializer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector4f;

public class TileEntityModelSettings implements IByteBufSerializable, INBTSerializable, ITransformationObject, ICopy<TileEntityModelSettings>
{
    private final ValueBoolean enabled = new ValueBoolean("enabled", true);
    private final ValueInt lightValue = new ValueInt("lightValue");
    private final ValueBoolean shadow = new ValueBoolean("shadow", true);
    private final ValueBoolean global = new ValueBoolean("global");
    private final ValueBoolean excludeResetPlayback = new ValueBoolean("excludeResetPlayback");
    private final ValueBoolean renderLast = new ValueBoolean("renderLast");
    private final ValueBoolean renderAlways = new ValueBoolean("renderAlways");
    private final ValueBoolean enableBlockHitbox = new ValueBoolean("enableBlockHitbox");
    private final ValueItemSlots slots = new ValueItemSlots("slots", 6);

    /* Entity rotations */
    private final ValueFloat rotateYawHead = new ValueFloat("rotateYawHead");
    private final ValueFloat rotatePitch = new ValueFloat("rotatePitch");
    private final ValueFloat rotateBody = new ValueFloat("rotateBody");

    /* Translation */
    private final ValueFloat x = new ValueFloat("x");
    private final ValueFloat y = new ValueFloat("y");
    private final ValueFloat z = new ValueFloat("z");

    /* Rotation */
    private final ValueRotationOrder order = new ValueRotationOrder("order", MatrixUtils.RotationOrder.ZYX);
    private final ValueFloat rx = new ValueFloat("rx");
    private final ValueFloat ry = new ValueFloat("ry");
    private final ValueFloat rz = new ValueFloat("rz");

    /* Scale */
    private final ValueBoolean uniform = new ValueBoolean("uniform");
    private final ValueFloat sx = new ValueFloat("sx",1);
    private final ValueFloat sy = new ValueFloat("sy",1);
    private final ValueFloat sz = new ValueFloat("sz",1);

    private ValueSerializer serializer = new ValueSerializer();

    public TileEntityModelSettings()
    {
        this.serializer.registerNBTValue("Enabled", this.enabled);
        this.serializer.registerNBTValue("Order", this.order);
        this.serializer.registerNBTValue("Yaw", this.rotateYawHead);
        this.serializer.registerNBTValue("Pitch", this.rotatePitch);
        this.serializer.registerNBTValue("Body", this.rotateBody);
        this.serializer.registerNBTValue("ShiftX", this.x);
        this.serializer.registerNBTValue("ShiftY", this.y);
        this.serializer.registerNBTValue("ShiftZ", this.z);
        this.serializer.registerNBTValue("RotateX", this.rx);
        this.serializer.registerNBTValue("RotateY", this.ry);
        this.serializer.registerNBTValue("RotateZ", this.rz);
        this.serializer.registerNBTValue("Scale", this.uniform);
        this.serializer.registerNBTValue("ScaleX", this.sx);
        this.serializer.registerNBTValue("ScaleY", this.sy);
        this.serializer.registerNBTValue("ScaleZ", this.sz);
        this.serializer.registerNBTValue("Shadow", this.shadow);
        this.serializer.registerNBTValue("Global", this.global);
        this.serializer.registerNBTValue("Items", this.slots);
        this.serializer.registerNBTValue("LightValue", this.lightValue);
        this.serializer.registerNBTValue("RenderLast", this.renderLast);
        this.serializer.registerNBTValue("RenderAlways", this.renderAlways);
        this.serializer.registerNBTValue("Hitbox", this.enableBlockHitbox);
        this.serializer.registerNBTValue("ExcludeResetPlayback", this.excludeResetPlayback);
    }

    public boolean isBlockHitbox()
    {
        return this.enableBlockHitbox.get();
    }

    public void setEnableBlockHitbox(boolean enableBlockHitbox)
    {
        this.enableBlockHitbox.set(enableBlockHitbox);
    }

    public int getLightValue()
    {
        return this.lightValue.get();
    }

    public void setLightValue(int lightValue)
    {
        this.lightValue.set(lightValue);
    }

    public boolean isExcludeResetPlayback()
    {
        return this.excludeResetPlayback.get();
    }

    public void setExcludeResetPlayback(boolean excludeResetPlayback)
    {
        this.excludeResetPlayback.set(excludeResetPlayback);
    }

    public boolean isRenderLast()
    {
        return this.renderLast.get();
    }

    public void setRenderLast(boolean renderLast)
    {
        this.renderLast.set(renderLast);
    }

    public boolean isRenderAlways()
    {
        return this.renderAlways.get();
    }

    public void setRenderAlways(boolean renderAlways)
    {
        this.renderAlways.set(renderAlways);
    }

    public ItemStack[] getSlots()
    {
        return slots.get();
    }

    public void setSlots(ItemStack[] slots)
    {
        this.slots.set(slots);
    }

    /**
     * Calls {@link ValueItemSlots#set(ItemStack, int)}, which copies the provided ItemStack
     * @param item
     * @param slot
     */
    public void setSlot(ItemStack item, int slot)
    {
        this.slots.set(item, slot);
    }

    public MatrixUtils.RotationOrder getOrder()
    {
        return this.order.get();
    }

    public void setOrder(MatrixUtils.RotationOrder order)
    {
        this.order.set(order);
    }

    public float getRotateYawHead()
    {
        return this.rotateYawHead.get();
    }

    public void setRotateYawHead(float rotateYawHead)
    {
        this.rotateYawHead.set(rotateYawHead);
    }

    public float getRotatePitch()
    {
        return rotatePitch.get();
    }

    public void setRotatePitch(float rotatePitch)
    {
        this.rotatePitch.set(rotatePitch);
    }

    public float getRotateBody()
    {
        return rotateBody.get();
    }

    public void setRotateBody(float rotateBody)
    {
        this.rotateBody.set(rotateBody);
    }

    public float getX()
    {
        return x.get();
    }

    public void setX(float x)
    {
        this.x.set(x);
    }

    public float getY()
    {
        return this.y.get();
    }

    public void setY(float y)
    {
        this.y.set(y);
    }

    public float getZ()
    {
        return this.z.get();
    }

    public void setZ(float z)
    {
        this.z.set(z);
    }

    /**
     * Add a translation on top of this translation.
     * @param x
     * @param y
     * @param z
     */
    @Override
    public void addTranslation(double x, double y, double z, TransformOrientation orientation)
    {
        Vector4f trans = new Vector4f((float) x, (float) y, (float) z, 1F);

        if (orientation == TransformOrientation.LOCAL)
        {
            float rotX = (float) Math.toRadians(this.getRx());
            float rotY = (float) Math.toRadians(this.getRy());
            float rotZ = (float) Math.toRadians(this.getRz());

            MatrixUtils.getRotationMatrix(rotX, rotY, rotZ, this.order.get()).transform(trans);
        }

        this.x.set(this.x.get() + trans.x);
        this.y.set(this.y.get() + trans.y);
        this.z.set(this.z.get() + trans.z);
    }

    public float getRx()
    {
        return this.rx.get();
    }

    public void setRx(float rx)
    {
        this.rx.set(rx);
    }

    public float getRy()
    {
        return this.ry.get();
    }

    public void setRy(float ry)
    {
        this.ry.set(ry);
    }

    public float getRz()
    {
        return this.rz.get();
    }

    public void setRz(float rz)
    {
        this.rz.set(rz);
    }

    public boolean isUniform()
    {
        return this.uniform.get();
    }

    public void setUniform(boolean uniform)
    {
        this.uniform.set(uniform);
    }

    public float getSx()
    {
        return this.sx.get();
    }

    public void setSx(float sx)
    {
        this.sx.set(sx);
    }

    public float getSy()
    {
        return this.sy.get();
    }

    public void setSy(float sy)
    {
        this.sy.set(sy);
    }

    public float getSz()
    {
        return this.sz.get();
    }

    public void setSz(float sz)
    {
        this.sz.set(sz);
    }

    public boolean isShadow()
    {
        return this.shadow.get();
    }

    public void setShadow(boolean shadow)
    {
        this.shadow.set(shadow);
    }

    public boolean isGlobal()
    {
        return this.global.get();
    }

    public void setGlobal(boolean global)
    {
        this.global.set(global);
    }

    public boolean isEnabled()
    {
        return this.enabled.get();
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled.set(enabled);
    }

    @Override
    public TileEntityModelSettings copy()
    {
        TileEntityModelSettings copy = new TileEntityModelSettings();

        copy.copy(this);

        return copy;
    }

    @Override
    public void copy(TileEntityModelSettings settings)
    {
        this.lightValue.copy(settings.lightValue);
        this.renderLast.copy(settings.renderLast);
        this.renderAlways.copy(settings.renderAlways);
        this.enableBlockHitbox.copy(settings.enableBlockHitbox);
        this.excludeResetPlayback.copy(settings.excludeResetPlayback);
        this.order.copy(settings.order);
        this.rotateYawHead.copy(settings.rotateYawHead);
        this.rotatePitch.copy(settings.rotatePitch);
        this.rotateBody.copy(settings.rotateBody);
        this.x.copy(settings.x);
        this.y.copy(settings.y);
        this.z.copy(settings.z);
        this.rx.copy(settings.rx);
        this.ry.copy(settings.ry);
        this.rz.copy(settings.rz);
        this.uniform.copy(settings.uniform);
        this.sx.copy(settings.sx);
        this.sy.copy(settings.sy);
        this.sz.copy(settings.sz);
        this.shadow.copy(settings.shadow);
        this.global.copy(settings.global);
        this.enabled.copy(settings.enabled);
        this.slots.copy(settings.slots);
    }

    @Override
    public void fromBytes(ByteBuf byteBuf)
    {
        this.serializer.fromBytes(byteBuf);
    }

    @Override
    public void toBytes(ByteBuf byteBuf)
    {
        this.serializer.toBytes(byteBuf);
    }

    @Override
    public void fromNBT(NBTTagCompound nbtTagCompound)
    {
        this.serializer.fromNBT(nbtTagCompound);
    }

    @Override
    public NBTTagCompound toNBT(NBTTagCompound nbtTagCompound)
    {
        return this.serializer.toNBT(nbtTagCompound);
    }
}
