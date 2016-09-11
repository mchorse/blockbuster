package mchorse.blockbuster;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

import mchorse.blockbuster.camera.ProfileRunner;
import mchorse.blockbuster.client.ActorsPack;
import mchorse.blockbuster.client.KeyboardHandler;
import mchorse.blockbuster.client.ProfileRenderer;
import mchorse.blockbuster.client.RenderingHandler;
import mchorse.blockbuster.client.gui.GuiRecordingOverlay;
import mchorse.blockbuster.client.model.ModelCustom;
import mchorse.blockbuster.client.model.parsing.ModelParser;
import mchorse.blockbuster.client.render.RenderActor;
import mchorse.blockbuster.client.render.RenderPlayer;
import mchorse.blockbuster.commands.CommandCamera;
import mchorse.blockbuster.commands.CommandExportModel;
import mchorse.blockbuster.commands.CommandMorph;
import mchorse.blockbuster.entity.EntityActor;
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
    public static RenderPlayer playerRender;

    public static ProfileRunner profileRunner = new ProfileRunner();
    public static ProfileRenderer profileRenderer = new ProfileRenderer();

    public static File config;

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

        this.registerEntityRender(EntityActor.class, new RenderActor.FactoryActor());

        this.injectResourcePack(event.getSuggestedConfigurationFile().getAbsolutePath());
    }

    /**
     * Load some actor models
     */
    public void loadActorModels()
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

        config = new File(path);

        try
        {
            Field field = FMLClientHandler.class.getDeclaredField("resourcePackList");
            field.setAccessible(true);

            List<IResourcePack> packs = (List<IResourcePack>) field.get(FMLClientHandler.instance());
            packs.add(actorPack = new ActorsPack(path + "/models", path + "/downloads"));
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
        playerRender = new RenderPlayer(Minecraft.getMinecraft().getRenderManager(), 0.5F);

        MinecraftForge.EVENT_BUS.register(new KeyboardHandler());
        MinecraftForge.EVENT_BUS.register(new RenderingHandler(recordingOverlay, playerRender));
        MinecraftForge.EVENT_BUS.register(profileRenderer);

        ClientCommandHandler.instance.registerCommand(new CommandCamera());
        ClientCommandHandler.instance.registerCommand(new CommandMorph());
        ClientCommandHandler.instance.registerCommand(new CommandExportModel());
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
