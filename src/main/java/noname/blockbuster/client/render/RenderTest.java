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
 * Render actor test clas
 *
 * This is a temporary class for rendering an actor
 */
public class RenderTest extends RenderLiving<EntityActor>
{
    private static final ResourceLocation defaultTexture = new ResourceLocation(Blockbuster.MODID, "textures/entity/actor.png");

    public RenderTest(RenderManager manager, ModelBase model, float f)
    {
        super(manager, model, f);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityActor entity)
    {
        return defaultTexture;
    }

    /**
     * Renderer factory
     */
    public static class FactoryActor implements IRenderFactory<EntityActor>
    {
        @Override
        public RenderTest createRenderFor(RenderManager manager)
        {
            return new RenderTest(manager, ModelCustom.MODELS.get("steve"), 0.5F);
        }
    }
}
