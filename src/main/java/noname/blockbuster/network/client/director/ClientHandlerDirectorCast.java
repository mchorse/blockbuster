package noname.blockbuster.network.client.director;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import noname.blockbuster.GuiHandler;
import noname.blockbuster.client.gui.GuiDirector;
import noname.blockbuster.network.client.ClientMessageHandler;
import noname.blockbuster.network.common.director.PacketDirectorCast;

public class ClientHandlerDirectorCast extends ClientMessageHandler<PacketDirectorCast>
{
    @Override
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
