package mchorse.blockbuster_pack.client.model;

import mchorse.blockbuster.api.Model;
import mchorse.blockbuster.client.model.ModelCustom;
import mchorse.blockbuster.client.model.ModelCustomRenderer;
import mchorse.blockbuster.client.model.parsing.IModelCustom;
import net.minecraft.entity.Entity;

/**
 * Model for YikeFilms easter egg
 */
public class ModelYike extends ModelCustom implements IModelCustom
{
    public ModelCustomRenderer anchor;

    public ModelYike(Model model)
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

        if (this.anchor != null)
        {
            this.anchor.rotateAngleZ = (ageInTicks % 360) * (entityIn.isSprinting() ? 0.7F : 0.5F);
        }
    }
}