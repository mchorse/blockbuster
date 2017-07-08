package mchorse.blockbuster.common;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.aperture.CameraHandler;
import mchorse.blockbuster.aperture.gui.GuiPlayback;
import mchorse.blockbuster.client.gui.GuiActor;
import mchorse.blockbuster.client.gui.GuiDirector;
import mchorse.blockbuster.common.entity.EntityActor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional.Method;
import net.minecraftforge.fml.common.network.IGuiHandler;

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

    /**
     * Shortcut for {@link EntityPlayer#openGui(Object, int, World, int, int, int)}
     */
    public static void open(EntityPlayer player, int ID, int x, int y, int z)
    {
        player.openGui(Blockbuster.instance, ID, player.worldObj, x, y, z);
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
            return new GuiDirector(new BlockPos(x, y, z));
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