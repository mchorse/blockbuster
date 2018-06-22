package mchorse.blockbuster.api;

import com.google.common.base.Objects;

/**
 * Transform class
 *
 * This class simply holds basic transformation data for every limb.
 */
public class ModelTransform
{
    public float[] translate = new float[] {0, 0, 0};
    public float[] scale = new float[] {1, 1, 1};
    public float[] rotate = new float[] {0, 0, 0};

    /**
     * Clone a model transform
     */
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