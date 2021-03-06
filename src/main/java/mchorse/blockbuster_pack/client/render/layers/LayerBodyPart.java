package mchorse.blockbuster_pack.client.render.layers;

import org.lwjgl.opengl.GL11;

import mchorse.blockbuster.api.ModelPose;
import mchorse.blockbuster.client.model.ModelCustom;
import mchorse.blockbuster.client.model.ModelCustomRenderer;
import mchorse.blockbuster.client.render.RenderCustomModel;
import mchorse.blockbuster_pack.morphs.CustomMorph;
import mchorse.metamorph.bodypart.BodyPart;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;

/**
 * Layer body part
 * 
 * This bad boy is responsible for rendering body-parts on the model. 
 * This should look very cool! 
 */
public class LayerBodyPart implements LayerRenderer<EntityLivingBase>
{
    private RenderCustomModel renderer;

    public LayerBodyPart(RenderCustomModel renderer)
    {
        this.renderer = renderer;
    }

    @Override
    public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        CustomMorph morph = this.renderer.current;

        if (morph == null)
        {
            return;
        }

        ModelCustom model = (ModelCustom) this.renderer.getMainModel();

        renderBodyParts(entitylivingbaseIn, morph, model, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);

        this.renderer.current = morph;
        this.renderer.setupModel(entitylivingbaseIn, partialTicks);
    }

    public static void renderBodyParts(EntityLivingBase target, CustomMorph morph, ModelCustom model, float partialTicks, float scale)
    {
        renderBodyParts(target, morph, model, 0, 0, partialTicks, 0, 0, 0, scale);
    }

    public static void renderBodyParts(EntityLivingBase target, CustomMorph morph, ModelCustom model, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        ModelPose pose = model.pose;
        float swingProgress = model.swingProgress;

        for (BodyPart part : morph.parts.parts)
        {
            for (ModelCustomRenderer limb : model.limbs)
            {
                if (limb.limb.name.equals(part.limb))
                {
                    GL11.glPushMatrix();
                    limb.postRender(scale);
                    part.render(morph, target, partialTicks);
                    GL11.glPopMatrix();

                    break;
                }
            }

            /* Restore back properties */
            model.swingProgress = swingProgress;
            model.pose = pose;
            model.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, target);

            /* No point to render here since if a limb wasn't found 
             * then it wouldn't be transformed correctly */
        }
    }

    @Override
    public boolean shouldCombineTextures()
    {
        return false;
    }
}