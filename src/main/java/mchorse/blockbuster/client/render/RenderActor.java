package mchorse.blockbuster.client.render;

import java.util.Map;

import mchorse.blockbuster.client.model.ModelCustom;
import mchorse.blockbuster.client.render.layers.LayerElytra;
import mchorse.blockbuster.client.render.layers.LayerHeldItem;
import mchorse.blockbuster.common.Blockbuster;
import mchorse.blockbuster.common.ClientProxy;
import mchorse.blockbuster.common.entity.EntityActor;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
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
    public RenderActor(RenderManager manager, float f)
    {
        super(manager, ModelCustom.MODELS.get(defaultModel), f);

        this.addLayer(new LayerElytra(this));
        this.addLayer(new LayerHeldItem(this));
        this.addLayer(new LayerBipedArmor(this));
    }

    /**
     * Use skin from resource pack or default one (if skin is empty or just
     * wasn't found by actor pack)
     */
    @Override
    protected ResourceLocation getEntityTexture(EntityActor entity)
    {
        ResourceLocation skin = entity.skin;

        if (skin != null)
        {
            if (skin.getResourceDomain().equals("blockbuster.actors") && ClientProxy.actorPack.resourceExists(skin))
            {
                return skin;
            }
            else
            {
                return skin;
            }
        }

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

    /**
     * Another important extension. Assign sneaking property, without it, actor
     * would look like an idiot who's clipping through the ground for a minute.
     *
     * Also, head rotation is interpolated inside of this method, another thing,
     * yeah previousYaw thing is pretty stupid (the renderer is one for all),
     * but it works...
     */
    @Override
    public void doRender(EntityActor entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        if (entity.invisible)
        {
            return;
        }

        this.setupModel(entity);

        super.doRender(entity, x, y, z, entityYaw, partialTicks);
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
        String pose = entity.isSneaking() ? "sneaking" : (entity.isElytraFlying() ? "flying" : "standing");

        ModelCustom model = models.get(key);

        model.pose = model.model.poses.get(pose);
        this.mainModel = model;
    }

    /**
     * Make actor a little bit smaller (so he looked like steve, and not like a
     * overgrown rodent).
     */
    @Override
    protected void preRenderCallback(EntityActor actor, float partialTickTime)
    {
        float f = 0.935F;
        GlStateManager.scale(f, f, f);
    }

    /**
     * Taken from RenderPlayer
     *
     * This code is primarily changes the angle of the actor while it's flying
     * an elytra. You know,
     */
    @Override
    protected void rotateCorpse(EntityActor actor, float pitch, float yaw, float partialTicks)
    {
        super.rotateCorpse(actor, pitch, yaw, partialTicks);

        if (actor.isElytraFlying())
        {
            float f = actor.getTicksElytraFlying() + partialTicks;
            float f1 = MathHelper.clamp_float(f * f / 100.0F, 0.0F, 1.0F);

            Vec3d vec3d = actor.getLook(partialTicks);

            double d0 = actor.motionX * actor.motionX + actor.motionZ * actor.motionZ;
            double d1 = vec3d.xCoord * vec3d.xCoord + vec3d.zCoord * vec3d.zCoord;

            GlStateManager.rotate(f1 * (-90.0F - actor.rotationPitch), 1.0F, 0.0F, 0.0F);

            if (d0 > 0.0D && d1 > 0.0D)
            {
                double d2 = (actor.motionX * vec3d.xCoord + actor.motionZ * vec3d.zCoord) / (Math.sqrt(d0) * Math.sqrt(d1));
                double d3 = actor.motionX * vec3d.zCoord - actor.motionZ * vec3d.xCoord;

                GlStateManager.rotate((float) (Math.signum(d3) * Math.acos(d2)) * 180.0F / (float) Math.PI, 0.0F, 1.0F, 0.0F);
            }
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