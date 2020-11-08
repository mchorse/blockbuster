package mchorse.blockbuster.common;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.ClientProxy;
import mchorse.blockbuster.client.gui.GuiActor;
import mchorse.blockbuster.client.gui.dashboard.GuiBlockbusterPanels;
import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.common.tileentity.TileEntityModel;
import mchorse.mclib.client.gui.mclib.GuiDashboard;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Gui handler class
 *
 * This class is responsible for opening GUIs.
 */
public class GuiHandler implements IGuiHandler
{
    /* GUI ids */
    public static final int ACTOR = 1;
    public static final int MODEL_BLOCK = 3;

    /**
     * Shortcut for {@link EntityPlayer#openGui(Object, int, World, int, int, int)}
     */
    public static void open(EntityPlayer player, int ID, int x, int y, int z)
    {
        player.openGui(Blockbuster.instance, ID, player.world, x, y, z);
    }

    /**
     * There's two types of GUI are available right now:
     *
     * - Actor configuration GUI
     * - Director block management GUIs
     * - Playback button GUI
     *
     * IGuiHandler is used to centralize GUI invocations
     */
    @Override
    @SideOnly(Side.CLIENT)
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        Entity entity = world.getEntityByID(x);

        if (ID == ACTOR && entity instanceof EntityActor)
        {
            return new GuiActor(Minecraft.getMinecraft(), (EntityActor) entity);
        }
        else if (ID == MODEL_BLOCK)
        {
            TileEntityModel model = (TileEntityModel) world.getTileEntity(new BlockPos(x, y, z));

            GuiDashboard dashboard = GuiDashboard.get();
            GuiBlockbusterPanels panels = ClientProxy.panels;

            dashboard.panels.setPanel(panels.modelPanel);
            panels.modelPanel.openModelBlock(model);

            return dashboard;
        }

        return null;
    }

    /**
     * This method is empty, because there's no need for this method to be
     * filled with code. This mod doesn't seem to provide any interaction with
     * Containers.
     */
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        return null;
    }
}