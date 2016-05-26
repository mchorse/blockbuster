package noname.blockbuster.client.render;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import noname.blockbuster.Blockbuster;
import noname.blockbuster.entity.ActorEntity;

public class ActorRender extends RenderBiped<ActorEntity>
{
	private static final ResourceLocation resource = new ResourceLocation(Blockbuster.MODID, "textures/entity/actor.png");
	
	public ActorRender(RenderManager renderManagerIn, ModelBiped modelBipedIn, float shadowSize) 
	{
		super(renderManagerIn, modelBipedIn, shadowSize);
	}
	
	@Override
	public void doRender(ActorEntity entity, double x, double y, double z, float entityYaw, float partialTicks) 
	{
		if (!entity.isRecording) 
		{
			super.doRender(entity, x, y, z, entityYaw, partialTicks);
		}
	}

	@Override
	protected ResourceLocation getEntityTexture(ActorEntity entity) 
	{
		return resource;
	}
	
	public static class ActorFactory implements IRenderFactory
	{
		@Override
		public Render createRenderFor(RenderManager manager) 
		{
			return new ActorRender(manager, new ModelBiped(), 1.0F);
		}
	}
}
