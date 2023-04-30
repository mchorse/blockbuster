package mchorse.blockbuster.client.render;

import mchorse.blockbuster.common.entity.ExpirableDummyEntity;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;

import javax.annotation.Nullable;

public class RenderExpirableDummy extends RenderLivingBase<ExpirableDummyEntity> {
    public RenderExpirableDummy(RenderManager renderManagerIn, ModelBase modelBaseIn, float shadowSizeIn) {
        super(renderManagerIn, modelBaseIn, shadowSizeIn);
    }

    @Override
    public void doRenderShadowAndFire(Entity entityIn, double x, double y, double z, float yaw, float partialTicks) {}

    @Override
    public boolean shouldRender(ExpirableDummyEntity livingEntity, ICamera camera, double camX, double camY, double camZ)
    {
        return false;
    }

    @Override
    public void doRender(ExpirableDummyEntity entity, double x, double y, double z, float entityYaw, float partialTicks) { }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(ExpirableDummyEntity entity) {
        return null;
    }

    public static class FactoryExpirableDummy implements IRenderFactory<ExpirableDummyEntity>
    {
        @Override
        public RenderExpirableDummy createRenderFor(RenderManager manager)
        {
            return new RenderExpirableDummy(manager, null, 0);
        }
    }
}
