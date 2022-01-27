package mchorse.blockbuster.events;

import mchorse.blockbuster.common.GunProps;
import mchorse.blockbuster.common.item.ItemGun;
import mchorse.blockbuster.utils.NBTUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.function.Function;

/**
 * \* User: Evanechecssss
 * \* https://evanechecssss.github.io
 * \
 */
public class PlayerTickHandler
{
    private Function<GunProps, Boolean> leftHandler = (props) -> props.preventLeftClick;
    private Function<GunProps, Boolean> rightHandler = (props) -> props.preventRightClick;
    private Function<GunProps, Boolean> attackHandler = (props) -> props.preventEntityAttack;

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onLivingAttack(LivingAttackEvent event)
    {
        Entity source = event.getSource().getTrueSource();

        if (source instanceof EntityPlayer)
        {
            this.handle((EntityPlayer) source, event, attackHandler);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent.LeftClickBlock event)
    {
        this.handle(event.getEntityPlayer(), event, leftHandler);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent.EntityInteract event)
    {
        this.handle(event.getEntityPlayer(), event, rightHandler);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent.RightClickBlock event)
    {
        this.handle(event.getEntityPlayer(), event, rightHandler);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent.RightClickItem event)
    {
        this.handle(event.getEntityPlayer(), event, rightHandler);
    }

    private void handle(EntityPlayer player, LivingEvent event, Function<GunProps, Boolean> handler)
    {
        ItemStack stack = player.getHeldItemMainhand();

        if (!(stack.getItem() instanceof ItemGun))
        {
            return;
        }

        GunProps props = NBTUtils.getGunProps(stack);

        if (props == null)
        {
            return;
        }

        if (handler.apply(props) && event.isCancelable())
        {
            event.setCanceled(true);

            if (event instanceof PlayerInteractEvent)
            {
                ((PlayerInteractEvent) event).setCancellationResult(EnumActionResult.FAIL);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerTick(TickEvent.PlayerTickEvent event)
    {
        EntityPlayer player = event.player;
        ItemStack stack = player.getHeldItemMainhand();

        if (stack.getItem() instanceof ItemGun)
        {
            ItemGun.decreaseReload(stack, player);
            ItemGun.decreaseTime(stack, player);
            ItemGun.checkGunState(stack, player);
        }
    }
}