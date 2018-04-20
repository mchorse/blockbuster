package mchorse.metamorph.client.model.custom;

import mchorse.blockbuster.api.Model;
import mchorse.blockbuster.client.model.ModelCustom;
import mchorse.blockbuster.client.model.ModelCustomRenderer;
import mchorse.blockbuster.client.model.parsing.IModelCustom;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class ModelBat extends ModelCustom implements IModelCustom
{
    public ModelCustomRenderer right_wing;
    public ModelCustomRenderer right_wing_2;

    public ModelCustomRenderer left_wing;
    public ModelCustomRenderer left_wing_2;

    public ModelBat(Model model)
    {
        super(model);
    }

    @Override
    public void onGenerated()
    {}

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn)
    {
        GlStateManager.translate(0.0F, -0.4F - MathHelper.cos(ageInTicks * 0.3F) * 0.1F, 0.0F);

        super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);

        this.right_wing.rotateAngleY = MathHelper.cos(ageInTicks * 1.3F) * (float) Math.PI * 0.25F * (0.5F + limbSwingAmount);
        this.left_wing.rotateAngleY = -this.right_wing.rotateAngleY;
        this.right_wing_2.rotateAngleY = this.right_wing.rotateAngleY * 0.5F;
        this.left_wing_2.rotateAngleY = -this.right_wing.rotateAngleY * 0.5F;
    }
}