package mchorse.blockbuster.client.render;

import java.util.Map;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.client.gui.utils.GlStateManager;
import mchorse.blockbuster.client.model.ModelCustom;
import mchorse.blockbuster.common.ClientProxy;
import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.utils.EntityUtils;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

/**
 * Render actor class
 *
 * Render actor entities with custom swaggalicious models ?8|
 */
public class RenderActor extends RenderLiving
{
    /**
     * Default texture of the renderer
     */
    private static final ResourceLocation defaultTexture = new ResourceLocation(Blockbuster.MODID, "textures/entity/actor.png");
    private static final String defaultModel = "steve";

    /**
     * Initiate render actor with set of layers.
     *
     * - Layer elytra is responsible for rendering elytra on the back of the
     *   custom model
     * - Layer held item is responsible for rendering item that selected in
     *   hot bar and located in off hand slot for every limb that
     *   has "holding" property
     * - Layer biped armor is responsible for rendering armor on every limb
     *   that has "armor" property
     */
    public RenderActor(float f)
    {
        super(ModelCustom.MODELS.get(defaultModel), f);
    }

    /**
     * Use skin from resource pack or default one (if skin is empty or just
     * wasn't found by actor pack)
     */
    @Override
    protected ResourceLocation getEntityTexture(Entity entity)
    {
        EntityActor actor = (EntityActor) entity;
        ResourceLocation skin = actor.skin;

        if (skin != null)
        {
            boolean actors = skin.getResourceDomain().equals("blockbuster.actors");

            if (!actors || (actors && ClientProxy.actorPack.resourceExists(skin)))
            {
                return skin;
            }
        }

        return defaultTexture;
    }

    /**
     * Another important extension. Assign sneaking property, without it, actor
     * would look like an idiot who's clipping through the ground for a minute.
     *
     * Also, head rotation is interpolated inside of this method, another thing,
     * yeah previousYaw thing is pretty stupid (the renderer is one for all),
     * but it works...
     */
    @Override
    public void doRender(Entity entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        EntityActor actor = (EntityActor) entity;
        this.shadowOpaque = actor.invisible ? 0.0F : 1.0F;

        if (actor.invisible)
        {
            return;
        }

        this.setupModel(actor);

        if (this.mainModel != null)
        {
            super.doRender(entity, x, y, z, entityYaw, partialTicks);
        }
    }

    /**
     * Setup the model for actor instance.
     *
     * This method is responsible for picking the right model and pose based
     * on actor properties.
     */
    protected void setupModel(EntityActor entity)
    {
        Map<String, ModelCustom> models = ModelCustom.MODELS;

        String key = models.containsKey(entity.model) ? entity.model : defaultModel;
        String pose = EntityUtils.poseForEntity(entity);

        ModelCustom model = models.get(key);

        if (model != null)
        {
            model.pose = model.model.getPose(pose);

            this.mainModel = model;
        }
    }

    /**
     * Make actor a little bit smaller (so he looked like steve, and not like a
     * overgrown rodent).
     */
    @Override
    protected void preRenderCallback(EntityLivingBase actor, float partialTickTime)
    {
        float f = 0.935F;
        GlStateManager.scale(f, f, f);
    }
}