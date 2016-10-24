package mchorse.blockbuster.common;

import java.io.File;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.actor.ModelHandler;
import mchorse.blockbuster.actor.ModelPack;
import mchorse.blockbuster.capabilities.CapabilityHandler;
import mchorse.blockbuster.capabilities.morphing.IMorphing;
import mchorse.blockbuster.capabilities.morphing.Morphing;
import mchorse.blockbuster.capabilities.morphing.MorphingStorage;
import mchorse.blockbuster.capabilities.recording.IRecording;
import mchorse.blockbuster.capabilities.recording.Recording;
import mchorse.blockbuster.capabilities.recording.RecordingStorage;
import mchorse.blockbuster.common.block.BlockDirector;
import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.common.item.ItemActorConfig;
import mchorse.blockbuster.common.item.ItemPlayback;
import mchorse.blockbuster.common.item.ItemRegister;
import mchorse.blockbuster.common.tileentity.TileEntityDirector;
import mchorse.blockbuster.config.BlockbusterConfig;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.recording.ActionHandler;
import mchorse.blockbuster.recording.RecordManager;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
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
     * Registers network messages (and their handlers), items, blocks, director
     * block tile entities and actor entity.
     */
    public void preLoad(FMLPreInitializationEvent event)
    {
        Dispatcher.register();
        NetworkRegistry.INSTANCE.registerGuiHandler(Blockbuster.instance, new GuiHandler());

        /* Configuration */
        File config = new File(event.getModConfigurationDirectory(), "blockbuster/config.cfg");

        this.forge = new Configuration(config);
        this.config = new BlockbusterConfig(this.forge);

        MinecraftForge.EVENT_BUS.register(this.config);

        /* Creative tab */
        Blockbuster.blockbusterTab = new BlockbusterTab();

        /* Items */
        this.registerItem(Blockbuster.registerItem = new ItemRegister());
        this.registerItem(Blockbuster.playbackItem = new ItemPlayback());
        this.registerItem(Blockbuster.actorConfigItem = new ItemActorConfig());

        /* Blocks */
        this.registerBlock(Blockbuster.directorBlock = new BlockDirector());

        /* Entities */
        this.registerEntityWithEgg(EntityActor.class, "Actor", 0xffc1ab33, 0xffa08d2b);

        /* Tile Entities */
        GameRegistry.registerTileEntity(TileEntityDirector.class, "blockbuster_director_tile_entity");

        /* Capabilities */
        CapabilityManager.INSTANCE.register(IMorphing.class, new MorphingStorage(), Morphing.class);
        CapabilityManager.INSTANCE.register(IRecording.class, new RecordingStorage(), Recording.class);
    }

    /**
     * This method is responsible for registering Mocap's event handler which
     * is responsible for capturing <s>pokemons</s> player actions.
     */
    public void load(FMLInitializationEvent event)
    {
        this.models = new ModelHandler();
        this.loadModels(this.getPack());

        MinecraftForge.EVENT_BUS.register(this.models);
        MinecraftForge.EVENT_BUS.register(new ActionHandler());
        MinecraftForge.EVENT_BUS.register(new CapabilityHandler());
    }

    /**
     * Load models from given model pack
     *
     * This method is responsible only for loading domain models (in form of
     * data). For client models, you should look up {@link ClientProxy}'s
     * {@link #loadModels(ModelPack)} method.
     */
    public void loadModels(ModelPack pack)
    {
        this.models.loadModels(pack);
    }

    /**
     * Get an actor pack
     */
    public ModelPack getPack()
    {
        return new ModelPack();
    }

    /**
     * Register an item with Forge's game registry
     */
    protected void registerItem(Item item)
    {
        GameRegistry.register(item);
    }

    /**
     * Register block (and also add register an item for the block)
     */
    protected void registerBlock(Block block)
    {
        GameRegistry.register(block);
        GameRegistry.register(new ItemBlock(block).setRegistryName(block.getRegistryName()));
    }

    /**
     * Thanks to animal bikes mod for this wonderful example! Kids, wanna learn
     * how to mod minecraft with forge? That's simple. Find mods for specific
     * minecraft version and decompile the .jar files with JD-GUI. Isn't that
     * simple?
     *
     * Or go to minecraft(forge/forum) and ask people to help you #smartass
     */
    protected void registerEntityWithEgg(Class<? extends Entity> entity, String name, int primary, int secondary)
    {
        EntityRegistry.registerModEntity(entity, name, this.ID++, Blockbuster.instance, 64, 3, false, primary, secondary);
    }
}