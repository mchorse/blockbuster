package mchorse.blockbuster.network.client.guns;

import mchorse.blockbuster.common.item.ItemGun;
import mchorse.blockbuster.network.common.guns.PacketGunInfo;
import mchorse.blockbuster.network.common.guns.PacketGunInteract;
import mchorse.mclib.network.ClientMessageHandler;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;

/**
 * \* User: Evanechecssss
 * \* https://evanechecssss.github.io
 * \
 */
public class ClientHandlerGunInteract extends ClientMessageHandler<PacketGunInteract> {
    @Override
    public void run(EntityPlayerSP entityPlayerSP, PacketGunInteract packetGunInteract) {
        if (entityPlayerSP.world.isRemote){
            ItemGun gun = (ItemGun) packetGunInteract.itemStack.getItem();
            Entity entity = entityPlayerSP.world.getEntityByID(packetGunInteract.id);
            if (entity instanceof EntityPlayer){
                gun.shootIt(packetGunInteract.itemStack,entityPlayerSP, entity.world);
            }
        }
    }
}