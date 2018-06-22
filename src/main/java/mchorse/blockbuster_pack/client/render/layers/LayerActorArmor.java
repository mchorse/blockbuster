package mchorse.blockbuster_pack.client.render.layers;

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
        this.modelArmor = new ModelBiped(0.5F);
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
        ModelBiped t = this.getModelFromSlot(slot);
        t = this.getArmorModelHook(entity, stack, slot, t);

        if (t == null)
        {
            return;
        }

        t.setModelAttributes(this.renderer.getMainModel());
        this.setModelSlotVisible(t, slot);
        this.renderer.bindTexture(this.getArmorResource(entity, stack, slot, null));

        t.bipedBody.setRotationPoint(0, 0, 0);
        t.bipedHead.setRotationPoint(0, 0, 0);
        t.bipedHeadwear.setRotationPoint(0, 0, 0);
        t.bipedLeftArm.setRotationPoint(-0.1F, 0, 0);
        t.bipedRightArm.setRotationPoint(0.1F, 0, 0);
        t.bipedLeftLeg.setRotationPoint(0, 0, 0);
        t.bipedRightLeg.setRotationPoint(0, 0, 0);

        t.setInvisible(false);

        if (limb.limb.slot == ArmorSlot.HEAD) t.bipedHead.showModel = true;
        else if (limb.limb.slot == ArmorSlot.CHEST) t.bipedBody.showModel = true;
        else if (limb.limb.slot == ArmorSlot.LEFT_SHOULDER) t.bipedLeftArm.showModel = true;
        else if (limb.limb.slot == ArmorSlot.RIGHT_SHOULDER) t.bipedRightArm.showModel = true;
        else if (limb.limb.slot == ArmorSlot.LEFT_LEG) t.bipedLeftLeg.showModel = true;
        else if (limb.limb.slot == ArmorSlot.RIGHT_LEG) t.bipedRightLeg.showModel = true;
        else if (limb.limb.slot == ArmorSlot.LEFT_FOOT)
        {
            t.bipedLeftLeg.showModel = true;
            t.bipedLeftLeg.setRotationPoint(0, -6, 0);
        }
        else if (limb.limb.slot == ArmorSlot.RIGHT_FOOT)
        {
            t.bipedRightLeg.showModel = true;
            t.bipedRightLeg.setRotationPoint(0, -6, 0);
        }

        GlStateManager.pushMatrix();
        limb.postRender(scale);

        if (item.hasOverlay(stack))
        {
            int i = item.getColor(stack);
            float f = (float) (i >> 16 & 255) / 255.0F;
            float f1 = (float) (i >> 8 & 255) / 255.0F;
            float f2 = (float) (i & 255) / 255.0F;

            GlStateManager.color(f, f1, f2, 1);
            t.bipedHead.render(scale);
            t.bipedBody.render(scale);
            t.bipedRightArm.render(scale);
            t.bipedLeftArm.render(scale);
            t.bipedRightLeg.render(scale);
            t.bipedLeftLeg.render(scale);
            this.renderer.bindTexture(this.getArmorResource(entity, stack, slot, "overlay"));
        }

        GlStateManager.color(1, 1, 1, 1);
        t.bipedHead.render(scale);
        t.bipedBody.render(scale);
        t.bipedRightArm.render(scale);
        t.bipedLeftArm.render(scale);
        t.bipedRightLeg.render(scale);
        t.bipedLeftLeg.render(scale);

        GlStateManager.popMatrix();

        if (stack.hasEffect())
        {
            // renderEnchantedGlint(this.renderer, entityLivingBaseIn, t, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
        }
    }

    @Override
    @SuppressWarnings("incomplete-switch")
    protected void setModelSlotVisible(ModelBiped model, EntityEquipmentSlot slotIn)
    {
        this.setModelVisible(model);

        switch (slotIn)
        {
            case HEAD:
                model.bipedHead.showModel = true;
                model.bipedHeadwear.showModel = true;
            break;
            case CHEST:
                model.bipedBody.showModel = true;
                model.bipedRightArm.showModel = true;
                model.bipedLeftArm.showModel = true;
            break;
            case LEGS:
                model.bipedBody.showModel = true;
                model.bipedRightLeg.showModel = true;
                model.bipedLeftLeg.showModel = true;
            break;
            case FEET:
                model.bipedRightLeg.showModel = true;
                model.bipedLeftLeg.showModel = true;
        }
    }

    protected void setModelVisible(ModelBiped model)
    {
        model.setInvisible(false);
    }

    @Override
    protected ModelBiped getArmorModelHook(net.minecraft.entity.EntityLivingBase entity, net.minecraft.item.ItemStack itemStack, EntityEquipmentSlot slot, ModelBiped model)
    {
        return net.minecraftforge.client.ForgeHooksClient.getArmorModel(entity, itemStack, slot, model);
    }
}