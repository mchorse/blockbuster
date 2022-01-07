package mchorse.blockbuster.events;

import mchorse.blockbuster.common.item.ItemGun;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * \* User: Evanechecssss
 * \* https://evanechecssss.github.io
 * \
 */
public class PlayerTickHandler {
    
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void playerTick(TickEvent.PlayerTickEvent event)
    {
        EntityPlayer player = event.player;
        if (player.getHeldItemMainhand().getItem() instanceof ItemGun)
        {
            doGunStaff(player.getHeldItemMainhand(), player);
        }
    }
    private void doGunStaff(ItemStack stack, EntityPlayer player)
    {
        
        ItemGun.minusReload(stack, player);
        ItemGun.minusTime(stack, player);
        ItemGun.checkGunState(stack,player);
    }
    
}