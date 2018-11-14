package mchorse.blockbuster.api;

import com.google.common.base.Objects;

/**
 * Transform class
 *
 * This class simply holds basic transformation data for every limb.
 */
public class ModelTransform
{
    /**
     * Default model transform. Please don't modify its values. 
     */
    public static final ModelTransform DEFAULT = new ModelTransform();

    public float[] translate = new float[] {0, 0, 0};
    public float[] scale = new float[] {1, 1, 1};
    public float[] rotate = new float[] {0, 0, 0};

    public static boolean equalFloatArray(float[] a, float[] b)
    {
        if (a.length != b.length)
        {
            return false;
        }

        for (int i = 0; i < a.length; i++)
        {
            if (Math.abs(a[i] - b[i]) > 0.001F)
            {
                return false;
            }
        }

        return true;
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

    @Override
    public String toString()
    {
        return Objects.toStringHelper(this).add("translate", this.translate).add("scale", this.scale).add("rotate", this.rotate).toString();
    }
}