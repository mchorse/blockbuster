package mchorse.blockbuster.client.render.layer;

import java.util.Stack;

import mchorse.blockbuster.client.model.ModelCustom;
import mchorse.blockbuster.client.model.ModelCustomRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * That's unbelievable!
 *
 * - How many classes are you going to steal from Minecraft core?
 * - As much as I want to. Duh!
 *
 * This is patched LayerHeldItem layer class. This class is responsible for
 * rendering items in designated limbs in custom actor model. Lots of fun
 * stuff going on here.
 */
@SideOnly(Side.CLIENT)
public class LayerHeldItem implements LayerRenderer<EntityLivingBase>
{
    protected final RenderLivingBase<?> livingEntityRenderer;

    private Stack<HeldModel> models = new Stack<HeldModel>();

    public LayerHeldItem(RenderLivingBase<?> livingEntityRendererIn)
    {
        this.livingEntityRenderer = livingEntityRendererIn;
    }

    @Override
    public void doRenderLayer(EntityLivingBase entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        ItemStack itemstack1 = entity.getHeldItemMainhand();
        ItemStack itemstack = entity.getHeldItemOffhand();

        if (itemstack != null || itemstack1 != null)
        {
            HeldModel model = new HeldModel(((ModelCustom) this.livingEntityRenderer.getMainModel()));

            model.limbSwing = limbSwing;
            model.limbSwingAmount = limbSwingAmount;
            model.ageInTicks = ageInTicks;
            model.netHeadYaw = netHeadYaw;
            model.headPitch = headPitch;
            model.scale = scale;

            this.models.push(model);
            this.renderHeldItem(entity, itemstack1, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, EnumHandSide.RIGHT);
            this.renderHeldItem(entity, itemstack, ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, EnumHandSide.LEFT);
            this.models.pop();
        }
    }

    /**
     * Render item in every arm.
     *
     * Items could be rendered to several limbs.
     */
    private void renderHeldItem(EntityLivingBase entity, ItemStack item, ItemCameraTransforms.TransformType transform, EnumHandSide handSide)
    {
        if (item != null)
        {
            HeldModel model = this.models.pop();

            for (ModelCustomRenderer arm : model.model.getRenderForArm(handSide))
            {
                boolean flag = handSide == EnumHandSide.LEFT;

                model.model.setRotationAngles(model.limbSwing, model.limbSwingAmount, model.ageInTicks, model.netHeadYaw, model.headPitch, model.scale, entity);
                GlStateManager.pushMatrix();
                this.applyTransform(arm);

                Minecraft.getMinecraft().getItemRenderer().renderItemSide(entity, item, transform, flag);
                GlStateManager.popMatrix();
            }

            this.models.push(model);
        }
    }

    private void applyTransform(ModelCustomRenderer arm)
    {
        float x = 0.0F;
        float y = arm.limb.size[1] * (arm.limb.size[1] * (1 - arm.limb.anchor[1]) / arm.limb.size[1]) * -0.0625F;
        float z = arm.limb.size[2] / 2 * 0.0625F;

        if (arm.limb.size[0] > arm.limb.size[1])
        {
            x = arm.limb.size[0] * (10.0F / 12.0F) * 0.0625F;
        }

        arm.postRender(0.0625F);

        GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.translate(x, z, y);

        if (arm.limb.size[0] > arm.limb.size[1])
        {
            GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
        }
    }

    /**
     * Don't really understand how this method is going to affect the rendering
     * of this layer.
     */
    @Override
    public boolean shouldCombineTextures()
    {
        return false;
    }

    public static class HeldModel
    {
        public float limbSwing;
        public float limbSwingAmount;
        public float ageInTicks;
        public float netHeadYaw;
        public float headPitch;
        public float scale;
        public ModelCustom model;

        public HeldModel(ModelCustom model)
        {
            this.model = model;
        }
    }
}