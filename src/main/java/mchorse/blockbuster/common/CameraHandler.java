package mchorse.blockbuster.common;

import mchorse.aperture.ClientProxy;
import mchorse.aperture.client.gui.GuiCameraEditor;
import mchorse.aperture.events.CameraEditorPlaybackStateEvent;
import mchorse.aperture.events.CameraEditorScrubbedEvent;
import mchorse.aperture.network.common.PacketCameraProfileList;
import mchorse.blockbuster.common.item.ItemPlayback;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.client.ClientHandlerCameraProfileList;
import mchorse.blockbuster.network.client.ClientHandlerSceneLength;
import mchorse.blockbuster.network.common.PacketPlaybackButton;
import mchorse.blockbuster.network.common.camera.PacketRequestLength;
import mchorse.blockbuster.network.common.camera.PacketRequestProfiles;
import mchorse.blockbuster.network.common.camera.PacketSceneLength;
import mchorse.blockbuster.network.common.director.sync.PacketDirectorGoto;
import mchorse.blockbuster.network.common.director.sync.PacketDirectorPlay;
import mchorse.blockbuster.network.server.ServerHandlerPlaybackButton;
import mchorse.blockbuster.network.server.ServerHandlerRequestLength;
import mchorse.blockbuster.network.server.ServerHandlerRequestProfiles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Optional.Method;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

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

    @Method(modid = "aperture")
    public static void register()
    {
        ClientProxy.EVENT_BUS.register(new CameraHandler());
        MinecraftForge.EVENT_BUS.register(new CameraGUIHandler());
    }

    @Method(modid = "aperture")
    public static void registerMessages()
    {
        /* Camera management */
        Dispatcher.register(PacketPlaybackButton.class, ServerHandlerPlaybackButton.class, Side.SERVER);
        Dispatcher.register(PacketRequestProfiles.class, ServerHandlerRequestProfiles.class, Side.SERVER);
        Dispatcher.register(PacketCameraProfileList.class, ClientHandlerCameraProfileList.class, Side.CLIENT);

        Dispatcher.register(PacketRequestLength.class, ServerHandlerRequestLength.class, Side.SERVER);
        Dispatcher.register(PacketSceneLength.class, ClientHandlerSceneLength.class, Side.CLIENT);
    }

    @Method(modid = "aperture")
    @SubscribeEvent
    public void onCameraScrub(CameraEditorScrubbedEvent event)
    {
        BlockPos pos = getDirectorPos();

        if (pos != null)
        {
            Dispatcher.sendToServer(new PacketDirectorGoto(pos, event.position));
        }
    }

    @Method(modid = "aperture")
    @SubscribeEvent
    public void onCameraPlause(CameraEditorPlaybackStateEvent event)
    {
        BlockPos pos = getDirectorPos();

        if (pos != null)
        {
            Dispatcher.sendToServer(new PacketDirectorPlay(pos, event.play ? PacketDirectorPlay.PLAY : PacketDirectorPlay.PAUSE, event.position));
        }
    }

    /**
     * Get director block position from player's playback button
     */
    public static BlockPos getDirectorPos()
    {
        BlockPos pos = null;
        ItemStack left = Minecraft.getMinecraft().player.getHeldItemMainhand();

        if (left != null && left.getItem() instanceof ItemPlayback)
        {
            pos = ItemPlayback.getBlockPos("Dir", left);
        }

        return pos;
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
            BlockPos pos = getDirectorPos();

            if (pos != null)
            {
                int tick = ClientProxy.cameraEditor.scrub.value;

                if (current != ClientProxy.cameraEditor && toOpen instanceof GuiCameraEditor)
                {
                    /* Camera editor opens */
                    CameraHandler.tick = tick;

                    Dispatcher.sendToServer(new PacketDirectorPlay(pos, PacketDirectorPlay.START, tick));
                    Dispatcher.sendToServer(new PacketRequestLength(pos));
                }
            }
        }
    }
}