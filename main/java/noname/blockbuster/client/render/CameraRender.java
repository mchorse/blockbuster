package noname.blockbuster.client.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noname.blockbuster.Main;
import noname.blockbuster.client.model.CameraModel;
import noname.blockbuster.entity.CameraEntity;

@SideOnly(Side.CLIENT)
public class CameraRender extends RenderLiving 
{
	private static final ResourceLocation resource = new ResourceLocation(Main.MODID, "textures/entity/camera.png");
	
	public CameraRender(RenderManager rendermanagerIn, ModelBase modelbaseIn, float shadowsizeIn) 
	{
		super(rendermanagerIn, modelbaseIn, shadowsizeIn);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) 
	{
		return resource;
	}
	
	public void doRender(EntityLiving entity, double x, double y, double z, float entityYaw, float partialTicks) 
	{
		CameraEntity camera = (CameraEntity)entity;
		
		if (camera.isVisible && !camera.isBeingRidden()) 
		{
			super.doRender(entity, x, y, z, entityYaw, partialTicks);
		}
	}
	
	/**
	 * Renderer's factory
	 */
	public static class CameraFactory implements IRenderFactory<CameraEntity>
	{
		@Override
		public Render<? super CameraEntity> createRenderFor(RenderManager manager) 
		{
			return new CameraRender(manager, new CameraModel(), 1);
		}
	}
}
