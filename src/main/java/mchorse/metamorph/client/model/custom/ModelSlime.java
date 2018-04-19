package mchorse.metamorph.client.model.custom;

import mchorse.blockbuster.api.Model;
import mchorse.blockbuster.client.model.ModelCustom;
import mchorse.blockbuster.client.model.ModelCustomRenderer;
import mchorse.blockbuster.client.model.parsing.IModelCustom;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;

public class ModelSlime extends ModelCustom implements IModelCustom
{
    public ModelCustomRenderer head;
    public ModelCustomRenderer right_eye;
    public ModelCustomRenderer left_eye;
    public ModelCustomRenderer mouth;
    public ModelCustomRenderer outer;

    public ModelSlime(Model model)
    {
        super(model);
    }

    @Override
    public void onGenerated()
    {}

    @Override
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        GlStateManager.pushMatrix();
        GlStateManager.translate(0, -1, 0);
        this.head.render(scale);

        if (!this.model.name.equals("Slime"))
        {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 0.8F);
        }

        GlStateManager.enableNormalize();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        this.outer.render(scale);
        GlStateManager.disableBlend();
        GlStateManager.disableNormalize();
        GlStateManager.popMatrix();
    }
}