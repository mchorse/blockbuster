package noname.blockbuster.client.model;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noname.blockbuster.client.model.parsing.Model;

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
     * Model data
     */
    public Model model;

    /**
     * Array of all limbs that has been parsed from JSON model
     */
    public ModelCustomRenderer[] limbs;

    /**
     * Array of limbs that has to be rendered (child limbs doesn't have to
     * be rendered, because they're getting render call from parent).
     */
    public ModelCustomRenderer[] renderable;

    /**
     * Initiate the model with the size of the texture
     */
    public ModelCustom(Model model)
    {
        this.model = model;
        this.textureWidth = model.texture[0];
        this.textureHeight = model.texture[1];
    }

    @Override
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);

        for (ModelRenderer limb : this.renderable)
        {
            limb.render(scale);
        }
    }

    /**
     * This method is responsible for setting gameplay wise features like head
     * looking, idle rotating (like arm swinging), swinging and swiping
     */
    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn)
    {
        for (ModelCustomRenderer limb : this.limbs)
        {
            boolean mirror = limb.limb.mirror;
            float factor = mirror ? -1 : 1;

            if (limb.limb.looking)
            {
                limb.rotateAngleX = headPitch * 0.017453292F;
                limb.rotateAngleY = netHeadYaw * 0.017453292F;
            }

            if (limb.limb.idle)
            {
                limb.rotateAngleZ = 0.0F;
                limb.rotateAngleX = 0.0F;

                limb.rotateAngleZ += (MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F) * factor;
                limb.rotateAngleX += (MathHelper.sin(ageInTicks * 0.067F) * 0.05F) * factor;
            }

            if (limb.limb.swinging)
            {
                float f = 1.0F;

                if (limb.limb.mirror)
                {
                    limb.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 2.0F * limbSwingAmount * 0.5F / f;
                }
                else
                {
                    limb.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F / f;
                }
            }

            if (limb.limb.swiping && this.swingProgress > 0.0F)
            {
                /* @TODO: Implement swiping */
            }
        }
    }

    /**
     * Apply a pose on this model
     */
    public void applyPose(String name)
    {
        Model.Pose pose = this.model.poses.get(name);

        for (ModelCustomRenderer limb : this.limbs)
        {
            limb.applyTransform(pose.limbs.get(limb.limb.name));
        }
    }
}