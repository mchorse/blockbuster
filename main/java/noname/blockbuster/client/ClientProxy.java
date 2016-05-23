package noname.blockbuster.client;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import noname.blockbuster.Main;
import noname.blockbuster.client.render.CameraRender;
import noname.blockbuster.common.CommonProxy;
import noname.blockbuster.entity.CameraEntity;

public class ClientProxy extends CommonProxy 
{
	@Override
	public void preLoad()
	{
		ModelLoader.setCustomModelResourceLocation(Main.camera, 0, new ModelResourceLocation("blockbuster:cameraItem", "inventory"));
		RenderingRegistry.registerEntityRenderingHandler(CameraEntity.class, new CameraRender.CameraFactory());
	}
}
