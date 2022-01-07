package mchorse.blockbuster.network.server.gun;

import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.guns.PacketGunInfo;
import mchorse.blockbuster.utils.NBTUtils;
import mchorse.mclib.network.ServerMessageHandler;
import mchorse.mclib.utils.OpHelper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class ServerHandlerGunInfo extends ServerMessageHandler<PacketGunInfo>
{
    @Override
    public void run(EntityPlayerMP player, PacketGunInfo message)
    {
        if (!OpHelper.isPlayerOp(player))
        {
            return;
        }
        
        ItemStack stack = player.getHeldItemMainhand();

        if (NBTUtils.saveGunProps(stack, message.tag))
        {
            IMessage packet = new PacketGunInfo(message.tag, player.getEntityId());
            Dispatcher.sendTo(packet, player);
            Dispatcher.sendToTracked(player, packet);
        }
    }
}