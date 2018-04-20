package mchorse.metamorph.client.model.custom;

import mchorse.blockbuster.api.Model;
import mchorse.blockbuster.client.model.ModelCustom;
import mchorse.blockbuster.client.model.parsing.IModelCustom;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

/**
 * Custom spider model class
 * 
 * This class is responsible mainly for doing spider animations.
 */
public class ModelSpider extends ModelCustom implements IModelCustom
{
    public ModelRenderer left_leg_1;
    public ModelRenderer left_leg_2;
    public ModelRenderer left_leg_3;
    public ModelRenderer left_leg_4;

    public ModelRenderer right_leg_1;
    public ModelRenderer right_leg_2;
    public ModelRenderer right_leg_3;
    public ModelRenderer right_leg_4;

    public ModelSpider(Model model)
    {
        super(model);
    }

    @Override
    public void onGenerated()
    {}

    /**
     * Does the spider animation. The animation code was taken from {@link ModelSpider} 
     */
    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn)
    {
        super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);

        this.right_leg_1.rotateAngleZ = -((float) Math.PI / 4F);
        this.left_leg_1.rotateAngleZ = ((float) Math.PI / 4F);
        this.right_leg_2.rotateAngleZ = -0.58119464F;
        this.left_leg_2.rotateAngleZ = 0.58119464F;
        this.right_leg_3.rotateAngleZ = -0.58119464F;
        this.left_leg_3.rotateAngleZ = 0.58119464F;
        this.right_leg_4.rotateAngleZ = -((float) Math.PI / 4F);
        this.left_leg_4.rotateAngleZ = ((float) Math.PI / 4F);

        this.right_leg_1.rotateAngleY = ((float) Math.PI / 4F);
        this.left_leg_1.rotateAngleY = -((float) Math.PI / 4F);
        this.right_leg_2.rotateAngleY = 0.3926991F;
        this.left_leg_2.rotateAngleY = -0.3926991F;
        this.right_leg_3.rotateAngleY = -0.3926991F;
        this.left_leg_3.rotateAngleY = 0.3926991F;
        this.right_leg_4.rotateAngleY = -((float) Math.PI / 4F);
        this.left_leg_4.rotateAngleY = ((float) Math.PI / 4F);

        float f3 = -(MathHelper.cos(limbSwing * 0.6662F * 2.0F + 0.0F) * 0.4F) * limbSwingAmount;
        float f4 = -(MathHelper.cos(limbSwing * 0.6662F * 2.0F + (float) Math.PI) * 0.4F) * limbSwingAmount;
        float f5 = -(MathHelper.cos(limbSwing * 0.6662F * 2.0F + ((float) Math.PI / 2F)) * 0.4F) * limbSwingAmount;
        float f6 = -(MathHelper.cos(limbSwing * 0.6662F * 2.0F + ((float) Math.PI * 3F / 2F)) * 0.4F) * limbSwingAmount;
        float f7 = Math.abs(MathHelper.sin(limbSwing * 0.6662F + 0.0F) * 0.4F) * limbSwingAmount;
        float f8 = Math.abs(MathHelper.sin(limbSwing * 0.6662F + (float) Math.PI) * 0.4F) * limbSwingAmount;
        float f9 = Math.abs(MathHelper.sin(limbSwing * 0.6662F + ((float) Math.PI / 2F)) * 0.4F) * limbSwingAmount;
        float f10 = Math.abs(MathHelper.sin(limbSwing * 0.6662F + ((float) Math.PI * 3F / 2F)) * 0.4F) * limbSwingAmount;

        this.right_leg_1.rotateAngleY += f3;
        this.left_leg_1.rotateAngleY += -f3;
        this.right_leg_2.rotateAngleY += f4;
        this.left_leg_2.rotateAngleY += -f4;
        this.right_leg_3.rotateAngleY += f5;
        this.left_leg_3.rotateAngleY += -f5;
        this.right_leg_4.rotateAngleY += f6;
        this.left_leg_4.rotateAngleY += -f6;
        this.right_leg_1.rotateAngleZ += f7;
        this.left_leg_1.rotateAngleZ += -f7;
        this.right_leg_2.rotateAngleZ += f8;
        this.left_leg_2.rotateAngleZ += -f8;
        this.right_leg_3.rotateAngleZ += f9;
        this.left_leg_3.rotateAngleZ += -f9;
        this.right_leg_4.rotateAngleZ += f10;
        this.left_leg_4.rotateAngleZ += -f10;
    }
}