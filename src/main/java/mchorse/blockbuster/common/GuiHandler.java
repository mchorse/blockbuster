package mchorse.blockbuster.common;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.aperture.CameraHandler;
import mchorse.blockbuster.aperture.gui.GuiPlayback;
import mchorse.blockbuster.client.gui.GuiActor;
import mchorse.blockbuster.client.gui.dashboard.GuiDashboard;
import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.common.tileentity.TileEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional.Method;
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
    public static final int PLAYBACK = 0;
    public static final int ACTOR = 1;
    public static final int DIRECTOR = 2;
    public static final int MODEL_BLOCK = 3;
    public static final int DASHBOARD = 4;

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

        if (ID == PLAYBACK && CameraHandler.isApertureLoaded())
        {
            return this.getPlayback();
        }
        if (ID == ACTOR)
        {
            return new GuiActor((EntityActor) entity);
        }
        else if (ID == DIRECTOR)
        {
            return null;
        }
        else if (ID == MODEL_BLOCK)
        {
            TileEntityModel model = (TileEntityModel) world.getTileEntity(new BlockPos(x, y, z));
            GuiDashboard dashboard = new GuiDashboard();

            dashboard.openPanel(dashboard.modelPanel.openModelBlock(model));

            return dashboard;
        }
        else if (ID == DASHBOARD)
        {
            return new GuiDashboard();
        }

        return null;
    }

    /**
     * Returns created playback GUI
     *
     * The reason behind creating it here, instead of in the
     * getClientGuiElement method, is because it may get Aperture's classes get
     * referenced which might cause I crash.
     *
     * So instead, I'm creating it here, so Method annotation would strip away
     * reference to {@link GuiPlayback} (which in turn will reference
     * Aperture's classes).
     */
    @Method(modid = "aperture")
    private Object getPlayback()
    {
        return new GuiPlayback();
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