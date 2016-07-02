package noname.blockbuster.network.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noname.blockbuster.network.AbstractMessageHandler;

/**
 * This class passes operation from Netty to Minecraft (Client) Thread. Also
 * prevents the server-side message handling method from appearing in client
 * message handler classes.
 *
 * @author Ernio (Ernest Sadowski)
 */
@SideOnly(Side.CLIENT)
public abstract class ClientMessageHandler<T extends IMessage> extends AbstractMessageHandler<T>
{
    public abstract void run(final EntityPlayerSP player, final T message);

    @Override
    public IMessage handleClientMessage(final T message)
    {
        Minecraft.getMinecraft().addScheduledTask(new Runnable()
        {
            @Override
            public void run()
            {
                ClientMessageHandler.this.run(Minecraft.getMinecraft().thePlayer, message);
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