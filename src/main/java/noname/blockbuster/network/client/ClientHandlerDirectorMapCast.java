package noname.blockbuster.network.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import noname.blockbuster.Blockbuster;
import noname.blockbuster.GuiHandler;
import noname.blockbuster.client.gui.GuiDirectorMap;
import noname.blockbuster.network.common.director.PacketDirectorMapCast;

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

            player.openGui(Blockbuster.instance, GuiHandler.DIRECTOR_MAP, player.worldObj, x, y, z);
        }

        GuiScreen screen = Minecraft.getMinecraft().currentScreen;

        if (screen instanceof GuiDirectorMap)
        {
            ((GuiDirectorMap) screen).setCast(message.cast);
        }
    }
}
