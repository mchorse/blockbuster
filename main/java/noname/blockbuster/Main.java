package noname.blockbuster;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityList;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import noname.blockbuster.client.render.CameraRender;
import noname.blockbuster.common.CommonProxy;
import noname.blockbuster.entity.CameraEntity;
import noname.blockbuster.item.CameraItem;

/**
 * Blockbuster's main entry
 * 
 * This mod allows you to create machinimas in minecraft. Blockbuster provides you
 * with the most needed tools to create machinimas alone (with bunch of complaining
 * actors).
 */
@Mod(modid = Main.MODID, name=Main.MODNAME, version = Main.VERSION)
public class Main
{
	/* Mod name and version info */
    public static final String MODID = "blockbuster";
    public static final String MODNAME = "Blockbuster";
    public static final String VERSION = "1.0";
    
    public static int ID = 0;
    
    /* Items and blocks */
    public static Item camera;
    public static Item directorItem;
    public static Block directorBlock;
    
    /* Creative tabs */
    public static final CreativeTabs busterTab = new CreativeTabs("blockbusterTab") 
	{
		@Override
		public Item getTabIconItem() 
		{
			return Main.camera;
		} 
	};
    
	@SidedProxy(clientSide="noname.blockbuster.client.ClientProxy", serverSide="noname.blockbuster.common.CommonProxy")
	public static CommonProxy proxy;
	
	/* Event handling */
    @EventHandler
    public void preLoad(FMLPreInitializationEvent event)
    {
    	// Register camera, actors and props eggs
    	GameRegistry.register(camera = new CameraItem());
    	registerEntity(CameraEntity.class, "Camera");
    	
    	proxy.preLoad();
    }
    
    /**
     * Thanks to animal bikes mod for this wonderful example!
     * Kids, wanna learn how to mod minecraft? That's simple. Find mods for specific minecraft version
     * and decompile the .jar files with JD-GUI. Isn't that simple?
     */
    private void registerEntity(Class entity, String name)
    {
    	EntityRegistry.registerModEntity(entity, name, ID++, this, 40, 1, false);
    }
}