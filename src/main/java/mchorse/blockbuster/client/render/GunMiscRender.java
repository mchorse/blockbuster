package mchorse.blockbuster.client.render;

import akka.actor.Props;
import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.client.KeyboardHandler;
import mchorse.blockbuster.client.RenderingHandler;
import mchorse.blockbuster.common.GunProps;
import mchorse.blockbuster.common.item.ItemGun;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.guns.PacketGunInteract;
import mchorse.blockbuster.network.common.guns.PacketZoomCommand;
import mchorse.blockbuster.network.server.gun.ServerHandlerZoomCommand;
import mchorse.blockbuster.utils.NBTUtils;
import mchorse.mclib.events.RenderOverlayEvent;
import mchorse.mclib.math.functions.limit.Min;
import mchorse.mclib.utils.Interpolation;
import mchorse.metamorph.api.MorphUtils;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;
import scala.sys.Prop;

import static mchorse.blockbuster.client.KeyboardHandler.gun_shoot;

/**
 * \* User: Evanechecssss
 * \* https://bio.link/evanechecssss
 * \* Data: 17.11.2021
 * \* Description:
 * \
 */
@SideOnly(Side.CLIENT)
public class GunMiscRender {

    public static float ZOOM_TIME;
    private boolean hasChangedSensitivity = false;
    private float lastMouseSensitivity;
    public Vector3f translate = new Vector3f();

    public Vector3f scale = new Vector3f(1.0F, 1.0F, 1.0F);

    public Vector3f rotate = new Vector3f();
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onTick(TickEvent.RenderTickEvent event) {
        if (Minecraft.getMinecraft().player != null && event.phase.equals(TickEvent.Phase.END)) {
            EntityPlayer entityPlayer = Minecraft.getMinecraft().player;

            ItemStack heldItem = entityPlayer.getHeldItem(EnumHand.MAIN_HAND);
            GunProps props =  NBTUtils.getGunProps(heldItem);
            if ( heldItem!= null && heldItem.getItem().equals(Blockbuster.gunItem) && KeyboardHandler.zoom.isKeyDown()) {
                ServerHandlerZoomCommand.onZoom = true;
                ZOOM_TIME = Math.min(ZOOM_TIME + (event.renderTickTime * 0.1f), 1);
                Dispatcher.sendToServer(new PacketZoomCommand(Minecraft.getMinecraft().player.getEntityId(),true));
            } else {
                ServerHandlerZoomCommand.onZoom = false;
                ZOOM_TIME = Math.max(ZOOM_TIME - (event.renderTickTime * 0.2f), 0);
                Dispatcher.sendToServer(new PacketZoomCommand(Minecraft.getMinecraft().player.getEntityId(),false));
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

                   Minecraft.getMinecraft().gameSettings.mouseSensitivity = lastMouseSensitivity * (0.4f - (ZOOM_TIME * 0.5f));
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
                Minecraft mc = Minecraft.getMinecraft();
                mc.renderGlobal.setDisplayListEntitiesDirty();
                mc.entityRenderer.loadEntityShader(mc.getRenderViewEntity());
            }

        }else {
            event.setNewfov(event.getFov());
        }

    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority =  EventPriority.HIGHEST)
    public void renderGameOverlay(RenderGameOverlayEvent event)
    {
        if (event.getType() == RenderGameOverlayEvent.ElementType.CROSSHAIRS){
            Minecraft mc =    Minecraft.getMinecraft();
            ItemStack gun = mc.player.getHeldItemMainhand();
            if (gun.getItem() instanceof ItemGun){
                GunProps props = NBTUtils.getGunProps(gun);
                if (props == null){
                    return;
                }

                if((props.hideAimOnZoom && KeyboardHandler.zoom.isKeyDown()) || !props.current_aim.isEmpty()){
                    event.setCanceled(true);
                }
            }
        }
    }
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onHUDRender(RenderGameOverlayEvent.Post event)
    {
        ScaledResolution resolution = event.getResolution();

        if (event.getType() == RenderGameOverlayEvent.ElementType.ALL)
        {
            EntityPlayer player = Minecraft.getMinecraft().player;
            if (player.getHeldItemMainhand().getItem() instanceof ItemGun)
            {
                GunProps gunProps = NBTUtils.getGunProps(player.getHeldItemMainhand());
                if (gunProps.aimMorph!=null && !(KeyboardHandler.zoom.isKeyDown() && gunProps.hideAimOnZoom )){
                    render(gunProps.current_aim.get(),resolution.getScaledWidth(), resolution.getScaledHeight());
                }



            }

        }
    }
    public void render(AbstractMorph morph, int width, int height)
    {
        if (morph!=null)
        {
        Minecraft mc = Minecraft.getMinecraft();
            GlStateManager.pushMatrix();
            enableGLStates();
            GL11.glTranslatef(0.5F, 0, 0.5F);
            morph.renderOnScreen(mc.player, (width/2 )+(int) morph.cachedTranslation.x, (height/2 )+ (int) morph.cachedTranslation.y, 15, 1f);
            GlStateManager.popMatrix();
        }
    }
    private void enableGLStates() {
        RenderHelper.enableStandardItemLighting();GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableDepth();
        GlStateManager.disableCull();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }
}