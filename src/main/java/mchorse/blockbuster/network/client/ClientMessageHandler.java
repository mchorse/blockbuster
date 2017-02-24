package mchorse.blockbuster.network.client;

import mchorse.blockbuster.network.AbstractMessageHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * This class passes operation from Netty to Minecraft (Client) Thread. Also
 * prevents the server-side message handling method from appearing in client
 * message handler classes.
 *
 * @author Ernio (Ernest Sadowski)
 */
public abstract class ClientMessageHandler<T extends IMessage> extends AbstractMessageHandler<T>
{
    @SideOnly(Side.CLIENT)
    public abstract void run(final EntityPlayerSP player, final T message);

    @Override
    @SideOnly(Side.CLIENT)
    public IMessage handleClientMessage(final T message)
    {
        Minecraft.getMinecraft().addScheduledTask(new Runnable()
        {
            @Override
            public void run()
            {
                ClientMessageHandler.this.run(Minecraft.getMinecraft().player, message);
            }
        });

        return null;
    }

    @Override
    public final IMessage handleServerMessage(final EntityPlayerMP player, final T message)
    {
        return null;
    }
}