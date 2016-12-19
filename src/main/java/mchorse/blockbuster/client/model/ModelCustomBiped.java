package mchorse.blockbuster.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;

public class ModelCustomBiped extends ModelBiped
{
    public ModelCustomBiped(float extrusion)
    {
        super(extrusion);
    }

    @Override
    public void setModelAttributes(ModelBase model)
    {
        super.setModelAttributes(model);

        if (model instanceof ModelCustom)
        {
            ModelCustom custom = (ModelCustom) model;

            this.leftArmPose = custom.leftPose;
            this.rightArmPose = custom.rightPose;
            this.isSneak = custom.pose.equals(custom.model.poses.get("sneaking"));
        }
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn)
    {
        super.setRotationAngles(limbSwing, -limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);

        this.bipedRightArm.rotationPointZ = 0.0F;
        this.bipedRightArm.rotationPointX = -5.0F;
        this.bipedLeftArm.rotationPointZ = 0.0F;
        this.bipedLeftArm.rotationPointX = 5.0F;
    }
}