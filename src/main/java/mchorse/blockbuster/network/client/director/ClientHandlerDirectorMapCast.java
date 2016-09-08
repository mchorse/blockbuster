package mchorse.blockbuster.network.client.director;

import mchorse.blockbuster.GuiHandler;
import mchorse.blockbuster.client.gui.GuiDirectorMap;
import mchorse.blockbuster.network.client.ClientMessageHandler;
import mchorse.blockbuster.network.common.director.PacketDirectorMapCast;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;

public class ClientHandlerDirectorMapCast extends ClientMessageHandler<PacketDirectorMapCast>
{
    @Override
    public void run(EntityPlayerSP player, PacketDirectorMapCast message)
    {
        if (Minecraft.getMinecraft().currentScreen == null)
        {
            int x = message.pos.getX();
            int y = message.pos.getY();
            int z = message.pos.getZ();

            GuiHandler.open(player, GuiHandler.DIRECTOR_MAP, x, y, z);
        }

        GuiScreen screen = Minecraft.getMinecraft().currentScreen;

        if (screen instanceof GuiDirectorMap)
        {
            ((GuiDirectorMap) screen).setCast(message.cast);
        }
    }
}
