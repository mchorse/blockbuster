package mchorse.blockbuster.aperture;

import mchorse.aperture.Aperture;
import mchorse.aperture.camera.CameraAPI;
import mchorse.aperture.client.gui.dashboard.GuiCameraDashboard;
import mchorse.aperture.client.gui.dashboard.GuiCameraEditor;
import mchorse.aperture.events.CameraEditorDashboardEvent;
import mchorse.aperture.events.CameraEditorEvent;
import mchorse.aperture.network.common.PacketCameraProfileList;
import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.ClientProxy;
import mchorse.blockbuster.aperture.gui.GuiPlayback;
import mchorse.blockbuster.aperture.network.client.ClientHandlerCameraProfileList;
import mchorse.blockbuster.aperture.network.client.ClientHandlerSceneLength;
import mchorse.blockbuster.aperture.network.common.PacketAudioShift;
import mchorse.blockbuster.aperture.network.common.PacketRequestLength;
import mchorse.blockbuster.aperture.network.common.PacketRequestProfiles;
import mchorse.blockbuster.aperture.network.common.PacketSceneLength;
import mchorse.blockbuster.aperture.network.server.ServerHandlerAudioShift;
import mchorse.blockbuster.aperture.network.server.ServerHandlerRequestLength;
import mchorse.blockbuster.aperture.network.server.ServerHandlerRequestProfiles;
import mchorse.blockbuster.audio.AudioRenderer;
import mchorse.blockbuster.client.gui.dashboard.GuiBlockbusterPanels;
import mchorse.blockbuster.client.gui.dashboard.panels.recording_editor.GuiRecordingEditorPanel;
import mchorse.blockbuster.common.item.ItemPlayback;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.scene.PacketSceneRequestCast;
import mchorse.blockbuster.network.common.scene.sync.PacketSceneGoto;
import mchorse.blockbuster.network.common.scene.sync.PacketScenePlay;
import mchorse.blockbuster.recording.scene.SceneLocation;
import mchorse.blockbuster.utils.mclib.BBIcons;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.mclib.GuiDashboard;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.ScrollArea;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.utils.resources.RLUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional.Method;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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

    public static SceneLocation location;

    /**
     * Check whether Aperture is loaded
     */
    public static boolean isApertureLoaded()
    {
        return Loader.isModLoaded(Aperture.MOD_ID);
    }

    public static void register()
    {
        if (CameraHandler.isApertureLoaded())
        {
            registerHandlers();
        }
    }

    @Method(modid = Aperture.MOD_ID)
    private static void registerHandlers()
    {
        Aperture.EVENT_BUS.register(new CameraHandler());
        MinecraftForge.EVENT_BUS.register(new CameraGUIHandler());
    }

    public static void registerMessages()
    {
        if (CameraHandler.isApertureLoaded())
        {
            registerApertureMessages();
        }
    }

    @Method(modid = Aperture.MOD_ID)
    private static void registerApertureMessages()
    {
        Dispatcher.DISPATCHER.register(PacketRequestProfiles.class, ServerHandlerRequestProfiles.class, Side.SERVER);
        Dispatcher.DISPATCHER.register(PacketCameraProfileList.class, ClientHandlerCameraProfileList.class, Side.CLIENT);

        Dispatcher.DISPATCHER.register(PacketRequestLength.class, ServerHandlerRequestLength.class, Side.SERVER);
        Dispatcher.DISPATCHER.register(PacketSceneLength.class, ClientHandlerSceneLength.class, Side.CLIENT);

        Dispatcher.DISPATCHER.register(PacketAudioShift.class, ServerHandlerAudioShift.class, Side.SERVER);
    }

    @SideOnly(Side.CLIENT)
    @Method(modid = Aperture.MOD_ID)
    public static void openCameraEditor()
    {
        GuiCameraDashboard.openCameraEditor();
    }

    @Method(modid = Aperture.MOD_ID)
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

    @SideOnly(Side.CLIENT)
    @Method(modid = Aperture.MOD_ID)
	public static void attach(SceneLocation location)
    {
        GuiPlayback playback = new GuiPlayback();

        playback.setLocation(location);
        Minecraft.getMinecraft().displayGuiScreen(playback);
	}

    public static boolean isCameraEditorOpen()
    {
        if (isApertureLoaded())
        {
            return isCurrentScreenCameraEditor();
        }

        return false;
    }

    @Method(modid = Aperture.MOD_ID)
    @SideOnly(Side.CLIENT)
    private static boolean isCurrentScreenCameraEditor()
    {
        return Minecraft.getMinecraft().currentScreen instanceof GuiCameraDashboard;
    }

    /* Event listeners */

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    @Method(modid = Aperture.MOD_ID)
    public void onCameraScrub(CameraEditorEvent.Scrubbed event)
    {
        SceneLocation location = get();

        if (location != null)
        {
            Dispatcher.sendToServer(new PacketSceneGoto(location, event.position, CameraHandler.actions));
        }

        GuiBlockbusterPanels dashboard = ClientProxy.panels;

        if (dashboard != null && dashboard.recordingEditorPanel.selector.isVisible())
        {
            ScrollArea scroll = dashboard.recordingEditorPanel.selector.scroll;

            scroll.scrollIntoView(scroll.scrollItemSize * (event.position - dashboard.recordingEditorPanel.record.preDelay), 2);
            dashboard.recordingEditorPanel.selector.cursor = event.position;
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    @Method(modid = Aperture.MOD_ID)
    public void onCameraPlause(CameraEditorEvent.Playback event)
    {
        SceneLocation location = get();

        if (location != null)
        {
            Dispatcher.sendToServer(new PacketScenePlay(location, event.play ? PacketScenePlay.PLAY : PacketScenePlay.PAUSE, event.position));
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    @Method(modid = Aperture.MOD_ID)
    public void onCameraRewind(CameraEditorEvent.Rewind event)
    {
        SceneLocation location = get();

        if (location != null)
        {
            Dispatcher.sendToServer(new PacketScenePlay(location, PacketScenePlay.RESTART, event.position));
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    @Method(modid = Aperture.MOD_ID)
    public void onCameraRegisterPanels(CameraEditorDashboardEvent.RegisteringPanels event)
    {
        location = null;

        GuiDashboard.get();

        GuiBlockbusterPanels panels = ClientProxy.panels;
        GuiCameraEditor editor = event.editor.camera;

        event.editor.panels.registerPanel(panels.modelPanel, IKey.lang("blockbuster.gui.dashboard.model"), Icons.BLOCK);
        event.editor.panels.registerPanel(panels.recordingEditorPanel, IKey.lang("blockbuster.gui.dashboard.player_recording"), BBIcons.EDITOR);

        GuiElement editorElement = new GuiElement(Minecraft.getMinecraft())
        {
            @Override
            public void draw(GuiContext context)
            {
                GuiRecordingEditorPanel record = panels.recordingEditorPanel;

                if (editor.getRunner().isRunning() && record.record != null)
                {
                    ScrollArea scroll = record.selector.scroll;

                    scroll.scrollIntoView(scroll.scrollItemSize * (int) (editor.getRunner().ticks - record.record.preDelay), 2);
                    record.selector.cursor = (int) editor.getRunner().ticks;
                }

                int w = (int) (editor.area.w * Blockbuster.audioWaveformWidth.get());

                AudioRenderer.renderAll(editor.area.x + (editor.area.w - w) / 2, editor.dashboard.timeline.area.y - 15, w, Blockbuster.audioWaveformHeight.get(), context.screen.width, context.screen.height);
                record.selector.cursor = editor.dashboard.timeline.value;
            }
        };

        event.editor.panels.prepend(editorElement);
        // open.keys().register(IKey.lang("blockbuster.gui.aperture.keys.toggle_list"), Keyboard.KEY_L, () -> open.clickItself(editor.context)).held(Keyboard.KEY_LCONTROL).category(category);
        // editor.dashboard.panels.keys().register(detachScene, Keyboard.KEY_D, () -> directorOptions.detachScene.clickItself(GuiBase.getCurrent())).held(Keyboard.KEY_LSHIFT).category(category).active(() -> !editor.dashboard.flight.isFlightEnabled() && directorOptions.detachScene.isEnabled());
        // editor.dashboard.panels.keys().register(reloadScene, Keyboard.KEY_R, () -> directorOptions.reloadScene.clickItself(GuiBase.getCurrent())).held(Keyboard.KEY_LSHIFT).category(category).active(() -> !editor.dashboard.flight.isFlightEnabled());
    }

    /**
     * Get scene location from playback button
     */
    @SideOnly(Side.CLIENT)
    public static SceneLocation get()
    {
        ItemStack right = Minecraft.getMinecraft().player.getHeldItemMainhand();

        if (right.getItem() instanceof ItemPlayback && right.getTagCompound() != null && right.getTagCompound().hasKey("Scene"))
        {
            return new SceneLocation(right.getTagCompound().getString("Scene"));
        }

        return location;
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
        @SubscribeEvent
        @SideOnly(Side.CLIENT)
        @Method(modid = Aperture.MOD_ID)
        public void onGuiOpen(GuiOpenEvent event)
        {
            if (Minecraft.getMinecraft().player == null)
            {
                return;
            }

            GuiScreen current = Minecraft.getMinecraft().currentScreen;
            GuiScreen toOpen = event.getGui();
            SceneLocation location = get();
            boolean toOpenCamera = toOpen instanceof GuiCameraDashboard;

            if (location != null)
            {
                int tick = GuiCameraDashboard.getCameraEditor().timeline.value;

                if (!(current instanceof GuiCameraDashboard) && toOpenCamera)
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
        }
    }
}