package mchorse.blockbuster.network.client.guns;

import mchorse.blockbuster.common.GunProps;
import mchorse.blockbuster.common.item.ItemGun;
import mchorse.blockbuster.network.common.guns.PacketGunInteract;
import mchorse.blockbuster.utils.NBTUtils;
import mchorse.mclib.network.ClientMessageHandler;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * \* User: Evanechecssss
 * \* https://evanechecssss.github.io
 * \
 */
public class ClientHandlerGunInteract extends ClientMessageHandler<PacketGunInteract>
{
    @Override
    @SideOnly(Side.CLIENT)
    public void run(EntityPlayerSP player, PacketGunInteract packet)
    {
        if (!(packet.stack.getItem() instanceof ItemGun))
        {
            return;
        }

        ItemGun gun = (ItemGun) packet.stack.getItem();
        Entity entity = player.world.getEntityByID(packet.id);
        GunProps props = NBTUtils.getGunProps(packet.stack);

        if (entity instanceof EntityPlayer)
        {
            if (props.state == ItemGun.GunState.READY_TO_SHOOT && props.storedShotDelay == 0)
            {
                gun.shootIt(packet.stack, player, entity.world);
            }
        }
    }
}