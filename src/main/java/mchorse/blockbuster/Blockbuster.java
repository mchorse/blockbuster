package mchorse.blockbuster;

import mchorse.blockbuster.commands.CommandAction;
import mchorse.blockbuster.commands.CommandDirector;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

/**
 * Blockbuster's main entry
 *
 * This mod allows you to create machinimas in minecraft. Blockbuster provides
 * you with the most needed tools to create machinimas alone (without bunch of
 * complaining actors).
 *
 * This mod is possible thanks to the following code/examples/resources:
 * - Jabelar's and TGG's minecraft modding tutorials
 * - AnimalBikes and Mocap mods
 * - MinecraftByExample
 * - Ernio for helping with camera attributes sync + sharing with his own
 *   network abstract layer code
 * - diesieben07 for giving idea for actor skins
 * - Choonster for pointing out that processInteract triggers for each hand
 * - Lightwave for porting some of the code to 1.9.4
 * - NlL5 for testing, giving feedback and ideas for Blockbuster mod
 */
@Mod(modid = Blockbuster.MODID, name = Blockbuster.MODNAME, version = Blockbuster.VERSION, acceptedMinecraftVersions = "[1.9.4]")
public class Blockbuster
{
    /* Mod info */
    public static final String MODID = "blockbuster";
    public static final String MODNAME = "Blockbuster";
    public static final String VERSION = "1.3-pre";

    /* Items */
    public static Item playbackItem;
    public static Item registerItem;
    public static Item actorConfigItem;

    /* Blocks */
    public static Block directorBlock;
    public static Block directorBlockMap;

    /* Creative tabs */
    public static final CreativeTabs blockbusterTab = new CreativeTabs("blockbuster")
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

    @SidedProxy(clientSide = "mchorse.blockbuster.ClientProxy", serverSide = "mchorse.blockbuster.CommonProxy")
    public static CommonProxy proxy;

    /**
     * "Macro" for getting resource location for Blockbuster mod items,
     * entities, blocks, etc.
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
        event.registerServerCommand(new CommandAction());
        event.registerServerCommand(new CommandDirector());
    }
}
