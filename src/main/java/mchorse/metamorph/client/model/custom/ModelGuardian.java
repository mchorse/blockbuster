package mchorse.metamorph.client.model.custom;

import mchorse.blockbuster.api.Model;
import mchorse.blockbuster.client.model.ModelCustom;
import mchorse.blockbuster.client.model.ModelCustomRenderer;
import mchorse.blockbuster.client.model.parsing.IModelCustom;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

/**
 * Guardian model
 * 
 * This class is responsible for making eye and tail animations
 */
public class ModelGuardian extends ModelCustom implements IModelCustom
{
    public ModelCustomRenderer eye;

    public ModelCustomRenderer tail_1;
    public ModelCustomRenderer tail_2;
    public ModelCustomRenderer tail_3;
    public ModelCustomRenderer tail_end;

    public ModelGuardian(Model model)
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

        /* Make eye look up and down */
        eye.rotationPointY += headPitch / 90;

        limbSwingAmount += 0.1;

        /* Make tail look cool */
        this.tail_1.rotateAngleY = limbSwingAmount * MathHelper.sin(ageInTicks / 2) * (float) Math.PI * 0.1F;
        this.tail_2.rotateAngleY = limbSwingAmount * MathHelper.sin(ageInTicks / 2) * (float) Math.PI * 0.075F;
        this.tail_3.rotateAngleY = limbSwingAmount * MathHelper.sin(ageInTicks / 2) * (float) Math.PI * 0.05F;
    }
}