package mchorse.blockbuster_pack.client.render.layers;

import mchorse.blockbuster.api.ModelLimb;
import mchorse.blockbuster.api.ModelLimb.ArmorSlot;
import mchorse.blockbuster.client.model.ModelCustom;
import mchorse.blockbuster.client.model.ModelCustomRenderer;
import mchorse.blockbuster_pack.client.model.ModelElytra;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Fully copy-paste of LayerElytra from net.minecraft.client.render.layers
 */
@SideOnly(Side.CLIENT)
public class LayerElytra implements LayerRenderer<EntityLivingBase>
{
    /**
     * Texture of elytra located in minecraft's assets package
     */
    private static final ResourceLocation TEXTURE_ELYTRA = new ResourceLocation("textures/entity/elytra.png");

    private final ModelElytra modelElytra = new ModelElytra();
    private final RenderLivingBase<?> renderPlayer;

    public LayerElytra(RenderLivingBase<?> render)
    {
        this.renderPlayer = render;
    }

    @Override
    public void doRenderLayer(EntityLivingBase entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        ItemStack itemstack = entity.getItemStackFromSlot(EntityEquipmentSlot.CHEST);

        if (itemstack != null && itemstack.getItem() == Items.ELYTRA)
        {
            ModelBase base = this.renderPlayer.getMainModel();

            if (!(base instanceof ModelCustom))
            {
                return;
            }

            ModelCustom model = (ModelCustom) base;

            for (ModelCustomRenderer renderer : model.armor)
            {
                ModelLimb limb = renderer.limb;

                if (limb.slot != ArmorSlot.CHEST)
                {
                    continue;
                }

                int w = limb.size[0];
                int h = limb.size[1];
                int d = limb.size[2];

                float ww = w / 8F;
                float hh = h / 8F;
                float dd = d / 8F;

                float offsetX = limb.anchor[0] * ww / 2;
                float offsetY = limb.anchor[1] * hh / 2;
                float offsetZ = limb.anchor[2] * dd / 2;

                this.renderPlayer.bindTexture(TEXTURE_ELYTRA);

                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                GlStateManager.pushMatrix();

                renderer.postRender(scale);
                GlStateManager.translate(-ww / 4 + offsetX, hh / 4 - offsetY, dd / 4 - offsetZ);
                GlStateManager.scale(w / 8F, h / 12F, d / 4F);
                GlStateManager.translate(0.0F, -0.125F * 2.75F, 0.125F);
                this.modelElytra.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
                this.modelElytra.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);

                if (itemstack.isItemEnchanted())
                {
                    LayerArmorBase.renderEnchantedGlint(this.renderPlayer, entity, this.modelElytra, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
                }

                GlStateManager.popMatrix();
            }
        }
    }

    @Override
    public boolean shouldCombineTextures()
    {
        return false;
    }
}