package mchorse.blockbuster.client;

import mchorse.blockbuster.capabilities.morphing.IMorphing;
import mchorse.blockbuster.capabilities.morphing.MorphingProvider;
import mchorse.blockbuster.client.gui.GuiRecordingOverlay;
import mchorse.blockbuster.client.render.RenderPlayer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
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

    public RenderingHandler(GuiRecordingOverlay overlay, RenderPlayer render)
    {
        this.overlay = overlay;
        this.render = render;
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

    /**
     * Morph (render) player into custom model if player has its variables
     * related to morphing (model and skin)
     */
    @SubscribeEvent
    public void onPlayerRender(RenderPlayerEvent.Pre event)
    {
        EntityPlayer player = event.getEntityPlayer();
        IMorphing capability = player.getCapability(MorphingProvider.MORPHING_CAP, null);

        if (capability == null || capability.getModel().isEmpty()) return;

        event.setCanceled(true);
        this.render.doRender(player, event.getX(), event.getY(), event.getZ(), player.rotationYaw, event.getPartialRenderTick());
    }
}
