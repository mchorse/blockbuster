package mchorse.metamorph.client.model.custom;

import mchorse.blockbuster.api.Model;
import mchorse.blockbuster.client.model.ModelCustom;
import mchorse.blockbuster.client.model.ModelCustomRenderer;
import mchorse.blockbuster.client.model.parsing.IModelCustom;
import net.minecraft.entity.Entity;

public class ModelIronGolem extends ModelCustom implements IModelCustom
{
    public ModelCustomRenderer left_arm;
    public ModelCustomRenderer right_arm;

    public ModelIronGolem(Model model)
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

        float i = this.swingProgress;

        if (i != 0)
        {
            this.right_arm.rotateAngleX = -2.0F + 1.2F * this.triangleWave(i, 1.0F);
            this.left_arm.rotateAngleX = -2.0F + 1.2F * this.triangleWave(i, 1.0F);
        }
    }

    private float triangleWave(float input, float magnitude)
    {
        return (Math.abs(input % magnitude - magnitude * 0.5F) - magnitude * 0.25F) / (magnitude * 0.25F);
    }
}