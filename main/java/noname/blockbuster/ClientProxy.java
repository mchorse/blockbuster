package noname.blockbuster;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noname.blockbuster.client.gui.GuiCamera;
import noname.blockbuster.client.render.ActorRender;
import noname.blockbuster.client.render.CameraRender;
import noname.blockbuster.entity.ActorEntity;
import noname.blockbuster.entity.CameraEntity;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy
{
    @Override
    public void preLoad()
    {
        super.preLoad();

        this.registerItemModel(Blockbuster.cameraItem, Blockbuster.path("cameraItem"));
        this.registerItemModel(Blockbuster.cameraConfigItem, Blockbuster.path("cameraConfigItem"));
        this.registerItemModel(Blockbuster.recordItem, Blockbuster.path("recordItem"));
        this.registerItemModel(Blockbuster.registerItem, Blockbuster.path("registerItem"));

        this.registerItemModel(Blockbuster.directorBlock, Blockbuster.path("directorBlock"));

        this.registerEntityRender(CameraEntity.class, new CameraRender.CameraFactory());
        this.registerEntityRender(ActorEntity.class, new ActorRender.ActorFactory());
    }

    /**
     * Register block model
     */
    protected void registerItemModel(Block block, String path)
    {
        this.registerItemModel(Item.getItemFromBlock(block), path);
    }

    /**
     * Register item model
     */
    protected void registerItemModel(Item item, String path)
    {
        ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(path, "inventory"));
    }

    /**
     * Register entity renderer
     */
    protected void registerEntityRender(Class eclass, IRenderFactory factory)
    {
        RenderingRegistry.registerEntityRenderingHandler(eclass, factory);
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        if (ID == 0)
        {
            return new GuiCamera((CameraEntity) world.getEntityByID(x));
        }

        return null;
    }
}
