package noname.blockbuster.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import noname.blockbuster.client.model.parsing.Model;

/**
 * Custom model renderer class
 *
 * This class extended only for purpose of storing more
 */
public class ModelCustomRenderer extends ModelRenderer
{
    public Model.Limb limb;
    public Model.Transform trasnform;

    public ModelCustomRenderer(ModelBase model, int texOffX, int texOffY)
    {
        super(model, texOffX, texOffY);
    }

    /**
     * Initiate with limb and transform instances
     */
    public ModelCustomRenderer(ModelBase model, Model.Limb limb, Model.Transform transform)
    {
        this(model, limb.texture[0], limb.texture[1]);

        this.limb = limb;
        this.trasnform = transform;
    }

    /**
     * Apply transformations on this model renderer
     */
    public void applyTransform(Model.Transform transform)
    {
        this.trasnform = transform;

        float x = transform.translate[0];
        float y = transform.translate[1];
        float z = transform.translate[2];

        this.offsetX = x / 16;
        this.offsetY = this.limb.parent.isEmpty() ? (-y + 24) / 16 : -y / 16;
        this.offsetZ = -z / 16;

        this.rotateAngleX = transform.rotate[0] * (float) Math.PI / 180;
        this.rotateAngleY = transform.rotate[1] * (float) Math.PI / 180;
        this.rotateAngleZ = transform.rotate[2] * (float) Math.PI / 180;
    }
}
