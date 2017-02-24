package mchorse.blockbuster_pack.client.render;

import mchorse.blockbuster_pack.client.render.layers.LayerActorArmor;
import mchorse.blockbuster_pack.client.render.layers.LayerElytra;
import mchorse.blockbuster_pack.morphs.ActorMorph;
import mchorse.metamorph.api.EntityUtils;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.client.render.RenderCustomModel;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

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
    protected void rotateCorpse(EntityLivingBase entity, float pitch, float yaw, float partialTicks)
    {
        if (entity.isEntityAlive() && entity.isPlayerSleeping())
        {
            /* Nap time! */
            GlStateManager.rotate(((EntityPlayer) entity).getBedOrientationInDegrees(), 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(this.getDeathMaxRotation(entity), 0.0F, 0.0F, 1.0F);
            GlStateManager.rotate(270.0F, 0.0F, 1.0F, 0.0F);
        }
        else if (entity.isElytraFlying())
        {
            /* Elytra rotation */
            GlStateManager.rotate(180.0F - yaw, 0.0F, 1.0F, 0.0F);

            if (entity.deathTime > 0)
            {
                float f = (entity.deathTime + partialTicks - 1.0F) / 20.0F * 1.6F;
                f = MathHelper.sqrt_float(f);

                if (f > 1.0F)
                {
                    f = 1.0F;
                }

                GlStateManager.rotate(f * this.getDeathMaxRotation(entity), 0.0F, 0.0F, 1.0F);
            }

            float f = entity.getTicksElytraFlying() + partialTicks;
            float f1 = MathHelper.clamp_float(f * f / 100.0F, 0.0F, 1.0F);

            Vec3d vec3d = entity.getLook(partialTicks);

            double d0 = entity.motionX * entity.motionX + entity.motionZ * entity.motionZ;
            double d1 = vec3d.xCoord * vec3d.xCoord + vec3d.zCoord * vec3d.zCoord;

            GlStateManager.rotate(f1 * (-90.0F - entity.rotationPitch), 1.0F, 0.0F, 0.0F);

            if (d0 > 0.0D && d1 > 0.0D)
            {
                double d2 = (entity.motionX * vec3d.xCoord + entity.motionZ * vec3d.zCoord) / (Math.sqrt(d0) * Math.sqrt(d1));
                double d3 = entity.motionX * vec3d.zCoord - entity.motionZ * vec3d.xCoord;
                float angle = (float) (Math.signum(d3) * Math.acos(d2)) * 180.0F / (float) Math.PI;

                GlStateManager.rotate(angle, 0.0F, 1.0F, 0.0F);
            }
        }
        else
        {
            GlStateManager.rotate(180.0F - yaw, 0.0F, 1.0F, 0.0F);

            if (entity.deathTime > 0)
            {
                float f = (entity.deathTime + partialTicks - 1.0F) / 20.0F * 1.6F;
                f = MathHelper.sqrt_float(f);

                if (f > 1.0F)
                {
                    f = 1.0F;
                }

                GlStateManager.rotate(f * this.getDeathMaxRotation(entity), 0.0F, 0.0F, 1.0F);
            }
        }
    }
}