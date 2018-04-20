package mchorse.blockbuster.client;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.client.gui.GuiRecordingOverlay;
import mchorse.blockbuster.common.ClientProxy;
import mchorse.blockbuster.common.tileentity.TileEntityModel;
import mchorse.blockbuster.recording.RecordRecorder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
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
    public static final Map<BlockPos, TEModel> models = new HashMap<BlockPos, TEModel>();

    private GuiRecordingOverlay overlay;

    public RenderingHandler(GuiRecordingOverlay overlay)
    {
        this.overlay = overlay;
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
     * Add Blockbuster debug strings, such as length of client records and
     * recording information, to the debug overlay.
     */
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

        RecordRecorder recorder = ClientProxy.manager.recorders.get(Minecraft.getMinecraft().player);

        if (recorder != null)
        {
            list.add("Recording frame " + recorder.tick + " (delay: " + recorder.delay + ")");
        }
    }

    /**
     * Render {@link TileEntityModel}s when they're getting culled 
     */
    @SubscribeEvent
    public void onLastRender(RenderWorldLastEvent event)
    {
        if (Blockbuster.proxy.config.model_block_disable_culling_workaround)
        {
            return;
        }

        float partial = event.getPartialTicks();
        float distance = Blockbuster.proxy.config.actor_rendering_range;

        Iterator<TEModel> it = models.values().iterator();
        RenderHelper.enableStandardItemLighting();
        Minecraft mc = Minecraft.getMinecraft();

        mc.entityRenderer.enableLightmap();

        while (it.hasNext())
        {
            TEModel model = it.next();
            BlockPos pos = model.model.getPos();
            Entity render = mc.getRenderViewEntity();

            double x = render.prevPosX + (render.posX - render.prevPosX) * partial;
            double y = render.prevPosY + (render.posY - render.prevPosY) * partial;
            double z = render.prevPosZ + (render.posZ - render.prevPosZ) * partial;

            /* Remove TE if it's too far away away or it was removed */
            if (Math.abs(pos.getX() - x) > distance && Math.abs(pos.getZ() - z) > distance || mc.world.getTileEntity(pos) != model.model)
            {
                it.remove();
            }
            else if (model.render)
            {
                int i = mc.world.getCombinedLight(model.model.getPos(), 0);
                int j = i % 65536;
                int k = i / 65536;
                OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) j, (float) k);
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

                x = pos.getX() - x;
                y = pos.getY() - y;
                z = pos.getZ() - z;

                ClientProxy.modelRenderer.renderTileEntityAt(model.model, x, y, z, partial, 0);
            }

            model.render = true;
        }

        mc.entityRenderer.disableLightmap();

        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
    }

    /**
     * {@link TileEntityModel} capsule class
     * 
     * This class is responsible for storing the TE, and also the flag 
     * for rendering.
     */
    public static class TEModel
    {
        public TileEntityModel model;
        public boolean render;

        public TEModel(TileEntityModel model)
        {
            this.model = model;
            this.render = false;
        }
    }
}