package noname.blockbuster;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import noname.blockbuster.recording.CommandPlay;
import noname.blockbuster.recording.CommandRecord;

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
@Mod(
	modid = Blockbuster.MODID, 
	name = Blockbuster.MODNAME, 
	version = Blockbuster.VERSION, 
	acceptedMinecraftVersions = "[1.9]"
)
public class Blockbuster
{
	/* Mod name and version info */
    public static final String MODID = "blockbuster";
    public static final String MODNAME = "Blockbuster";
    public static final String VERSION = "1.0";
    
    /* Items */
    public static Item cameraItem;
    public static Item cameraConfigItem;
    public static Item recordItem;
    public static Item registerItem;
    
    /* Blocks */
    public static Block directorBlock;
    
    /* Creative tabs */
    public static final CreativeTabs blockbusterTab = new CreativeTabs("blockbusterTab") 
	{
		@Override
		public Item getTabIconItem() 
		{
			return Item.getItemFromBlock(Blockbuster.directorBlock);
		} 
	};
	
	@Mod.Instance
	public static Blockbuster instance;
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
    	proxy.preLoad();
    }
    
    /**
     * Register event handler
     */
    @EventHandler
    public void load(FMLInitializationEvent event)
    {
    	proxy.load();
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
}