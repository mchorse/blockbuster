package noname.blockbuster;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noname.blockbuster.client.render.ActorRender;
import noname.blockbuster.client.render.CameraRender;
import noname.blockbuster.entity.ActorEntity;
import noname.blockbuster.entity.CameraEntity;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy 
{
	@Override
	public void preLoad()
	{
		registerItemModel(Blockbuster.cameraItem, Blockbuster.path("cameraItem"));
		registerItemModel(Blockbuster.cameraConfigItem, Blockbuster.path("cameraConfigItem"));
		
		registerEntityRender(CameraEntity.class, new CameraRender.CameraFactory());
		registerEntityRender(ActorEntity.class, new ActorRender.ActorFactory());
	}
	
	/**
	 * Register item model
	 */
	protected void registerItemModel(Item item, String path)
	{
		ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(path, "inventory"));
	}
	
	/**
	 * Register entity renderer 
	 */
	protected void registerEntityRender(Class eclass, IRenderFactory factory)
	{
		RenderingRegistry.registerEntityRenderingHandler(eclass, factory);
	}
}
