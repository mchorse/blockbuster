package mchorse.blockbuster.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * Base of all MessageHandlers.
 *
 * @author Ernio (Ernest Sadowski)
 */
public abstract class AbstractMessageHandler<T extends IMessage> implements IMessageHandler<T, IMessage>
{
    /**
     * Handle a message received on the client side
     *
     * @return a message to send back to the Server, or null if no reply is
     *         necessary
     */
    @SideOnly(Side.CLIENT)
    public abstract IMessage handleClientMessage(final T message);

    /**
     * Handle a message received on the server side
     *
     * @return a message to send back to the Client, or null if no reply is
     *         necessary
     */
    public abstract IMessage handleServerMessage(final EntityPlayerMP player, final T message);

    @Override
    public IMessage onMessage(T message, MessageContext ctx)
    {
        if (ctx.side.isClient())
        {
            return this.handleClientMessage(message);
        }
        else
        {
            return this.handleServerMessage(ctx.getServerHandler().playerEntity, message);
        }
    }
}