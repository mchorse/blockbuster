package noname.blockbuster.client.model;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Custom Model class
 *
 * This class is responsible for managing available custom models that have
 * been loaded from config folder or from server and also render a custom model
 * itself.
 */
@SideOnly(Side.CLIENT)
public class ModelCustom extends ModelBase
{
    /**
     * Repository of custom models that are available for usage
     */
    public static Map<String, ModelCustom> MODELS = new HashMap<String, ModelCustom>();

    /**
     * Array of all limbs that has been parsed from JSON model
     */
    public ModelRenderer[] limbs;

    /**
     * Array of limbs that has to be rendered (child limbs doesn't have to
     * be rendered, because they're getting render call from parent).
     */
    public ModelRenderer[] renderable;

    /**
     * Initiate the model with the size of the texture
     */
    public ModelCustom(int width, int height)
    {
        this.textureWidth = width;
        this.textureHeight = height;
    }

    @Override
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        for (ModelRenderer limb : this.renderable)
        {
            limb.render(scale);
        }
    }
}