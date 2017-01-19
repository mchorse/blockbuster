package mchorse.blockbuster.client.render;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.metamorph.api.morphs.AbstractMorph;
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
    public RenderActor(RenderManager manager, float f)
    {
        super(manager, null, f);
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

    /**
     * Most important extension! Don't render the name in GUI, that looks
     * irritating. actor.renderName is switched for awhile to false during GUI
     * rendering.
     *
     * See GuiActorSkin for a reference.
     */
    @Override
    protected boolean canRenderName(EntityActor entity)
    {
        return super.canRenderName(entity) && entity.renderName;
    }

    @Override
    public void doRender(EntityActor entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        this.shadowOpaque = entity.invisible ? 0.0F : 1.0F;

        if (entity.invisible)
        {
            return;
        }

        AbstractMorph morph = entity.getMorph();

        if (morph != null)
        {
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