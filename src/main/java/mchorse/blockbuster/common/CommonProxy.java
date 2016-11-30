
package mchorse.blockbuster.common;

import java.io.File;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.api.ModelHandler;
import mchorse.blockbuster.api.ModelPack;
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
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

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
        this.registerItem(Blockbuster.registerItem = new ItemRegister(), "register");
        this.registerItem(Blockbuster.playbackItem = new ItemPlayback(), "playback");
        this.registerItem(Blockbuster.actorConfigItem = new ItemActorConfig(), "actor_config");

        /* Blocks */
        this.registerBlock(Blockbuster.directorBlock = new BlockDirector(), "director");

        /* Entities */
        this.registerEntityWithEgg(EntityActor.class, "Actor", 0xffc1ab33, 0xffa08d2b);

        /* Tile Entities */
        GameRegistry.registerTileEntity(TileEntityDirector.class, "blockbuster_director_tile_entity");
    }

    /**
     * This method is responsible for registering Mocap's event handler which
     * is responsible for capturing <s>pokemons</s> player actions.
     */
    public void load(FMLInitializationEvent event)
    {
        this.models = new ModelHandler();
        this.loadModels(this.getPack());

        ActionHandler handler = new ActionHandler();

        MinecraftForge.EVENT_BUS.register(this.models);
        MinecraftForge.EVENT_BUS.register(handler);
        FMLCommonHandler.instance().bus().register(handler);
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
    protected void registerItem(Item item, String name)
    {
        GameRegistry.registerItem(item, name, Blockbuster.MODID);
    }

    /**
     * Register block (and also add register an item for the block)
     */
    protected void registerBlock(Block block, String name)
    {
        GameRegistry.registerBlock(block, name);
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
        EntityRegistry.registerModEntity(entity, name, this.ID, Blockbuster.instance, 96, 3, false);
        EntityRegistry.registerGlobalEntityID(entity, name, this.ID++, primary, secondary);
    }
}