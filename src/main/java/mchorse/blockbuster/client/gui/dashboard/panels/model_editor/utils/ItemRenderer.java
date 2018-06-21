package mchorse.blockbuster.client.gui.dashboard.panels.model_editor.utils;

import mchorse.blockbuster.client.model.ModelCustom;
import mchorse.blockbuster.client.model.ModelCustomRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;

/**
 * Item renderer in a model editor
 *
 * This class is responsible for only one thing, rendering items in the model
 * editor.
 */
public class ItemRenderer
{
    public static void renderItems(EntityLivingBase entity, ModelCustom model, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        ItemStack itemstack1 = entity.getHeldItemMainhand();
        ItemStack itemstack = entity.getHeldItemOffhand();

        if (itemstack != null || itemstack1 != null)
        {
            GlStateManager.pushMatrix();
            renderHeldItem(entity, model, itemstack, ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, EnumHandSide.LEFT);
            renderHeldItem(entity, model, itemstack1, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, EnumHandSide.RIGHT);
            GlStateManager.popMatrix();
        }
    }

    /**
     * Render item in every arm.
     *
     * Items could be rendered to several limbs.
     */
    private static void renderHeldItem(EntityLivingBase entity, ModelCustom model, ItemStack item, ItemCameraTransforms.TransformType transform, EnumHandSide handSide)
    {
        if (item != null)
        {
            for (ModelCustomRenderer arm : model.getRenderForArm(handSide))
            {
                boolean flag = handSide == EnumHandSide.LEFT;

                float x = 0.0F;
                float y = arm.limb.size[1] * (arm.limb.size[1] * (1 - arm.limb.anchor[1]) / arm.limb.size[1]) * -0.0625F;
                float z = arm.limb.size[2] / 2 * 0.0625F;

                if (arm.limb.size[0] > arm.limb.size[1])
                {
                    x = arm.limb.size[0] * (10.0F / 12.0F) * 0.0625F;
                }

                GlStateManager.pushMatrix();
                arm.postRender(0.0625F);

                GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                GlStateManager.translate(x, z, y);

                if (arm.limb.size[0] > arm.limb.size[1])
                {
                    GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
                }

                Minecraft.getMinecraft().getItemRenderer().renderItemSide(entity, item, transform, flag);
                GlStateManager.popMatrix();
            }
        }
    }
}