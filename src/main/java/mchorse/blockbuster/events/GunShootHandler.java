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

public class GunShootHandler {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerTick(TickEvent.ClientTickEvent event){
            if (event.side.isClient()) {
                if (event.phase.equals(TickEvent.Phase.START)) {
                    execute();

                }
            }
    }
    boolean flag = true;
    @SideOnly(Side.CLIENT)
    private void execute() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player==null){return;}
        ItemStack stack = mc.player.getHeldItemMainhand();
        if (stack.getItem() instanceof ItemGun)
        {
            GunProps props = NBTUtils.getGunProps(stack);
            if (gun_shoot.isKeyDown() && props.acceptPressed && flag)
            {
                ItemGun gun = (ItemGun) stack.getItem();
                Dispatcher.sendToServer(new PacketGunInteract(stack, mc.player.getEntityId()));
                flag = false;
                return;
            }
            if (gun_shoot.isPressed()&& flag)
            {
                ItemGun gun = (ItemGun) stack.getItem();
                Dispatcher.sendToServer(new PacketGunInteract(stack, mc.player.getEntityId()));
                flag = false;
                return;
            }

            if (gun_reload.isKeyDown()&& flag){
                ItemGun gun = (ItemGun) stack.getItem();
                Dispatcher.sendToServer(new PacketGunReloading(stack, mc.player.getEntityId()));
                flag = false;
                return;

            }

            flag = true;
        }
    }
}