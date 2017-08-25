package mchorse.blockbuster.network.client.director;

import mchorse.blockbuster.client.gui.GuiConfirm;
import mchorse.blockbuster.network.client.ClientMessageHandler;
import mchorse.blockbuster.network.common.director.PacketConfirmBreak;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClientHandlerConfirmBreak extends ClientMessageHandler<PacketConfirmBreak>
{
    @Override
    @SideOnly(Side.CLIENT)
    public void run(EntityPlayerSP player, PacketConfirmBreak message)
    {
        Minecraft mc = Minecraft.getMinecraft();

        if (mc.currentScreen == null)
        {
            mc.displayGuiScreen(new GuiConfirm(message.pos, message.count));
        }
    }
}