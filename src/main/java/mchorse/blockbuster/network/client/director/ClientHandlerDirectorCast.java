package mchorse.blockbuster.network.client.director;

import mchorse.blockbuster.client.gui.GuiDirector;
import mchorse.blockbuster.client.gui.GuiDirectorNew;
import mchorse.blockbuster.common.GuiHandler;
import mchorse.blockbuster.network.client.ClientMessageHandler;
import mchorse.blockbuster.network.common.director.PacketDirectorCast;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
        else if (screen instanceof GuiDirectorNew)
        {
            ((GuiDirectorNew) screen).setCast(message.actors);
        }
    }
}
