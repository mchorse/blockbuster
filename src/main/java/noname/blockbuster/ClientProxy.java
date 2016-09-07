package noname.blockbuster;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noname.blockbuster.camera.ProfileRunner;
import noname.blockbuster.client.ActorsPack;
import noname.blockbuster.client.KeyboardHandler;
import noname.blockbuster.client.ProfileRenderer;
import noname.blockbuster.client.RenderingHandler;
import noname.blockbuster.client.gui.GuiRecordingOverlay;
import noname.blockbuster.client.model.ModelCustom;
import noname.blockbuster.client.model.parsing.ModelParser;
import noname.blockbuster.client.render.RenderTest;
import noname.blockbuster.commands.CommandCamera;
import noname.blockbuster.entity.EntityActor;

/**
 * Client proxy
 *
 * This class is responsible for registering item models, block models, entity
 * renders and injecting actor skin resource pack.
 *
 * This class als provides GUIs.
 */
@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy
{
    public static ActorsPack actorPack;
    public static GuiRecordingOverlay recordingOverlay;

    public static ProfileRunner profileRunner = new ProfileRunner();
    public static ProfileRenderer profileRenderer = new ProfileRenderer();

    /**
     * Register mod items, blocks, tile entites and entities, and load item,
     * block models and register entity renderer.
     */
    @Override
    public void preLoad(FMLPreInitializationEvent event)
    {
        super.preLoad(event);

        this.registerItemModel(Blockbuster.playbackItem, Blockbuster.path("playback"));
        this.registerItemModel(Blockbuster.registerItem, Blockbuster.path("register"));
        this.registerItemModel(Blockbuster.actorConfigItem, Blockbuster.path("actor_config"));

        this.registerItemModel(Blockbuster.directorBlock, Blockbuster.path("director"));
        this.registerItemModel(Blockbuster.directorBlockMap, Blockbuster.path("director_map"));

        this.registerEntityRender(EntityActor.class, new RenderTest.FactoryActor());

        this.injectResourcePack(event.getSuggestedConfigurationFile().getAbsolutePath());
        this.loadActorModels();
    }

    /**
     * Load some actor models
     */
    private void loadActorModels()
    {
        ModelCustom.MODELS.clear();

        /* Load user supplied models */
        for (String model : actorPack.getModels())
        {
            if (model.equals("steve") || model.equals("alex")) continue;

            try
            {
                ModelParser.parse(model, actorPack.getInputStream(new ResourceLocation("blockbuster.actors", model)));
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        /* Default models */
        ModelParser.parse("alex", this.getClass().getClassLoader().getResourceAsStream("assets/blockbuster/models/entity/alex.json"));
        ModelParser.parse("steve", this.getClass().getClassLoader().getResourceAsStream("assets/blockbuster/models/entity/steve.json"));
    }

    /**
     * Inject actors skin pack into FML's resource packs list
     *
     * It's done by accessing private FMLClientHandler list (via reflection) and
     * appending actor pack.
     *
     * Thanks to diesieben07 for giving the idea.
     */
    @SuppressWarnings("unchecked")
    private void injectResourcePack(String path)
    {
        path = path.substring(0, path.length() - 4);

        try
        {
            Field field = FMLClientHandler.class.getDeclaredField("resourcePackList");
            field.setAccessible(true);

            List<IResourcePack> packs = (List<IResourcePack>) field.get(FMLClientHandler.instance());
            packs.add(actorPack = new ActorsPack(path + "/models"));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Subscribe all event listeners to EVENT_BUS and attach any client-side
     * commands to the ClientCommandRegistry.
     */
    @Override
    public void load(FMLInitializationEvent event)
    {
        super.load(event);

        recordingOverlay = new GuiRecordingOverlay(Minecraft.getMinecraft());

        MinecraftForge.EVENT_BUS.register(new KeyboardHandler());
        MinecraftForge.EVENT_BUS.register(new RenderingHandler(recordingOverlay));
        MinecraftForge.EVENT_BUS.register(profileRenderer);

        ClientCommandHandler.instance.registerCommand(new CommandCamera());
    }

    protected void registerItemModel(Block block, String path)
    {
        this.registerItemModel(Item.getItemFromBlock(block), path);
    }

    protected void registerItemModel(Item item, String path)
    {
        ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(path, "inventory"));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void registerEntityRender(Class eclass, IRenderFactory factory)
    {
        RenderingRegistry.registerEntityRenderingHandler(eclass, factory);
    }
}
