package mchorse.blockbuster_pack.client.render.layers;

import mchorse.blockbuster.api.ModelLimb;
import mchorse.blockbuster.api.ModelLimb.ArmorSlot;
import mchorse.blockbuster.client.model.ModelCustom;
import mchorse.blockbuster.client.model.ModelCustomRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

/**
 * Actor's armor layer
 *
 * This is a temporary fix for the armor. In next
 */
public class LayerActorArmor extends LayerArmorBase<ModelBiped>
{
    private RenderLivingBase<EntityLivingBase> renderer;

    public LayerActorArmor(RenderLivingBase<EntityLivingBase> renderer)
    {
        super(renderer);
        this.renderer = renderer;
    }

    @Override
    protected void initArmor()
    {
        this.modelArmor = new ModelBiped(1);
        this.modelLeggings = new ModelBiped(0.5F);
    }

    @Override
    public void doRenderLayer(EntityLivingBase entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        ModelBase base = this.renderer.getMainModel();

        if (base instanceof ModelCustom)
        {
            ModelCustom model = (ModelCustom) base;

            for (ModelCustomRenderer limb : model.armor)
            {
                ItemStack stack = entity.getItemStackFromSlot(limb.limb.slot.slot);

                if (stack != null && stack.getItem() instanceof ItemArmor)
                {
                    ItemArmor item = (ItemArmor) stack.getItem();

                    if (item.getEquipmentSlot() == limb.limb.slot.slot)
                    {
                        this.renderArmorSlot(entity, stack, item, limb, limb.limb.slot.slot, partialTicks, scale);
                    }
                }
            }
        }
    }

    private void renderArmorSlot(EntityLivingBase entity, ItemStack stack, ItemArmor item, ModelCustomRenderer limb, EntityEquipmentSlot slot, float partialTicks, float scale)
    {
        ModelBiped model = this.getModelFromSlot(slot);
        model = this.getArmorModelHook(entity, stack, slot, model);

        if (model == null)
        {
            return;
        }

        GlStateManager.pushMatrix();

        model.setModelAttributes(this.renderer.getMainModel());
        this.renderer.bindTexture(this.getArmorResource(entity, stack, slot, null));
        limb.postRender(scale);

        ModelRenderer renderer = this.setModelSlotVisible(model, limb.limb, limb.limb.slot);

        if (renderer != null)
        {
            GlStateManager.enableRescaleNormal();

            if (item.hasOverlay(stack))
            {
                int i = item.getColor(stack);
                float r = (float) (i >> 16 & 255) / 255F;
                float g = (float) (i >> 8 & 255) / 255F;
                float b = (float) (i & 255) / 255F;

                GlStateManager.color(r, g, b, 1);
                renderer.render(scale);
                this.renderer.bindTexture(this.getArmorResource(entity, stack, slot, "overlay"));
            }

            GlStateManager.color(1, 1, 1, 1);
            renderer.render(scale);

            GlStateManager.disableRescaleNormal();

            if (stack.hasEffect())
            {
                this.renderMyEnchantedGlint(this.renderer, entity, renderer, partialTicks, scale);
            }
        }

        GlStateManager.popMatrix();
    }

    private void renderMyEnchantedGlint(RenderLivingBase<?> layer, EntityLivingBase entity, ModelRenderer renderer, float partialTicks, float p_188364_9_)
    {
        float timer = entity.ticksExisted + partialTicks;

        layer.bindTexture(ENCHANTED_ITEM_GLINT_RES);
        Minecraft.getMinecraft().entityRenderer.setupFogColor(true);

        GlStateManager.enableBlend();
        GlStateManager.depthFunc(GL11.GL_EQUAL);
        GlStateManager.depthMask(false);
        GlStateManager.color(0.5F, 0.5F, 0.5F, 1.0F);

        for (int iter = 0; iter < 2; ++iter)
        {
            GlStateManager.disableLighting();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);
            GlStateManager.color(0.38F, 0.19F, 0.608F, 1.0F);
            GlStateManager.matrixMode(5890);
            GlStateManager.loadIdentity();
            GlStateManager.scale(0.33333334F, 0.33333334F, 0.33333334F);
            GlStateManager.rotate(30.0F - iter * 60.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.translate(0.0F, timer * (0.001F + iter * 0.003F) * 20.0F, 0.0F);
            GlStateManager.matrixMode(5888);
            renderer.render(p_188364_9_);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        }

        GlStateManager.matrixMode(5890);
        GlStateManager.loadIdentity();
        GlStateManager.matrixMode(5888);
        GlStateManager.enableLighting();
        GlStateManager.depthMask(true);
        GlStateManager.depthFunc(GL11.GL_LEQUAL);
        GlStateManager.disableBlend();

        Minecraft.getMinecraft().entityRenderer.setupFogColor(false);
    }

    protected ModelRenderer setModelSlotVisible(ModelBiped model, ModelLimb limb, ArmorSlot slot)
    {
        model.bipedBody.setRotationPoint(0, 0, 0);
        model.bipedHead.setRotationPoint(0, 0, 0);
        model.bipedHeadwear.setRotationPoint(0, 0, 0);
        model.bipedLeftArm.setRotationPoint(-0.1F, 0, 0);
        model.bipedRightArm.setRotationPoint(0.1F, 0, 0);
        model.bipedLeftLeg.setRotationPoint(0, 0, 0);
        model.bipedRightLeg.setRotationPoint(0, 0, 0);

        model.setVisible(false);

        int w = limb.size[0];
        int h = limb.size[1];
        int d = limb.size[2];

        float ww = w / 8F;
        float hh = h / 8F;
        float dd = d / 8F;

        float offsetX = limb.anchor[0] * ww / 2;
        float offsetY = limb.anchor[1] * hh / 2;
        float offsetZ = limb.anchor[2] * dd / 2;

        GlStateManager.translate(-ww / 4 + offsetX, hh / 4 - offsetY, dd / 4 - offsetZ);

        if (slot == ArmorSlot.HEAD)
        {
            GlStateManager.scale(w / 8F, h / 8F, d / 8F);
            model.bipedHead.showModel = true;
            model.bipedHead.setRotationPoint(0, 4, 0);

            return model.bipedHead;
        }
        else if (slot == ArmorSlot.CHEST)
        {
            GlStateManager.scale(w / 8F, h / 12F, d / 4F);
            model.bipedBody.showModel = true;
            model.bipedBody.setRotationPoint(0, -6, 0);

            return model.bipedBody;
        }
        else if (slot == ArmorSlot.LEFT_SHOULDER)
        {
            GlStateManager.scale(w / 4F, h / 12F, d / 4F);
            model.bipedLeftArm.showModel = true;
            model.bipedLeftArm.setRotationPoint(-1, -4, 0);

            return model.bipedLeftArm;
        }
        else if (slot == ArmorSlot.RIGHT_SHOULDER)
        {
            GlStateManager.scale(w / 4F, h / 12F, d / 4F);
            model.bipedRightArm.showModel = true;
            model.bipedRightArm.setRotationPoint(1, -4, 0);

            return model.bipedRightArm;
        }
        else if (slot == ArmorSlot.LEGGINGS)
        {
            GlStateManager.scale(w / 8F, h / 12F, d / 4F);
            model.bipedBody.showModel = true;
            model.bipedBody.setRotationPoint(0, -6, 0);

            return model.bipedBody;
        }
        else if (slot == ArmorSlot.LEFT_LEG)
        {
            GlStateManager.scale(w / 4F, h / 12F, d / 4F);
            model.bipedLeftLeg.showModel = true;
            model.bipedLeftLeg.setRotationPoint(0, -6, 0);

            return model.bipedLeftLeg;
        }
        else if (slot == ArmorSlot.RIGHT_LEG)
        {
            GlStateManager.scale(w / 4F, h / 12F, d / 4F);
            model.bipedRightLeg.showModel = true;
            model.bipedRightLeg.setRotationPoint(0, -6, 0);

            return model.bipedRightLeg;
        }
        else if (slot == ArmorSlot.LEFT_FOOT)
        {
            GlStateManager.scale(w / 4F, h / 12F, d / 4F);
            model.bipedLeftLeg.showModel = true;
            model.bipedLeftLeg.setRotationPoint(0, -6, 0);

            return model.bipedLeftLeg;
        }
        else if (slot == ArmorSlot.RIGHT_FOOT)
        {
            GlStateManager.scale(w / 4F, h / 12F, d / 4F);
            model.bipedRightLeg.showModel = true;
            model.bipedRightLeg.setRotationPoint(0, -6, 0);

            return model.bipedRightLeg;
        }

        return null;
    }

    @Override
    protected void setModelSlotVisible(ModelBiped p_188359_1_, EntityEquipmentSlot slotIn)
    {}

    @Override
    protected ModelBiped getArmorModelHook(net.minecraft.entity.EntityLivingBase entity, net.minecraft.item.ItemStack itemStack, EntityEquipmentSlot slot, ModelBiped model)
    {
        return net.minecraftforge.client.ForgeHooksClient.getArmorModel(entity, itemStack, slot, model);
    }
}