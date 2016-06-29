package noname.blockbuster;

import java.lang.reflect.Field;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noname.blockbuster.client.ActorsPack;
import noname.blockbuster.client.KeyboardHandler;
import noname.blockbuster.client.RenderingHandler;
import noname.blockbuster.client.gui.GuiActor;
import noname.blockbuster.client.gui.GuiCamera;
import noname.blockbuster.client.gui.GuiDirectorMap;
import noname.blockbuster.client.gui.GuiRecordingOverlay;
import noname.blockbuster.client.render.RenderActor;
import noname.blockbuster.client.render.RenderCamera;
import noname.blockbuster.entity.EntityActor;
import noname.blockbuster.entity.EntityCamera;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy
{
    public static ActorsPack actorPack;
    public static GuiRecordingOverlay recordingOverlay;

    /**
     * Register mod items, blocks, tile entites and entities, and load
     * item, block models and register entity renderer.
     */
    @Override
    public void preLoad(FMLPreInitializationEvent event)
    {
        super.preLoad(event);

        this.registerItemModel(Blockbuster.cameraItem, Blockbuster.path("camera"));
        this.registerItemModel(Blockbuster.cameraConfigItem, Blockbuster.path("camera_config"));
        this.registerItemModel(Blockbuster.playbackItem, Blockbuster.path("playback"));
        this.registerItemModel(Blockbuster.registerItem, Blockbuster.path("register"));
        this.registerItemModel(Blockbuster.actorConfigItem, Blockbuster.path("actor_config"));

        this.registerItemModel(Blockbuster.directorBlock, Blockbuster.path("director"));
        this.registerItemModel(Blockbuster.directorBlockMap, Blockbuster.path("director_map"));

        this.registerEntityRender(EntityCamera.class, new RenderCamera.CameraFactory());
        this.registerEntityRender(EntityActor.class, new RenderActor.ActorFactory());

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
    private void injectResourcePack(String path)
    {
        path = path.substring(0, path.length() - 4);

        try
        {
            Field field = FMLClientHandler.class.getDeclaredField("resourcePackList");
            field.setAccessible(true);

            List<IResourcePack> packs = (List<IResourcePack>) field.get(FMLClientHandler.instance());
            packs.add(actorPack = new ActorsPack(path + "/skins"));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Subscribe keyboard handler and rendering event listener to EVENT_BUS
     */
    @Override
    public void load(FMLInitializationEvent event)
    {
        super.load(event);

        recordingOverlay = new GuiRecordingOverlay(Minecraft.getMinecraft());

        MinecraftForge.EVENT_BUS.register(new KeyboardHandler());
        MinecraftForge.EVENT_BUS.register(new RenderingHandler(recordingOverlay));
    }

    protected void registerItemModel(Block block, String path)
    {
        this.registerItemModel(Item.getItemFromBlock(block), path);
    }

    protected void registerItemModel(Item item, String path)
    {
        ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(path, "inventory"));
    }

    protected void registerEntityRender(Class eclass, IRenderFactory factory)
    {
        RenderingRegistry.registerEntityRenderingHandler(eclass, factory);
    }

    /**
     * There's two types of GUI are available right now:
     * - Camera configuration GUI (0)
     * - Actor configuration GUI (1)
     * - Director block management GUI (2)
     * - Director map block management GUI (3)
     *
     * IGuiHandler is used to centralize GUI invocations
     */
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        Entity entity = world.getEntityByID(x);

        if (ID == 0)
        {
            return new GuiCamera((EntityCamera) entity);
        }
        else if (ID == 1)
        {
            return new GuiActor(null, (EntityActor) entity);
        }
        else if (ID == 3)
        {
            return new GuiDirectorMap(new BlockPos(x, y, z));
        }

        return null;
    }
}
