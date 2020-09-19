package mchorse.blockbuster.client;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.ClientProxy;
import mchorse.blockbuster.aperture.CameraHandler;
import mchorse.blockbuster.audio.AudioFile;
import mchorse.blockbuster.audio.AudioRenderer;
import mchorse.blockbuster.client.gui.GuiRecordingOverlay;
import mchorse.blockbuster.client.particles.emitter.BedrockEmitter;
import mchorse.blockbuster.client.render.tileentity.TileEntityGunItemStackRenderer;
import mchorse.blockbuster.client.render.tileentity.TileEntityModelItemStackRenderer;
import mchorse.blockbuster.client.textures.GifTexture;
import mchorse.blockbuster.recording.RecordRecorder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Rendering handler
 *
 * This handler is another handler in this mod that responsible for rendering.
 * Currently this handler only renders recording overlay
 */
@SideOnly(Side.CLIENT)
public class RenderingHandler
{
    private static TileEntityModelItemStackRenderer model = new TileEntityModelItemStackRenderer();
    private static TileEntityGunItemStackRenderer gun = new TileEntityGunItemStackRenderer();

    /**
     * GIFs which should be updated 
     */
    public static Map<ResourceLocation, GifTexture> gifs = new HashMap<ResourceLocation, GifTexture>();

    /**
     * Bedrock particle emitters
     */
    private static final List<BedrockEmitter> emitters = new ArrayList<>();

    private GuiRecordingOverlay overlay;

    /**
     * Register GIF 
     */
    public static void registerGif(ResourceLocation rl, GifTexture texture)
    {
        GifTexture old = gifs.remove(rl);

        if (old != null)
        {
            old.deleteGlTexture();
        }

        gifs.put(rl, texture);
    }

    /**
     * Render green sky, this is getting invoked from the ASM patched 
     * code in {@link RenderGlobal}
     */
    public static void renderGreenSky()
    {
        int color = Blockbuster.chromaSkyColor.get();
        float skyR = (color >> 16 & 0xff) / 255F;
        float skyG = (color >> 8 & 0xff) / 255F;
        float skyB = (color & 0xff) / 255F;
        float skyA = (color >> 24 & 0xff) / 255F;

        GlStateManager.clearColor(skyR, skyG, skyB, skyA);
        GlStateManager.clear(GL11.GL_COLOR_BUFFER_BIT);
        GL11.glDisable(GL11.GL_FOG);
    }

    /**
     * Check whether it's time to render green sky, this is getting 
     * invoked from ASM patched code in {@link RenderGlobal}  
     */
    public static boolean isGreenSky()
    {
        return Blockbuster.chromaSky.get();
    }

    /**
     * Render lit particles (call by ASM, but not used for anything yet...
     * I might use it for morph based Snowstorm system)...
     */
    public static void renderLitParticles(float partialTicks)
    {}

    /**
     * Render particle emitters (called by ASM)
     */
    public static void renderParticles(float partialTicks)
    {
        if (!emitters.isEmpty())
        {
            Entity camera = Minecraft.getMinecraft().getRenderViewEntity();
            double playerX = camera.prevPosX + (camera.posX - camera.prevPosX) * (double) partialTicks;
            double playerY = camera.prevPosY + (camera.posY - camera.prevPosY) * (double) partialTicks;
            double playerZ = camera.prevPosZ + (camera.posZ - camera.prevPosZ) * (double) partialTicks;

            GlStateManager.enableBlend();
            GlStateManager.enableAlpha();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            GlStateManager.alphaFunc(516, 0.003921569F);

            BufferBuilder builder = Tessellator.getInstance().getBuffer();

            GlStateManager.disableTexture2D();

            builder.setTranslation(-playerX, -playerY, -playerZ);

            GlStateManager.disableCull();
            GlStateManager.enableTexture2D();

            if (Blockbuster.particleSorting.get())
            {
                emitters.sort((a, b) ->
                {
                    double ad = a.getDistanceSq();
                    double bd = b.getDistanceSq();

                    if (ad < bd)
                    {
                        return 1;
                    }
                    else if (ad > bd)
                    {
                        return -1;
                    }

                    return 0;
                });
            }

            for (BedrockEmitter emitter : emitters)
            {
                emitter.render(partialTicks);
                emitter.running = emitter.sanityTicks < 2;
            }

            builder.setTranslation(0, 0, 0);

            GlStateManager.disableBlend();
            GlStateManager.alphaFunc(516, 0.1F);
        }
    }

    public static void addEmitter(BedrockEmitter emitter, EntityLivingBase target)
    {
        if (!emitter.added)
        {
            emitters.add(emitter);

            emitter.added = true;
            emitter.setTarget(target);
        }
    }

    public static void updateEmitters()
    {
        Iterator<BedrockEmitter> it = emitters.iterator();

        while (it.hasNext())
        {
            BedrockEmitter emitter = it.next();

            emitter.update();

            if (emitter.isFinished())
            {
                it.remove();
                emitter.added = false;
            }
        }
    }

    public RenderingHandler(GuiRecordingOverlay overlay)
    {
        this.overlay = overlay;
    }

    /**
     * Fixes lightmap for TEISR
     */
    @SubscribeEvent
    public void onHUDRender(RenderGameOverlayEvent.Pre event)
    {
        if (event.getType() == RenderGameOverlayEvent.ElementType.ALL)
        {
            Minecraft.getMinecraft().entityRenderer.enableLightmap();
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
            Minecraft.getMinecraft().entityRenderer.disableLightmap();
        }
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
            int w = resolution.getScaledWidth();
            int h = resolution.getScaledHeight();

            this.overlay.draw(w, h);

            if (!CameraHandler.isCameraEditorOpen())
            {
                int width = (int) (w * Blockbuster.audioWaveformWidth.get());

                AudioRenderer.renderAll((w - width) / 2, h / 2 + h / 4, width, Blockbuster.audioWaveformHeight.get(), w, h, false);
            }
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
     * On render last world event, this bad boy will tick all of the GIF 
     * textures which were registered 
     */
    @SubscribeEvent
    public void onRenderLast(RenderWorldLastEvent event)
    {
        for (GifTexture texture : gifs.values())
        {
            texture.tick();
        }
    }
}