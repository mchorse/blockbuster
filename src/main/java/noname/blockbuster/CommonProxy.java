package noname.blockbuster;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import noname.blockbuster.block.BlockDirector;
import noname.blockbuster.block.BlockDirectorMap;
import noname.blockbuster.entity.EntityActor;
import noname.blockbuster.item.ItemActorConfig;
import noname.blockbuster.item.ItemPlayback;
import noname.blockbuster.item.ItemRegister;
import noname.blockbuster.network.Dispatcher;
import noname.blockbuster.recording.PlayerEventHandler;
import noname.blockbuster.tileentity.TileEntityDirector;
import noname.blockbuster.tileentity.TileEntityDirectorMap;

public class CommonProxy
{
    protected static int ID = 0;

    /**
     * Registers network messages (and their handlers), items, blocks, director
     * block tile entities and actor entity.
     */
    public void preLoad(FMLPreInitializationEvent event)
    {
        Dispatcher.register();
        NetworkRegistry.INSTANCE.registerGuiHandler(Blockbuster.instance, new GuiHandler());

        this.registerItem(Blockbuster.registerItem = new ItemRegister());
        this.registerItem(Blockbuster.playbackItem = new ItemPlayback());
        this.registerItem(Blockbuster.actorConfigItem = new ItemActorConfig());

        this.registerBlock(Blockbuster.directorBlock = new BlockDirector());
        this.registerBlock(Blockbuster.directorBlockMap = new BlockDirectorMap());

        this.registerEntityWithEgg(EntityActor.class, "Actor", 0xffc1ab33, 0xffa08d2b);

        GameRegistry.registerTileEntity(TileEntityDirector.class, "blockbuster_director_tile_entity");
        GameRegistry.registerTileEntity(TileEntityDirectorMap.class, "blockbuster_director_map_tile_entity");
    }

    /**
     * This method is responsible for registering Mocap's event handler which
     * is responsible for capturing <s>pokemons</s> player actions.
     */
    public void load(FMLInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(new PlayerEventHandler());
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
    protected void registerEntity(Class<? extends Entity> entity, String name)
    {
        EntityRegistry.registerModEntity(entity, name, ID++, Blockbuster.instance, 64, 3, false);
    }

    protected void registerEntityWithEgg(Class<? extends Entity> entity, String name, int primary, int secondary)
    {
        EntityRegistry.registerModEntity(entity, name, ID++, Blockbuster.instance, 64, 3, false, primary, secondary);
    }
}
