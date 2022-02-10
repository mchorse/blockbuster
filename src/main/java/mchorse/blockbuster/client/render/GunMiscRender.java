package mchorse.blockbuster.client.render;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.ClientProxy;
import mchorse.blockbuster.client.KeyboardHandler;
import mchorse.blockbuster.common.GunProps;
import mchorse.blockbuster.common.item.ItemGun;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.guns.PacketZoomCommand;
import mchorse.blockbuster.utils.NBTUtils;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.util.vector.Vector3f;

/**
 * \* User: Evanechecssss
 * \* https://bio.link/evanechecssss
 * \* Data: 17.11.2021
 * \* Description:
 * \
 */
@SideOnly(Side.CLIENT)
public class GunMiscRender
{
    public static float ZOOM_TIME;
    public static float UN_ZOOM_TIME;
    public static boolean onZoom = true;
    private boolean writeZoom = true;
    private boolean hasChangedSensitivity = false;
    private float lastMouseSensitivity;

    public Vector3f translate = new Vector3f();
    public Vector3f scale = new Vector3f(1F, 1F, 1F);
    public Vector3f rotate = new Vector3f();
    
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onTick(TickEvent.RenderTickEvent event)
    {
        if (Minecraft.getMinecraft().player != null && event.phase.equals(TickEvent.Phase.END))
        {
            EntityPlayer player = Minecraft.getMinecraft().player;
            ItemStack heldItem = player.getHeldItem(EnumHand.MAIN_HAND);
            GunProps props = NBTUtils.getGunProps(heldItem);
            Minecraft mc = Minecraft.getMinecraft();
            
            if (heldItem.getItem().equals(Blockbuster.gunItem))
            {
                this.handleZoom(event.renderTickTime);
                
                if (writeZoom)
                {
                    ClientProxy.oldFov = mc.gameSettings.fovSetting;
                    writeZoom = false;
                    mc.renderGlobal.setDisplayListEntitiesDirty();
                }
                
                if (onZoom)
                {
                    if (props != null)
                    {
                        mc.gameSettings.fovSetting = ClientProxy.oldFov - ClientProxy.oldFov * ZOOM_TIME * props.zoomFactor;
                        mc.renderGlobal.setDisplayListEntitiesDirty();
                    }
                }
                else
                {
                    mc.gameSettings.fovSetting = ClientProxy.oldFov;
                    mc.renderGlobal.setDisplayListEntitiesDirty();
                }
            }

            if (ZOOM_TIME == 0)
            {
                if (hasChangedSensitivity)
                {
                    hasChangedSensitivity = false;
                    Minecraft.getMinecraft().gameSettings.mouseSensitivity = lastMouseSensitivity;
                }
                else
                {
                    lastMouseSensitivity = Minecraft.getMinecraft().gameSettings.mouseSensitivity;
                }
            }
            else if (ZOOM_TIME != 0)
            {
                if (heldItem.getItem().equals(Blockbuster.gunItem) && KeyboardHandler.zoom.isKeyDown())
                {
                    hasChangedSensitivity = true;

                    if (props != null)
                    {
                        Minecraft.getMinecraft().gameSettings.mouseSensitivity = lastMouseSensitivity * props.mouseZoom - 0.3f;
                    }
                }
                else
                {
                    hasChangedSensitivity = true;
                    Minecraft.getMinecraft().gameSettings.mouseSensitivity = lastMouseSensitivity;
                }
            }

        }
    }
    
    private void handleZoom(float partialTick)
    {
        boolean zoomed = onZoom;
        
        if (KeyboardHandler.zoom.isKeyDown())
        {
            onZoom = true;
            ZOOM_TIME = Math.min(ZOOM_TIME + (partialTick * 0.1F), 1);
            UN_ZOOM_TIME = Math.max(UN_ZOOM_TIME - (partialTick * 0.2F), 0);
            
            if (!zoomed)
            {
                Dispatcher.sendToServer(new PacketZoomCommand(Minecraft.getMinecraft().player.getEntityId(), true));
            }
        }
        else
        {
            onZoom = false;
            ZOOM_TIME = Math.max(ZOOM_TIME - (partialTick * 0.1F), 0);
            UN_ZOOM_TIME = Math.min(UN_ZOOM_TIME + (partialTick * 0.2F), 1);
            
            if (zoomed)
            {
                Dispatcher.sendToServer(new PacketZoomCommand(Minecraft.getMinecraft().player.getEntityId(), false));
            }
        }
    }
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void renderGameOverlay(RenderGameOverlayEvent event)
    {
        if (event.getType() == RenderGameOverlayEvent.ElementType.CROSSHAIRS)
        {
            Minecraft mc = Minecraft.getMinecraft();
            ItemStack gun = mc.player.getHeldItemMainhand();

            if (gun.getItem() instanceof ItemGun)
            {
                GunProps props = NBTUtils.getGunProps(gun);

                if (props == null)
                {
                    return;
                }
                if ((props.hideCrosshairOnZoom && KeyboardHandler.zoom.isKeyDown()) || !props.currentCrosshair.isEmpty())
                {
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
                GunProps props = NBTUtils.getGunProps(player.getHeldItemMainhand());

                if (props != null && props.crosshairMorph != null && !(KeyboardHandler.zoom.isKeyDown() && props.hideCrosshairOnZoom))
                {
                    render(props.currentCrosshair.get(), resolution.getScaledWidth(), resolution.getScaledHeight());
                }
            }
        }
    }
    
    public void render(AbstractMorph morph, int width, int height)
    {
        if (morph == null)
        {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();

        GlStateManager.pushMatrix();
        GlStateManager.translate(0.5F, 0, 0.5F);
        enableGLStates();

        morph.renderOnScreen(mc.player, (width / 2) + (int) morph.cachedTranslation.x, (height / 2) + (int) morph.cachedTranslation.y, 15, 1f);

        GlStateManager.popMatrix();
    }
    
    private void enableGLStates()
    {
        RenderHelper.enableStandardItemLighting();
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableDepth();
        GlStateManager.disableCull();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }
}