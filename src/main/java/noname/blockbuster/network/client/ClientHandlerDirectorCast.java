package noname.blockbuster.network.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import noname.blockbuster.Blockbuster;
import noname.blockbuster.client.gui.GuiDirector;
import noname.blockbuster.network.common.director.PacketDirectorCast;

public class ClientHandlerDirectorCast extends ClientMessageHandler<PacketDirectorCast>
{
    @Override
    public void run(EntityPlayerSP player, PacketDirectorCast message)
    {
        if (Minecraft.getMinecraft().currentScreen == null)
        {
            player.openGui(Blockbuster.instance, 2, player.worldObj, message.pos.getX(), message.pos.getY(), message.pos.getZ());
        }

        GuiScreen screen = Minecraft.getMinecraft().currentScreen;

        if (screen instanceof GuiDirector)
        {
            ((GuiDirector) screen).setCast(message.actors);
        }
    }
}
