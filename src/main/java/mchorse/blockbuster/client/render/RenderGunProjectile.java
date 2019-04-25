package mchorse.blockbuster.client.render;

import mchorse.blockbuster.common.entity.EntityGunProjectile;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class RenderGunProjectile extends Render<EntityGunProjectile>
{
    protected RenderGunProjectile(RenderManager renderManager)
    {
        super(renderManager);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityGunProjectile entity)
    {
        return null;
    }

    @Override
    public void doRender(EntityGunProjectile entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        if (entity.props != null)
        {
            GlStateManager.pushMatrix();
            GlStateManager.translate(x, y, z);
            GlStateManager.rotate(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(-(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks) + 90, 1.0F, 0.0F, 0.0F);

            entity.props.renderProjectile(partialTicks);

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