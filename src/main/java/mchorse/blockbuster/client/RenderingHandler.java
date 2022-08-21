package mchorse.blockbuster.client;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.ClientProxy;
import mchorse.blockbuster.aperture.CameraHandler;
import mchorse.blockbuster.audio.AudioRenderer;
import mchorse.blockbuster.client.gui.GuiRecordingOverlay;
import mchorse.blockbuster.client.model.parsing.ModelExtrudedLayer;
import mchorse.blockbuster.client.particles.emitter.BedrockEmitter;
import mchorse.blockbuster.client.render.IRenderLast;
import mchorse.blockbuster.client.render.tileentity.TileEntityGunItemStackRenderer;
import mchorse.blockbuster.client.render.tileentity.TileEntityModelItemStackRenderer;
import mchorse.blockbuster.client.render.tileentity.TileEntityModelRenderer;
import mchorse.blockbuster.client.textures.GifTexture;
import mchorse.blockbuster.common.GunProps;
import mchorse.blockbuster.common.OrientedBB;
import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.common.item.ItemGun;
import mchorse.blockbuster.common.tileentity.TileEntityModel;
import mchorse.blockbuster.recording.RecordPlayer;
import mchorse.blockbuster.recording.RecordRecorder;
import mchorse.blockbuster.recording.data.Frame;
import mchorse.blockbuster.recording.data.Record;
import mchorse.blockbuster.utils.EntityUtils;
import mchorse.blockbuster.utils.NBTUtils;
import mchorse.mclib.utils.Color;
import mchorse.mclib.utils.ColorUtils;
import mchorse.mclib.utils.Interpolations;
import mchorse.mclib.utils.OptifineHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.objectweb.asm.tree.MethodNode;
import scala.Int;

import javax.vecmath.Vector3d;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Rendering handler
 *
 * This handler is another handler in this mod that responsible for rendering.
 * Currently this handler only renders recording overlay
 */
@SideOnly(Side.CLIENT)
public class RenderingHandler
{
    private static EntityLivingBase lastItemHolder;
    public static Set<Record> recordsToRender = new HashSet<Record>();
    public static Set<OrientedBB> obbsToRender = new HashSet<OrientedBB>();

    private static final List<IRenderLast> renderLasts = new ArrayList<>();
    /** Runnables to be executed at the end of renderlast event */
    private static final List<Runnable> renderLastBus = new ArrayList<>();
    private static ListIterator<Runnable> renderLastBusIterator = renderLastBus.listIterator();

    /**
     * is true when renderLasts List is being iterated
     */
    private static boolean isRenderingLast;

    private boolean wasPaused;

    /**
     * Bedrock particle emitters
     */
    private static final List<BedrockEmitter> emitters = new ArrayList<BedrockEmitter>();
    private static final List<BedrockEmitter> emittersAdd = new ArrayList<BedrockEmitter>();
    private static boolean emitterIsIterating;

    private GuiRecordingOverlay overlay;

    /**
     *  The transformType of the item currently rendered like TransformType.GUI, TransformType.GROUND, TransformType.THIRD_PERSON_RIGHT_HAND etc.
     *  This variable is set by {@link #setTSRTTransform(ItemCameraTransforms.TransformType)} which is called by ASM.
     */
    public static ItemCameraTransforms.TransformType itemTransformType;

    /**
     * Add a renderLast object to the renderLast List. They will be rendered after entities.
     * This method only adds the given object if the renderLast List is not being iterated
     * @param renderLast
     * @return true if added to the list, false if not added, for example, when RenderingHandler is currently rendering last.
     */
    public static boolean addRenderLast(IRenderLast renderLast)
    {
        /* only add a render last object when the renderLasts List is not iterated */
        if (!isRenderingLast)
        {
            renderLasts.add(renderLast);

            return true;
        }

        return false;
    }

    /**
     * Add a runnable that will be executed at the end of {@link #onRenderLast(RenderWorldLastEvent)}
     */
    public static void registerRenderLastEvent(Runnable runnable)
    {
        renderLastBusIterator.add(runnable);
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
     * Render BB gun рands
     */
    public static void changePlayerHand(AbstractClientPlayer player, ModelPlayer modelPlayer)
    {
        ItemStack itemstack = player.getHeldItemMainhand();
        ModelBiped.ArmPose armPose = ModelBiped.ArmPose.EMPTY;
        
        if (!itemstack.isEmpty() && itemstack.getItem() instanceof ItemGun)
        {
            GunProps props = NBTUtils.getGunProps(itemstack);

            if (props.alwaysArmsShootingPose)
            {
                armPose = ModelBiped.ArmPose.BOW_AND_ARROW;
            }
            else
            {
                if (props.enableArmsShootingPose)
                {
                    if (KeyboardHandler.gunShoot.isKeyDown())
                    {
                        armPose = ModelBiped.ArmPose.BOW_AND_ARROW;
                    }
                }
            }
        }

        if (player.getPrimaryHand() == EnumHandSide.RIGHT)
        {
            modelPlayer.rightArmPose = armPose;
        }
        else
        {
            modelPlayer.leftArmPose = armPose;
        }
        
    }
    /**
     * Render particle emitters (called by ASM)
     */
    public static void renderParticles(float partialTicks)
    {
        if (!emitters.isEmpty())
        {
            if (Blockbuster.snowstormDepthSorting.get())
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

            emitterIsIterating = true;

            for (BedrockEmitter emitter : emitters)
            {
                emitter.render(partialTicks);
                emitter.running = emitter.sanityTicks < 2;
            }

            addEmitters();

            emitterIsIterating = false;
        }
    }

    public static void addEmitter(BedrockEmitter emitter, EntityLivingBase target)
    {
        if (!emitter.added)
        {
            if (emitterIsIterating)
            {
                emittersAdd.add(emitter);
            }
            else
            {
                emitters.add(emitter);
            }

            emitter.added = true;
            emitter.setTarget(target);
        }
    }

    private static void addEmitters()
    {
        if (!emittersAdd.isEmpty())
        {
            emitters.addAll(emittersAdd);
            emittersAdd.clear();
        }
    }

    public static void updateEmitters()
    {
        List<BedrockEmitter> emittersRemove = new ArrayList<>();

        emitterIsIterating = true;

        for(BedrockEmitter emitter : emitters)
        {
            emitter.update();

            if (emitter.isFinished())
            {
                emittersRemove.add(emitter);

                emitter.added = false;
            }
        }

        if (!emittersRemove.isEmpty())
        {
            emitters.removeAll(emittersRemove);
        }

        addEmitters();

        emitterIsIterating = false;
    }

    public static void resetEmitters()
    {
        emitters.clear();
    }

    /**
     * Called by ASM {@link mchorse.blockbuster.core.transformers.RenderGlobalTransformer}
     * to render the entities that should be depth sorted after tileentities and entities have been rendered
     */
    public static void renderLastEntities()
    {
        if (MinecraftForgeClient.getRenderPass() != 0)
        {
            renderLasts.clear();

            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        Entity camera = mc.getRenderViewEntity();

        renderLasts.sort((a, b) ->
        {
            Vector3d aPos = a.getRenderLastPos();
            Vector3d bPos = b.getRenderLastPos();

            double dist1 = camera.getDistanceSq(aPos.x, aPos.y, aPos.z);
            double dist2 = camera.getDistanceSq(bPos.x, bPos.y, bPos.z);

            return dist1 == dist2 ? 0 : (dist2 - dist1 > 0 ? 1 : -1);
        });

        isRenderingLast = true;

        for (IRenderLast renderLast : renderLasts)
        {
            if (renderLast instanceof EntityActor)
            {
                /*
                 * Optifine calls net.optifine.shaders.Shaders.nextEntity before entity is rendered
                 * Without this the lighting / shadows are weird on renderLast models
                 */
                OptifineHelper.nextEntity((EntityActor) renderLast);

                mc.getRenderManager().renderEntityStatic((EntityActor) renderLast, mc.getRenderPartialTicks(), false);
            }
            else if (renderLast instanceof TileEntityModel)
            {
                /*
                 * Optifine calls net.optifine.shaders.Shaders.nextBlockEntity before tileEntity is rendered
                 * Without this the lighting / shadows are weird on renderLast models
                 */
                OptifineHelper.nextBlockEntity((TileEntityModel) renderLast);

                TileEntityRendererDispatcher.instance.render((TileEntityModel) renderLast, mc.getRenderPartialTicks(), -1);
            }
        }

        isRenderingLast = false;

        renderLasts.clear();
    }

    /**
     * Called by ASM
     */
    public static void setLastItemHolder(EntityLivingBase entity)
    {
        if (lastItemHolder == null)
        {
            lastItemHolder = entity;
        }
    }

    /**
     * Called by ASM {@link mchorse.blockbuster.core.transformers.RenderItemTransformer}
     * and sets the transform type of the currently rendered item
     * Called in renderItem, renderItemModel and renderItemModelIntoGUI method of Minecraft.
     */
    public static void setTSRTTransform(ItemCameraTransforms.TransformType type)
    {
        itemTransformType = type;
    }

    /**
     * Called by ASM {@link mchorse.blockbuster.core.transformers.RenderItemTransformer}.
     * This method sets the entity that is holding the item which is currently rendered.
     * Called in renderItem method of RenderItem class.
     */
    public static void resetLastItemHolder(EntityLivingBase entity)
    {
        if (lastItemHolder == entity)
        {
            lastItemHolder = null;
        }
    }

    public static EntityLivingBase getLastItemHolder()
    {
        return lastItemHolder;
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

                AudioRenderer.renderAll((w - width) / 2, h / 2 + h / 4, width, Blockbuster.audioWaveformHeight.get(), w, h);
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
            list.add("Recording frame " + recorder.tick);
        }
    }

    /**
     * On render last world event, this bad boy will tick all of the GIF 
     * textures which were registered, and will keep track of audio
     */
    @SubscribeEvent
    public void onRenderLast(RenderWorldLastEvent event)
    {
        ModelExtrudedLayer.tickCache();

        Minecraft mc = Minecraft.getMinecraft();
        boolean isPaused = mc.isGamePaused();

        if (this.wasPaused != isPaused)
        {
            ClientProxy.audio.pause(isPaused);

            this.wasPaused = isPaused;
        }

        /* render actor paths */
        if (mc.gameSettings.showDebugInfo && !recordsToRender.isEmpty())
        {
            renderPaths(event, recordsToRender);
        }

        recordsToRender.clear();

        /* TODO render OBB outlines (move this code to limb renderer or so, later)*/
        if (mc.gameSettings.showDebugInfo && !obbsToRender.isEmpty())
        {
            for (OrientedBB obb : this.obbsToRender)
            {
                obb.render(event);
            }
        }
        obbsToRender.clear();

        renderLastBusIterator = renderLastBus.listIterator();

        while (renderLastBusIterator.hasNext())
        {
            Runnable runnable = renderLastBusIterator.next();

            runnable.run();
        }

        renderLastBus.clear();
        renderLastBusIterator = renderLastBus.listIterator();
    }

    private void renderPaths(RenderWorldLastEvent event, Set<Record> recordsToRender)
    {
        int shader = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);

        if (shader != 0)
        {
            OpenGlHelper.glUseProgram(0);
        }

        final int delta = 2;
        Color color = ColorUtils.COLOR;
        Entity player = Minecraft.getMinecraft().getRenderViewEntity();
        Random random = new Random();

        double playerX = player.prevPosX + (player.posX - player.prevPosX) * event.getPartialTicks();
        double playerY = player.prevPosY + (player.posY - player.prevPosY) * event.getPartialTicks();
        double playerZ = player.prevPosZ + (player.posZ - player.prevPosZ) * event.getPartialTicks();

        GlStateManager.glLineWidth(2F);
        GlStateManager.disableLighting();
        GlStateManager.disableTexture2D();

        for (Record record : recordsToRender)
        {
            int length = record.frames.size();

            if (length < delta + 1)
            {
                continue;
            }

            random.setSeed(record.filename.hashCode());
            random.setSeed(random.nextLong());

            int hex = MathHelper.hsvToRGB(random.nextFloat(), 1F, 1F);

            color.set(hex, false);

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder builder = tessellator.getBuffer();

            builder.setTranslation(-playerX, -playerY, -playerZ);
            builder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);

            for (int i = delta; i < length; i += delta)
            {
                Frame prev = record.frames.get(i - delta);
                Frame current = record.frames.get(i);

                builder.pos(prev.x, prev.y + 1F, prev.z).color(color.r, color.g, color.b, color.a).endVertex();
                builder.pos(current.x, current.y + 1F, current.z).color(color.r, color.g, color.b, color.a).endVertex();
            }

            builder.setTranslation(0, 0, 0);
            tessellator.draw();
        }

        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();
        GlStateManager.glLineWidth(1F);

        if (shader != 0)
        {
            OpenGlHelper.glUseProgram(shader);
        }
    }

    @SubscribeEvent
    public void onOrientCamera(EntityViewRenderEvent.CameraSetup event)
    {
        EntityPlayer thePlayer = Minecraft.getMinecraft().player;
        RecordPlayer player = EntityUtils.getRecordPlayer(thePlayer);

        if (player != null && player.record != null && !player.record.frames.isEmpty())
        {
            Frame frame = player.record.getFrameSafe(player.tick);
            Frame prev = player.record.getFrameSafe(player.tick - 1);
            float partial = (float) event.getRenderPartialTicks();

            event.setYaw(Interpolations.lerp(prev.yawHead, frame.yawHead, partial) - 180);
            event.setPitch(Interpolations.lerp(prev.pitch, frame.pitch, partial));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onRenderHand(RenderHandEvent event)
    {
        EntityPlayer thePlayer = Minecraft.getMinecraft().player;
        RecordPlayer player = EntityUtils.getRecordPlayer(thePlayer);

        if (player != null && player.record != null && !player.record.frames.isEmpty())
        {
            Frame frame = player.record.getFrameSafe(player.tick);
            Frame prev = player.record.getFrameSafe(player.tick - 1);

            float partial = event.getPartialTicks();
            float yaw = Interpolations.lerp(prev.yaw, frame.yaw, partial);
            float pitch = Interpolations.lerp(prev.pitch, frame.pitch, partial);

            thePlayer.rotationYaw = yaw;
            thePlayer.rotationPitch = pitch;
            thePlayer.prevRotationYaw = yaw;
            thePlayer.prevRotationPitch = pitch;
        }
    }

    @SubscribeEvent
    public void onPreRenderEntity(RenderLivingEvent.Pre event)
    {
        GifTexture.entityTick = event.getEntity().ticksExisted;
    }

    @SubscribeEvent
    public void onPostRenderEntity(RenderLivingEvent.Post event)
    {
        GifTexture.entityTick = -1;
    }
}