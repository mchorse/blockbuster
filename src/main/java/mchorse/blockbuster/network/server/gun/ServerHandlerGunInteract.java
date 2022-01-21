package mchorse.blockbuster.network.server.gun;

import mchorse.blockbuster.common.GunProps;
import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.common.item.ItemGun;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.guns.PacketGunInteract;
import mchorse.blockbuster.utils.NBTUtils;
import mchorse.mclib.network.ServerMessageHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * \* User: Evanechecssss
 * \* https://evanechecssss.github.io
 * \
 */
public class ServerHandlerGunInteract extends ServerMessageHandler<PacketGunInteract>
{
    @Override
    public void run(EntityPlayerMP player, PacketGunInteract packet)
    {
        if (!(packet.stack.getItem() instanceof ItemGun))
        {
            return;
        }

        ItemGun gun = (ItemGun) packet.stack.getItem();
        Entity entity = player.world.getEntityByID(packet.id);
        GunProps props = NBTUtils.getGunProps(packet.stack);

        if (props == null)
        {
            return;
        }

        if (entity instanceof EntityPlayer)
        {
            if (props.state == ItemGun.GunState.READY_TO_SHOOT && props.storedShotDelay == 0)
            {
                Dispatcher.sendTo(new PacketGunInteract(packet.stack, packet.id), player);
                gun.shootIt(packet.stack, (EntityPlayer) entity, player.world);
            }
        }
        else if (entity instanceof EntityActor)
        {
            if (props.state == ItemGun.GunState.READY_TO_SHOOT)
            {
                Dispatcher.sendTo(new PacketGunInteract(packet.stack, packet.id), player);
                gun.shootIt(packet.stack, ((EntityActor) entity).fakePlayer, player.world);
            }
        }
    }
}