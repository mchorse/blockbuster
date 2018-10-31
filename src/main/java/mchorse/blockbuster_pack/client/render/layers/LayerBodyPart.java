package mchorse.blockbuster_pack.client.render.layers;

import org.lwjgl.opengl.GL11;

import mchorse.blockbuster.api.ModelPose;
import mchorse.blockbuster.client.model.ModelCustom;
import mchorse.blockbuster.client.model.ModelCustomRenderer;
import mchorse.blockbuster.client.render.RenderCustomModel;
import mchorse.blockbuster_pack.morphs.CustomMorph;
import mchorse.blockbuster_pack.morphs.CustomMorph.BodyPart;
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
        float swingProgress = model.swingProgress;
        ModelPose pose = model.pose;

        renderBodyParts(morph, model, partialTicks, scale);

        /* Restore back properties */
        model.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entitylivingbaseIn);
        model.swingProgress = swingProgress;
        model.pose = pose;
    }

    public static void renderBodyParts(CustomMorph morph, ModelCustom model, float partialTicks, float scale)
    {
        for (BodyPart part : morph.parts)
        {
            for (ModelCustomRenderer limb : model.limbs)
            {
                if (limb.limb.name.equals(part.limb))
                {
                    GL11.glPushMatrix();
                    limb.postRender(scale);
                    part.render(partialTicks);
                    GL11.glPopMatrix();

                    break;
                }
            }

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