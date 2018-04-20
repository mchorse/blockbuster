package mchorse.metamorph.client.model.custom;

import mchorse.blockbuster.api.Model;
import mchorse.blockbuster.client.model.ModelCustom;
import mchorse.blockbuster.client.model.ModelCustomRenderer;
import mchorse.blockbuster.client.model.parsing.IModelCustom;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

/**
 * Model silverfish
 * 
 * Makes silverfish model make wobble-wobble... Just kidding, it makes it 
 * move like real silverfish model.
 */
public class ModelSilverfish extends ModelCustom implements IModelCustom
{
    public ModelCustomRenderer head;
    public ModelCustomRenderer wing_1;
    public ModelCustomRenderer wing_2;
    public ModelCustomRenderer wing_3;
    public ModelCustomRenderer wing_4;
    public ModelCustomRenderer wing_5;
    public ModelCustomRenderer tail_end;

    public ModelCustomRenderer spike_1;
    public ModelCustomRenderer spike_2;
    public ModelCustomRenderer spike_3;

    public ModelCustomRenderer[] spikes;
    public ModelCustomRenderer[] wings;

    public ModelSilverfish(Model model)
    {
        super(model);
    }

    @Override
    public void onGenerated()
    {
        this.spikes = new ModelCustomRenderer[] {spike_1, spike_2, spike_3};
        this.wings = new ModelCustomRenderer[] {head, wing_1, wing_2, wing_3, wing_4, wing_5, tail_end};
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn)
    {
        super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);

        for (int i = 0; i < this.wings.length; ++i)
        {
            this.wings[i].rotateAngleY = MathHelper.cos(ageInTicks * 0.9F + (float) i * 0.15F * (float) Math.PI) * (float) Math.PI * 0.05F * (float) (1 + Math.abs(i - 2));
            this.wings[i].rotationPointX = MathHelper.sin(ageInTicks * 0.9F + (float) i * 0.15F * (float) Math.PI) * (float) Math.PI * 0.2F * (float) Math.abs(i - 2);
        }

        this.spikes[0].rotateAngleY = this.wings[2].rotateAngleY;
        this.spikes[1].rotateAngleY = this.wings[4].rotateAngleY;
        this.spikes[1].rotationPointX = this.wings[4].rotationPointX;
        this.spikes[2].rotateAngleY = this.wings[1].rotateAngleY;
        this.spikes[2].rotationPointX = this.wings[1].rotationPointX;
    }
}