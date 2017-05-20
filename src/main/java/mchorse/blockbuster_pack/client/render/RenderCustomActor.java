package mchorse.blockbuster_pack.client.render;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster_pack.client.render.layers.LayerActorArmor;
import mchorse.blockbuster_pack.client.render.layers.LayerCustomHead;
import mchorse.blockbuster_pack.client.render.layers.LayerElytra;
import mchorse.blockbuster_pack.morphs.ActorMorph;
import mchorse.metamorph.api.EntityUtils;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.client.render.RenderCustomModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

/**
 * Overriden {@link RenderCustomModel} to support {@link ActorMorph}'s skin
 * property.
 */
public class RenderCustomActor extends RenderCustomModel
{
    public RenderCustomActor(RenderManager renderManagerIn, ModelBase modelBaseIn, float shadowSizeIn)
    {
        super(renderManagerIn, modelBaseIn, shadowSizeIn);

        this.addLayer(new LayerElytra(this));
        this.addLayer(new LayerActorArmor(this));
        this.addLayer(new LayerCustomHead());
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
        AbstractMorph morph = EntityUtils.getMorph(entity);

        if (morph != null && morph instanceof ActorMorph)
        {
            ResourceLocation skin = ((ActorMorph) morph).skin;

            if (skin != null)
            {
                return skin;
            }
        }

        return super.getEntityTexture(entity);
    }

    @Override
    protected boolean canRenderName(EntityLivingBase entity)
    {
        return entity.hasCustomName() && (Blockbuster.proxy.config.actor_always_render_names || (Minecraft.isGuiEnabled() && entity == this.renderManager.pointedEntity));
    }
}