package mchorse.blockbuster.client.render;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.common.entity.EntityGunProjectile;
import mchorse.mclib.utils.Interpolations;
import mchorse.mclib.utils.MatrixUtils;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;

/**
 * Gun projectile renderer
 * 
 * This bad boy is responsible for rendering the projectile shot from 
 * the gun.
 */
public class RenderGunProjectile extends Render<EntityGunProjectile>
{
    protected RenderGunProjectile(RenderManager renderManager)
    {
        super(renderManager);
    }

    @Override
    public boolean shouldRender(EntityGunProjectile livingEntity, ICamera camera, double camX, double camY, double camZ)
    {
        if (Blockbuster.actorAlwaysRender.get())
        {
            return true;
        }

        return super.shouldRender(livingEntity, camera, camX, camY, camZ);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityGunProjectile entity)
    {
        return null;
    }

    @Override
    public void doRender(EntityGunProjectile entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        AbstractMorph morph = entity.morph.get();

        if (entity.props != null && morph != null)
        {
            int length = entity.props.lifeSpan;
            float timer = entity.timer + partialTicks;
            float scale = Interpolations.envelope(timer > length ? length : timer, 0, entity.props.fadeIn, length - entity.props.fadeOut, length);

            /* A small scale factor to avoid Z fighting */
            scale += (entity.getEntityId() % 100) / 10000F;

            GlStateManager.pushMatrix();
            GlStateManager.translate(x, y, z);

            boolean captured = MatrixUtils.captureMatrix();

            GlStateManager.scale(scale, scale, scale);

            if (entity.props.yaw) GlStateManager.rotate(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks, 0.0F, 1.0F, 0.0F);
            if (entity.props.pitch) GlStateManager.rotate(-(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks) + 90, 1.0F, 0.0F, 0.0F);

            entity.props.projectileTransform.transform();
            entity.props.createEntity();
            morph.render(entity.props.getEntity(entity), 0, 0, 0, 0, partialTicks);

            if (captured)
            {
                MatrixUtils.releaseMatrix();
            }

            GlStateManager.popMatrix();
        }
    }

    public static class FactoryGunProjectile implements IRenderFactory<EntityGunProjectile>
    {
        @Override
        public Render<? super EntityGunProjectile> createRenderFor(RenderManager manager)
        {
            return new RenderGunProjectile(manager);
        }
    }
}