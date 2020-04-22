package mchorse.blockbuster.aperture;

import mchorse.aperture.ClientProxy;
import mchorse.aperture.camera.CameraAPI;
import mchorse.aperture.client.gui.GuiCameraEditor;
import mchorse.aperture.events.CameraEditorEvent;
import mchorse.aperture.network.common.PacketCameraProfileList;
import mchorse.blockbuster.aperture.gui.GuiDirectorConfigOptions;
import mchorse.blockbuster.aperture.network.client.ClientHandlerCameraProfileList;
import mchorse.blockbuster.aperture.network.client.ClientHandlerSceneLength;
import mchorse.blockbuster.aperture.network.common.PacketPlaybackButton;
import mchorse.blockbuster.aperture.network.common.PacketRequestLength;
import mchorse.blockbuster.aperture.network.common.PacketRequestProfiles;
import mchorse.blockbuster.aperture.network.common.PacketSceneLength;
import mchorse.blockbuster.aperture.network.server.ServerHandlerPlaybackButton;
import mchorse.blockbuster.aperture.network.server.ServerHandlerRequestLength;
import mchorse.blockbuster.aperture.network.server.ServerHandlerRequestProfiles;
import mchorse.blockbuster.client.gui.dashboard.GuiDashboard;
import mchorse.blockbuster.client.gui.dashboard.panels.recording_editor.GuiRecordingEditorPanel;
import mchorse.blockbuster.common.item.ItemPlayback;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.scene.PacketSceneRequestCast;
import mchorse.blockbuster.network.common.scene.sync.PacketSceneGoto;
import mchorse.blockbuster.network.common.scene.sync.PacketScenePlay;
import mchorse.blockbuster.recording.scene.SceneLocation;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiIconElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDrawable;
import mchorse.mclib.client.gui.utils.Area;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.ScrollArea;
import mchorse.mclib.utils.resources.RLUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional.Method;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.function.Consumer;

/**
 * Camera handler
 *
 * This is the event listener for soft dependency integration with Aperture mod.
 * Basically what it does is hooks up these event listeners to Aperture and GUI
 * events and control the director block playback based on these events.
 */
public class CameraHandler
{
    /**
     * Tick which is used to set the value of the camera editor scrub back
     */
    public static int tick = 0;

    /**
     * Whether director should be reloaded when entering camera editor GUI
     */
    public static boolean reload = true;

    /**
     * Whether actions should played back also
     */
    public static boolean actions = true;

    /**
     * Camera editor integrations
     */
    @SideOnly(Side.CLIENT)
    public static GuiElement cameraEditorElements;

    /**
     * Reset camera editor elements
     */
    public static boolean resetCameraEditorElements;

    /**
     * Check whether Aperture is loaded
     */
    public static boolean isApertureLoaded()
    {
        return Loader.isModLoaded("aperture");
    }

    @Method(modid = "aperture")
    public static void register()
    {
        ClientProxy.EVENT_BUS.register(new CameraHandler());
        MinecraftForge.EVENT_BUS.register(new CameraGUIHandler());
    }

    @Method(modid = "aperture")
    public static void registerMessages()
    {
        Dispatcher.DISPATCHER.register(PacketRequestProfiles.class, ServerHandlerRequestProfiles.class, Side.SERVER);
        Dispatcher.DISPATCHER.register(PacketCameraProfileList.class, ClientHandlerCameraProfileList.class, Side.CLIENT);

        Dispatcher.DISPATCHER.register(PacketPlaybackButton.class, ServerHandlerPlaybackButton.class, Side.SERVER);
        Dispatcher.DISPATCHER.register(PacketRequestLength.class, ServerHandlerRequestLength.class, Side.SERVER);
        Dispatcher.DISPATCHER.register(PacketSceneLength.class, ClientHandlerSceneLength.class, Side.CLIENT);
    }

    @SideOnly(Side.CLIENT)
    @Method(modid = "aperture")
    public static void reloadCameraEditor()
    {
        /* Reinitiate the recording GUI integration */
        GuiCameraEditor editor = ClientProxy.cameraEditor;

        if (editor != null)
        {
            resetCameraEditorElements = true;
            ClientProxy.EVENT_BUS.post(new CameraEditorEvent.Init(editor));
        }
    }

    @Method(modid = "aperture")
    public static void handlePlaybackItem(EntityPlayer player, NBTTagCompound tag)
    {
        /* To allow actors using playback item without a crash */
        if (player instanceof EntityPlayerMP)
        {
            if (tag.hasKey("CameraPlay"))
            {
                CameraAPI.playCurrentProfile((EntityPlayerMP) player);
            }
            else if (tag.hasKey("CameraProfile"))
            {
                CameraAPI.playCameraProfile((EntityPlayerMP) player, RLUtils.create(tag.getString("CameraProfile")));
            }
        }
    }

    /* Event listeners */

    @SideOnly(Side.CLIENT)
    @Method(modid = "aperture")
    @SubscribeEvent
    public void onCameraScrub(CameraEditorEvent.Scrubbed event)
    {
        SceneLocation location = get();

        if (location != null)
        {
            Dispatcher.sendToServer(new PacketSceneGoto(location, event.position, CameraHandler.actions));
        }

        GuiDashboard dashboard = mchorse.blockbuster.ClientProxy.dashboard;

        if (dashboard != null && dashboard.recordingEditorPanel.selector.isVisible())
        {
            ScrollArea scroll = dashboard.recordingEditorPanel.selector.scroll;

            scroll.scroll = scroll.scrollItemSize * event.position;
            scroll.clamp();
        }
    }

    @SideOnly(Side.CLIENT)
    @Method(modid = "aperture")
    @SubscribeEvent
    public void onCameraPlause(CameraEditorEvent.Playback event)
    {
        SceneLocation location = get();

        if (location != null)
        {
            Dispatcher.sendToServer(new PacketScenePlay(location, event.play ? PacketScenePlay.PLAY : PacketScenePlay.PAUSE, event.position));
        }
    }

    @SideOnly(Side.CLIENT)
    @Method(modid = "aperture")
    @SubscribeEvent
    public void onCameraOptions(CameraEditorEvent.Options event)
    {
        event.options.add(new GuiDirectorConfigOptions(Minecraft.getMinecraft(), event.editor));
    }

    @SideOnly(Side.CLIENT)
    @Method(modid = "aperture")
    @SubscribeEvent
    public void onCameraEditorInit(CameraEditorEvent.Init event)
    {
        GuiCameraEditor editor = event.editor;
        GuiDashboard dashboard = mchorse.blockbuster.ClientProxy.getDashboard(false);

        dashboard.createWorldPanels(dashboard.mc, false);

        GuiRecordingEditorPanel record = dashboard.recordingEditorPanel;

        /* Just in case */
        if (record == null)
        {
            return;
        }

        GuiElement elements = new GuiElement(dashboard.mc);
        Consumer<GuiIconElement> refresh = (b) ->
        {
            boolean show = elements.isVisible();

            editor.panel.flex().h(1, show ? -150 : -70);
            editor.scrub.flex().y(1, show ? -100 : -20);
            record.records.flex().h(1, show ? -80 : 0);
            b.icon(show ? Icons.DOWNLOAD : Icons.UPLOAD);

            editor.root.resize();
        };

        GuiIconElement toggle = new GuiIconElement(dashboard.mc, Icons.UPLOAD, (b) ->
        {
            if (!record.selector.isVisible())
            {
                return;
            }

            elements.setVisible(!elements.isVisible());
            refresh.accept(b);
        });

        GuiDrawable drawable = new GuiDrawable((v) ->
        {
            if (elements.isVisible() && record.editor.delegate != null)
            {
                Area area = record.editor.delegate.area;

                area.draw(0x66000000);
            }
        });

        if (cameraEditorElements == null || resetCameraEditorElements)
        {
            if (resetCameraEditorElements)
            {
                editor.root.remove(cameraEditorElements);
            }

            cameraEditorElements = new GuiElement(dashboard.mc);
            editor.hidden.remove(editor.scrub);
            editor.root.add(cameraEditorElements);

            resetCameraEditorElements = false;
        }

        elements.setVisible(false);
        elements.add(drawable, record.selector, record.editor);

        toggle.flex().relative(editor.scrub.resizer()).set(0, 0, 16, 16).x(1, 2).y(2);

        editor.scrub.flex().x(30).w(1, -60);

        cameraEditorElements.clear();
        cameraEditorElements.add(record.records, editor.scrub, toggle, record.open, elements);
        refresh.accept(toggle);
    }

    /**
     * Get scene location from playback button
     */
    public static SceneLocation get()
    {
        ItemStack right = Minecraft.getMinecraft().player.getHeldItemMainhand();

        if (right != null && right.getItem() instanceof ItemPlayback)
        {
            BlockPos pos = ItemPlayback.getBlockPos("Dir", right);

            if (pos != null)
            {
                return new SceneLocation(pos);
            }
            else if (right.getTagCompound() != null && right.getTagCompound().hasKey("Scene"))
            {
                return new SceneLocation(right.getTagCompound().getString("Scene"));
            }
        }

        return null;
    }

    public static boolean canSync()
    {
        return get() != null;
    }

    /**
     * Camera editor GUI handler
     *
     * This is also the part of the whole camera editor thing, but for
     * exception it only spawns actors when the camera editor GUI is getting
     * opened.
     */
    public static class CameraGUIHandler
    {
        @SideOnly(Side.CLIENT)
        @Method(modid = "aperture")
        @SubscribeEvent
        public void onGuiOpen(GuiOpenEvent event)
        {
            if (Minecraft.getMinecraft().player == null)
            {
                return;
            }

            GuiScreen current = Minecraft.getMinecraft().currentScreen;
            GuiScreen toOpen = event.getGui();
            SceneLocation location = get();
            boolean toOpenCamera = toOpen instanceof GuiCameraEditor;

            if (location != null)
            {
                int tick = ClientProxy.getCameraEditor().scrub.value;

                if (!(current instanceof GuiCameraEditor) && toOpenCamera)
                {
                    /* Camera editor opens */
                    CameraHandler.tick = tick;

                    if (CameraHandler.reload)
                    {
                        Dispatcher.sendToServer(new PacketScenePlay(location, PacketScenePlay.START, tick));
                    }

                    Dispatcher.sendToServer(new PacketRequestLength(location));
                    Dispatcher.sendToServer(new PacketSceneRequestCast(location));
                }
            }

            if (toOpenCamera)
            {
                GuiCameraEditor editor = ClientProxy.getCameraEditor();
                GuiDashboard dashboard = mchorse.blockbuster.ClientProxy.getDashboard(false);

                dashboard.createWorldPanels(dashboard.mc, true);
                dashboard.onOpen();

                GuiRecordingEditorPanel panel = dashboard.recordingEditorPanel;

                dashboard.openPanel(panel);
                panel.selector.flex().relative(editor.viewport);
                panel.editor.flex().relative(editor.viewport);
                panel.records.flex().relative(editor.viewport);
                panel.records.setVisible(false);
                panel.open.flex().relative(editor.scrub.resizer()).set(-18, 2, 16, 16);
            }
            else if (current instanceof GuiCameraEditor)
            {
                GuiDashboard dashboard = mchorse.blockbuster.ClientProxy.getDashboard(false);

                dashboard.recordingEditorPanel.save();
            }
        }
    }
}