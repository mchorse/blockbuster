package mchorse.blockbuster;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Logger;

import mchorse.blockbuster.commands.CommandAction;
import mchorse.blockbuster.commands.CommandDirector;
import mchorse.blockbuster.commands.CommandOnHead;
import mchorse.blockbuster.commands.CommandRecord;
import mchorse.blockbuster.commands.CommandSpectate;
import mchorse.blockbuster.common.tileentity.TileEntityDirector;
import mchorse.blockbuster_pack.morphs.StructureMorph;
import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;

/**
 * <p>Blockbuster's main entry</p>
 *
 * <p>
 * This mod allows you to create machinimas in minecraft. Blockbuster provides
 * you with the most needed tools to create machinimas alone (without bunch of
 * complaining body actors).
 * </p>
 *
 * <p>
 * This mod is possible thanks to the following code/examples/resources/people:
 * </p>
 *
 * <ul>
 *     <li>Jabelar's and TGG's minecraft modding tutorials</li>
 *     <li>AnimalBikes and Mocap mods (EchebKeso)</li>
 *     <li>MinecraftByExample</li>
 *     <li>Ernio for helping with camera attributes sync, sharing with his own
 *         network abstract layer code, and fixing the code so it would work on
 *         dedicated server</li>
 *     <li>diesieben07 for giving idea for actor skins</li>
 *     <li>Choonster for pointing out that processInteract triggers for each
 *         hand + TestMod3 config example</li>
 *     <li>Lightwave for porting some of the code to 1.9.4</li>
 *     <li>NlL5 for a lot of testing, giving lots of feedback and ideas for
 *         Blockbuster mod</li>
 *     <li>daipenger for giving me consultation on how to make cameras and
 *         actors frame-based</li>
 *      <li>TheImaginationCrafter for suggesting the OBJ feature which made
 *          Blockbuster super popular and also more customizable (in terms 
 *          of custom models)</li>
 * </ul>
 */
@Mod(modid = Blockbuster.MODID, name = Blockbuster.MODNAME, version = Blockbuster.VERSION, guiFactory = Blockbuster.GUI_FACTORY, dependencies = "required-after:metamorph@[%METAMORPH%,);required-after:mclib@[%MCLIB%,)", updateJSON = "https://raw.githubusercontent.com/mchorse/blockbuster/master/version.json")
public class Blockbuster
{
    /* Mod info */
    public static final String MODID = "blockbuster";
    public static final String MODNAME = "Blockbuster";
    public static final String VERSION = "%VERSION%";
    public static final String GUI_FACTORY = "mchorse.blockbuster.config.gui.GuiFactory";

    @SideOnly(Side.CLIENT)
    public static String WIKI_URL()
    {
        return langOrDefault("blockbuster.gui.links.wiki", "https://github.com/mchorse/blockbuster/wiki/");
    }

    @SideOnly(Side.CLIENT)
    public static String DISCORD_URL()
    {
        return langOrDefault("blockbuster.gui.links.discord", "https://discord.gg/qfxrqUF");
    }

    @SideOnly(Side.CLIENT)
    public static String CHANNEL_URL()
    {
        return langOrDefault("blockbuster.gui.links.channel", "https://www.youtube.com/c/McHorsesMods");
    }

    @SideOnly(Side.CLIENT)
    public static String TWITTER_URL()
    {
        return langOrDefault("blockbuster.gui.links.twitter", "https://twitter.com/McHorsy");
    }

    @SideOnly(Side.CLIENT)
    public static String TUTORIAL_URL()
    {
        return langOrDefault("blockbuster.gui.links.tutorial", "https://www.youtube.com/watch?v=vo8fquY-TUM&list=PLLnllO8nnzE-LIHZiaq0-ZAZiDO82K1I9&index=2");
    }

    @SideOnly(Side.CLIENT)
    public static String langOrDefault(String lang, String orDefault)
    {
        String result = I18n.format(lang);

        return result.equals(lang) ? orDefault : result;
    }

    /* Proxies */
    public static final String CLIENT_PROXY = "mchorse.blockbuster.ClientProxy";
    public static final String SERVER_PROXY = "mchorse.blockbuster.CommonProxy";

    /* Creative tab */
    public static CreativeTabs blockbusterTab;

    /* Items */
    public static Item playbackItem;
    public static Item registerItem;
    public static Item actorConfigItem;
    public static Item modelBlockItem;
    public static Item gunItem;

    /* Blocks */
    public static Block directorBlock;
    public static Block modelBlock;
    public static Block greenBlock;

    /* Forge stuff */
    @Mod.Instance
    public static Blockbuster instance;

    @SidedProxy(clientSide = CLIENT_PROXY, serverSide = SERVER_PROXY)
    public static CommonProxy proxy;

    public static Logger LOGGER;

    /**
     * "Macro" for getting resource location for Blockbuster mod items,
     * entities, blocks, etc.
     */
    public static String path(String path)
    {
        return MODID + ":" + path;
    }

    /**
     * Reloads server side models 
     */
    public static void reloadServerModels(boolean force)
    {
        String path = DimensionManager.getCurrentSaveRootDirectory() + "/blockbuster/models";

        proxy.models.pack = proxy.getPack();
        proxy.models.pack.addFolder(path);
        proxy.loadModels(proxy.models.pack, force);
    }

    @EventHandler
    public void preLoad(FMLPreInitializationEvent event)
    {
        LOGGER = event.getModLog();

        proxy.preLoad(event);
    }

    @EventHandler
    public void load(FMLInitializationEvent event)
    {
        proxy.load(event);
    }

    @EventHandler
    public void postLoad(FMLPostInitializationEvent event)
    {
        proxy.postLoad(event);
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event)
    {
        TileEntityDirector.playing = 0;
        StructureMorph.STRUCTURE_CACHE.clear();

        /* Register commands */
        event.registerServerCommand(new CommandAction());
        event.registerServerCommand(new CommandDirector());
        event.registerServerCommand(new CommandRecord());
        event.registerServerCommand(new CommandSpectate());
        event.registerServerCommand(new CommandOnHead());
    }

    @EventHandler
    public void serverStopping(FMLServerStoppingEvent event)
    {
        CommonProxy.manager.reset();
        CommonProxy.damage.reset();
        CommonProxy.scenes.reset();
    }
}
