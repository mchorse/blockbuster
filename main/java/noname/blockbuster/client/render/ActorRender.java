package noname.blockbuster.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import noname.blockbuster.Blockbuster;
import noname.blockbuster.ClientProxy;
import noname.blockbuster.entity.ActorEntity;

public class ActorRender extends RenderBiped<ActorEntity>
{
    private static final ResourceLocation defaultTexture = new ResourceLocation(Blockbuster.MODID, "textures/entity/actor.png");

    public ActorRender(RenderManager renderManagerIn, ModelBiped modelBipedIn, float shadowSize)
    {
        super(renderManagerIn, modelBipedIn, shadowSize);

        this.addLayer(new LayerBipedArmor(this));
    }

    @Override
    public void doRender(ActorEntity entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        this.modelBipedMain.isSneak = entity.isSneaking();

        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    @Override
    protected boolean canRenderName(ActorEntity entity)
    {
        return super.canRenderName(entity) && Minecraft.getMinecraft().currentScreen == null;
    }

    protected void preRenderCallback(AbstractClientPlayer entitylivingbaseIn, float partialTickTime)
    {
        float f = 0.920F;
        GlStateManager.scale(f, f, f);
    }

    /**
     * Use skin from resource pack or default one (if skin is empty or just
     * wasn't found by actor pack)
     */
    @Override
    protected ResourceLocation getEntityTexture(ActorEntity entity)
    {
        ResourceLocation location = new ResourceLocation("blockbuster.actors", entity.skin);

        if (ClientProxy.actorPack.resourceExists(location))
        {
            return location;
        }

        return defaultTexture;
    }

    public static class ActorFactory implements IRenderFactory
    {
        @Override
        public Render createRenderFor(RenderManager manager)
        {
            return new ActorRender(manager, new ModelBiped(), 0.5F);
        }
    }
}
