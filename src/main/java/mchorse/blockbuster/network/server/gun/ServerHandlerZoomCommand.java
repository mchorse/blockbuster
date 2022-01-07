package mchorse.blockbuster.network.server.gun;

import mchorse.blockbuster.common.GunProps;
import mchorse.blockbuster.common.item.ItemGun;
import mchorse.blockbuster.network.common.guns.PacketGunReloading;
import mchorse.blockbuster.network.common.guns.PacketZoomCommand;
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
public class ServerHandlerZoomCommand extends ServerMessageHandler<PacketZoomCommand>
{
    public static boolean onZoom = true;
    private static boolean was = false;
    
    @Override
    public void run (EntityPlayerMP player, PacketZoomCommand message)
    {
        if (!(player.getHeldItemMainhand().getItem() instanceof ItemGun))
        {
            return;
        }
        ItemGun gun = (ItemGun) player.getHeldItemMainhand().getItem();
        Entity entity = player.world.getEntityByID(message.entity);
        GunProps props = NBTUtils.getGunProps(player.getHeldItemMainhand());
        
        if (props==null)
        {
            return;
        }
        
        if (!(entity instanceof EntityPlayer))
        {
            return;
        }
        
        if (message.zoomOn)
        {
            if (!props.zoomOnCommand.isEmpty() && onZoom && !was)
            {
                player.getServer().commandManager.executeCommand(player, props.zoomOnCommand);
                was = true;
            }
        }
        else
        {
            if (!props.zoomOffCommand.isEmpty() && !onZoom && was)
            {
                player.getServer().commandManager.executeCommand(player, props.zoomOffCommand);
                was = false;
            }
        }
    }
}