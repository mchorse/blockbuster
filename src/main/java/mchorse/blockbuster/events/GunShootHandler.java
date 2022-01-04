package mchorse.blockbuster.events;

import mchorse.blockbuster.common.GunProps;
import mchorse.blockbuster.common.item.ItemGun;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.guns.PacketGunInteract;
import mchorse.blockbuster.network.common.guns.PacketGunReloading;
import mchorse.blockbuster.utils.NBTUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static mchorse.blockbuster.client.KeyboardHandler.gun_reload;
import static mchorse.blockbuster.client.KeyboardHandler.gun_shoot;

/**
 * \* User: Evanechecssss
 * \* https://evanechecssss.github.io
 * \
 */

public class GunShootHandler
{
    
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerTick(TickEvent.PlayerTickEvent event)
    {
            if (event.side.isClient())
            {
                if (event.phase.equals(TickEvent.Phase.END))
                {
                    
                    handlerShootKeyPress();
                    handlerShootKeyDown();
                    handlerReloading();
                }
            }
    }
    
    private boolean canBeShootPress =  true;
    @SideOnly(Side.CLIENT)
    private void handlerShootKeyPress()
    {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player==null){return;}
        ItemStack stack = mc.player.getHeldItemMainhand();
        if (stack.getItem() instanceof ItemGun)
        {
            GunProps props = NBTUtils.getGunProps(stack);
            if (gun_shoot.isPressed() && !props.acceptPressed)
            {
                if (canBeShootPress)
                {
                    ItemGun gun = (ItemGun) stack.getItem();
                    Dispatcher.sendToServer(new PacketGunInteract(stack, mc.player.getEntityId()));
                    canBeShootPress = false;
                }
            }
            else
            {
                canBeShootPress = true;
            }
        }
    }
    
    private boolean canBeReload= true;
    @SideOnly(Side.CLIENT)
    private void handlerReloading()
    {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player==null){return;}
        ItemStack stack = mc.player.getHeldItemMainhand();
        if (stack.getItem() instanceof ItemGun)
        {
            if (gun_reload.isKeyDown()){
                if (canBeReload)
                {
                ItemGun gun = (ItemGun) stack.getItem();
                Dispatcher.sendToServer(new PacketGunReloading(stack, mc.player.getEntityId()));
                canBeReload = false;
                }
            }
            else
            {
                canBeReload = true;
            }
        }
        
    }
    
    private boolean canBeShootDown =  true;
    @SideOnly(Side.CLIENT)
    private void handlerShootKeyDown()
    {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player==null){return;}
        ItemStack stack = mc.player.getHeldItemMainhand();
        if (stack.getItem() instanceof ItemGun)
        {
            GunProps props = NBTUtils.getGunProps(stack);
            if (gun_shoot.isKeyDown() && props.acceptPressed)
            {
                if (canBeShootDown)
                {
                    ItemGun gun = (ItemGun) stack.getItem();
                    Dispatcher.sendToServer(new PacketGunInteract(stack, mc.player.getEntityId()));
                    props.timeBetweenShoot=props.inputTimeBetweenShoot;
                    canBeShootDown = false;
                    
                }
                
                if (props.timeBetweenShoot==0){
                    canBeShootDown = true;
                }
               
            }
            else
            {
                canBeShootDown = true;
            }
            Minecraft.getMinecraft().player.sendMessage(new TextComponentString("" +canBeShootDown ));
    
        }
    }
    
}