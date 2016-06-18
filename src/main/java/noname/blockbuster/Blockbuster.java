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
import noname.blockbuster.api.Comment;
import noname.blockbuster.recording.CommandPlay;
import noname.blockbuster.recording.CommandPlayDirector;
import noname.blockbuster.recording.CommandRecord;

/**
 * Blockbuster's main entry
 *
 * This mod allows you to create machinimas in minecraft. Blockbuster provides
 * you with the most needed tools to create machinimas alone (without bunch of
 * complaining actors).
 *
 * This mod is possible thanks to the following code/examples/resources:
 * - Jabelar's minecraft modding tutorials
 * - AnimalBikes and Mocap mods
 * - MinecraftByExample
 * - Ernio for helping with camera attributes sync + sharing with his own
 *   network abstract layer
 * - diesieben07 for giving idea for actor skins
 * - Choonster for pointing out that processInteract triggers for each hand
 *
 * ToDo list:
 * - Add GUI for director tile entity
 * - Make camera's model prettier
 */
@Comment(comment = "This mod uses portion of Mocap mod's recording code")
@Mod(modid = Blockbuster.MODID, name = Blockbuster.MODNAME, version = Blockbuster.VERSION, acceptedMinecraftVersions = "[1.9]")
public class Blockbuster
{
    /* Mod name and version info */
    public static final String MODID = "blockbuster";
    public static final String MODNAME = "Blockbuster";
    public static final String VERSION = "1.0";

    /* Items */
    public static Item cameraItem;
    public static Item cameraConfigItem;
    public static Item playbackItem;
    public static Item registerItem;
    public static Item skinManagerItem;

    /* Blocks */
    public static Block directorBlock;
    public static Block directorBlockMap;

    /* Creative tabs */
    public static final CreativeTabs blockbusterTab = new CreativeTabs("blockbusterTab")
    {
        @Override
        public Item getTabIconItem()
        {
            return Item.getItemFromBlock(Blockbuster.directorBlock);
        }
    };

    /* Forge stuff */
    @Mod.Instance
    public static Blockbuster instance;

    @SidedProxy(clientSide = "noname.blockbuster.ClientProxy", serverSide = "noname.blockbuster.CommonProxy")
    public static CommonProxy proxy;

    /**
     * "Macro" for getting resource location for Blockbuster mod items,
     * entities, blocks, or etc.
     */
    public static String path(String path)
    {
        return MODID + ":" + path;
    }

    @EventHandler
    public void registerAllItemsBlocksAndEntities(FMLPreInitializationEvent event)
    {
        proxy.preLoad(event);
    }

    @EventHandler
    public void registerEventHandlerAndInjectActorPack(FMLInitializationEvent event)
    {
        proxy.load(event);
    }

    @EventHandler
    public void registerServerCommands(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new CommandRecord());
        event.registerServerCommand(new CommandPlay());
        event.registerServerCommand(new CommandPlayDirector());
    }
}