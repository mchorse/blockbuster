package mchorse.blockbuster;

import mchorse.blockbuster.aperture.CameraHandler;
import mchorse.blockbuster.api.ModelClientHandler;
import mchorse.blockbuster.api.ModelHandler;
import mchorse.blockbuster.audio.AudioLibrary;
import mchorse.blockbuster.client.ActorsPack;
import mchorse.blockbuster.client.KeyboardHandler;
import mchorse.blockbuster.client.RenderingHandler;
import mchorse.blockbuster.client.gui.GuiRecordingOverlay;
import mchorse.blockbuster.client.gui.dashboard.GuiBlockbusterPanels;
import mchorse.blockbuster.client.render.RenderActor;
import mchorse.blockbuster.client.render.RenderGunProjectile;
import mchorse.blockbuster.client.render.tileentity.TileEntityDirectorRenderer;
import mchorse.blockbuster.client.render.tileentity.TileEntityGunItemStackRenderer;
import mchorse.blockbuster.client.render.tileentity.TileEntityModelItemStackRenderer;
import mchorse.blockbuster.client.render.tileentity.TileEntityModelRenderer;
import mchorse.blockbuster.commands.CommandItemNBT;
import mchorse.blockbuster.commands.CommandModel;
import mchorse.blockbuster.common.block.BlockGreen.ChromaColor;
import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.common.entity.EntityGunProjectile;
import mchorse.blockbuster.common.tileentity.TileEntityDirector;
import mchorse.blockbuster.common.tileentity.TileEntityModel;
import mchorse.blockbuster.recording.RecordManager;
import mchorse.blockbuster.recording.capturing.FrameHandler;
import mchorse.blockbuster.utils.mclib.BlockbusterTree;
import mchorse.blockbuster_pack.client.render.RenderCustomActor;
import mchorse.blockbuster_pack.morphs.StructureMorph;
import mchorse.blockbuster_pack.morphs.StructureMorph.StructureRenderer;
import mchorse.mclib.McLib;
import mchorse.mclib.utils.files.GlobalTree;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;

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
    public static GuiBlockbusterPanels panels;

    public static RecordManager manager = new RecordManager();

    public static RenderCustomActor actorRenderer;
    public static TileEntityModelRenderer modelRenderer;
    public static KeyboardHandler keys;
    public static BlockbusterTree tree;
    public static AudioLibrary audio;

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
        this.registerItemModel(Blockbuster.gunItem, Blockbuster.path("gun"));

        /* Blocks */
        this.registerItemModel(Blockbuster.directorBlock, Blockbuster.path("director"));

        final ModelResourceLocation modelStatic = new ModelResourceLocation(Blockbuster.path("model_static"), "inventory");
        final ModelResourceLocation model = new ModelResourceLocation(Blockbuster.path("model"), "inventory");

        /* Register model block's configurable render disable */
        Item item = Item.getItemFromBlock(Blockbuster.modelBlock);
        ModelBakery.registerItemVariants(item, model, modelStatic);

        ModelLoader.setCustomMeshDefinition(item, (stack) -> Blockbuster.modelBlockDisableItemRendering.get() ? modelStatic : model);

        Blockbuster.modelBlockItem.setTileEntityItemStackRenderer(new TileEntityModelItemStackRenderer());
        Blockbuster.gunItem.setTileEntityItemStackRenderer(new TileEntityGunItemStackRenderer());

        /* Entities */
        this.registerEntityRender(EntityActor.class, new RenderActor.FactoryActor());
        this.registerEntityRender(EntityGunProjectile.class, new RenderGunProjectile.FactoryGunProjectile());

        /* Tile entity */
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityModel.class, modelRenderer = new TileEntityModelRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDirector.class, new TileEntityDirectorRenderer());

        this.injectResourcePack(CommonProxy.configFile.getAbsolutePath());

        /* Structure morph */
        StructureMorph.STRUCTURES = new HashMap<String, StructureRenderer>();

        audio = new AudioLibrary(new File(CommonProxy.configFile, "audio"));
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
            IResourceManager manager = Minecraft.getMinecraft().getResourceManager();

            if (manager instanceof SimpleReloadableResourceManager)
            {
                ((SimpleReloadableResourceManager) manager).reloadResourcePack(actorPack);
            }

            /* File tree */
            GlobalTree.TREE.register(tree = new BlockbusterTree(this.pack.folders.get(0)));

            /* Create steve, alex and fred skins folders */
            new File(path + "/models/steve/skins").mkdirs();
            new File(path + "/models/alex/skins").mkdirs();
            new File(path + "/models/fred/skins").mkdirs();
            new File(path + "/models/image/skins").mkdirs();
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

        /* Register manually models for all chroma blocks */
        Item green = Item.getItemFromBlock(Blockbuster.greenBlock);
        Item dimGreen = Item.getItemFromBlock(Blockbuster.dimGreenBlock);
        ItemModelMesher mesher = mc.getRenderItem().getItemModelMesher();

        for (ChromaColor color : ChromaColor.values())
        {
            ModelResourceLocation location = new ModelResourceLocation("blockbuster:green", "color=" + color.name);

            mesher.register(green, color.ordinal(), location);
            mesher.register(dimGreen, color.ordinal(), location);
        }

        /* Initiate rendering overlay and renderer */
        recordingOverlay = new GuiRecordingOverlay(mc);
        actorRenderer = new RenderCustomActor(mc.getRenderManager(), null, 0);

        super.load(event);

        /* Event listeners */
        MinecraftForge.EVENT_BUS.register(new FrameHandler());
        MinecraftForge.EVENT_BUS.register(keys = new KeyboardHandler());
        MinecraftForge.EVENT_BUS.register(new RenderingHandler(recordingOverlay));

        McLib.EVENT_BUS.register(panels = new GuiBlockbusterPanels());

        CameraHandler.register();

        /* Client commands */
        ClientCommandHandler.instance.registerCommand(new CommandModel());
        ClientCommandHandler.instance.registerCommand(new CommandItemNBT());
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
    public void loadModels(boolean force)
    {
        audio.reload();

        super.loadModels(force);
    }

    @Override
    public boolean isClient()
    {
        return true;
    }

    @Override
    public ModelHandler getHandler()
    {
        return new ModelClientHandler();
    }

    /**
     * Client version of get language string.
     */
    @Override
    public String getLanguageString(String key, String defaultComment)
    {
        String comment = I18n.format(key);

        return comment.equals(key) ? defaultComment : comment;
    }
}