package mchorse.blockbuster.network.server.gun;

import mchorse.blockbuster.common.item.ItemGun;
import mchorse.blockbuster.network.common.guns.PacketGunReloading;
import mchorse.mclib.network.ServerMessageHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

/**
 * \* User: Evanechecssss
 * \* https://evanechecssss.github.io
 * \
 */
public class ServerHandlerGunReloading extends ServerMessageHandler<PacketGunReloading>
{
    @Override
    public void run(EntityPlayerMP entityPlayerMP, PacketGunReloading packet)
    {
        ItemStack item = entityPlayerMP.getHeldItemMainhand();
        
        if (item.getItem() instanceof ItemGun)
        {
            ItemGun gun = (ItemGun) item.getItem();

            gun.reload(entityPlayerMP, item);
        }
    }
}