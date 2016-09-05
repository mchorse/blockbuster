package noname.blockbuster.client.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import noname.blockbuster.Blockbuster;
import noname.blockbuster.client.model.ModelCustom;
import noname.blockbuster.entity.EntityActor;

/**
 * Render actor test class
 *
 * This is a temporary class for rendering an actor
 */
public class RenderTest extends RenderLiving<EntityActor>
{
    private static final ResourceLocation defaultTexture = new ResourceLocation(Blockbuster.MODID, "textures/entity/actor.png");

    private float previousYaw;

    public RenderTest(RenderManager manager, ModelBase model, float f)
    {
        super(manager, model, f);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityActor entity)
    {
        return defaultTexture;
    }

    @Override
    public void doRender(EntityActor entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        if (!entity.renderName)
        {
            super.doRender(entity, x, y, z, entityYaw, partialTicks);
        }
        else
        {
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
    }

    /**
     * Renderer factory
     */
    public static class FactoryActor implements IRenderFactory<EntityActor>
    {
        @Override
        public RenderTest createRenderFor(RenderManager manager)
        {
            return new RenderTest(manager, ModelCustom.MODELS.get("alex"), 0.5F);
        }
    }
}