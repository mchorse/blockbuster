package noname.blockbuster.client;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noname.blockbuster.Main;
import noname.blockbuster.client.render.CameraRender;
import noname.blockbuster.common.CommonProxy;
import noname.blockbuster.entity.CameraEntity;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy 
{
	@Override
	public void preLoad()
	{
		registerItemModel(Main.cameraItem, "blockbuster:cameraItem");
		registerItemModel(Main.cameraConfigItem, "blockbuster:cameraConfigItem");
		
		RenderingRegistry.registerEntityRenderingHandler(CameraEntity.class, new CameraRender.CameraFactory());
	}
	
	/**
	 * Register item model
	 */
	protected void registerItemModel(Item item, String path)
	{
		ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(path, "inventory"));
	}
}
