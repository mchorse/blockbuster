package mchorse.blockbuster.common;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
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
import mchorse.blockbuster.commands.CommandCamera;
import mchorse.blockbuster.commands.CommandModel;
import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.recording.FrameHandler;
import mchorse.blockbuster.recording.RecordManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.resources.IResourcePack;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;

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

        /* Entities */
        this.registerEntityRender(EntityActor.class, new RenderActor(0.5F));

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

        /* Event listeners */
        FMLCommonHandler.instance().bus().register(new FrameHandler());
        MinecraftForge.EVENT_BUS.register(new KeyboardHandler());
        MinecraftForge.EVENT_BUS.register(new RenderingHandler(recordingOverlay));
        MinecraftForge.EVENT_BUS.register(profileRenderer);

        /* Client commands */
        ClientCommandHandler.instance.registerCommand(new CommandCamera());
        ClientCommandHandler.instance.registerCommand(new CommandModel());

        System.out.println("Hello, ClientProxy!");
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

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void registerEntityRender(Class eclass, Render render)
    {
        RenderingRegistry.registerEntityRenderingHandler(eclass, render);
    }
}