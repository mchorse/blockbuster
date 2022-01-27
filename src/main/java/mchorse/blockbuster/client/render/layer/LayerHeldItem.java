package mchorse.blockbuster.client.render.layer;

import mchorse.blockbuster.api.ModelPose;
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
 * This is patched LayerHeldItem layer class. This class is responsible for
 * rendering items in designated limbs in custom actor model. Lots of fun
 * stuff going on here.
 */
@SideOnly(Side.CLIENT)
public class LayerHeldItem implements LayerRenderer<EntityLivingBase>
{
    protected final RenderLivingBase<?> livingEntityRenderer;

    public LayerHeldItem(RenderLivingBase<?> livingEntityRendererIn)
    {
        this.livingEntityRenderer = livingEntityRendererIn;
    }

    @Override
    public void doRenderLayer(EntityLivingBase entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        ItemStack itemstack1 = entity.getHeldItemMainhand();
        ItemStack itemstack = entity.getHeldItemOffhand();

        if (!itemstack.isEmpty() || !itemstack1.isEmpty())
        {
            HeldModel model = new HeldModel(((ModelCustom) this.livingEntityRenderer.getMainModel()));

            model.limbSwing = limbSwing;
            model.limbSwingAmount = limbSwingAmount;
            model.ageInTicks = ageInTicks;
            model.netHeadYaw = netHeadYaw;
            model.headPitch = headPitch;
            model.scale = scale;

            renderHeldItem(entity, itemstack1, model, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, EnumHandSide.RIGHT);
            renderHeldItem(entity, itemstack, model, ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, EnumHandSide.LEFT);

            model.model.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
        }
    }

    /**
     * Render item in every arm.
     *
     * <p>Items could be rendered to several limbs.</p>
     */
    public static void renderHeldItem(EntityLivingBase entity, ItemStack item, HeldModel model, ItemCameraTransforms.TransformType transform, EnumHandSide handSide)
    {
        if (!item.isEmpty())
        {
            for (ModelCustomRenderer arm : model.model.getRenderForArm(handSide))
            {
                boolean flag = handSide == EnumHandSide.LEFT;

                model.setup(entity);
                GlStateManager.pushMatrix();
                applyTransform(arm);

                Minecraft.getMinecraft().getItemRenderer().renderItemSide(entity, item, transform, flag);
                GlStateManager.popMatrix();
            }
        }
    }

    /**
     * Render item in every arm.
     * <p>
     * Items could be rendered to several limbs.
     */
    public static void renderHeldItem(EntityLivingBase entity, ItemStack item, ModelCustom model, ItemCameraTransforms.TransformType transform, EnumHandSide handSide)
    {
        if (item != null)
        {
            for (ModelCustomRenderer arm : model.getRenderForArm(handSide))
            {
                boolean flag = handSide == EnumHandSide.LEFT;

                GlStateManager.pushMatrix();
                applyTransform(arm);

                Minecraft.getMinecraft().getItemRenderer().renderItemSide(entity, item, transform, flag);
                GlStateManager.popMatrix();
            }
        }
    }

    private static void applyTransform(ModelCustomRenderer arm)
    {
        float x = (arm.limb.size[0] * (0.5F - arm.limb.anchor[0])) * 0.0625F;
        float y = arm.limb.size[1] * (arm.limb.size[1] * (1 - arm.limb.anchor[1]) / arm.limb.size[1]) * -0.0625F;
        float z = (arm.limb.size[2] * (arm.limb.anchor[2])) * 0.0625F;

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

        GlStateManager.scale(arm.limb.itemScale, arm.limb.itemScale, arm.limb.itemScale);
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

    /**
     * Held model class
     * <p>
     * This class is responsible for storing the data related to rendering of
     * some stuff in the layer class. This is needed to store the rotation and
     * angles during that stage, because recursive model block item stack
     * rendering messing up the angles, this class used to restore the original
     * state.
     */
    public static class HeldModel
    {
        public float limbSwing;
        public float limbSwingAmount;
        public float ageInTicks;
        public float netHeadYaw;
        public float headPitch;
        public float scale;

        public ModelCustom model;
        public ModelPose pose;

        public HeldModel(ModelCustom model)
        {
            this.model = model;
            this.pose = model.pose;
        }

        public void setup(EntityLivingBase entity)
        {
            this.model.pose = this.pose;
            this.model.setRotationAngles(this.limbSwing, this.limbSwingAmount, this.ageInTicks, this.netHeadYaw, this.headPitch, this.scale, entity);
        }
    }
}