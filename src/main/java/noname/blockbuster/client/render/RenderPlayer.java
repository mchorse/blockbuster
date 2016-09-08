package noname.blockbuster.client.render;

import java.util.Map;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noname.blockbuster.Blockbuster;
import noname.blockbuster.ClientProxy;
import noname.blockbuster.client.model.ModelCustom;

/**
 * player renderer
 *
 * Renders player entities with swag
 */
@SideOnly(Side.CLIENT)
public class RenderPlayer extends RenderLivingBase<EntityPlayer>
{
    private static final ResourceLocation defaultTexture = new ResourceLocation(Blockbuster.MODID, "textures/entity/actor.png");

    /**
     * This thing gonna be responsible for storing the model for player's
     * rendering
     */
    public String model = "";

    /**
     * This thing is going to be responsible for storing the skin for player's
     * rendering
     */
    public String skin = "";

    public RenderPlayer(RenderManager renderManagerIn, float shadowSize)
    {
        super(renderManagerIn, null, shadowSize);

        this.addLayer(new LayerElytra(this));
        this.addLayer(new LayerHeldItem(this));
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityPlayer entity)
    {
        ResourceLocation location = new ResourceLocation("blockbuster.actors", this.model + "/" + this.skin);

        if (ClientProxy.actorPack.resourceExists(location))
        {
            return location;
        }

        return defaultTexture;
    }

    @Override
    public void doRender(EntityPlayer entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        this.setupModel(entity);

        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    /**
     * Setup the model for player instance.
     *
     * This method is responsible for picking the right model and pose based
     * on player properties.
     */
    protected void setupModel(EntityPlayer entity)
    {
        Map<String, ModelCustom> models = ModelCustom.MODELS;

        String pose = entity.isSneaking() ? "sneaking" : (entity.isElytraFlying() ? "flying" : "standing");

        ModelCustom model = models.get(this.model);

        model.pose = model.model.poses.get(pose);
        this.mainModel = model;
    }

    /**
     * Make player a little bit smaller (so he looked like steve, and not like a
     * overgrown rodent).
     */
    protected void preRenderCallback(AbstractClientPlayer entitylivingbaseIn, float partialTickTime)
    {
        float f = 0.920F;
        GlStateManager.scale(f, f, f);
    }

    /**
     * Taken from RenderPlayer
     *
     * This code is primarily changes the angle of the player while it's flying
     * an elytra. You know,
     */
    @Override
    protected void rotateCorpse(EntityPlayer player, float pitch, float yaw, float partialTicks)
    {
        super.rotateCorpse(player, pitch, yaw, partialTicks);

        if (player.isElytraFlying())
        {
            float f = player.getTicksElytraFlying() + partialTicks;
            float f1 = MathHelper.clamp_float(f * f / 100.0F, 0.0F, 1.0F);

            Vec3d vec3d = player.getLook(partialTicks);

            double d0 = player.motionX * player.motionX + player.motionZ * player.motionZ;
            double d1 = vec3d.xCoord * vec3d.xCoord + vec3d.zCoord * vec3d.zCoord;

            GlStateManager.rotate(f1 * (-90.0F - player.rotationPitch), 1.0F, 0.0F, 0.0F);

            if (d0 > 0.0D && d1 > 0.0D)
            {
                double d2 = (player.motionX * vec3d.xCoord + player.motionZ * vec3d.zCoord) / (Math.sqrt(d0) * Math.sqrt(d1));
                double d3 = player.motionX * vec3d.zCoord - player.motionZ * vec3d.xCoord;

                GlStateManager.rotate((float) (Math.signum(d3) * Math.acos(d2)) * 180.0F / (float) Math.PI, 0.0F, 1.0F, 0.0F);
            }
        }
    }
}