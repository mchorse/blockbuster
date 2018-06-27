package mchorse.blockbuster_pack.client.render.layers;

import mchorse.blockbuster.api.ModelLimb;
import mchorse.blockbuster.api.ModelLimb.ArmorSlot;
import mchorse.blockbuster.client.model.ModelCustom;
import mchorse.blockbuster.client.model.ModelCustomRenderer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

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
                ItemStack stack = this.getItemStackFromSlot(entity, limb.limb.slot.slot);

                if (stack != null && stack.getItem() instanceof ItemArmor)
                {
                    ItemArmor item = (ItemArmor) stack.getItem();

                    if (item.getEquipmentSlot() == limb.limb.slot.slot)
                    {
                        this.renderArmorSlot(entity, stack, item, limb, limb.limb.slot.slot, scale);
                    }
                }
            }
        }
    }

    private void renderArmorSlot(EntityLivingBase entity, ItemStack stack, ItemArmor item, ModelCustomRenderer limb, EntityEquipmentSlot slot, float scale)
    {
        ModelBiped model = this.getModelFromSlot(slot);
        model = this.getArmorModelHook(entity, stack, slot, model);

        if (model == null)
        {
            return;
        }

        model.setModelAttributes(this.renderer.getMainModel());
        this.renderer.bindTexture(this.getArmorResource(entity, stack, slot, null));

        GlStateManager.pushMatrix();
        limb.postRender(scale);
        this.setModelSlotVisible(model, limb.limb, limb.limb.slot);

        if (item.hasOverlay(stack))
        {
            int i = item.getColor(stack);
            float f = (float) (i >> 16 & 255) / 255.0F;
            float f1 = (float) (i >> 8 & 255) / 255.0F;
            float f2 = (float) (i & 255) / 255.0F;

            GlStateManager.color(f, f1, f2, 1);
            model.bipedHead.render(scale);
            model.bipedBody.render(scale);
            model.bipedRightArm.render(scale);
            model.bipedLeftArm.render(scale);
            model.bipedRightLeg.render(scale);
            model.bipedLeftLeg.render(scale);
            this.renderer.bindTexture(this.getArmorResource(entity, stack, slot, "overlay"));
        }

        GlStateManager.color(1, 1, 1, 1);
        model.bipedHead.render(scale);
        model.bipedBody.render(scale);
        model.bipedRightArm.render(scale);
        model.bipedLeftArm.render(scale);
        model.bipedRightLeg.render(scale);
        model.bipedLeftLeg.render(scale);

        GlStateManager.popMatrix();

        if (stack.hasEffect())
        {
            // renderEnchantedGlint(this.renderer, entity, model, 0, 0, 0, 0, 0, 0, scale);
        }
    }

    protected void setModelSlotVisible(ModelBiped model, ModelLimb limb, ArmorSlot slot)
    {
        model.bipedBody.setRotationPoint(0, 0, 0);
        model.bipedHead.setRotationPoint(0, 0, 0);
        model.bipedHeadwear.setRotationPoint(0, 0, 0);
        model.bipedLeftArm.setRotationPoint(-0.1F, 0, 0);
        model.bipedRightArm.setRotationPoint(0.1F, 0, 0);
        model.bipedLeftLeg.setRotationPoint(0, 0, 0);
        model.bipedRightLeg.setRotationPoint(0, 0, 0);

        model.setInvisible(false);

        int w = limb.size[0];
        int h = limb.size[1];
        int d = limb.size[2];

        float ax = limb.anchor[0];
        float ay = limb.anchor[1];
        float az = limb.anchor[2];

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
        }
        else if (slot == ArmorSlot.CHEST)
        {
            GlStateManager.scale(w / 8F, h / 12F, d / 4F);
            model.bipedBody.showModel = true;
            model.bipedBody.setRotationPoint(0, -6, 0);
        }
        else if (slot == ArmorSlot.LEFT_SHOULDER)
        {
            GlStateManager.scale(w / 4F, h / 12F, d / 4F);
            model.bipedLeftArm.showModel = true;
            model.bipedLeftArm.setRotationPoint(-1, -4, 0);
        }
        else if (slot == ArmorSlot.RIGHT_SHOULDER)
        {
            GlStateManager.scale(w / 4F, h / 12F, d / 4F);
            model.bipedRightArm.showModel = true;
            model.bipedRightArm.setRotationPoint(1, -4, 0);
        }
        else if (slot == ArmorSlot.LEFT_LEG)
        {
            GlStateManager.scale(w / 4F, h / 12F, d / 4F);
            model.bipedLeftLeg.showModel = true;
            model.bipedLeftLeg.setRotationPoint(0, -6, 0);
        }
        else if (slot == ArmorSlot.RIGHT_LEG)
        {
            GlStateManager.scale(w / 4F, h / 12F, d / 4F);
            model.bipedRightLeg.showModel = true;
            model.bipedRightLeg.setRotationPoint(0, -6, 0);
        }
        else if (slot == ArmorSlot.LEFT_FOOT)
        {
            GlStateManager.scale(w / 4F, h / 12F, d / 4F);
            model.bipedLeftLeg.showModel = true;
            model.bipedLeftLeg.setRotationPoint(0, -6, 0);
        }
        else if (slot == ArmorSlot.RIGHT_FOOT)
        {
            GlStateManager.scale(w / 4F, h / 12F, d / 4F);
            model.bipedRightLeg.showModel = true;
            model.bipedRightLeg.setRotationPoint(0, -6, 0);
        }
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