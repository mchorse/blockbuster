package mchorse.blockbuster.common;

import mchorse.blockbuster.client.gui.GuiActor;
import mchorse.blockbuster.client.gui.GuiDirectorNew;
import mchorse.blockbuster.client.gui.GuiPlayback;
import mchorse.blockbuster.common.entity.EntityActor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

/**
 * Gui handler class
 *
 * This class is responsible for opening GUIs.
 */
public class GuiHandler implements IGuiHandler
{
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
     * - Director map block management GUI
     *
     * IGuiHandler is used to centralize GUI invocations
     */
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        Entity entity = world.getEntityByID(x);

        if (ID == PLAYBACK)
        {
            return new GuiPlayback(player);
        }
        if (ID == ACTOR)
        {
            return new GuiActor(null, (EntityActor) entity);
        }
        else if (ID == DIRECTOR)
        {
            return new GuiDirectorNew(new BlockPos(x, y, z));
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
