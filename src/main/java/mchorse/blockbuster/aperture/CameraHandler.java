package mchorse.blockbuster.aperture;

import mchorse.aperture.Aperture;
import mchorse.aperture.ClientProxy;
import mchorse.aperture.camera.CameraAPI;
import mchorse.aperture.client.gui.GuiCameraEditor;
import mchorse.aperture.events.CameraEditorEvent;
import mchorse.aperture.network.common.PacketCameraProfileList;
import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.aperture.gui.GuiDirectorConfigOptions;
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
import mchorse.mclib.client.gui.framework.elements.buttons.GuiIconElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDrawable;
import mchorse.mclib.client.gui.mclib.GuiDashboard;
import mchorse.mclib.client.gui.utils.Area;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.ScrollArea;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.utils.Direction;
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
import org.lwjgl.input.Keyboard;

import java.util.List;
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

    @SideOnly(Side.CLIENT)
    public static GuiElement editorElement;

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
        ClientProxy.EVENT_BUS.register(new CameraHandler());
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
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.player;
        GuiCameraEditor editor = ClientProxy.getCameraEditor();

        editor.updateCameraEditor(player);
        player.setVelocity(0, 0, 0);
        mc.displayGuiScreen(editor);
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

    public static int getModeFromNBT(NBTTagCompound tag)
    {
        if (tag.hasKey("CameraPlay"))
        {
            return 1;
        }
        else if (tag.hasKey("CameraProfile"))
        {
            return 2;
        }

        return 0;
    }

    @SideOnly(Side.CLIENT)
    @Method(modid = Aperture.MOD_ID)
	public static void attach(SceneLocation location, List<String> scenes)
    {
        GuiPlayback playback = new GuiPlayback();

        playback.setLocation(location, scenes);
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

    @SideOnly(Side.CLIENT)
    @Method(modid = Aperture.MOD_ID)
    private static boolean isCurrentScreenCameraEditor()
    {
        return Minecraft.getMinecraft().currentScreen instanceof GuiCameraEditor;
    }

    @SideOnly(Side.CLIENT)
	public static float getRoll()
    {
        if (isApertureLoaded())
        {
            return getApertureRoll();
        }

        return 0;
	}

    @SideOnly(Side.CLIENT)
    public static float getRoll(float partialTicks)
    {
        if (isApertureLoaded())
        {
            return getApertureRoll(partialTicks);
        }

        return 0;
    }

    @SideOnly(Side.CLIENT)
    @Method(modid = Aperture.MOD_ID)
    private static float getApertureRoll()
    {
        return ClientProxy.control.roll;
    }

    @SideOnly(Side.CLIENT)
    @Method(modid = Aperture.MOD_ID)
    private static float getApertureRoll(float partialTicks)
    {
        return ClientProxy.control.getRoll(partialTicks);
    }

    @SideOnly(Side.CLIENT)
    public static void setRoll(float prevRoll, float roll)
    {
        if (isApertureLoaded())
        {
            setApertureRoll(prevRoll, roll);
        }
    }

    @SideOnly(Side.CLIENT)
    @Method(modid = Aperture.MOD_ID)
    private static void setApertureRoll(float prevRoll, float roll)
    {
        ClientProxy.control.setRoll(prevRoll, roll);
    }

    @SideOnly(Side.CLIENT)
    public static void resetRoll()
    {
        if (isApertureLoaded())
        {
            resetApertureRoll();
        }
    }

    @SideOnly(Side.CLIENT)
    @Method(modid = Aperture.MOD_ID)
    private static void resetApertureRoll()
    {
        ClientProxy.control.resetRoll();
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

        GuiBlockbusterPanels dashboard = mchorse.blockbuster.ClientProxy.panels;

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
    public void onCameraOptions(CameraEditorEvent.Options event)
    {
        event.options.add(new GuiDirectorConfigOptions(Minecraft.getMinecraft(), event.editor));
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    @Method(modid = Aperture.MOD_ID)
    public void onCameraEditorInit(CameraEditorEvent.Init event)
    {
        location = null;

        GuiDashboard.get();

        Minecraft mc = Minecraft.getMinecraft();
        GuiCameraEditor editor = event.editor;
        GuiBlockbusterPanels panels = mchorse.blockbuster.ClientProxy.panels;
        GuiRecordingEditorPanel record = panels.recordingEditorPanel;

        /* Just in case */
        if (record == null)
        {
            return;
        }

        editorElement = new GuiElement(mc)
        {
            @Override
            public boolean mouseClicked(GuiContext context)
            {
                return super.mouseClicked(context) || (record.editor.delegate != null && record.editor.area.isInside(context));
            }

            @Override
            public boolean mouseScrolled(GuiContext context)
            {
                return super.mouseScrolled(context) || (record.editor.delegate != null && record.editor.area.isInside(context));
            }

            @Override
            public void draw(GuiContext context)
            {
                if (this.isVisible() && record.editor.delegate != null)
                {
                    Area area = record.editor.delegate.area;

                    area.draw(0x66000000);
                }

                if (editor.getRunner().isRunning())
                {
                    ScrollArea scroll = panels.recordingEditorPanel.selector.scroll;

                    scroll.scrollIntoView(scroll.scrollItemSize * (int) (editor.getRunner().ticks - panels.recordingEditorPanel.record.preDelay), 2);
                    panels.recordingEditorPanel.selector.cursor = (int) editor.getRunner().ticks;
                }

                super.draw(context);
            }
        };
        editorElement.noCulling();

        Consumer<GuiIconElement> refresh = (b) ->
        {
            boolean show = editorElement.isVisible();

            editor.panel.flex().h(1, show ? -150 : -70);
            editor.timeline.flex().y(1, show ? -100 : -20);
            record.records.flex().h(1, show ? -80 : 0);
            b.both(show ? Icons.DOWNLOAD : Icons.UPLOAD);

            editor.root.resize();
        };

        GuiIconElement open = new GuiIconElement(mc, BBIcons.EDITOR, (b) -> panels.recordingEditorPanel.records.toggleVisible());
        GuiIconElement toggle = new GuiIconElement(mc, Icons.UPLOAD, (b) ->
        {
            if (!record.selector.isVisible())
            {
                return;
            }

            editorElement.setVisible(!editorElement.isVisible());
            refresh.accept(b);
        });

        GuiDrawable drawable = new GuiDrawable((context) ->
        {
            int w = (int) (editor.root.area.w * Blockbuster.audioWaveformWidth.get());

            AudioRenderer.renderAll(editor.root.area.x + (editor.root.area.w - w) / 2, editor.timeline.area.y - 15, w, Blockbuster.audioWaveformHeight.get(), context.screen.width, context.screen.height);
            record.selector.cursor = editor.timeline.value;
        });

        IKey category = IKey.lang("blockbuster.gui.aperture.keys.category");
        IKey toggleEditor = IKey.lang("blockbuster.gui.aperture.keys.toggle_editor");
        IKey detachScene = IKey.lang("blockbuster.gui.aperture.keys.detach_scene");
        IKey reloadScene = IKey.lang("blockbuster.gui.aperture.keys.reload_scene");

        GuiDirectorConfigOptions directorOptions = editor.config.getChildren(GuiDirectorConfigOptions.class).get(0);

        open.tooltip(IKey.lang("blockbuster.gui.dashboard.player_recording"), Direction.TOP);
        open.keys().register(IKey.lang("blockbuster.gui.aperture.keys.toggle_list"), Keyboard.KEY_L, () -> open.clickItself(editor.context)).held(Keyboard.KEY_LCONTROL).category(category);
        toggle.tooltip(toggleEditor, Direction.TOP);
        toggle.keys().register(toggleEditor, Keyboard.KEY_E, () -> toggle.clickItself(editor.context)).held(Keyboard.KEY_LCONTROL).category(category);
        toggle.keys().register(detachScene, Keyboard.KEY_D, () -> directorOptions.detachScene.clickItself(editor.context)).held(Keyboard.KEY_LSHIFT).category(category).active(() -> !editor.flight.isFlightEnabled() && directorOptions.detachScene.isEnabled());
        toggle.keys().register(reloadScene, Keyboard.KEY_R, () -> directorOptions.reloadScene.clickItself(editor.context)).held(Keyboard.KEY_LSHIFT).category(category).active(() -> !editor.flight.isFlightEnabled());

        editorElement.setVisible(false);

        toggle.flex().relative(editor.timeline).set(0, 0, 20, 20).x(1F);
        open.flex().relative(editor.timeline).set(-20, 0, 20, 20);
        editor.timeline.flex().x(30).w(1, -60);

        editor.top.remove(editor.timeline);
        cameraEditorElements = new GuiElement(mc).noCulling();
        cameraEditorElements.add(drawable, editor.timeline, toggle, open, editorElement);

        editor.top.add(cameraEditorElements);
        refresh.accept(toggle);
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
            boolean toOpenCamera = toOpen instanceof GuiCameraEditor;

            if (location != null)
            {
                int tick = ClientProxy.getCameraEditor().timeline.value;

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
                GuiDashboard.get();

                GuiCameraEditor editor = ClientProxy.getCameraEditor();
                GuiRecordingEditorPanel panel = mchorse.blockbuster.ClientProxy.panels.recordingEditorPanel;

                panel.open();
                panel.appear();
                panel.selector.removeFromParent();
                panel.selector.flex().relative(editor.viewport);
                panel.editor.removeFromParent();
                panel.editor.flex().relative(editor.viewport);
                panel.records.removeFromParent();
                panel.records.flex().relative(editor.viewport).h(1F, editorElement.isVisible() ? -80 : 0);
                panel.records.setVisible(false);

                cameraEditorElements.prepend(panel.records);
                editorElement.add(panel.selector, panel.editor);
            }
            else if (current instanceof GuiCameraEditor)
            {
                mchorse.blockbuster.ClientProxy.panels.recordingEditorPanel.save();
            }
        }
    }
}