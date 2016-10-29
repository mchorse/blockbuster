package mchorse.blockbuster.common;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.api.Model;
import mchorse.blockbuster.api.ModelPack;
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
import mchorse.blockbuster.commands.CommandModel;
import mchorse.blockbuster.commands.CommandMorph;
import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.recording.FrameHandler;
import mchorse.blockbuster.recording.RecordManager;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.item.Item;
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
 */
@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy
{
    public static ActorsPack actorPack;
    public static GuiRecordingOverlay recordingOverlay;
    public static RenderPlayer playerRender;

    public static ProfileRunner profileRunner = new ProfileRunner();
    public static ProfileRenderer profileRenderer = new ProfileRenderer();

    public static RecordManager manager = new RecordManager();

    public static File config;

    /**
     * Register mod items, blocks, tile entites and entities, load item,
     * block models and register entity renderer.
     */
    @Override
    public void preLoad(FMLPreInitializationEvent event)
    {
        super.preLoad(event);

        /* Items */
        this.registerItemModel(Blockbuster.playbackItem, Blockbuster.path("playback"));
        this.registerItemModel(Blockbuster.registerItem, Blockbuster.path("register"));
        this.registerItemModel(Blockbuster.actorConfigItem, Blockbuster.path("actor_config"));

        /* Blocks */
        this.registerItemModel(Blockbuster.directorBlock, Blockbuster.path("director"));

        /* Entities */
        this.registerEntityRender(EntityActor.class, new RenderActor.FactoryActor());

        this.injectResourcePack(event.getSuggestedConfigurationFile().getAbsolutePath());
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
            packs.add(actorPack = new ActorsPack());

            actorPack.pack.addFolder(path + "/models");
            actorPack.pack.addFolder(path + "/downloads");
            actorPack.pack.reload();
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

        Minecraft mc = Minecraft.getMinecraft();

        recordingOverlay = new GuiRecordingOverlay(mc);
        playerRender = new RenderPlayer(mc.getRenderManager(), 0.5F);

        /* Event listeners */
        MinecraftForge.EVENT_BUS.register(new FrameHandler());
        MinecraftForge.EVENT_BUS.register(new KeyboardHandler());
        MinecraftForge.EVENT_BUS.register(new RenderingHandler(recordingOverlay, playerRender));
        MinecraftForge.EVENT_BUS.register(profileRenderer);

        /* Client commands */
        ClientCommandHandler.instance.registerCommand(new CommandCamera());
        ClientCommandHandler.instance.registerCommand(new CommandModel());
        ClientCommandHandler.instance.registerCommand(new CommandMorph());
    }

    /**
     * Load models into the game
     *
     * This method is responsible for loading models on the client.
     */
    @Override
    public void loadModels(ModelPack pack)
    {
        super.loadModels(pack);

        ModelCustom.MODELS.clear();

        for (Map.Entry<String, Model> model : this.models.models.entrySet())
        {
            ModelParser.parse(model.getKey(), model.getValue());
        }
    }

    /**
     * Get server pack. This method adds another directory where to look up
     * the models. This method only invoked for intergraded server.
     */
    @Override
    public ModelPack getPack()
    {
        ModelPack pack = super.getPack();
        pack.addFolder(config.getAbsolutePath() + "/models");

        return pack;
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