package mchorse.blockbuster.events;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.client.KeyboardHandler;
import mchorse.blockbuster.common.GunProps;
import mchorse.blockbuster.common.item.ItemGun;
import mchorse.blockbuster.utils.NBTUtils;
import mchorse.mclib.events.RenderOverlayEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * \* User: Evanechecssss
 * \* https://bio.link/evanechecssss
 * \* Data: 17.11.2021
 * \* Description:
 * \
 */
@SideOnly(Side.CLIENT)
public class GunZoomRender {

    public static float ZOOM_TIME;
    private boolean hasChangedSensitivity = false;
    private float lastMouseSensitivity;

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onTick(TickEvent.RenderTickEvent event) {
        if (Minecraft.getMinecraft().player != null && event.phase.equals(TickEvent.Phase.END)) {
            EntityPlayer entityPlayer = Minecraft.getMinecraft().player;

            ItemStack heldItem = entityPlayer.getHeldItem(EnumHand.MAIN_HAND);
            GunProps props =  NBTUtils.getGunProps(heldItem);
            if ( heldItem!= null && heldItem.getItem().equals(Blockbuster.gunItem) && KeyboardHandler.zoom.isKeyDown()) {
                    ZOOM_TIME = Math.min(ZOOM_TIME + (event.renderTickTime * 0.1f), 1);
            } else {
                ZOOM_TIME = Math.max(ZOOM_TIME - (event.renderTickTime * 0.2f), 0);
            }

            if (ZOOM_TIME == 0) {
                if (hasChangedSensitivity) {
                    hasChangedSensitivity = false;
                    Minecraft.getMinecraft().gameSettings.mouseSensitivity = lastMouseSensitivity;
                } else {
                    lastMouseSensitivity = Minecraft.getMinecraft().gameSettings.mouseSensitivity;
                }
            } else if (ZOOM_TIME != 0) {
                if (heldItem != null && heldItem.getItem().equals(Blockbuster.gunItem) && KeyboardHandler.zoom.isKeyDown()) {
                    hasChangedSensitivity = true;
                    assert props != null;
                    Minecraft.getMinecraft().gameSettings.mouseSensitivity = lastMouseSensitivity * (1f - (ZOOM_TIME *props.zoom ));
                } else {
                    hasChangedSensitivity = true;
                    Minecraft.getMinecraft().gameSettings.mouseSensitivity = lastMouseSensitivity;
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void fovUpdateEvent(FOVUpdateEvent event){
        ItemStack heldItem = event.getEntity().getHeldItem(EnumHand.MAIN_HAND);
        if (heldItem!= null && heldItem.getItem().equals(Blockbuster.gunItem)) {
            GunProps props =  NBTUtils.getGunProps(heldItem);
            if(props!=null){
                event.setNewfov(event.getFov() - event.getFov() * ZOOM_TIME * props.zoom);

            }

        }else {
            event.setNewfov(event.getFov());
        }

    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void renderOverlayEvent(RenderOverlayEvent event){

    }

}