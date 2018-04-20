package mchorse.metamorph.client.model.custom;

import mchorse.blockbuster.api.Model;
import mchorse.blockbuster.client.model.ModelCustom;
import mchorse.blockbuster.client.model.ModelCustomRenderer;
import mchorse.blockbuster.client.model.parsing.IModelCustom;
import mchorse.blockbuster.client.model.parsing.ModelParser;
import net.minecraft.entity.Entity;

/**
 * Squid's custom model, isn't it exciting, huh? 
 */
public class ModelSquid extends ModelCustom implements IModelCustom
{
    /**
     * Going to be injected by {@link ModelParser} 
     */
    public ModelCustomRenderer head;

    public ModelSquid(Model model)
    {
        super(model);
    }

    /**
     * This is empty, because this class doesn't requires some fancy 
     * configuration of the model before doing its job. 
     */
    @Override
    public void onGenerated()
    {}

    /**
     * Move those tentacles, dude... 
     */
    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn)
    {
        float pi = (float) Math.PI;
        boolean inWater = entityIn.isInWater();

        head.rotateAngleX = 0;

        for (ModelCustomRenderer limb : this.limbs)
        {
            this.applyLimbPose(limb);

            if (limb == this.head)
            {
                continue;
            }

            limb.rotateAngleX = pi / 8 + (float) Math.sin(limbSwing / (inWater ? 4 : 2)) * pi / 8;
        }

        if (inWater)
        {
            head.rotateAngleX = pi / 2 + (float) Math.sin(limbSwing / 6) * pi / 16;
        }
    }
}