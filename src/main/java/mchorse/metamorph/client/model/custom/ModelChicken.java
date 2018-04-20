package mchorse.metamorph.client.model.custom;

import mchorse.blockbuster.api.Model;
import mchorse.blockbuster.client.model.ModelCustom;
import mchorse.blockbuster.client.model.ModelCustomRenderer;
import mchorse.blockbuster.client.model.parsing.IModelCustom;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

/**
 * Model chicken class
 * 
 * Just adds wing flapping animation to this model.
 */
public class ModelChicken extends ModelCustom implements IModelCustom
{
    public ModelCustomRenderer left_arm;
    public ModelCustomRenderer right_arm;

    public ModelChicken(Model model)
    {
        super(model);
    }

    @Override
    public void onGenerated()
    {}

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn)
    {
        super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);

        if (!entityIn.onGround || Math.abs(entityIn.motionY) > 0.1)
        {
            float flap = MathHelper.sin(ageInTicks) + 1.0F;

            this.right_arm.rotateAngleZ = flap;
            this.left_arm.rotateAngleZ = -flap;
        }
    }
}