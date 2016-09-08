package noname.blockbuster.client.render;

import net.minecraft.client.model.ModelElytra;
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
    private static final ResourceLocation TEXTURE_ELYTRA = new ResourceLocation("textures/entity/elytra.png");

    private final ModelElytra modelElytra = new ModelElytra();

    protected final RenderLivingBase<?> renderPlayer;

    public LayerElytra(RenderLivingBase<?> livingEntityRendererIn)
    {
        this.renderPlayer = livingEntityRendererIn;
    }

    @Override
    public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        ItemStack itemstack = entitylivingbaseIn.getItemStackFromSlot(EntityEquipmentSlot.CHEST);

        if (itemstack != null && itemstack.getItem() == Items.ELYTRA)
        {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            this.renderPlayer.bindTexture(TEXTURE_ELYTRA);

            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0F, 0.0F, 0.125F);
            this.modelElytra.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entitylivingbaseIn);
            this.modelElytra.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);

            if (itemstack.isItemEnchanted())
            {
                LayerArmorBase.renderEnchantedGlint(this.renderPlayer, entitylivingbaseIn, this.modelElytra, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
            }

            GlStateManager.popMatrix();
        }
    }

    @Override
    public boolean shouldCombineTextures()
    {
        return false;
    }
}