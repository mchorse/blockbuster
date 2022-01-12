package mchorse.blockbuster.events;

import mchorse.blockbuster.common.GunProps;
import mchorse.blockbuster.common.item.ItemGun;
import mchorse.blockbuster.utils.NBTUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * \* User: Evanechecssss
 * \* https://evanechecssss.github.io
 * \
 */
public class PlayerTickHandler
{
    
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void playerInteract(PlayerInteractEvent.EntityInteract event)
    {
        EntityPlayer player = event.getEntityPlayer();
        
        if (!(player.getHeldItemMainhand().getItem() instanceof ItemGun))
        {
            return;
        }
        
        ItemStack stack = player.getHeldItemMainhand();
        GunProps props = NBTUtils.getGunProps(stack);
        
        if (props == null)
        {
            return;
        }
        
        if (props.off_click)
        {
            if (event.isCancelable())
            {
                event.setCanceled(true);
                event.setCancellationResult(EnumActionResult.FAIL);
            }
        }
    }
    
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void playerInteract(PlayerInteractEvent.LeftClickBlock event)
    {
        EntityPlayer player = event.getEntityPlayer();
        
        if (!(player.getHeldItemMainhand().getItem() instanceof ItemGun))
        {
            return;
        }
        
        ItemStack stack = player.getHeldItemMainhand();
        GunProps props = NBTUtils.getGunProps(stack);
        
        if (props == null)
        {
            return;
        }
        
        if (props.int_click)
        {
            if (event.isCancelable())
            {
                event.setCanceled(true);
                event.setCancellationResult(EnumActionResult.FAIL);
            }
        }
    }
    
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onLivingAttack(LivingAttackEvent event)
    {
        Entity damaged = event.getEntity();
        Entity src =  event.getSource().getTrueSource();
        
        if (!(src instanceof EntityPlayer))
        {
            return;
        }
        
        EntityPlayer player = (EntityPlayer) src;
        
        if (!(player.getHeldItemMainhand().getItem() instanceof ItemGun))
        {
            return;
        }
        
        ItemStack stack = player.getHeldItemMainhand();
        GunProps props = NBTUtils.getGunProps(stack);
        
        if (props == null)
        {
            return;
        }
        
        if (props.ent_clock)
        {
            if (event.isCancelable())
            {
                event.setCanceled(true);
            }
        }
    }
    
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void playerInteract(PlayerInteractEvent.RightClickBlock event)
    {
        
        EntityPlayer player = event.getEntityPlayer();
        
        if (!(player.getHeldItemMainhand().getItem() instanceof ItemGun))
        {
            return;
        }
        
        ItemStack stack = player.getHeldItemMainhand();
        GunProps props = NBTUtils.getGunProps(stack);
        
        if (props == null)
        {
            return;
        }
        
        if (props.off_click)
        {
            if (event.isCancelable())
            {
                event.setCanceled(true);
                event.setCancellationResult(EnumActionResult.FAIL);
            }
        }
    }
    
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void playerInteract(PlayerInteractEvent.RightClickItem event)
    {
        EntityPlayer player = event.getEntityPlayer();
        
        if (!(player.getHeldItemMainhand().getItem() instanceof ItemGun))
        {
            return;
        }
        
        ItemStack stack = player.getHeldItemMainhand();
        GunProps props = NBTUtils.getGunProps(stack);
        
        if (props == null)
        {
            return;
        }
        
        if (props.off_click)
        {
            if (event.isCancelable())
            {
                event.setCanceled(true);
                event.setCancellationResult(EnumActionResult.FAIL);
            }
        }
    }
    

    
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