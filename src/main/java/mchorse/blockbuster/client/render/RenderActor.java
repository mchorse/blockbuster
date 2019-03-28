package mchorse.blockbuster.client.render;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.api.Model;
import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster_pack.morphs.CustomMorph;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;

/**
 * Render actor class
 *
 * Render actor entities with custom swaggalicious models ?8|
 */
public class RenderActor extends RenderLiving<EntityActor>
{
    /**
     * Default texture of the renderer
     */
    private static final ResourceLocation defaultTexture = new ResourceLocation(Blockbuster.MODID, "textures/entity/actor.png");

    /**
     * Initiate render actor
     */
    public RenderActor(RenderManager manager, float shadow)
    {
        super(manager, null, shadow);
    }

    /**
     * Use skin from resource pack or default one (if skin is empty or just
     * wasn't found by actor pack)
     */
    @Override
    protected ResourceLocation getEntityTexture(EntityActor entity)
    {
        return defaultTexture;
    }

    @Override
    public boolean shouldRender(EntityActor livingEntity, ICamera camera, double camX, double camY, double camZ)
    {
        if (Blockbuster.proxy.config.actor_always_render)
        {
            return true;
        }

        return super.shouldRender(livingEntity, camera, camX, camY, camZ);
    }

    @Override
    public void doRender(EntityActor entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        this.shadowOpaque = 0;

        if (entity.invisible)
        {
            return;
        }

        AbstractMorph morph = entity.getMorph();

        if (morph != null)
        {
            this.shadowOpaque = 1.0F;

            float shadow = 0.5F;

            if (morph instanceof CustomMorph)
            {
                Model model = ((CustomMorph) morph).model;

                if (model != null)
                {
                    shadow = model.getPose("standing").size[0] * 0.9F;
                }
            }

            this.shadowSize = shadow;

            morph.render(entity, x, y, z, entityYaw, partialTicks);
        }
    }

    /**
     * Renderer factory
     *
     * Some interface provided by Minecraft Forge that will pass a RenderManager
     * instance into the method for easier Renders initiation.
     */
    public static class FactoryActor implements IRenderFactory<EntityActor>
    {
        @Override
        public RenderActor createRenderFor(RenderManager manager)
        {
            return new RenderActor(manager, 0.5F);
        }
    }
}