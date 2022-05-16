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
import net.minecraft.item.ItemStack;

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
        interactWithGun(player, player.world.getEntityByID(packet.id), packet.stack);
    }

    public static void interactWithGun(EntityPlayerMP player, Entity entity, ItemStack stack)
    {
        if (!(stack.getItem() instanceof ItemGun))
        {
            return;
        }

        ItemGun gun = (ItemGun) stack.getItem();
        GunProps props = NBTUtils.getGunProps(stack);

        if (props == null)
        {
            return;
        }

        EntityPlayer entityPlayer = entity instanceof EntityPlayer ? (EntityPlayer) entity : ((EntityActor) entity).fakePlayer;

        if (props.state == ItemGun.GunState.READY_TO_SHOOT && (entity instanceof EntityActor || props.storedShotDelay == 0))
        {
            if (player != null)
            {
                Dispatcher.sendTo(new PacketGunInteract(stack, entity.getEntityId()), player);
            }

            gun.shootIt(stack, entityPlayer, entityPlayer.world);
        }
    }
}