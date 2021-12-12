package mchorse.blockbuster.events;

import mchorse.blockbuster.common.item.ItemGun;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.guns.PacketGunInteract;
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

import static mchorse.blockbuster.client.KeyboardHandler.gun_shoot;

/**
 * \* User: Evanechecssss
 * \* https://evanechecssss.github.io
 * \
 */

public class GunShootHandler {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerTick(TickEvent.ClientTickEvent event){
       if (event.side.isClient()){
        if (event.phase.equals(TickEvent.Phase.START)) {
            execute();

        }
       }
    }
    @SideOnly(Side.CLIENT)
    private void execute(){
            Minecraft mc = Minecraft.getMinecraft();
            if (gun_shoot.isKeyDown()) {
                ItemStack stack = mc.player.getHeldItemMainhand();
                if (stack.getItem() instanceof ItemGun) {
                    ItemGun gun = (ItemGun) stack.getItem();
                    Dispatcher.sendToServer(new PacketGunInteract(stack,mc.player.getEntityId()));
                }

            }
    }
}