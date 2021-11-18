package mchorse.blockbuster.events;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.common.GunProps;
import mchorse.blockbuster.common.item.ItemGun;
import mchorse.blockbuster.utils.NBTUtils;
import mchorse.mclib.events.RenderOverlayEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
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
public class PlayerRender {

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void fovUpdateEvent(FOVUpdateEvent event){
        if (event.getEntity()!=null){
            ItemStack stack = event.getEntity().getHeldItemMainhand();
            if (stack.getItem().equals(Blockbuster.gunItem)){
               GunProps props =  NBTUtils.getGunProps(stack);
               if(props!=null){
                 if(Minecraft.getMinecraft().gameSettings.keyBindSneak.isKeyDown()) {
                     event.setNewfov(props.zoom);
                 }else {
                     event.setNewfov(event.getFov());
                 }
               }
            }else {
                event.setNewfov(event.getFov());
            }
        }

    }
}