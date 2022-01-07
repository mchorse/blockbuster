package mchorse.blockbuster.network.server.gun;

import mchorse.blockbuster.common.GunProps;
import mchorse.blockbuster.common.item.ItemGun;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.guns.PacketGunInfo;
import mchorse.blockbuster.network.common.guns.PacketGunReloading;
import mchorse.blockbuster.utils.NBTUtils;
import mchorse.mclib.network.ServerMessageHandler;
import mchorse.mclib.utils.OpHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

/**
 * \* User: Evanechecssss
 * \* https://evanechecssss.github.io
 * \
 */
public class ServerHandlerGunReloading  extends ServerMessageHandler<PacketGunReloading>
{
    
    @Override
    public void run(EntityPlayerMP entityPlayerMP, PacketGunReloading packetGunReloading)
    {
        if (!entityPlayerMP.world.isRemote){
            if (!(packetGunReloading.itemStack.getItem() instanceof ItemGun))
            {
                return;
            }
            ItemGun gun = (ItemGun) packetGunReloading.itemStack.getItem();
            Entity entity = entityPlayerMP.world.getEntityByID(packetGunReloading.id);
            GunProps props = NBTUtils.getGunProps(packetGunReloading.itemStack);
            if (props==null)
            {
                return;
            }
            
            if (entity instanceof EntityPlayer)
            {
                EntityPlayer player = (EntityPlayer) entity;
                if (props.getGUNState()== ItemGun.GunState.NEED_TO_BE_RELOAD)
                {
                    if (!player.capabilities.isCreativeMode && !props.ammoStack.isEmpty())
                    {
                        ItemStack ammo = props.ammoStack;
                        if (gun.consumeAmmoStack(player,ammo))
                        {
                            props.setGUNState(ItemGun.GunState.RELOADING);
                            update((EntityPlayerMP) player,props.toNBT());
                            ammo(packetGunReloading.itemStack,props,player);
                        }
                    }
                    else
                    {
                        props.setGUNState(ItemGun.GunState.RELOADING);
                        update((EntityPlayerMP) player,props.toNBT());
                        ammo(packetGunReloading.itemStack,props,player);
                    }
                }
            }
        }
    }
    private void update(EntityPlayerMP player, NBTTagCompound compound)
    {
        if (!OpHelper.isPlayerOp(player))
        {
            return;
        }
    
        ItemStack stack = player.getHeldItemMainhand();
    
        if (NBTUtils.saveGunProps(stack, compound))
        {
            IMessage packet = new PacketGunInfo(compound, player.getEntityId());
            Dispatcher.sendTo(packet, player);
            Dispatcher.sendToTracked(player, packet);
        }
    }
    private void ammo(ItemStack stack,GunProps props, EntityPlayer entityPlayer)
    {
        props.setGUNState(ItemGun.GunState.RELOADING);
        props.reloadTick = props.inputReloadingTime;
        if (!props.reloadCommand.isEmpty())
        {
            entityPlayer.getServer().commandManager.executeCommand(entityPlayer, props.reloadCommand);
        }
        update((EntityPlayerMP) entityPlayer,props.toNBT());
    }
}