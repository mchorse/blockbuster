package mchorse.blockbuster.network.client.guns;

import mchorse.blockbuster.common.GunProps;
import mchorse.blockbuster.common.item.ItemGun;
import mchorse.blockbuster.network.common.guns.PacketGunInfo;
import mchorse.blockbuster.network.common.guns.PacketGunInteract;
import mchorse.blockbuster.utils.NBTUtils;
import mchorse.mclib.network.ClientMessageHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Date;

/**
 * \* User: Evanechecssss
 * \* https://evanechecssss.github.io
 * \
 */
public class ClientHandlerGunInteract extends ClientMessageHandler<PacketGunInteract>
{
    
    @Override
    @SideOnly(Side.CLIENT)
    public void run(EntityPlayerSP entityPlayerSP, PacketGunInteract packetGunInteract) {
        if (entityPlayerSP.world.isRemote)
        {
            if (!(packetGunInteract.itemStack.getItem() instanceof ItemGun))
            {
                return;
            }
            ItemGun gun = (ItemGun) packetGunInteract.itemStack.getItem();
            Entity entity = entityPlayerSP.world.getEntityByID(packetGunInteract.id);
            GunProps props = NBTUtils.getGunProps(packetGunInteract.itemStack);
    
            if (entity instanceof EntityPlayer)
            {
                if (props.getGUNState()== ItemGun.GunState.READY_TO_SHOOT && props.timeBetweenShoot == 0) {
                    gun.shootIt(packetGunInteract.itemStack, entityPlayerSP, entity.world);
                }
            }
        }
    }
    
}