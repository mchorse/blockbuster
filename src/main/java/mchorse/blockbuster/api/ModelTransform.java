package mchorse.blockbuster.api;

import mchorse.mclib.client.gui.framework.elements.input.GuiTransformations;
import mchorse.mclib.utils.ITransformationObject;
import mchorse.mclib.utils.MatrixUtils;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

import com.google.common.base.MoreObjects;

import mchorse.mclib.utils.Interpolation;
import mchorse.mclib.utils.NBTUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

/**
 * Transform class
 *
 * This class simply holds basic transformation data for every limb.
 */
public class ModelTransform implements ITransformationObject
{
    /**
     * Default model transform. Please don't modify its values. 
     */
    public static final ModelTransform DEFAULT = new ModelTransform();

    public float[] translate = new float[] {0, 0, 0};
    public float[] scale = new float[] {1, 1, 1};
    public float[] rotate = new float[] {0, 0, 0};

    @Override
    public void addTranslation(double x, double y, double z, GuiTransformations.TransformOrientation orientation)
    {
        Vector4f trans = new Vector4f((float) x,(float) y,(float) z, 1);

        if (orientation == GuiTransformations.TransformOrientation.LOCAL)
        {
            float rotX = (float) Math.toRadians(this.rotate[0]);
            float rotY = (float) Math.toRadians(this.rotate[1]);
            float rotZ = (float) Math.toRadians(this.rotate[2]);

            MatrixUtils.getRotationMatrix(rotX, rotY, rotZ, MatrixUtils.RotationOrder.XYZ).transform(trans);
        }

        this.translate[0] += trans.x;
        this.translate[1] += trans.y;
        this.translate[2] += trans.z;
    }

    public static boolean equalFloatArray(float[] a, float[] b)
    {
        if (a.length != b.length)
        {
            return false;
        }

        for (int i = 0; i < a.length; i++)
        {
            if (Math.abs(a[i] - b[i]) > 0.0001F)
            {
                return false;
            }
        }

        return true;
    }

    public boolean isDefault()
    {
        return this.equals(DEFAULT);
    }

    public void copy(ModelTransform transform)
    {
        for (int i = 0; i < 3; i++)
            this.translate[i] = transform.translate[i];
        for (int i = 0; i < 3; i++)
            this.scale[i] = transform.scale[i];
        for (int i = 0; i < 3; i++)
            this.rotate[i] = transform.rotate[i];
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof ModelTransform)
        {
            ModelTransform transform = (ModelTransform) obj;

            return equalFloatArray(this.translate, transform.translate) && equalFloatArray(this.rotate, transform.rotate) && equalFloatArray(this.scale, transform.scale);
        }

        return super.equals(obj);
    }

    /**
     * Clone a model transform
     */
    @Override
    public ModelTransform clone()
    {
        ModelTransform b = new ModelTransform();

        b.translate = new float[] {this.translate[0], this.translate[1], this.translate[2]};
        b.rotate = new float[] {this.rotate[0], this.rotate[1], this.rotate[2]};
        b.scale = new float[] {this.scale[0], this.scale[1], this.scale[2]};

        return b;
    }

    public void fromNBT(NBTTagCompound tag)
    {
        if (tag.hasKey("P", NBT.TAG_LIST)) NBTUtils.readFloatList(tag.getTagList("P", 5), this.translate);
        if (tag.hasKey("S", NBT.TAG_LIST)) NBTUtils.readFloatList(tag.getTagList("S", 5), this.scale);
        if (tag.hasKey("R", NBT.TAG_LIST)) NBTUtils.readFloatList(tag.getTagList("R", 5), this.rotate);
    }

    public NBTTagCompound toNBT()
    {
        NBTTagCompound tag = new NBTTagCompound();

        if (!this.isDefault())
        {
            if (!equalFloatArray(DEFAULT.translate, this.translate)) tag.setTag("P", NBTUtils.writeFloatList(new NBTTagList(), this.translate));
            if (!equalFloatArray(DEFAULT.scale, this.scale)) tag.setTag("S", NBTUtils.writeFloatList(new NBTTagList(), this.scale));
            if (!equalFloatArray(DEFAULT.rotate, this.rotate)) tag.setTag("R", NBTUtils.writeFloatList(new NBTTagList(), this.rotate));
        }

        return tag;
    }

    @SideOnly(Side.CLIENT)
    public void transform()
    {
        this.applyTranslate();
        this.applyRotate();
        this.applyScale();
    }

    @SideOnly(Side.CLIENT)
    public void applyTranslate()
    {
        GL11.glTranslatef(this.translate[0], this.translate[1], this.translate[2]);
    }

    @SideOnly(Side.CLIENT)
    public void applyRotate()
    {
        GL11.glRotatef(this.rotate[2], 0, 0, 1);
        GL11.glRotatef(this.rotate[1], 0, 1, 0);
        GL11.glRotatef(this.rotate[0], 1, 0, 0);
    }

    @SideOnly(Side.CLIENT)
    public void applyScale()
    {
        GL11.glScalef(this.scale[0], this.scale[1], this.scale[2]);
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper(this).add("translate", this.translate).add("scale", this.scale).add("rotate", this.rotate).toString();
    }

    public void interpolate(ModelTransform a, ModelTransform b, float x, Interpolation interp)
    {
        for (int i = 0; i < this.translate.length; i++)
        {
            this.translate[i] = interp.interpolate(a.translate[i], b.translate[i], x);
            this.scale[i] = interp.interpolate(a.scale[i], b.scale[i], x);
            this.rotate[i] = interp.interpolate(a.rotate[i], b.rotate[i], x);
        }
    }
}