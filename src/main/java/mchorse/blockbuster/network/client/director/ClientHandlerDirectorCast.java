package mchorse.blockbuster.network.client.director;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mchorse.blockbuster.client.gui.GuiDirector;
import mchorse.blockbuster.common.GuiHandler;
import mchorse.blockbuster.network.client.ClientMessageHandler;
import mchorse.blockbuster.network.common.director.PacketDirectorCast;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;

/**
 * Client handler director cast
 *
 * This client handler is responsible for transferring requested director block
 * cast to current {@link GuiDirector}.
 */
public class ClientHandlerDirectorCast extends ClientMessageHandler<PacketDirectorCast>
{
    @Override
    @SideOnly(Side.CLIENT)
    public void run(EntityPlayerSP player, PacketDirectorCast message)
    {
        if (Minecraft.getMinecraft().currentScreen == null)
        {
            int x = message.pos.getX();
            int y = message.pos.getY();
            int z = message.pos.getZ();

            GuiHandler.open(player, GuiHandler.DIRECTOR, x, y, z);
        }

        GuiScreen screen = Minecraft.getMinecraft().currentScreen;

        if (screen instanceof GuiDirector)
        {
            ((GuiDirector) screen).setCast(message.actors);
        }
    }
}