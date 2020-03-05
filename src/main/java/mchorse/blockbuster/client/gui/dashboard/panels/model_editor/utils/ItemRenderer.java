package mchorse.blockbuster.client.gui.dashboard.panels.model_editor.utils;

import mchorse.blockbuster.client.model.ModelCustom;
import mchorse.blockbuster.client.model.ModelCustomRenderer;
import mchorse.blockbuster.client.render.layer.LayerHeldItem;
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
            LayerHeldItem.renderHeldItem(entity, itemstack, model, ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, EnumHandSide.LEFT);
            LayerHeldItem.renderHeldItem(entity, itemstack1, model, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, EnumHandSide.RIGHT);
            GlStateManager.popMatrix();
        }
    }
}