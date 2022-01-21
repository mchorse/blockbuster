package mchorse.blockbuster.events;

import mchorse.blockbuster.client.KeyboardHandler;
import mchorse.blockbuster.common.GunProps;
import mchorse.blockbuster.common.item.ItemGun;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.guns.PacketGunInteract;
import mchorse.blockbuster.network.common.guns.PacketGunReloading;
import mchorse.blockbuster.utils.NBTUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
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
    private boolean canBeShotDown = true;

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
            this.handleShootKeyPress(mc, stack);
            this.handleShootKeyDown(mc, stack);
            this.handleReloading(mc, stack);
        }
    }

    @SideOnly(Side.CLIENT)
    private void handleShootKeyPress(Minecraft mc, ItemStack stack)
    {
        GunProps props = NBTUtils.getGunProps(stack);

        if (KeyboardHandler.gunShoot.isPressed() && !props.shootWhenHeld)
        {
            if (canBeShotPress && props.storedShotDelay == 0 && props.state == ItemGun.GunState.READY_TO_SHOOT)
            {
                Dispatcher.sendToServer(new PacketGunInteract(stack, mc.player.getEntityId()));

                canBeShotPress = false;

                return;
            }

            if (props.storedShotDelay == 0)
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
        if (KeyboardHandler.gunReload.isKeyDown() && canBeReloaded)
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
    private void handleShootKeyDown(Minecraft mc, ItemStack stack)
    {
        GunProps props = NBTUtils.getGunProps(stack);

        if (KeyboardHandler.gunShoot.isKeyDown() && props.shootWhenHeld)
        {
            if (canBeShotDown && props.storedShotDelay == 0 && props.state == ItemGun.GunState.READY_TO_SHOOT)
            {
                Dispatcher.sendToServer(new PacketGunInteract(stack, mc.player.getEntityId()));

                canBeShotDown = false;

                return;
            }

            if (props.storedShotDelay == 0)
            {
                canBeShotDown = true;
            }
        }
        else
        {
            canBeShotDown = true;
        }
    }
}