package noname.blockbuster.client.render;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import noname.blockbuster.Blockbuster;
import noname.blockbuster.ClientProxy;
import noname.blockbuster.entity.ActorEntity;

/**
 * Actor renderer
 *
 * Renders actor entities with
 */
public class ActorRender extends RenderBiped<ActorEntity>
{
    private static final ResourceLocation defaultTexture = new ResourceLocation(Blockbuster.MODID, "textures/entity/actor.png");

    private float previousYaw;
    private int ticks;

    /**
     * Add armor layer to my biped texture
     */
    public ActorRender(RenderManager renderManagerIn, ModelBiped modelBipedIn, float shadowSize)
    {
        super(renderManagerIn, modelBipedIn, shadowSize);

        this.addLayer(new LayerBipedArmor(this));
    }

    /**
     * Another important extension. Assign sneaking property, without it, actor
     * would look like an idiot who's clipping through the ground for a minute.
     */
    @Override
    public void doRender(ActorEntity entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        this.modelBipedMain.isSneak = entity.isSneaking();

        float lastYaw = entity.rotationYawHead;
        float lastPrevYaw = entity.prevRotationYawHead;

        if (this.previousYaw != entityYaw)
        {
            this.previousYaw = entityYaw;
        }

        entity.prevRotationYawHead = entityYaw;
        entityYaw = this.interpolateRotation(entityYaw, this.previousYaw, partialTicks);
        entity.rotationYawHead = entityYaw;

        super.doRender(entity, x, y, z, entityYaw, partialTicks * 0.5F);

        entity.rotationYawHead = lastYaw;
        entity.prevRotationYawHead = lastPrevYaw;
    }

    /**
     * Most important extension! Don't render the name in GUI, that looks
     * irritating. actor.renderName is switched for awhile to false during
     * GUI rendering.
     *
     * See GuiActorSkin for a reference.
     */
    @Override
    protected boolean canRenderName(ActorEntity entity)
    {
        return super.canRenderName(entity) && entity.renderName;
    }

    /**
     * Make actor a little bit smaller (so he looked like steve, and not like
     * a giant weirdo).
     */
    protected void preRenderCallback(AbstractClientPlayer entitylivingbaseIn, float partialTickTime)
    {
        float f = 0.920F;
        GlStateManager.scale(f, f, f);
    }

    /**
     * Use skin from resource pack or default one (if skin is empty or just
     * wasn't found by actor pack)
     */
    @Override
    protected ResourceLocation getEntityTexture(ActorEntity entity)
    {
        ResourceLocation location = new ResourceLocation("blockbuster.actors", entity.skin);

        if (ClientProxy.actorPack.resourceExists(location))
        {
            return location;
        }

        return defaultTexture;
    }

    /**
     * Renderer factory
     */
    public static class ActorFactory implements IRenderFactory
    {
        @Override
        public Render createRenderFor(RenderManager manager)
        {
            return new ActorRender(manager, new ModelBiped(), 0.5F);
        }
    }
}
