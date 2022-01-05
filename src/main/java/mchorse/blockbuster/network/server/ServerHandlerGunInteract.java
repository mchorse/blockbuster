package mchorse.blockbuster.network.server;

import mchorse.blockbuster.CommonProxy;
import mchorse.blockbuster.common.GunProps;
import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.common.item.ItemGun;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.guns.PacketGunInfo;
import mchorse.blockbuster.network.common.guns.PacketGunInteract;
import mchorse.blockbuster.recording.actions.Action;
import mchorse.blockbuster.recording.actions.ItemUseAction;
import mchorse.blockbuster.recording.actions.ShootGunAction;
import mchorse.blockbuster.utils.NBTUtils;
import mchorse.mclib.network.ServerMessageHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;

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
            Entity entity = entityPlayerMP.world.getEntityByID(packetGunInteract.id);
            GunProps props = NBTUtils.getGunProps(packetGunInteract.itemStack);
            if (props==null){return;}
            if (entity instanceof EntityPlayer){
                if (props.getGUNState()== ItemGun.GunState.READY_TO_SHOOT) {
                    Dispatcher.sendTo(new PacketGunInteract(packetGunInteract.itemStack, packetGunInteract.id), entityPlayerMP);
    
                    gun.shootIt(packetGunInteract.itemStack, (EntityPlayer) entity, entityPlayerMP.world);
                }
            }
            if (entity instanceof EntityActor){
                if (props.getGUNState()== ItemGun.GunState.READY_TO_SHOOT) {
                    Dispatcher.sendTo(new PacketGunInteract(packetGunInteract.itemStack,  packetGunInteract.id), entityPlayerMP);
    
                    gun.shootIt(packetGunInteract.itemStack, ((EntityActor) entity).fakePlayer, entityPlayerMP.world);
                }

            }
        }
 
        
    }
}