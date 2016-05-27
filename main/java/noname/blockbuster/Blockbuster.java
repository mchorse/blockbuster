package noname.blockbuster;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import noname.blockbuster.entity.ActorEntity;
import noname.blockbuster.entity.CameraEntity;
import noname.blockbuster.item.CameraConfigItem;
import noname.blockbuster.item.CameraItem;
import noname.blockbuster.test.CommandPlay;
import noname.blockbuster.test.CommandRecord;
import noname.blockbuster.test.MocapEventHandler;

/**
 * Blockbuster's main entry
 * 
 * This mod allows you to create machinimas in minecraft. Blockbuster provides you
 * with the most needed tools to create machinimas alone (with bunch of complaining
 * actors).
 * 
 * This mod is possible thanks to following code/examples/resources:
 * - Jabelar's forge tutorials
 * - AnimalBikes and Mocap mods
 * - MinecraftByExample
 */
@Mod(modid = Blockbuster.MODID, name = Blockbuster.MODNAME, version = Blockbuster.VERSION, acceptedMinecraftVersions = "[1.9]")
public class Blockbuster
{
	/* Mod name and version info */
    public static final String MODID = "blockbuster";
    public static final String MODNAME = "Blockbuster";
    public static final String VERSION = "1.0";

    
    /* Items and blocks */
    public static int ID = 0;
    
    public static Item cameraItem;
    public static Item cameraConfigItem;
    public static Item directorItem;
    public static Block directorBlock;
    
    /* Creative tabs */
    public static final CreativeTabs busterTab = new CreativeTabs("blockbusterTab") 
	{
		@Override
		public Item getTabIconItem() 
		{
			return Blockbuster.cameraItem;
		} 
	};
	
	@SidedProxy(clientSide="noname.blockbuster.ClientProxy", serverSide="noname.blockbuster.CommonProxy")
	public static CommonProxy proxy;
	
	/**
	 * "Macro" for getting id for Blockbuster mod items/entities/blocks/etc. 
	 */
	public static String path(String path)
	{
		return MODID + ":" + path;
	}
	
	/**
	 * Register all items, blocks and entities
	 */
    @EventHandler
    public void preLoad(FMLPreInitializationEvent event)
    {
    	registerItem(cameraItem = new CameraItem());
    	registerItem(cameraConfigItem = new CameraConfigItem());
    	
    	registerEntity(CameraEntity.class, "Camera");
    	registerEntity(ActorEntity.class, "Actor");
    	
    	proxy.preLoad();
    }
    
    /**
     * Register event handler
     */
    @EventHandler
    public void load(FMLInitializationEvent event)
    {
    	MinecraftForge.EVENT_BUS.register(new MocapEventHandler());
    }
    
    /**
     * Register server commands
     */
    @EventHandler
    public void serverStartup(FMLServerStartingEvent event)
    {
    	event.registerServerCommand(new CommandRecord());
    	event.registerServerCommand(new CommandPlay());
    }
    
    /**
     * Register an item with Forge's game registry
     */
    private void registerItem(Item item)
    {
    	GameRegistry.register(item);
    }
    
    /**
     * Thanks to animal bikes mod for this wonderful example!
     * Kids, wanna learn how to mod minecraft with forge? That's simple. Find mods for specific minecraft version
     * and decompile the .jar files with JD-GUI. Isn't that simple?
     */
    private void registerEntity(Class entity, String name)
    {
    	EntityRegistry.registerModEntity(entity, name, ID++, this, 40, 1, false);
    }
}