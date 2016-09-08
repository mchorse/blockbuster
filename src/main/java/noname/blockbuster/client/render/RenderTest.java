package noname.blockbuster.client.render;

import java.util.Map;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import noname.blockbuster.Blockbuster;
import noname.blockbuster.ClientProxy;
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

    public RenderTest(RenderManager manager, float f)
    {
        super(manager, ModelCustom.MODELS.get("steve"), f);

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
        String model = !entity.model.isEmpty() ? entity.model : "";
        String path = model + "/" + entity.skin;

        ResourceLocation location = new ResourceLocation("blockbuster.actors", path);

        if (ClientProxy.actorPack.resourceExists(location))
        {
            return location;
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
        this.setupModel(entity);

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
     * Setup the model for actor instance
     */
    protected void setupModel(EntityActor entity)
    {
        Map<String, ModelCustom> models = ModelCustom.MODELS;

        String key = models.containsKey(entity.model) ? entity.model : "alex";
        String pose = entity.isSneaking() ? "sneaking" : (entity.isElytraFlying() ? "flying" : "standing");

        ModelCustom model = models.get(key);

        model.pose = model.model.poses.get(pose);
        this.mainModel = model;
    }

    /**
     * Make actor a little bit smaller (so he looked like steve, and not like a
     * giant weirdo).
     */
    protected void preRenderCallback(AbstractClientPlayer entitylivingbaseIn, float partialTickTime)
    {
        float f = 0.920F;
        GlStateManager.scale(f, f, f);
    }

    /**
     * Taken from RenderPlayer
     *
     * This code is primarily changes the angle of the actor while it's flying
     * elytra
     */
    @Override
    protected void rotateCorpse(EntityActor actor, float p_77043_2_, float p_77043_3_, float partialTicks)
    {
        if (actor.isElytraFlying())
        {
            super.rotateCorpse(actor, p_77043_2_, p_77043_3_, partialTicks);

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
        else
        {
            super.rotateCorpse(actor, p_77043_2_, p_77043_3_, partialTicks);
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
            return new RenderTest(manager, 0.5F);
        }
    }
}