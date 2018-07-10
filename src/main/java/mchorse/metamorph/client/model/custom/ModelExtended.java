package mchorse.metamorph.client.model.custom;

import java.util.ArrayList;
import java.util.List;

import mchorse.blockbuster.api.Model;
import mchorse.blockbuster.client.model.ModelCustom;
import mchorse.blockbuster.client.model.ModelCustomRenderer;
import mchorse.blockbuster.client.model.parsing.IModelCustom;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

/**
 * Extended model
 * 
 * This extended model implements two additional limb animations. Wheels 
 * and wings. 
 * 
 * If a model specifies this class as this model, then by having 
 * "wheel_" or "wing_", then those limbs will have additional wheel or 
 * wing animation. 
 * 
 * You can also have wing wheel, but I don't know how convenient it is.
 */
public class ModelExtended extends ModelCustom implements IModelCustom
{
    public ModelCustomRenderer[] wheels;
    public ModelCustomRenderer[] wings;

    public ModelExtended(Model model)
    {
        super(model);
    }

    @Override
    public void onGenerated()
    {
        List<ModelCustomRenderer> wheels = new ArrayList<ModelCustomRenderer>();
        List<ModelCustomRenderer> wings = new ArrayList<ModelCustomRenderer>();

        for (ModelCustomRenderer limb : this.limbs)
        {
            String name = limb.limb.name;

            if (name.contains("wheel_")) wheels.add(limb);
            if (name.contains("wing_")) wings.add(limb);
        }

        this.wheels = wheels.toArray(new ModelCustomRenderer[wheels.size()]);
        this.wings = wings.toArray(new ModelCustomRenderer[wings.size()]);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn)
    {
        super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);

        for (ModelCustomRenderer wheel : this.wheels)
        {
            wheel.rotateAngleX += limbSwing;
        }

        for (ModelCustomRenderer wing : this.wings)
        {
            wing.rotateAngleY = MathHelper.cos(ageInTicks * 1.3F) * (float) Math.PI * 0.25F * (0.5F + limbSwingAmount) * (wing.limb.invert || wing.limb.mirror ? -1 : 1);
        }
    }
}