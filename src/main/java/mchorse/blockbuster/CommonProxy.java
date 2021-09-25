package mchorse.blockbuster;

import mchorse.blockbuster.api.ModelHandler;
import mchorse.blockbuster.api.ModelPack;
import mchorse.blockbuster.capabilities.CapabilityHandler;
import mchorse.blockbuster.capabilities.recording.IRecording;
import mchorse.blockbuster.capabilities.recording.Recording;
import mchorse.blockbuster.capabilities.recording.RecordingStorage;
import mchorse.blockbuster.client.particles.BedrockLibrary;
import mchorse.blockbuster.common.BlockbusterTab;
import mchorse.blockbuster.common.GuiHandler;
import mchorse.blockbuster.common.block.BlockDimGreen;
import mchorse.blockbuster.common.block.BlockDirector;
import mchorse.blockbuster.common.block.BlockGreen;
import mchorse.blockbuster.common.block.BlockModel;
import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.common.entity.EntityGunProjectile;
import mchorse.blockbuster.common.item.ItemActorConfig;
import mchorse.blockbuster.common.item.ItemBlockGreen;
import mchorse.blockbuster.common.item.ItemGun;
import mchorse.blockbuster.common.item.ItemPlayback;
import mchorse.blockbuster.common.item.ItemRegister;
import mchorse.blockbuster.common.tileentity.TileEntityDirector;
import mchorse.blockbuster.common.tileentity.TileEntityModel;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.recording.RecordManager;
import mchorse.blockbuster.recording.capturing.ActionHandler;
import mchorse.blockbuster.recording.capturing.DamageControlManager;
import mchorse.blockbuster.recording.scene.SceneManager;
import mchorse.blockbuster.utils.mclib.BlockbusterResourceTransformer;
import mchorse.blockbuster_pack.BlockbusterFactory;
import mchorse.blockbuster_pack.MetamorphHandler;
import mchorse.blockbuster_pack.trackers.ApertureTracker;
import mchorse.blockbuster_pack.trackers.TrackerRegistry;
import mchorse.mclib.utils.resources.RLUtils;
import mchorse.metamorph.api.MorphManager;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.io.File;

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
     * Damage control manager
     */
    public static DamageControlManager damage = new DamageControlManager();

    /**
     * Remote scene manager
     */
    public static SceneManager scenes = new SceneManager();

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
     * Model pack
     */
    public ModelPack pack;

    /**
     * Bedrock particle library
     */
    public BedrockLibrary particles;

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
        configFile = new File(event.getModConfigurationDirectory(), "blockbuster");
        this.particles = new BedrockLibrary(new File(configFile, "models/particles"));

        /* Creative tab */
        Blockbuster.blockbusterTab = new BlockbusterTab();

        /* Items */
        this.registerItem(Blockbuster.registerItem = new ItemRegister());
        this.registerItem(Blockbuster.playbackItem = new ItemPlayback());
        this.registerItem(Blockbuster.actorConfigItem = new ItemActorConfig());
        this.registerItem(Blockbuster.gunItem = new ItemGun());

        /* Blocks */
        Block director = new BlockDirector();
        Block model = new BlockModel();
        Block green = new BlockGreen();
        Block dimGreen = new BlockDimGreen();

        green.setRegistryName("green").setUnlocalizedName("blockbuster.green");
        dimGreen.setRegistryName("dim_green").setUnlocalizedName("blockbuster.dim_green");

        ForgeRegistries.BLOCKS.register(Blockbuster.directorBlock = director);
        ForgeRegistries.ITEMS.register(new ItemBlock(director).setRegistryName(director.getRegistryName()));

        ForgeRegistries.BLOCKS.register(Blockbuster.modelBlock = model);
        ForgeRegistries.ITEMS.register(Blockbuster.modelBlockItem = new ItemBlock(model).setRegistryName(model.getRegistryName()));

        ForgeRegistries.BLOCKS.register(Blockbuster.greenBlock = green);
        ForgeRegistries.ITEMS.register(new ItemBlockGreen(green, true).setRegistryName(green.getRegistryName()));

        ForgeRegistries.BLOCKS.register(Blockbuster.dimGreenBlock = dimGreen);
        ForgeRegistries.ITEMS.register(new ItemBlockGreen(dimGreen, true).setRegistryName(dimGreen.getRegistryName()));

        Blockbuster.modelBlockItem = Item.getItemFromBlock(Blockbuster.modelBlock);

        /* Tile Entities */
        GameRegistry.registerTileEntity(TileEntityDirector.class, "blockbuster_director_tile_entity");
        GameRegistry.registerTileEntity(TileEntityModel.class, "blockbuster_model_tile_entity");

        /* Capabilities */
        CapabilityManager.INSTANCE.register(IRecording.class, new RecordingStorage(), Recording::new);

        /* Models and morphing */
        this.pack = new ModelPack();
        this.models = this.getHandler();
        this.factory = new BlockbusterFactory();
        this.factory.models = this.models;

        MorphManager.INSTANCE.factories.add(this.factory);
        RLUtils.register(new BlockbusterResourceTransformer());

        /* Trackers */
        TrackerRegistry.registerTracker("aperture", ApertureTracker.class);
    }

    /**
     * This method is responsible for registering Mocap's event handler which
     * is responsible for capturing <s>pokemons</s> player actions.
     */
    public void load(FMLInitializationEvent event)
    {
        /* Entities */
        this.registerEntityWithEgg(EntityActor.class, new ResourceLocation("blockbuster:actor"), "blockbuster.Actor", 0xffc1ab33, 0xffa08d2b);
        EntityRegistry.registerModEntity(new ResourceLocation("blockbuster:projectile"), EntityGunProjectile.class, "blockbuster.GunProjectile", this.ID++, Blockbuster.instance, Blockbuster.actorTrackingRange.get(), 10, true);

        /* Event handlers */
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
     * data).
     */
    public void loadModels(boolean force)
    {
        this.models.loadModels(this.pack, force);
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
        EntityRegistry.registerModEntity(id, entity, name, this.ID++, Blockbuster.instance, Blockbuster.actorTrackingRange.get(), 3, false, primary, secondary);
    }

    /**
     * Whether physical side is client
     */
    public boolean isClient()
    {
        return false;
    }

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