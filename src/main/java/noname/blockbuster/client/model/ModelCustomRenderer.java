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
        super(model, limb.texture[0], limb.texture[1]);

        this.limb = limb;
        this.trasnform = transform;
    }
}
