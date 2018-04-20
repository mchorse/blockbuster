package mchorse.metamorph.client.model.custom;

import mchorse.blockbuster.api.Model;
import mchorse.blockbuster.client.model.ModelCustom;
import mchorse.blockbuster.client.model.ModelCustomRenderer;
import mchorse.blockbuster.client.model.parsing.IModelCustom;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

/**
 * Model ghast class
 * 
 * This class is mainly responsible for making ghast animate like vanilla 
 * ghast. Of course, some of the code is taken from {@link ModelGhast}.
 */
public class ModelGhast extends ModelCustom implements IModelCustom
{
    public ModelCustomRenderer left_arm;
    public ModelCustomRenderer right_arm;
    public ModelCustomRenderer arm_1;
    public ModelCustomRenderer arm_2;
    public ModelCustomRenderer arm_3;
    public ModelCustomRenderer arm_4;
    public ModelCustomRenderer arm_5;
    public ModelCustomRenderer arm_6;
    public ModelCustomRenderer arm_7;

    public ModelCustomRenderer[] tentacles;

    public ModelGhast(Model model)
    {
        super(model);
    }

    /**
     * Finally this method is used! 
     */
    @Override
    public void onGenerated()
    {
        this.tentacles = new ModelCustomRenderer[] {left_arm, right_arm, arm_1, arm_2, arm_3, arm_4, arm_5, arm_6, arm_7};
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn)
    {
        for (ModelCustomRenderer limb : this.limbs)
        {
            this.applyLimbPose(limb);
        }

        for (int i = 0; i < this.tentacles.length; ++i)
        {
            this.tentacles[i].rotateAngleX = 0.2F * MathHelper.sin(ageInTicks * 0.3F + (float) i) + 0.4F;
        }
    }

    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);

        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0F, 0.6F, 0.0F);

        super.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);

        GlStateManager.popMatrix();
    }
}