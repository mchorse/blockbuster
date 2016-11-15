package mchorse.blockbuster.client;

import java.util.List;

import mchorse.blockbuster.capabilities.morphing.IMorphing;
import mchorse.blockbuster.capabilities.morphing.Morphing;
import mchorse.blockbuster.client.gui.GuiRecordingOverlay;
import mchorse.blockbuster.client.render.ItemRenderer;
import mchorse.blockbuster.client.render.RenderPlayer;
import mchorse.blockbuster.common.ClientProxy;
import mchorse.blockbuster.recording.RecordRecorder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Rendering handler
 *
 * This handler is another handler in this mod that responsible for rendering.
 * Currently this handler only renders recording overlay
 */
@SideOnly(Side.CLIENT)
public class RenderingHandler
{
    private GuiRecordingOverlay overlay;
    private RenderPlayer render;
    private ItemRenderer item;

    public RenderingHandler(GuiRecordingOverlay overlay, RenderPlayer render)
    {
        this.overlay = overlay;
        this.render = render;
        this.item = new ItemRenderer(Minecraft.getMinecraft(), render);
    }

    /**
     * Renders recording overlay during HUD rendering
     */
    @SubscribeEvent
    public void onHUDRender(RenderGameOverlayEvent.Post event)
    {
        ScaledResolution resolution = event.getResolution();

        if (event.getType() == RenderGameOverlayEvent.ElementType.ALL)
        {
            this.overlay.draw(resolution.getScaledWidth(), resolution.getScaledHeight());
        }
    }

    @SubscribeEvent
    public void onHUDRender(RenderGameOverlayEvent.Text event)
    {
        if (!Minecraft.getMinecraft().gameSettings.showDebugInfo)
        {
            return;
        }

        List<String> list = event.getLeft();

        list.add("");
        list.add(ClientProxy.manager.records.size() + " client records");

        RecordRecorder recorder = ClientProxy.manager.recorders.get(Minecraft.getMinecraft().thePlayer);

        if (recorder != null)
        {
            list.add("Recording frame " + recorder.tick + " (delay: " + recorder.delay + ")");
        }
    }

    @SubscribeEvent
    public void onHandRender(RenderHandEvent event)
    {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.thePlayer;
        EntityRenderer renderer = mc.entityRenderer;

        IMorphing capability = Morphing.get(player);
        boolean flag = mc.getRenderViewEntity() instanceof EntityLivingBase && ((EntityLivingBase) mc.getRenderViewEntity()).isPlayerSleeping();

        this.render.setupModel(player);

        if (capability == null || capability.getModel().isEmpty() || this.render.getMainModel() == null)
        {
            return;
        }

        if (mc.gameSettings.thirdPersonView == 0 && !flag && !mc.gameSettings.hideGUI && !mc.playerController.isSpectator())
        {
            event.setCanceled(true);

            renderer.enableLightmap();
            this.item.renderItemInFirstPerson(event.getPartialTicks());
            renderer.disableLightmap();
        }
    }

    @SubscribeEvent
    public void onClientTick(ClientTickEvent event)
    {
        if (event.phase == Phase.START)
        {
            return;
        }

        this.item.updateEquippedItem();
    }

    /**
     * Morph (render) player into custom model if player has its variables
     * related to morphing (model and skin)
     */
    @SubscribeEvent
    public void onPlayerRender(RenderPlayerEvent.Pre event)
    {
        EntityPlayer player = event.getEntityPlayer();
        IMorphing capability = Morphing.get(player);

        if (capability == null || capability.getModel().isEmpty()) return;

        event.setCanceled(true);
        this.render.doRender(player, event.getX(), event.getY(), event.getZ(), player.rotationYaw, event.getPartialRenderTick());
    }
}