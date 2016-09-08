package noname.blockbuster.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noname.blockbuster.client.gui.GuiRecordingOverlay;
import noname.blockbuster.client.render.RenderPlayer;

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
    public void onPlayerRender(RenderPlayerEvent.Pre event)
    {
        if (this.render.model.isEmpty()) return;

        if (Minecraft.getMinecraft().gameSettings.thirdPersonView != 0)
        {
            EntityPlayer player = event.getEntityPlayer();

            event.setCanceled(true);

            this.render.doRender(player, event.getX(), event.getY(), event.getZ(), player.rotationYaw, event.getPartialRenderTick());
        }
    }
}
