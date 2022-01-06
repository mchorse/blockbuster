package mchorse.blockbuster.network.server;

import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.guns.PacketGunInfo;
import mchorse.blockbuster.network.common.guns.PacketGunInfoStack;
import mchorse.blockbuster.utils.NBTUtils;
import mchorse.mclib.network.ServerMessageHandler;
import mchorse.mclib.utils.OpHelper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

/**
 * \* User: Evanechecssss
 * \* https://evanechecssss.github.io
 * \
 */
public class ServerHandlerGunInfoStack extends ServerMessageHandler<PacketGunInfoStack>
{
    
    @Override
    public void run (EntityPlayerMP player, PacketGunInfoStack message) {
        if (!OpHelper.isPlayerOp(player))
        {
            return;
        }
        ItemStack stack = message.stack;
        if (NBTUtils.saveGunProps(stack, message.tag))
        {
            IMessage packet = new PacketGunInfoStack(message.tag, stack);
            Dispatcher.sendTo(packet, player);
            Dispatcher.sendToTracked(player, packet);
        }
    }
    
}