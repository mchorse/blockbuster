package mchorse.blockbuster.client.render.layers;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mchorse.blockbuster.client.gui.utils.GlStateManager;
import mchorse.blockbuster.client.model.ModelCustom;
import mchorse.blockbuster.client.model.ModelCustomRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

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
public class LayerHeldItem implements LayerRenderer
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

        if (itemstack != null || itemstack1 != null)
        {
            GlStateManager.pushMatrix();
            this.renderHeldItem(entity, itemstack1, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, EnumHandSide.RIGHT);
            this.renderHeldItem(entity, itemstack, ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, EnumHandSide.LEFT);
            GlStateManager.popMatrix();
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
            ModelCustom model = ((ModelCustom) this.livingEntityRenderer.getMainModel());

            for (ModelCustomRenderer arm : model.getRenderForArm(handSide))
            {
                boolean flag = handSide == EnumHandSide.LEFT;

                float y = arm.limb.size[1] * -0.0625F;
                float z = arm.limb.size[2] / 2 * 0.0625F;

                GlStateManager.pushMatrix();

                arm.postRender(0.0625F);

                GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                GlStateManager.translate(0.0F, z, y);

                Minecraft.getMinecraft().getItemRenderer().renderItemSide(entity, item, transform, flag);

                GlStateManager.popMatrix();
            }
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
}