package mchorse.blockbuster_pack.client.render;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.client.render.RenderCustomModel;
import mchorse.blockbuster.client.render.layer.LayerHeldItem;
import mchorse.blockbuster_pack.client.render.layers.LayerActorArmor;
import mchorse.blockbuster_pack.client.render.layers.LayerBodyPart;
import mchorse.blockbuster_pack.client.render.layers.LayerCustomHead;
import mchorse.blockbuster_pack.client.render.layers.LayerElytra;
import mchorse.blockbuster_pack.morphs.CustomMorph;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

/**
 * Overriden {@link RenderCustomModel} to support {@link CustomMorph}'s skin
 * property.
 */
public class RenderCustomActor extends RenderCustomModel
{
    public RenderCustomActor(RenderManager renderManagerIn, ModelBase modelBaseIn, float shadowSizeIn)
    {
        super(renderManagerIn, modelBaseIn, shadowSizeIn);

        this.addLayer(new LayerElytra(this));
        this.addLayer(new LayerBodyPart(this));
        this.addLayer(new LayerActorArmor(this));
        this.addLayer(new LayerCustomHead(this));
        this.addLayer(new LayerHeldItem(this));
    }

    /**
     * Get entity's texture
     *
     * The thing which is going on here, is that we're going to check, whether
     * given entity has a morph, and if it does, we're going to use its skin
     */
    @Override
    protected ResourceLocation getEntityTexture(EntityLivingBase entity)
    {
        AbstractMorph morph = this.current;

        if (morph != null && morph instanceof CustomMorph)
        {
            ResourceLocation skin = ((CustomMorph) morph).skin;

            if (skin != null)
            {
                return skin;
            }
        }

        return super.getEntityTexture(entity);
    }

    /**
     * Can the nametag be rendered by this entity
     *
     * This method is also takes in account the config option for making actor
     * nametags visible always.
     */
    @Override
    protected boolean canRenderName(EntityLivingBase entity)
    {
        return entity.hasCustomName() && (Blockbuster.actorAlwaysRenderNames.get() || (Minecraft.isGuiEnabled() && entity == this.renderManager.pointedEntity));
    }
}