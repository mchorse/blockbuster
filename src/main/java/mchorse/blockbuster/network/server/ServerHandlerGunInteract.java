package mchorse.blockbuster.network.server;

import mchorse.blockbuster.CommonProxy;
import mchorse.blockbuster.common.item.ItemGun;
import mchorse.blockbuster.network.common.guns.PacketGunInfo;
import mchorse.blockbuster.network.common.guns.PacketGunInteract;
import mchorse.blockbuster.recording.actions.Action;
import mchorse.blockbuster.recording.actions.ItemUseAction;
import mchorse.blockbuster.recording.actions.ShootGunAction;
import mchorse.mclib.network.ServerMessageHandler;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.List;

/**
 * \* User: Evanechecssss
 * \* https://evanechecssss.github.io
 * \
 */
public class ServerHandlerGunInteract extends ServerMessageHandler<PacketGunInteract>
{

    @Override
    public void run(EntityPlayerMP entityPlayerMP, PacketGunInteract packetGunInteract) {
        if (!entityPlayerMP.world.isRemote){
        ItemGun gun = (ItemGun) packetGunInteract.itemStack.getItem();
        gun.shootIt(packetGunInteract.itemStack,entityPlayerMP,entityPlayerMP.world);
        }

    }
}