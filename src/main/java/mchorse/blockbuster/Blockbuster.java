package mchorse.blockbuster;

import mchorse.blockbuster.commands.CommandAction;
import mchorse.blockbuster.commands.CommandDirector;
import mchorse.blockbuster.commands.CommandOnHead;
import mchorse.blockbuster.commands.CommandRecord;
import mchorse.blockbuster.commands.CommandScene;
import mchorse.blockbuster.commands.CommandSpectate;
import mchorse.blockbuster.common.tileentity.TileEntityDirector;
import mchorse.blockbuster.utils.mclib.ValueAudioButtons;
import mchorse.blockbuster.utils.mclib.ValueMainButtons;
import mchorse.blockbuster_pack.morphs.StructureMorph;
import mchorse.mclib.McLib;
import mchorse.mclib.config.ConfigBuilder;
import mchorse.mclib.config.values.ValueBoolean;
import mchorse.mclib.config.values.ValueFloat;
import mchorse.mclib.config.values.ValueInt;
import mchorse.mclib.config.values.ValueString;
import mchorse.mclib.events.RegisterConfigEvent;
import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Logger;

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
 *     <li>TheImaginationCrafter for suggesting the OBJ feature which made
 *          Blockbuster super popular and also more customizable (in terms 
 *          of custom models)</li>
 * </ul>
 */
@Mod(modid = Blockbuster.MOD_ID, name = Blockbuster.MODNAME, version = Blockbuster.VERSION, dependencies = "required-after:metamorph@[%METAMORPH%,);required-after:mclib@[%MCLIB%,);required-after:forge@[14.23.2.2638,)", updateJSON = "https://raw.githubusercontent.com/mchorse/blockbuster/1.12/version.json")
public class Blockbuster
{
    /* Mod info */
    public static final String MOD_ID = "blockbuster";
    public static final String MODNAME = "Blockbuster";
    public static final String VERSION = "%VERSION%";

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
    public static Block dimGreenBlock;

    /* Forge stuff */
    @Mod.Instance
    public static Blockbuster instance;

    @SidedProxy(clientSide = CLIENT_PROXY, serverSide = SERVER_PROXY)
    public static CommonProxy proxy;

    public static Logger LOGGER;

    /* Configuration */
    public static ValueBoolean generalFirstTime;
    public static ValueBoolean disableTPPlaybackButton;
    public static ValueBoolean debugPlaybackTicks;
    public static ValueBoolean chromaSky;
    public static ValueInt chromaSkyColor;
    public static ValueBoolean syncedURLTextureDownload;
    public static ValueBoolean addUtilityBlocks;

    public static ValueBoolean modelBlockDisableRendering;
    public static ValueBoolean modelBlockDisableItemRendering;
    public static ValueBoolean modelBlockRestore;

    public static ValueFloat recordingCountdown;
    public static ValueInt recordingDelay;
    public static ValueInt recordUnloadTime;
    public static ValueBoolean recordUnload;
    public static ValueInt recordSyncRate;
    public static ValueBoolean recordAttackOnSwipe;
    public static ValueBoolean recordCommands;
    public static ValueString recordChatPrefix;

    public static ValueBoolean actorFallDamage;
    public static ValueInt actorTrackingRange;
    public static ValueInt actorRenderingRange;
    public static ValueBoolean actorAlwaysRender;
    public static ValueBoolean actorAlwaysRenderNames;
    public static ValueBoolean actorSwishSwipe;
    public static ValueBoolean actorFixY;
    public static ValueBoolean actorDisableRiding;
    public static ValueBoolean actorPlaybackBodyYaw;

    public static ValueBoolean damageControl;
    public static ValueInt damageControlDistance;

    public static ValueString modelFolderPath;

    public static ValueBoolean snowstormDepthSorting;

    public static ValueInt audioWaveformDensity;
    public static ValueFloat audioWaveformWidth;
    public static ValueInt audioWaveformHeight;
    public static ValueBoolean audioWaveformFilename;

    /**
     * "Macro" for getting resource location for Blockbuster mod items,
     * entities, blocks, etc.
     */
    public static String path(String path)
    {
        return MOD_ID + ":" + path;
    }

    /**
     * Reloads server side models 
     */
    public static void reloadServerModels(boolean force)
    {
        proxy.loadModels(force);
    }

    @SubscribeEvent
    public void onConfigRegister(RegisterConfigEvent event)
    {
        ConfigBuilder builder = event.createBuilder(MOD_ID);

        /* General */
        builder.category("general").register(new ValueMainButtons("buttons"));

        generalFirstTime = builder.getBoolean("show_first_time_modal", true);
        disableTPPlaybackButton = builder.getBoolean("disable_teleport_playback_button", false);
        debugPlaybackTicks = builder.getBoolean("debug_playback_ticks", false);
        chromaSky = builder.getBoolean("green_screen_sky", false);
        chromaSkyColor = builder.getInt("green_screen_sky_color", 0xff00ff00).colorAlpha();
        syncedURLTextureDownload = builder.getBoolean("url_skins_sync_download", true);
        addUtilityBlocks = builder.getBoolean("add_utility_blocks", false);

        /* Model block */
        modelBlockDisableRendering = builder.category("model_block").getBoolean("model_block_disable_rendering", false);
        modelBlockDisableItemRendering = builder.getBoolean("model_block_disable_item_rendering", false);
        modelBlockRestore = builder.getBoolean("restore", false);

        /* Recording */
        recordingCountdown = builder.category("recording").getFloat("recording_countdown", 1.5F, 0, 10);
        recordingDelay = builder.getInt("recording_delay", 1, 1, 10);
        recordUnloadTime = builder.getInt("record_unload_time", 2400, 600, 72000);
        recordUnload = builder.getBoolean("record_unload", true);
        recordSyncRate = builder.getInt("record_sync_rate", 6, 1, 30);
        recordAttackOnSwipe = builder.getBoolean("record_attack_on_swipe", false);
        recordCommands = builder.getBoolean("record_commands", true);
        recordChatPrefix = builder.getString("record_chat_prefix", "");

        /* Actor */
        actorFallDamage = builder.category("actor").getBoolean("actor_fall_damage", true);
        actorTrackingRange = builder.getInt("actor_tracking_range", 256, 64, 1024);
        actorRenderingRange = builder.getInt("actor_rendering_range", 256, 64, 1024);
        actorAlwaysRender = builder.getBoolean("actor_always_render", false);
        actorAlwaysRenderNames = builder.getBoolean("actor_always_render_names", false);
        actorSwishSwipe = builder.getBoolean("actor_swish_swipe", false);
        actorFixY = builder.getBoolean("actor_y", false);
        actorDisableRiding = builder.getBoolean("actor_disable_riding", false);
        actorPlaybackBodyYaw = builder.getBoolean("actor_playback_body_yaw", true);

        /* Damage control */
        damageControl = builder.category("damage_control").getBoolean("damage_control", true);
        damageControlDistance = builder.getInt("damage_control_distance", 64, 1, 1024);

        /* Model Folder */
        modelFolderPath = builder.category("model_folders").getString("path", "");

        /* Snowstorm */
        snowstormDepthSorting = builder.category("snowstorm").getBoolean("depth_sorting", false);

        /* Audio */
        builder.category("audio").register(new ValueAudioButtons("buttons"));

        audioWaveformDensity = builder.getInt("waveform_density", 20, 10, 100);
        audioWaveformWidth = builder.getFloat("waveform_width", 0.5F, 0F, 1F);
        audioWaveformHeight = builder.getInt("waveform_height", 24, 10, 40);
        audioWaveformFilename = builder.getBoolean("waveform_filename", true);

        event.modules.add(builder.build());
    }

    @EventHandler
    public void preLoad(FMLPreInitializationEvent event)
    {
        LOGGER = event.getModLog();
        McLib.EVENT_BUS.register(this);

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
        proxy.loadModels(false);

        /* Register commands */
        event.registerServerCommand(new CommandAction());
        event.registerServerCommand(new CommandDirector());
        event.registerServerCommand(new CommandRecord());
        event.registerServerCommand(new CommandOnHead());
        event.registerServerCommand(new CommandSpectate());
        event.registerServerCommand(new CommandScene());
    }

    @EventHandler
    public void serverStopping(FMLServerStoppingEvent event)
    {
        CommonProxy.manager.reset();
        CommonProxy.damage.reset();
        CommonProxy.scenes.reset();
    }
}
