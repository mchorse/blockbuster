package mchorse.blockbuster.events;

import java.lang.reflect.Field;

import mchorse.blockbuster.client.KeyboardHandler;
import mchorse.blockbuster.common.GunProps;
import mchorse.blockbuster.common.item.ItemGun;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.guns.PacketGunInteract;
import mchorse.blockbuster.network.common.guns.PacketGunReloading;
import mchorse.blockbuster.utils.NBTUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * \* User: Evanechecssss
 * \* https://evanechecssss.github.io
 * \
 */

public class GunShootHandler
{
    private boolean canBeShotPress = true;
    private boolean canBeReloaded = true;
    private Field leftClickCounter = null;

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onTick(TickEvent.ClientTickEvent event)
    {
        if (event.phase != TickEvent.Phase.END)
        {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();

        if (mc.player == null)
        {
            return;
        }

        ItemStack stack = mc.player.getHeldItemMainhand();

        if (stack.getItem() instanceof ItemGun)
        {
            this.blockLeftClick(mc);
            this.handleShootKey(mc, stack);
            this.handleReloading(mc, stack);
        }
    }

    @SideOnly(Side.CLIENT)
    private void handleShootKey(Minecraft mc, ItemStack stack)
    {
        GunProps props = NBTUtils.getGunProps(stack);

        if (KeyboardHandler.gunShoot.isKeyDown())
        {
            if (canBeShotPress && props.storedShotDelay == 0 && props.state == ItemGun.GunState.READY_TO_SHOOT)
            {
                Dispatcher.sendToServer(new PacketGunInteract(stack, mc.player.getEntityId()));

                canBeShotPress = false;

                return;
            }

            if (props.storedShotDelay == 0 && props.shootWhenHeld)
            {
                canBeShotPress = true;
            }
        }
        else
        {
            canBeShotPress = true;
        }
    }

    @SideOnly(Side.CLIENT)
    private void handleReloading(Minecraft mc, ItemStack stack)
    {
        GunProps props = NBTUtils.getGunProps(stack);

        if (KeyboardHandler.gunReload.isKeyDown() && canBeReloaded && props.state == ItemGun.GunState.READY_TO_SHOOT)
        {
            Dispatcher.sendToServer(new PacketGunReloading(stack, mc.player.getEntityId()));

            canBeReloaded = false;
        }
        else
        {
            canBeReloaded = true;
        }
    }

    @SideOnly(Side.CLIENT)
    private void blockLeftClick(Minecraft mc)
    {
        if (KeyboardHandler.gunShoot.conflicts(mc.gameSettings.keyBindAttack))
        {
            if (leftClickCounter == null)
            {
                try
                {
                    leftClickCounter = Minecraft.class.getDeclaredField("field_71429_W");
                    leftClickCounter.setAccessible(true);
                }
                catch (NoSuchFieldException | SecurityException e)
                {
                    try
                    {
                        leftClickCounter = Minecraft.class.getDeclaredField("leftClickCounter");
                        leftClickCounter.setAccessible(true);
                    }
                    catch (NoSuchFieldException | SecurityException e1)
                    {}
                }
            }

            try
            {
                this.leftClickCounter.setInt(mc, 10000);
            }
            catch (IllegalArgumentException | IllegalAccessException e)
            {}
        }
    }
}