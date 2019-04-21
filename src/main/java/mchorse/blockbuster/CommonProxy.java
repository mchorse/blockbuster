package mchorse.blockbuster;

import java.io.File;

import mchorse.blockbuster.api.ModelHandler;
import mchorse.blockbuster.api.ModelPack;
import mchorse.blockbuster.capabilities.CapabilityHandler;
import mchorse.blockbuster.capabilities.recording.IRecording;
import mchorse.blockbuster.capabilities.recording.Recording;
import mchorse.blockbuster.capabilities.recording.RecordingStorage;
import mchorse.blockbuster.common.BlockbusterTab;
import mchorse.blockbuster.common.EventHandler;
import mchorse.blockbuster.common.GuiHandler;
import mchorse.blockbuster.common.block.BlockDirector;
import mchorse.blockbuster.common.block.BlockGreen;
import mchorse.blockbuster.common.block.BlockModel;
import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.common.item.ItemActorConfig;
import mchorse.blockbuster.common.item.ItemBlockGreen;
import mchorse.blockbuster.common.item.ItemGun;
import mchorse.blockbuster.common.item.ItemPlayback;
import mchorse.blockbuster.common.item.ItemRegister;
import mchorse.blockbuster.common.tileentity.TileEntityDirector;
import mchorse.blockbuster.common.tileentity.TileEntityModel;
import mchorse.blockbuster.config.BlockbusterConfig;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.recording.ActionHandler;
import mchorse.blockbuster.recording.RecordManager;
import mchorse.blockbuster_pack.BlockbusterFactory;
import mchorse.blockbuster_pack.MetamorphHandler;
import mchorse.metamorph.api.MorphManager;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * Common proxy
 *
 * This class is responsible for registering items, blocks, entities,
 * capabilities and event listeners on both sides (that's why it's a common
 * proxy).
 */
public class CommonProxy
{
    /**
     * Record manager for server side
     */
    public static RecordManager manager = new RecordManager();

    /**
     * Incremented ID for entities
     */
    protected int ID = 0;

    /**
     * Model manager, this class is responsible for managing domain custom
     * models for custom actors
     */
    public ModelHandler models;

    /**
     * Config
     */
    public BlockbusterConfig config;

    /**
     * Forge config
     */
    public Configuration forge;

    /**
     * Blockbuster's morphing factory
     */
    public BlockbusterFactory factory;

    public static File configFile;

    /**
     * Registers network messages (and their handlers), items, blocks, director
     * block tile entities and actor entity.
     */
    public void preLoad(FMLPreInitializationEvent event)
    {
        Dispatcher.register();
        NetworkRegistry.INSTANCE.registerGuiHandler(Blockbuster.instance, new GuiHandler());

        /* Configuration */
        File config = new File(event.getModConfigurationDirectory(), "blockbuster/config.cfg");

        configFile = new File(event.getModConfigurationDirectory(), "blockbuster");
        this.forge = new Configuration(config);
        this.config = new BlockbusterConfig(this.forge);

        MinecraftForge.EVENT_BUS.register(this.config);
        MinecraftForge.EVENT_BUS.register(new EventHandler());

        /* Creative tab */
        Blockbuster.blockbusterTab = new BlockbusterTab();

        /* Items */
        this.registerItem(Blockbuster.registerItem = new ItemRegister());
        this.registerItem(Blockbuster.playbackItem = new ItemPlayback());
        this.registerItem(Blockbuster.actorConfigItem = new ItemActorConfig());
        // this.registerItem(Blockbuster.gunItem = new ItemGun());

        /* Blocks */
        Block director = new BlockDirector();
        Block model = new BlockModel();
        Block green = new BlockGreen();

        ForgeRegistries.BLOCKS.register(Blockbuster.directorBlock = director);
        ForgeRegistries.ITEMS.register(new ItemBlock(director).setRegistryName(director.getRegistryName()));

        ForgeRegistries.BLOCKS.register(Blockbuster.modelBlock = model);
        ForgeRegistries.ITEMS.register(Blockbuster.modelBlockItem = new ItemBlock(model).setRegistryName(model.getRegistryName()));

        ForgeRegistries.BLOCKS.register(Blockbuster.greenBlock = green);
        ForgeRegistries.ITEMS.register(new ItemBlockGreen(green, true).setRegistryName(green.getRegistryName()));

        Blockbuster.modelBlockItem = Item.getItemFromBlock(Blockbuster.modelBlock);

        /* Entities */
        this.registerEntityWithEgg(EntityActor.class, new ResourceLocation("blockbuster:actor"), "blockbuster.Actor", 0xffc1ab33, 0xffa08d2b);

        /* Tile Entities */
        GameRegistry.registerTileEntity(TileEntityDirector.class, "blockbuster_director_tile_entity");
        GameRegistry.registerTileEntity(TileEntityModel.class, "blockbuster_model_tile_entity");

        /* Capabilities */
        CapabilityManager.INSTANCE.register(IRecording.class, new RecordingStorage(), Recording.class);

        /* Morphing */
        this.models = this.getHandler();
        this.factory = new BlockbusterFactory();
        this.factory.models = this.models;

        MorphManager.INSTANCE.factories.add(this.factory);
    }

    /**
     * This method is responsible for registering Mocap's event handler which
     * is responsible for capturing <s>pokemons</s> player actions.
     */
    public void load(FMLInitializationEvent event)
    {
        this.config.reload();

        MinecraftForge.EVENT_BUS.register(this.models);
        MinecraftForge.EVENT_BUS.register(new ActionHandler());
        MinecraftForge.EVENT_BUS.register(new CapabilityHandler());
        MinecraftForge.EVENT_BUS.register(new MetamorphHandler());
    }

    /**
     * Post load
     */
    public void postLoad(FMLPostInitializationEvent event)
    {}

    /**
     * Load models from given model pack
     *
     * This method is responsible only for loading domain models (in form of
     * data). For client models, you should look up {@link ClientProxy}'s
     * {@link #loadModels(ModelPack)} method.
     */
    public void loadModels(ModelPack pack, boolean force)
    {
        this.models.pack = pack;
        this.models.loadModels(pack, force);
    }

    /**
     * Get an model pack
     */
    public ModelPack getPack()
    {
        ModelPack pack = new ModelPack();

        pack.addFolder(configFile.getAbsolutePath() + "/models");

        return pack;
    }

    /**
     * Register an item with Forge's game registry
     */
    protected void registerItem(Item item)
    {
        ForgeRegistries.ITEMS.register(item);
    }

    /**
     * Thanks to animal bikes mod for this wonderful example! Kids, wanna learn
     * how to mod minecraft with forge? That's simple. Find mods for specific
     * minecraft version and decompile the .jar files with JD-GUI. Isn't that
     * simple?
     *
     * Or go to minecraft(forge/forum) and ask people to help you #smartass
     */
    protected void registerEntityWithEgg(Class<? extends Entity> entity, ResourceLocation id, String name, int primary, int secondary)
    {
        EntityRegistry.registerModEntity(id, entity, name, this.ID++, Blockbuster.instance, this.config.actor_tracking_range, 3, false, primary, secondary);
    }

    /**
     * Whether physical side is client
     */
    public boolean isClient()
    {
        return false;
    }

    /**
     * Triggered when config is changed
     */
    public void onConfigChange(Configuration config)
    {}

    /**
     * Get model handler
     */
    public ModelHandler getHandler()
    {
        return new ModelHandler();
    }

    /**
     * Get language string
     */
    public String getLanguageString(String key, String defaultComment)
    {
        return defaultComment;
    }
}