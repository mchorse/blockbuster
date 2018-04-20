package mchorse.blockbuster.common;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.aperture.CameraHandler;
import mchorse.blockbuster.api.Model;
import mchorse.blockbuster.api.ModelHandler.ModelCell;
import mchorse.blockbuster.api.ModelPack;
import mchorse.blockbuster.api.ModelPack.ModelEntry;
import mchorse.blockbuster.client.ActorsPack;
import mchorse.blockbuster.client.KeyboardHandler;
import mchorse.blockbuster.client.RenderingHandler;
import mchorse.blockbuster.client.gui.GuiRecordingOverlay;
import mchorse.blockbuster.client.model.ModelCustom;
import mchorse.blockbuster.client.model.parsing.ModelParser;
import mchorse.blockbuster.client.render.RenderActor;
import mchorse.blockbuster.client.render.tileentity.TileEntityModelRenderer;
import mchorse.blockbuster.commands.CommandLoadChunks;
import mchorse.blockbuster.commands.CommandModel;
import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.common.tileentity.TileEntityModel;
import mchorse.blockbuster.model_editor.MainMenuHandler;
import mchorse.blockbuster.recording.FrameHandler;
import mchorse.blockbuster.recording.RecordManager;
import mchorse.blockbuster_pack.client.gui.GuiCustomModelMorphBuilder;
import mchorse.blockbuster_pack.client.render.RenderCustomActor;
import mchorse.metamorph.client.gui.builder.GuiMorphBuilder;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.item.Item;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
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

    public static RecordManager manager = new RecordManager();

    public static RenderCustomActor actorRenderer;
    public static TileEntityModelRenderer modelRenderer;
    public static KeyboardHandler keys;

    public static File config;

    /**
     * Register mod items, blocks, tile entites and entities, load item,
     * block models and register entity renderer.
     */
    @Override
    public void preLoad(FMLPreInitializationEvent event)
    {
        String path = event.getSuggestedConfigurationFile().getAbsolutePath();
        path = path.substring(0, path.length() - 4);

        config = new File(path);
        super.preLoad(event);

        /* Items */
        this.registerItemModel(Blockbuster.playbackItem, Blockbuster.path("playback"));
        this.registerItemModel(Blockbuster.registerItem, Blockbuster.path("register"));
        this.registerItemModel(Blockbuster.actorConfigItem, Blockbuster.path("actor_config"));

        /* Blocks */
        this.registerItemModel(Blockbuster.directorBlock, Blockbuster.path("director"));
        this.registerItemModel(Blockbuster.modelBlock, Blockbuster.path("model"));

        /* Entities */
        this.registerEntityRender(EntityActor.class, new RenderActor.FactoryActor());

        /* Tile entity */
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityModel.class, modelRenderer = new TileEntityModelRenderer());

        this.injectResourcePack(path);
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
        try
        {
            Field field = FMLClientHandler.class.getDeclaredField("resourcePackList");
            field.setAccessible(true);

            List<IResourcePack> packs = (List<IResourcePack>) field.get(FMLClientHandler.instance());
            packs.add(actorPack = new ActorsPack());

            actorPack.pack.addFolder(path + "/models");
            actorPack.pack.addFolder(path + "/downloads");
            actorPack.pack.reload();

            /* Create steve, alex and fred skins folders */
            new File(path + "/models/steve/skins").mkdirs();
            new File(path + "/models/alex/skins").mkdirs();
            new File(path + "/models/fred/skins").mkdirs();
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
        Minecraft mc = Minecraft.getMinecraft();

        recordingOverlay = new GuiRecordingOverlay(mc);
        actorRenderer = new RenderCustomActor(mc.getRenderManager(), null, 0);

        super.load(event);

        /* Event listeners */
        MinecraftForge.EVENT_BUS.register(new MainMenuHandler());
        MinecraftForge.EVENT_BUS.register(new FrameHandler());
        MinecraftForge.EVENT_BUS.register(keys = new KeyboardHandler());
        MinecraftForge.EVENT_BUS.register(new RenderingHandler(recordingOverlay));

        if (CameraHandler.isApertureLoaded())
        {
            CameraHandler.register();
        }

        /* Client commands */
        ClientCommandHandler.instance.registerCommand(new CommandModel());
        ClientCommandHandler.instance.registerCommand(new CommandLoadChunks());

        /* Metamorph morph builder panel */
        GuiMorphBuilder.BUILDERS.put("blockbuster", new GuiCustomModelMorphBuilder());
    }

    @Override
    public void postLoad(FMLPostInitializationEvent event)
    {
        if (CameraHandler.isApertureLoaded())
        {
            CameraHandler.postRegister();
        }
    }

    /**
     * Load models into the game
     *
     * This method is responsible for loading models on the client.
     */
    @Override
    @SuppressWarnings("unchecked")
    public void loadModels(ModelPack pack)
    {
        super.loadModels(pack);

        for (Map.Entry<String, ModelCell> model : this.models.models.entrySet())
        {
            String key = model.getKey();
            ModelCell cell = model.getValue();
            ModelEntry entry = pack.models.get(key);
            Model mod = cell.model;

            if (entry != null && !cell.reload)
            {
                continue;
            }

            File objModel = null;
            boolean fallback = true;

            if (pack.models.containsKey(key))
            {
                objModel = entry.objModel;
            }

            if (!mod.model.isEmpty())
            {
                try
                {
                    Class<? extends ModelCustom> clazz = (Class<? extends ModelCustom>) Class.forName(mod.model);

                    /* Parse custom custom model with a custom class */
                    ModelParser.parse(model.getKey(), mod, clazz, objModel);

                    fallback = false;
                }
                catch (ClassNotFoundException e)
                {
                    e.printStackTrace();
                }
            }

            if (fallback)
            {
                ModelParser.parse(model.getKey(), mod, objModel);
            }

            cell.reload = false;
        }

        this.factory.registerClient(null);
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

    @Override
    public boolean isClient()
    {
        return true;
    }

    /**
     * Applies client side options
     */
    @Override
    public void onConfigChange(Configuration config)
    {}

    /**
     * Client version of get language string.
     */
    @Override
    public String getLanguageString(String key, String defaultComment)
    {
        String comment = I18n.format(key);

        return comment;
        // return comment.equals(key) ? defaultComment : key;
    }
}